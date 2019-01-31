package nhk.entity

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Word : BaseEntity() {
    @Column(unique = true)
    var name = ""

    var definition = ""

    var definitionWithRuby = ""
}