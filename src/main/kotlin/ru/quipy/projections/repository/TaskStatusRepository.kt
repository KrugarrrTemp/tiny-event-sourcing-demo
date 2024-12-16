package ru.quipy.projections.repository


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.entity.TaskStatusProjection
import java.util.*

@Repository
interface TaskStatusRepository : JpaRepository<TaskStatusProjection?, UUID> {
    fun findByNameAndProjectId(name: String, projectId: UUID) : TaskStatusProjection?
    fun findByIdAndProjectId(statusId: UUID, projectId: UUID) : TaskStatusProjection?
    fun findAllByProjectId(projectId: UUID, ) : List<TaskStatusProjection>?
}
