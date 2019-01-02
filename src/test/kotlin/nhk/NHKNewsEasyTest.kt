package nhk

import org.junit.Assert
import org.junit.Test

class NHKNewsEasyTest {
    @Test
    fun shouldGetTopNewsForToday() {
        val topNews = NHKNewsEasy.getTopNewsForToday()

        Assert.assertTrue(topNews.isNotEmpty())
    }
}