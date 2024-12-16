package ru.quipy.logic

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.projections.entity.*
import ru.quipy.projections.repository.*
import java.util.*

@Service
class GatewayService(
        private val userProjectionRepository: UserProjectionRepository,
        private val projectProjectionRepository: ProjectProjectionRepository,
        private val participantRepository: ParticipantRepository,
        private val taskStatusRepository: TaskStatusRepository,
        private val taskRepository: TaskRepository
) {
    val logger: Logger = LoggerFactory.getLogger(GatewayService::class.java)

    fun getProjectWithParticipants(projectId: UUID): ProjectWithParticipants? {
        val project = projectProjectionRepository.findById(projectId).orElse(null)
        if (project == null) {
            logger.warn("Project not found for ID: {}", projectId)
            return null
        }

        val participants = participantRepository.findAllByProjectId(projectId)

        val author = userProjectionRepository.findUserProjectionByUsername(project.authorUserName)

        if (author == null) {
            logger.warn("Author not found for project ID: {}", projectId)
        }

        return ProjectWithParticipants(
                id = project.id,
                name = project.name,
                description = project.description,
                participants = participants,
                author = author
        )
    }

    fun getProjectsByAuthor(userName: String) : List<ProjectProjection>? {
        val user = findUserOrLogError(userName) ?: return null

        return projectProjectionRepository.findAllByAuthorUserName(userName)
    }

    fun findAllStatuses(projectId: UUID) : List<TaskStatusProjection>? {
        val project = findProjectOrLogError(projectId) ?: return null
        return taskStatusRepository.findAllByProjectId(project.id)
    }

    fun findAllTasksWithConcreteStatus(projectId: UUID, statusName: String): List<TaskProjection>?  {
        val project = findProjectOrLogError(projectId) ?: return null
        val status = taskStatusRepository.findByNameAndProjectId(statusName, projectId) ?: return null
        return taskRepository.findAllByProjectIdAndTaskStatusId(project.id, status.id);
    }
    fun findParticipant(uuid: UUID, userName: String) : ParticipantProjection? {
        val user = findUserOrLogError(userName) ?: return null
        val project = findProjectOrLogError(uuid) ?: return null
        return participantRepository.findByProjectIdAndUsername(project.id, user.username)
    }

    private fun findProjectOrLogError(projectId: UUID): ProjectProjection? {
        return projectProjectionRepository.findById(projectId).orElse(null)?.also {
            logger.info("Found project '{}'", it.name)
        } ?: run {
            logger.error("Project with ID '{}' not found", projectId)
            null
        }
    }

    private fun findUserOrLogError(userName: String): UserProjection? {
        return userProjectionRepository.findUserProjectionByUsername(userName).also {
            logger.info("Found user '{}'", userName)
        } ?: run {
            logger.error("User '{}' not found", userName)
            null
        } ?: return null
    }

}

// DTO чтоб удобно было представлять проект и его участников/автора
data class ProjectWithParticipants(
        val id: UUID,
        val name: String,
        val description: String,
        val participants: List<ParticipantProjection>?,
        val author: UserProjection?
)

