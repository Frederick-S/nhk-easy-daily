package nhk.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class WordDefinition : BaseEntity() {
    @ManyToOne
    @JoinColumn
    var word = Word()

    @Column(length = 500)
    var definition = ""

    @Column(length = 1000)
    var definitionWithRuby = ""
}