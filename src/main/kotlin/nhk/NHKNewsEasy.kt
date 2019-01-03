package nhk

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.readValue
import nhk.domain.NHKTopNews
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat

object NHKNewsEasy {
    fun getTopNewsForToday(): List<NHKTopNews> {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(Constants.TOP_NEWS_URL)
                .build()
        val response = okHttpClient.newCall(request).execute()
        val json = response.body()?.string()

        json?.let {
            val objectMapper = ObjectMapper()
            objectMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            objectMapper.dateFormat = SimpleDateFormat("yyyy-dd-MM hh:mm:ss")

            return objectMapper.readValue(it)
        }

        return emptyList()
    }
}