package nhk.controller.api

import nhk.controller.BaseController
import nhk.dto.NHKNewsDto
import nhk.repository.NHKNewsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
class NewsController : BaseController() {
    @Autowired
    lateinit var nhkNewsRepository: NHKNewsRepository

    @GetMapping("/news")
    fun getNews(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: Date, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: Date): List<NHKNewsDto> {
        return nhkNewsRepository.findByPublishedAtUtcBetween(startDate.toInstant(), endDate.toInstant())
                .map {
                    val news = NHKNewsDto()
                    news.newsId = it.newsId
                    news.title = it.title
                    news.body = it.body
                    news.url = it.url
                    news.m3u8Url = it.m3u8Url
                    news.imageUrl = it.imageUrl
                    news.publishedAtUtc = it.publishedAtUtc

                    news
                }
    }
}