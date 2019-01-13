package nhk

import nhk.repository.NHKNewsRepository
import nhk.service.NHKNewsService
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class NHKNewsEasyClientTest : BaseTest() {
    @Autowired
    lateinit var nhkNewsService: NHKNewsService

    @Autowired
    lateinit var nhkNewsRepository: NHKNewsRepository

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

    @Test
    fun shouldSaveTopNewsForSpecifiedDate() {
        val topNews = nhkNewsService.getTopNews()
        val calendar = DateUtil.nhkDateToUtc(topNews[0].newsPrearrangedTime)

        nhkNewsService.saveTopNewsOf(calendar)

        val allNews = nhkNewsRepository.findAll().toList()

        Assert.assertTrue(allNews.isNotEmpty())
    }
}