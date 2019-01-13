package nhk.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id = 0

    var name = ""
}