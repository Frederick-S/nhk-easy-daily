package nhk.repository

import nhk.entity.NHKNews
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface NHKNewsRepository : JpaRepository<NHKNews, Int> {
    fun findByPublishedAtUtcBetween(start: Instant, end: Instant): List<NHKNews>
}