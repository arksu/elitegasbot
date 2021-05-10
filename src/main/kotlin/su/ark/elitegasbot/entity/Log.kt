package su.ark.elitegasbot.entity

import java.util.*
import javax.persistence.*

@Entity
class Log(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(columnDefinition = "TEXT", nullable = false)
    val text: String,

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    val date: Date = Date()
)