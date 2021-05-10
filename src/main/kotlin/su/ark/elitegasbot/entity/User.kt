package su.ark.elitegasbot.entity

import javax.persistence.*

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(length = 1000, nullable = false)
    val name: String
)