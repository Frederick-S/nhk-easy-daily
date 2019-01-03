package nhk

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.readValue
import nhk.domain.NHKTopNews
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*

object NHKNewsEasy {
    fun getTopNewsForToday(): List<NHKTopNews> {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(Constants.TOP_NEWS_URL)
                .build()
        val response = okHttpClient.newCall(request).execute()
        val json = response.body()?.string()

        json?.let {
            val utcNow = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val objectMapper = ObjectMapper()
            objectMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            objectMapper.dateFormat = SimpleDateFormat("yyyy-dd-MM hh:mm:ss")

            return objectMapper.readValue<List<NHKTopNews>>(it)
                    .filter { topNews ->
                        val publishedDateUtc = Calendar.getInstance()
                        publishedDateUtc.time = topNews.newsPrearrangedTime
                        publishedDateUtc.add(Calendar.HOUR, -9)

                        utcNow.get(Calendar.DAY_OF_MONTH) == publishedDateUtc.get(Calendar.DAY_OF_MONTH)
                    }
        }

        return emptyList()
    }
}