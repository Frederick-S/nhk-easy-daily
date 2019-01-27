package nhk.repository

import nhk.entity.NHKNews
import org.springframework.data.repository.CrudRepository

interface NHKNewsRepository : CrudRepository<NHKNews, Int>