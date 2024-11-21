import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
class UserProjection() {
    @Id
    lateinit var userId: UUID
    lateinit var username: String
    lateinit var fullName: String
    var createdAt: Long = 0

    constructor(userId: UUID, username: String, fullName: String, createdAt: Long) : this() {
        this.userId = userId
        this.username = username
        this.fullName = fullName
        this.createdAt = createdAt
    }
}