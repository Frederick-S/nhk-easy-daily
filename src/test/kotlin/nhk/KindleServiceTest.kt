package nhk

import nhk.service.KindleService
import nhk.service.NewsService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class KindleServiceTest : BaseTest() {
    @Autowired
    lateinit var newsService: NewsService

    @Autowired
    lateinit var kindleService: KindleService

    @Test
    fun shouldSendNewsToKindle() {
        val topNews = newsService.getTopNews()
        val news = newsService.parseNews(topNews[0])
        val mailFrom = System.getenv("NHK_MAIL_FROM_ADDRESS") ?: "example@example.com"
        val mailTo = System.getenv("NHK_MAIL_TO_ADDRESS") ?: "example@example.com"

        kindleService.sendToKindle(news, mailFrom, mailTo)
    }
}