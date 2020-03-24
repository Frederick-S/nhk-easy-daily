package nhk.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.module.kotlin.readValue
import nhk.Constants
import nhk.domain.TopNews
import nhk.entity.News
import nhk.entity.Word
import nhk.entity.WordDefinition
import nhk.repository.NewsRepository
import nhk.repository.WordRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class NewsService {
    private val logger: Logger = LoggerFactory.getLogger(NewsService::class.java)

    @Autowired
    lateinit var newsRepository: NewsRepository

    @Autowired
    lateinit var wordRepository: WordRepository

    fun saveTopNewsOf(date: ZonedDateTime) {
        val topNews = getTopNews()
        val newsForToday = topNews.filter { news ->
            val publishedDate = ZonedDateTime.of(news.newsPrearrangedTime, ZoneId.of("+9"))
                    .withZoneSameInstant(ZoneId.systemDefault())

            date.dayOfMonth == publishedDate.dayOfMonth
        }.map { news ->
            parseNews(news)
        }

        newsForToday.forEach { news ->
            newsRepository.save(news)
        }
    }

    fun getTopNews(): List<TopNews> {
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

    fun parseNews(topNews: TopNews): News {
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
        news.imageUrl = topNews.newsWebImageUri
        news.m3u8Url = "https://nhks-vh.akamaihd.net/i/news/easy/${topNews.newsId}.mp4/master.m3u8"
        news.publishedAtUtc = ZonedDateTime.of(topNews.newsPrearrangedTime, ZoneId.of("+9")).toInstant()
        news.words = parseWords(newsId)

        return news
    }

    private fun parseWords(newsId: String): Set<Word> {
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
            }.toSet()
        }

        return emptySet()
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
                                wordDefinition.word = word

                                wordDefinition
                            }

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