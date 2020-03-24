package nhk.entity

import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToMany
import javax.persistence.OneToMany

@Entity
class Word : BaseEntity() {
    @Column(length = 50)
    var name = ""

    @OneToMany(mappedBy = "word", cascade = [CascadeType.ALL])
    var definitions = emptyList<WordDefinition>()

    var createdAtUtc = Instant.now()

    var updatedAtUtc = Instant.now()

    @ManyToMany(mappedBy = "words", fetch = FetchType.LAZY)
    var news = emptySet<News>()
}