package nhk.entity

import javax.persistence.Entity

@Entity
class Word : BaseEntity() {
    var name = ""

    var definition = ""
}