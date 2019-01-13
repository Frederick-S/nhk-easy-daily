package nhk.task

import nhk.NHKNewsEasyClient
import nhk.repository.NHKNewsRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.Calendar
import java.util.TimeZone

@Component
class NHKNewsEasyDailyTask {
    val logger = LoggerFactory.getLogger(NHKNewsEasyDailyTask::class.java)

    @Autowired
    lateinit var nhkNewsRepository: NHKNewsRepository

    @Scheduled(cron = "*/10 * * * * *")
    fun getTopNewsForToday() {
        val topNews = NHKNewsEasyClient.getTopNews()
        val utcNow = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val newsForToday = topNews.filter {
            val publishedDateUtc = Calendar.getInstance()
            publishedDateUtc.time = it.newsPrearrangedTime
            publishedDateUtc.add(Calendar.HOUR, -9)

            utcNow.get(Calendar.DAY_OF_MONTH) == publishedDateUtc.get(Calendar.DAY_OF_MONTH)
        }.map { NHKNewsEasyClient.parseNews(it) }

        newsForToday.forEach { nhkNewsRepository.save(it) }
    }
}