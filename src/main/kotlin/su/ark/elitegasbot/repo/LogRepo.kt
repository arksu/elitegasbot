package su.ark.elitegasbot.repo

import org.springframework.data.repository.CrudRepository
import su.ark.elitegasbot.entity.Log

interface LogRepo : CrudRepository<Log, Long> {
}