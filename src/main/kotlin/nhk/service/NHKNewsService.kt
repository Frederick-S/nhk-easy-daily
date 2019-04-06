package nhk.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.readValue
import nhk.Constants
import nhk.DateUtil
import nhk.domain.NHKTopNews
import nhk.entity.NHKNews
import nhk.entity.Word
import nhk.entity.WordDefinition
import nhk.repository.NHKNewsRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.TimeZone

@Service
class NHKNewsService {
    private val logger: Logger = LoggerFactory.getLogger(NHKNewsService::class.java)

    @Autowired
    lateinit var nhkNewsRepository: NHKNewsRepository

    fun saveTopNewsOf(utcDate: ZonedDateTime) {
        val topNews = getTopNews()
        val newsForToday = topNews.filter {
            val publishedDateUtc = DateUtil.nhkDateToUtc(it.newsPrearrangedTime)

            utcDate.dayOfMonth == publishedDateUtc.dayOfMonth
        }.map {
            parseNews(it)
        }

        newsForToday.forEach {
            nhkNewsRepository.save(it)
        }
    }

    fun getTopNews(): List<NHKTopNews> {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(Constants.TOP_NEWS_URL)
                .build()
        val response = okHttpClient.newCall(request).execute()
        val json = response.body()?.string()

        json?.let {
            val objectMapper = ObjectMapper()
            objectMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            objectMapper.dateFormat = SimpleDateFormat(Constants.NHK_NEWS_EASY_DATE_FORMAT)
            objectMapper.setTimeZone(TimeZone.getTimeZone("JST"))

            return objectMapper.readValue(it)
        }

        return emptyList()
    }

    fun parseNews(nhkTopNews: NHKTopNews): NHKNews {
        val newsId = nhkTopNews.newsId
        val url = "https://www3.nhk.or.jp/news/easy/$newsId/$newsId.html"
        val document = Jsoup.connect(url).get()
        val body = document.getElementById("js-article-body")
        val content = body.html()

        val nhkNews = NHKNews()
        nhkNews.newsId = newsId
        nhkNews.title = nhkTopNews.title
        nhkNews.titleWithRuby = nhkTopNews.titleWithRuby
        nhkNews.outlineWithRuby = nhkTopNews.outlineWithRuby
        nhkNews.url = url
        nhkNews.body = content
        nhkNews.imageUrl = nhkTopNews.newsWebImageUri
        nhkNews.m3u8Url = "https://nhks-vh.akamaihd.net/i/news/easy/${nhkTopNews.newsId}.mp4/master.m3u8"
        nhkNews.publishedAtUtc = DateUtil.nhkDateToUtc(nhkTopNews.newsPrearrangedTime).toInstant()
        nhkNews.words = parseWords(newsId)
                .apply {
                    forEach { word ->
                        word.nhkNews = nhkNews
                    }
                }

        return nhkNews
    }

    fun parseWords(newsId: String): List<Word> {
        val url = "https://www3.nhk.or.jp/news/easy/$newsId/$newsId.out.dic"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()
        val response = okHttpClient.newCall(request).execute()
        val json = response.body()?.string()

        json?.let {
            val objectMapper = ObjectMapper()
            val root = objectMapper.readTree(it)
            val reikai = root.get("reikai")
            val entries = reikai.get("entries")

            return entries.flatMap { entry ->
                entry.toList()
                        .groupBy { w -> w.get("hyouki")[0].asText() }
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
        }

        return emptyList()
    }

    private fun extractWordDefinition(definitionWithRuby: String): String {
        val document = Jsoup.parse(definitionWithRuby)
        val rubies = document.select("ruby")

        rubies.forEach {
            it.select("rt").remove()
        }

        return document.text()
    }
}