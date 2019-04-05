package nhk.entity

import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Word : BaseEntity() {
    @Column(unique = true, length = 50)
    var name = ""

    @Column(length = 500)
    var definition = ""

    @Column(length = 1000)
    var definitionWithRuby = ""

    var createdAtUtc = Instant.now()

    var updatedAtUtc = Instant.now()
}