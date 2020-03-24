package nhk.controller.api

import nhk.controller.BaseController
import nhk.dto.NHKNewsDto
import nhk.repository.NHKNewsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NewsController : BaseController() {
    @Autowired
    lateinit var nhkNewsRepository: NHKNewsRepository

    @GetMapping("/news")
    fun getNews(): List<NHKNewsDto> {
        return nhkNewsRepository.findAll()
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