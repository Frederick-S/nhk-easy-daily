package nhk.task

import nhk.NHKNewsEasyClient
import java.util.Calendar
import java.util.TimeZone

class NHKNewsEasyDailyTask {
    fun getTopNewsForToday() {
        val topNews = NHKNewsEasyClient.getTopNews()
        val utcNow = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val topNewsForToday = topNews.filter {
            val publishedDateUtc = Calendar.getInstance()
            publishedDateUtc.time = it.newsPrearrangedTime
            publishedDateUtc.add(Calendar.HOUR, -9)

            utcNow.get(Calendar.DAY_OF_MONTH) == publishedDateUtc.get(Calendar.DAY_OF_MONTH)
        }
    }
}