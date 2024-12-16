package ru.quipy.projections.entity

import javax.persistence.*
import java.util.*
import javax.print.attribute.standard.RequestingUserName

@Entity
@Table(name = "project_projection", schema = "task_manager")
class ProjectProjection() {
    @Id
    lateinit var id: UUID
    lateinit var name: String
    lateinit var description: String
    lateinit var authorUserName: String
    lateinit var authorName: String

    constructor(id: UUID, name: String, description: String, authorUserName: String, authorName: String) : this() {
        this.id = id
        this.name = name
        this.description = description
        this.authorUserName = authorUserName
        this.authorName = authorName
    }
}
