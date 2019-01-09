package nhk

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.readValue
import nhk.domain.NHKNews
import nhk.domain.NHKTopNews
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.text.SimpleDateFormat

object NHKNewsEasyClient {
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

            return objectMapper.readValue<List<NHKTopNews>>(it)
                    .map { nhkTopNews ->
                        nhkTopNews.url = "https://www3.nhk.or.jp/news/easy/${nhkTopNews.newsId}/${nhkTopNews.newsId}.html"

                        nhkTopNews
                    }
        }

        return emptyList()
    }

    fun parseNews(url: String): NHKNews {
        val document = Jsoup.connect(url).get()

        return NHKNews()
    }
}