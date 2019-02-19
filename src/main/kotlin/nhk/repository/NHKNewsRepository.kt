package nhk.repository

import nhk.entity.NHKNews
import org.springframework.data.jpa.repository.JpaRepository

interface NHKNewsRepository : JpaRepository<NHKNews, Int>