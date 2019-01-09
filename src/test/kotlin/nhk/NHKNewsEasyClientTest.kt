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
        val url = "https://www3.nhk.or.jp/news/easy/k10011770571000/k10011770571000.html"
        val news = NHKNewsEasyClient.parseNews(url)

        Assert.assertNotNull(news)
    }
}