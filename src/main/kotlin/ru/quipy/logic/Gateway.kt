package ru.quipy.logic

import ru.quipy.projections.entity.UserProjection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.projections.repository.ProjectProjectionRepository
import ru.quipy.projections.repository.UserProjectionRepository
import java.util.*

@Service
class GatewayService(
        private val userProjectionRepository: UserProjectionRepository,
        private val projectProjectionRepository: ProjectProjectionRepository
) {
    val logger: Logger = LoggerFactory.getLogger(GatewayService::class.java)

    fun getProjectWithParticipants(projectId: UUID): ProjectWithParticipants? {
        val project = projectProjectionRepository.findById(projectId).orElse(null)
        if (project == null) {
            logger.warn("Project not found for ID: {}", projectId)
            return null
        }

        val participants = project.participants.mapNotNull { participantEntity ->
            userProjectionRepository.findById(participantEntity.id).orElse(null)
        }

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
}

// DTO чтоб удобно было представлять проект и его участников/автора
data class ProjectWithParticipants(
        val id: UUID,
        val name: String,
        val description: String,
        val participants: List<UserProjection>,
        val author: UserProjection?
)

