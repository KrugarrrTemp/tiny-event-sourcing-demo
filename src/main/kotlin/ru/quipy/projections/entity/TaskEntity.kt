package ru.quipy.projections.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "task", schema = "task_manager")
data class TaskEntity(
        @Id
        @GeneratedValue
        val id: UUID = UUID.randomUUID(),

        @Column(nullable = false)
        val name: String = "",

        @ElementCollection
        val taskStatusesAssigned: MutableSet<UUID> = mutableSetOf(),

        @ElementCollection
        val performersAssigned: MutableSet<UUID> = mutableSetOf(),

        @ManyToOne
        @JoinColumn(name = "project_id")
        var project: ProjectProjection? = null
) {
    constructor() : this(
            id = UUID.randomUUID(),
            name = "",
            taskStatusesAssigned = mutableSetOf(),
            performersAssigned = mutableSetOf(),
            project = null
    )
}
