package nhk

import nhk.repository.NewsRepository
import nhk.service.NewsService
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZoneId
import java.time.ZonedDateTime

class NewsServiceTest : BaseTest() {
    @Autowired
    lateinit var newsService: NewsService

    @Autowired
    lateinit var newsRepository: NewsRepository

    @Test
    fun shouldGetTopNewsForToday() {
        val topNews = newsService.getTopNews()

        Assert.assertTrue(topNews.isNotEmpty())
    }

    @Test
    fun shouldParseNewsAndWords() {
        val topNews = newsService.getTopNews()
        val news = newsService.parseNews(topNews[0])

        Assert.assertNotNull(news)
        Assert.assertTrue(news.words.isNotEmpty())
    }

    @Test
    fun shouldSaveTopNewsForSpecifiedDate() {
        val topNews = newsService.getTopNews()
        val date = ZonedDateTime.of(topNews[0].newsPrearrangedTime, ZoneId.of("+9"))
                .withZoneSameInstant(ZoneId.systemDefault())

        newsService.saveTopNewsOf(date)

        val allNews = newsRepository.findAll().toList()

        Assert.assertTrue(allNews.isNotEmpty())
    }
}