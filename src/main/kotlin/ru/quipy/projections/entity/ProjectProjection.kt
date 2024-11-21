package ru.quipy.projections.entity

import javax.persistence.*
import java.util.*
import javax.print.attribute.standard.RequestingUserName

@Entity
@Table(name = "project_projection", schema = "task_manager")
class ProjectProjection(
        @Id
        val id: UUID,
        val name: String,
        val description: String,
        val authorUserName: String,

        @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
        val tasks: MutableList<TaskEntity> = mutableListOf(),

        @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
        val taskStatuses: MutableList<TaskStatusEntity> = mutableListOf(),

        @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
        val participants: MutableList<ParticipantEntity> = mutableListOf()
) {
    constructor() : this(
            id = UUID.randomUUID(),
            name = "",
            description = "",
            authorUserName = ""
    )
}
