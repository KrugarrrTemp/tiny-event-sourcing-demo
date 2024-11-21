package ru.quipy.projections.entity

import java.util.*
import javax.persistence.*


@Entity
@Table(name = "task_status", schema = "task_manager")
data class TaskStatusEntity(
        @Id
        @GeneratedValue
        val id: UUID = UUID.randomUUID(),

        @Column(nullable = false)
        val name: String = "",

        @Column(nullable = false)
        val colour: String = "",

        @ManyToOne
        @JoinColumn(name = "project_id")
        var project: ProjectProjection? = null
) {
    constructor() : this(
            id = UUID.randomUUID(),
            name = "",
            colour = "",
            project = null
    )
}