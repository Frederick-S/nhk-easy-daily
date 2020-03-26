package nhk.entity

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class WordDefinition : BaseEntity() {
    @Column(length = 500)
    var definition = ""

    @Column(length = 1000)
    var definitionWithRuby = ""

    var wordId = 0
}