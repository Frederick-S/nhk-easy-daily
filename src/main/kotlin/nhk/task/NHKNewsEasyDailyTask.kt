package nhk.task

import nhk.service.NHKNewsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class NHKNewsEasyDailyTask {
    private val logger: Logger = LoggerFactory.getLogger(NHKNewsEasyDailyTask::class.java)

    @Autowired
    lateinit var nhkNewsService: NHKNewsService

    @Scheduled(cron = "0 0 18 * * *")
    fun saveTopNewsForToday() {
        val now = ZonedDateTime.now()

        logger.info("Start to fetch news, now={}", now)

        nhkNewsService.saveTopNewsOf(now)
    }
}