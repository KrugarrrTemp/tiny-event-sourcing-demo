package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.entity.ProjectProjection
import java.util.UUID

@Repository
interface ProjectProjectionRepository : JpaRepository<ProjectProjection, UUID> {
    fun findAllByAuthorUserName(userName: String): List<ProjectProjection>
}

