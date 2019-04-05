package nhk.entity

import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
class Word : BaseEntity() {
    @Column(unique = true, length = 50)
    var name = ""

    @OneToMany(mappedBy = "word", cascade = [CascadeType.ALL])
    var definitions = emptyList<WordDefinition>()

    var createdAtUtc = Instant.now()

    var updatedAtUtc = Instant.now()
}