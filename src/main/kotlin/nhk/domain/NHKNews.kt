package nhk.domain

import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Transient

@Entity
class NHKNews {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id = 0

    var newsId = ""

    var title = ""

    var titleWithRuby = ""

    var outlineWithRuby = ""

    var body = ""

    var url = ""

    var m3u8Url = ""

    var imageUrl = ""

    var publishedAtUtc = Date()

    @Transient
    var words = emptyList<Word>()
}