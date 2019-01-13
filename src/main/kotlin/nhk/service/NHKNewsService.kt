package nhk.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.readValue
import nhk.Constants
import nhk.domain.NHKNews
import nhk.domain.NHKTopNews
import nhk.repository.NHKNewsRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Calendar

@Service
class NHKNewsService {
    val logger = LoggerFactory.getLogger(NHKNewsService::class.java)

    @Autowired
    lateinit var nhkNewsRepository: NHKNewsRepository

    fun saveTopNewsOf(utcDate: Calendar) {
        val topNews = getTopNews()
        val newsForToday = topNews.filter {
            val publishedDateUtc = Calendar.getInstance()
            publishedDateUtc.time = it.newsPrearrangedTime
            publishedDateUtc.add(Calendar.HOUR, -9)

            utcDate.get(Calendar.DAY_OF_MONTH) == publishedDateUtc.get(Calendar.DAY_OF_MONTH)
        }.map { parseNews(it) }

        newsForToday.forEach { nhkNewsRepository.save(it) }
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

            return objectMapper.readValue(it)
        }

        return emptyList()
    }

    fun parseNews(nhkTopNews: NHKTopNews): NHKNews {
        val url = "https://www3.nhk.or.jp/news/easy/${nhkTopNews.newsId}/${nhkTopNews.newsId}.html"
        val document = Jsoup.connect(url).get()
        val body = document.getElementById("js-article-body")
        val content = body.html()

        val nhkNews = NHKNews()
        nhkNews.title = nhkTopNews.title
        nhkNews.titleWithRuby = nhkTopNews.titleWithRuby
        nhkNews.outlineWithRuby = nhkTopNews.outlineWithRuby
        nhkNews.url = url
        nhkNews.body = content
        nhkNews.imageUrl = nhkTopNews.newsWebImageUri
        nhkNews.m3u8Url = "https://nhks-vh.akamaihd.net/i/news/easy/${nhkTopNews.newsId}.mp4/master.m3u8"
        nhkNews.publishedAt = nhkTopNews.newsPrearrangedTime

        return nhkNews
    }
}