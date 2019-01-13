package nhk

import nhk.service.NHKNewsService
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class NHKNewsEasyClientTest : BaseTest() {
    @Autowired
    lateinit var nhkNewsService: NHKNewsService

    @Test
    fun shouldGetTopNewsForToday() {
        val topNews = nhkNewsService.getTopNews()

        Assert.assertTrue(topNews.isNotEmpty())
    }

    @Test
    fun shouldParseNews() {
        val topNews = nhkNewsService.getTopNews()
        val news = nhkNewsService.parseNews(topNews[0])

        Assert.assertNotNull(news)
    }
}