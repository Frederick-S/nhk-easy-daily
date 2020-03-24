package nhk.dto

import java.time.Instant

class NHKNewsDto {
    var newsId = ""

    var title = ""

    var body = ""

    var url = ""

    var m3u8Url = ""

    var imageUrl = ""

    var publishedAtUtc = Instant.now()
}