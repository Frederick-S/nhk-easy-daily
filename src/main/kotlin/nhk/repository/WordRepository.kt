package nhk.repository

import nhk.entity.Word
import org.springframework.data.repository.CrudRepository

interface WordRepository : CrudRepository<Word, Int> {
    fun findByName(name: String): List<Word>
}