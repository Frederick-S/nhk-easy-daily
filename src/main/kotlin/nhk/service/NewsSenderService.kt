package nhk.service

import nhk.repository.NewsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class NewsSenderService {
    private val logger: Logger = LoggerFactory.getLogger(KindleService::class.java)

    @Autowired
    private lateinit var newsRepository: NewsRepository

    @Autowired
    private lateinit var kindleService: KindleService

    fun sendToSubscribers(utcDate: ZonedDateTime) {
    }
}
