package nhk.entity

import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany

@Entity
class NHKNews : BaseEntity() {
    @Column(length = 50)
    var newsId = ""

    @Column(length = 50)
    var title = ""

    @Column(length = 500)
    var titleWithRuby = ""

    @Column(length = 1000)
    var outlineWithRuby = ""

    @Column(columnDefinition = "text")
    var body = ""

    @Column(length = 200)
    var url = ""

    @Column(length = 200)
    var m3u8Url = ""

    @Column(length = 200)
    var imageUrl = ""

    var publishedAtUtc = Instant.now()

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(name = "news_word",
            joinColumns = [JoinColumn(name = "news_id", referencedColumnName = "id", nullable = false, updatable = false)],
            inverseJoinColumns = [JoinColumn(name = "word_id", referencedColumnName = "id", nullable = false, updatable = false)])
    var words = emptySet<Word>()
}