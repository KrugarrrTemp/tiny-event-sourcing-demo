package ru.quipy.projections.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "task_projection", schema = "task_manager")
class TaskProjection() {

    @Id
    lateinit var id: UUID
    lateinit var name: String
    lateinit var projectId: UUID
    lateinit var taskStatusId: UUID

    constructor(id: UUID, name: String, projectId: UUID, taskStatusId: UUID) : this() {
        this.id = id
        this.name = name
        this.projectId = projectId
        this.taskStatusId = taskStatusId
    }
}
