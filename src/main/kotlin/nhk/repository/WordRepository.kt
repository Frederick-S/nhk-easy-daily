package nhk.repository

import nhk.domain.Word
import org.springframework.data.repository.CrudRepository

interface WordRepository : CrudRepository<Word, Int>