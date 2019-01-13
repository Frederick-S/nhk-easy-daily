package nhk.repository

import nhk.domain.NHKNews
import org.springframework.data.repository.CrudRepository

interface NHKNewsRepository : CrudRepository<NHKNews, Int>