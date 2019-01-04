package nhk.task

import nhk.NHKNewsEasy
import java.util.*

class NHKNewsEasyDailyTask {
    fun getTopNewsForToday() {
        val topNews = NHKNewsEasy.getTopNews()
        val utcNow = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val topNewsForToday = topNews.filter {
            val publishedDateUtc = Calendar.getInstance()
            publishedDateUtc.time = it.newsPrearrangedTime
            publishedDateUtc.add(Calendar.HOUR, -9)

            utcNow.get(Calendar.DAY_OF_MONTH) == publishedDateUtc.get(Calendar.DAY_OF_MONTH)
        }
    }
}