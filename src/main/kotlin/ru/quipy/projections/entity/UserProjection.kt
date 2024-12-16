package ru.quipy.projections.entity

import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "user_projection", schema = "task_manager")
class UserProjection() {
    @Id
    lateinit var username: String
    lateinit var userId: UUID
    lateinit var fullName: String

    constructor(userId: UUID, username: String, fullName: String) : this() {
        this.userId = userId
        this.username = username
        this.fullName = fullName
    }
}