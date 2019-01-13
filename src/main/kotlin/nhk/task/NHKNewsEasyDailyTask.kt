package nhk.task

import nhk.service.NHKNewsService
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
    lateinit var nhkNewsService: NHKNewsService

    @Scheduled(cron = "*/10 * * * * *")
    fun saveTopNewsForToday() {
        val utcNow = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        nhkNewsService.saveTopNewsOf(utcNow)
    }
}