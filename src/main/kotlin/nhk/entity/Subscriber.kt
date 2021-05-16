package nhk.entity

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Subscriber : BaseEntity() {
    @Column(unique = true, length = 50)
    var mailAddress = ""
}
