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

        nhkKindleService.sendToKindle(news, "mao_xiaodan@kindle.cn")
    }
}