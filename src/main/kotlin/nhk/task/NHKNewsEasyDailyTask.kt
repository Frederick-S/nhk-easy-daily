package nhk.task

import nhk.Constants
import nhk.service.NHKNewsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.time.ZonedDateTime

@Component
class NHKNewsEasyDailyTask {
    private val logger: Logger = LoggerFactory.getLogger(NHKNewsEasyDailyTask::class.java)

    @Autowired
    lateinit var nhkNewsService: NHKNewsService

    @Scheduled(cron = "0/10 * * * * *")
    fun saveTopNewsForToday() {
        val utcNow = ZonedDateTime.now(ZoneId.of(Constants.TIME_ZONE_UTC))

        nhkNewsService.saveTopNewsOf(utcNow)
    }
}