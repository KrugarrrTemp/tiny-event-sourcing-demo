package ru.quipy.projections.entity

import java.util.*
import javax.persistence.*


@Entity
@Table(name = "task_status_projection", schema = "task_manager")
class TaskStatusProjection() {
    @Id
    lateinit var id: UUID

    lateinit var  name:String
    lateinit var  colour: String
    lateinit var  projectId: UUID

    constructor(id: UUID, name: String, colour: String, projectId: UUID) : this() {
        this.id = id
        this.name = name
        this.colour = colour
        this.projectId = projectId
    }
}