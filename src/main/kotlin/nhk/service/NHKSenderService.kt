package nhk.service

import nhk.repository.NHKNewsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class NHKSenderService {
    private val logger: Logger = LoggerFactory.getLogger(NHKKindleService::class.java)

    @Autowired
    lateinit var nhkNewsRepository: NHKNewsRepository

    @Autowired
    lateinit var nhkKindleService: NHKKindleService

    fun sendToSubscribers(utcDate: ZonedDateTime) {
    }
}