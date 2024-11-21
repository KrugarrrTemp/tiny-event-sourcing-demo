package ru.quipy.projections.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "participant", schema = "task_manager")
data class ParticipantEntity(
        @Id
        @GeneratedValue
        val id: UUID = UUID.randomUUID(),

        @Column(nullable = false)
        val username: String = "",

        @Column(nullable = false)
        val fullName: String = "",

        @ManyToOne
        @JoinColumn(name = "project_id")
        var project: ProjectProjection? = null
) {
    constructor() : this(
            id = UUID.randomUUID(),
            username = "",
            fullName = "",
            project = null
    )
}
