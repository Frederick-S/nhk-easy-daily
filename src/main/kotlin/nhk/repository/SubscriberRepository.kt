package nhk.repository

import nhk.entity.Subscriber
import org.springframework.data.repository.CrudRepository

interface SubscriberRepository : CrudRepository<Subscriber, Int> {
    fun findByMailAddress(mailAddress: String): List<Subscriber>
}