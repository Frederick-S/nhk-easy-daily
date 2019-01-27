package nhk

import nhk.service.NHKKindleService
import nhk.service.NHKNewsService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class NHKKindleServiceTest : BaseTest() {
    @Autowired
    lateinit var nhkNewsService: NHKNewsService

    @Autowired
    lateinit var nhkKindleService: NHKKindleService

    @Test
    fun shouldSendToKindle() {
        val topNews = nhkNewsService.getTopNews()
        val news = nhkNewsService.parseNews(topNews[0])
        val mailFrom = System.getenv("NHK_SENDER_MAIL_ADDRESS")
        val mailTo = "mao_xiaodan@kindle.cn"

        mailFrom?.let {
            nhkKindleService.sendToKindle(news, it, mailTo)
        }
    }
}