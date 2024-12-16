package ru.quipy.projections.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "participant_projection", schema = "task_manager")
class ParticipantProjection() {
    @Id
    lateinit var username: String
    lateinit var id: UUID

    lateinit var projectId: UUID



    lateinit var fullName: String


    constructor(id: UUID, projectId: UUID, username: String, fullName: String) : this() {
        this.id = id
        this.projectId = projectId
        this.username = username
        this.fullName = fullName
    }
}
