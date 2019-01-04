package nhk

import org.junit.Assert
import org.junit.Test

class NHKNewsEasyClientTest {
    @Test
    fun shouldGetTopNewsForToday() {
        val topNews = NHKNewsEasyClient.getTopNews()

        Assert.assertTrue(topNews.isNotEmpty())
    }
}