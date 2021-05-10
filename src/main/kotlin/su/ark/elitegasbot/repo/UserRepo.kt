package su.ark.elitegasbot.repo

import org.springframework.data.jpa.repository.JpaRepository
import su.ark.elitegasbot.entity.User

interface UserRepo : JpaRepository<User, Long> {
}