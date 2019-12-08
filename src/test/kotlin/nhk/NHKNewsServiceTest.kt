package nhk

import nhk.repository.NHKNewsRepository
import nhk.service.NHKNewsService
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZoneId
import java.time.ZonedDateTime

class NHKNewsServiceTest : BaseTest() {
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
    fun shouldParseNewsAndWords() {
        val topNews = nhkNewsService.getTopNews()
        val news = nhkNewsService.parseNews(topNews[0])

        Assert.assertNotNull(news)
        Assert.assertTrue(news.words.isNotEmpty())
    }

    @Test
    fun shouldSaveTopNewsForSpecifiedDate() {
        val topNews = nhkNewsService.getTopNews()
        val date = ZonedDateTime.of(topNews[0].newsPrearrangedTime, ZoneId.of("+9"))
                .withZoneSameInstant(ZoneId.systemDefault())

        nhkNewsService.saveTopNewsOf(date)

        val allNews = nhkNewsRepository.findAll().toList()

        Assert.assertTrue(allNews.isNotEmpty())
    }
}