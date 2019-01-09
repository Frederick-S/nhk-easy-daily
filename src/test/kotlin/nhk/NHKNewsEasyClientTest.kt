package nhk

import org.junit.Assert
import org.junit.Test

class NHKNewsEasyClientTest {
    @Test
    fun shouldGetTopNewsForToday() {
        val topNews = NHKNewsEasyClient.getTopNews()

        Assert.assertTrue(topNews.isNotEmpty())
    }

    @Test
    fun shouldParseNews() {
        val topNews = NHKNewsEasyClient.getTopNews()
        val news = NHKNewsEasyClient.parseNews(topNews[0])

        Assert.assertNotNull(news)
    }
}