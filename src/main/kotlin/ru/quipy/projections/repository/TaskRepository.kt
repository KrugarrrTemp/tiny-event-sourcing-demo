package ru.quipy.projections.repository


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.entity.TaskProjection
import java.util.*

@Repository
interface TaskRepository : JpaRepository<TaskProjection?, UUID> {
    fun findByIdAndProjectId(taskId: UUID, projectId: UUID): TaskProjection
    fun findAllByProjectIdAndTaskStatusId(projectId: UUID, statusId: UUID): List<TaskProjection>?
}
