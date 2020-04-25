package nhk.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.module.kotlin.readValue
import nhk.Constants
import nhk.dto.TopNewsDto
import nhk.entity.News
import nhk.entity.Word
import nhk.entity.WordDefinition
import nhk.repository.NewsRepository
import nhk.repository.WordDefinitionRepository
import nhk.repository.WordRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
open class NewsService {
    private val logger: Logger = LoggerFactory.getLogger(NewsService::class.java)

    @Autowired
    lateinit var newsRepository: NewsRepository

    @Autowired
    lateinit var wordRepository: WordRepository

    @Autowired
    lateinit var wordDefinitionRepository: WordDefinitionRepository

    @Transactional
    open fun fetchAndSaveTopNews() {
        val topNews = getTopNews()
                .filter { news ->
                    newsRepository.findByTitle(news.title).isEmpty()
                }.map { news ->
                    parseNews(news)
                }

        newsRepository.saveAll(topNews)

        val words = topNews.flatMap { news ->
            news.words
        }.distinctBy { word ->
            word.name
        }.map { word ->
            val currentWords = wordRepository.findByName(word.name)

            if (currentWords.isEmpty()) {
                word
            } else {
                val currentWord = currentWords.first()
                currentWord.updatedAtUtc = Instant.now()
                currentWord.definitions = word.definitions

                currentWord
            }
        }
        val newWords = words.filter { word ->
            word.id == 0
        }

        wordRepository.saveAll(words)

        newWords.forEach { word ->
            val definitions = word.definitions
                    .map { definition ->
                        definition.wordId = word.id

                        definition
                    }

            wordDefinitionRepository.saveAll(definitions)
        }
    }

    fun getTopNews(): List<TopNewsDto> {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(Constants.TOP_NEWS_URL)
                .build()
        val response = okHttpClient.newCall(request).execute()
        val json = response.body?.string()

        json?.let {
            val javaTimeModule = JavaTimeModule()
            val localDateTimeDeserializer = LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(Constants.NHK_NEWS_EASY_DATE_FORMAT))
            javaTimeModule.addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)

            val objectMapper = ObjectMapper()
            objectMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            objectMapper.registerModule(javaTimeModule)

            return objectMapper.readValue(it)
        }

        return emptyList()
    }

    fun parseNews(topNews: TopNewsDto): News {
        val newsId = topNews.newsId
        val url = "https://www3.nhk.or.jp/news/easy/$newsId/$newsId.html"
        val document = Jsoup.connect(url).get()
        val body = document.getElementById("js-article-body")
        val content = body.html()

        val news = News()
        news.newsId = newsId
        news.title = topNews.title
        news.titleWithRuby = topNews.titleWithRuby
        news.outlineWithRuby = topNews.outlineWithRuby
        news.url = url
        news.body = content
        news.imageUrl = when (topNews.hasNewsWebImage) {
            true -> topNews.newsWebImageUri
            false -> "https://www3.nhk.or.jp/news/easy/${topNews.newsId}/${topNews.newsEasyImageUri}"
        }
        news.m3u8Url = "https://nhks-vh.akamaihd.net/i/news/easy/${topNews.newsId}.mp4/master.m3u8"
        news.publishedAtUtc = ZonedDateTime.of(topNews.newsPrearrangedTime, ZoneId.of("+9")).toInstant()
        news.words = parseWords(newsId)

        return news
    }

    private fun parseWords(newsId: String): MutableSet<Word> {
        val url = "https://www3.nhk.or.jp/news/easy/$newsId/$newsId.out.dic"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()
        val response = okHttpClient.newCall(request).execute()
        val json = response.body?.string()

        json?.let {
            val objectMapper = ObjectMapper()
            val root = objectMapper.readTree(it)
            val reikai = root.get("reikai")
            val entries = reikai.get("entries")

            return entries.flatMap { entry ->
                parseWord(entry)
            }
                    .toMutableSet()
        }

        return mutableSetOf()
    }

    private fun parseWord(root: JsonNode): List<Word> {
        return root.toList()
                .groupBy { node -> node.get("hyouki")[0].asText() }
                .entries
                .map { keyValue ->
                    val word = Word()
                    word.name = keyValue.key

                    val wordDefinitions = keyValue.value
                            .map { node ->
                                val wordDefinition = WordDefinition()
                                wordDefinition.definitionWithRuby = node.get("def").asText()
                                wordDefinition.definition = this.extractWordDefinition(wordDefinition.definitionWithRuby)

                                wordDefinition
                            }
                            .toMutableList()

                    word.definitions = wordDefinitions

                    word
                }
    }

    private fun extractWordDefinition(definitionWithRuby: String): String {
        val document = Jsoup.parse(definitionWithRuby)
        val rubies = document.select("ruby")

        rubies.forEach { ruby ->
            ruby.select("rt").remove()
        }

        return document.text()
    }
}