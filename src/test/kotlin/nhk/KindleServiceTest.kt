package nhk

import nhk.service.KindleService
import nhk.service.NewsParser
import nhk.service.NewsService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class KindleServiceTest : BaseTest() {
    @Autowired
    private lateinit var newsService: NewsService

    @Autowired
    private lateinit var kindleService: KindleService

    @Autowired
    private lateinit var newsParser: NewsParser

    @Test
    fun shouldSendNewsToKindle() {
        val topNews = newsService.getTopNews()
        val news = newsParser.parseNews(topNews[0])
        val mailFrom = System.getenv("NHK_MAIL_FROM_ADDRESS") ?: "example@example.com"
        val mailTo = System.getenv("NHK_MAIL_TO_ADDRESS") ?: "example@example.com"

        kindleService.sendToKindle(news, mailFrom, mailTo)
    }
}
