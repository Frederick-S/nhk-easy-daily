package nhk.entity

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Transient

@Entity
class NHKNews : BaseEntity() {
    var newsId = ""

    var title = ""

    var titleWithRuby = ""

    var outlineWithRuby = ""

    var body = ""

    var url = ""

    var m3u8Url = ""

    var imageUrl = ""

    var publishedAtUtc = Instant.now()

    @Transient
    var words = emptyList<Word>()
}