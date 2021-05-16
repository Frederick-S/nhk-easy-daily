package nhk.repository

import nhk.entity.Subscriber
import org.springframework.data.jpa.repository.JpaRepository

interface SubscriberRepository : JpaRepository<Subscriber, Int> {
    fun findByMailAddress(mailAddress: String): List<Subscriber>
}
