package ru.quipy.projections.entity

import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "user_projection", schema = "task_manager")
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