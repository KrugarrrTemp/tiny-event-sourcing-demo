package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.quipy.api.*
import ru.quipy.projections.entity.*
import ru.quipy.projections.exceptions.EntityNotFoundException
import ru.quipy.projections.repository.*
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectProjectionsService(
        private val projectProjectionRepository: ProjectProjectionRepository,
        private val taskStatusRepository: TaskStatusRepository,
        private val taskRepository: TaskRepository,
        private val participantRepository: ParticipantRepository,
        private val userProjectionRepository: UserProjectionRepository
) {

    val logger: Logger = LoggerFactory.getLogger(ProjectProjectionsService::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-aggregate") {

            `when`(ProjectCreatedEvent::class) { event ->
                handleProjectCreated(event)
            }

            `when`(TaskCreatedEvent::class) { event ->
                handleTaskCreated(event)
            }

            `when`(TaskStatusCreatedEvent::class) { event ->
                handleTaskStatusCreated(event)
            }

            `when`(TaskStatusAssignedToTaskEvent::class) { event ->
                handleTaskStatusAssigned(event)
            }
            `when`(ParticipantAddedEvent::class) { event ->
                handleParticipantAdded(event)
            }
        }
    }

    private fun handleProjectCreated(event: ProjectCreatedEvent) {
        val user = findUserOrThrow(event.authorUsername)
        logger.info("Project created: {}", event.projectName)
        val project = ProjectProjection(
                id = event.projectId,
                name = event.projectName,
                description = event.description,
                authorUserName = event.authorUsername,
                authorName = event.name
        )

        val authorAsParticipant = ParticipantProjection(
                id = user.userId,
                projectId = project.id,
                username = user.username,
                fullName = user.fullName
        )

        val statusFromEntity = event.taskStatuses.entries.first().value
        val defaultStatus = TaskStatusProjection(
                id = statusFromEntity.id,
                name = statusFromEntity.name,
                colour = statusFromEntity.colour,
                projectId = project.id
        )
        taskStatusRepository.save(defaultStatus)
        projectProjectionRepository.save(project)
        participantRepository.save(authorAsParticipant)
        logger.info("Project '{}' saved with ID '{}'", project.name, project.id)
    }

    private fun handleTaskCreated(event: TaskCreatedEvent) {
        val project = findProjectOrThrow(event.projectId)
        val defaultStatus = findDefaultStatusOrThrow(event.projectId)
        val task = TaskProjection(
                id = event.taskId,
                name = event.taskName,
                projectId = project.id,
                taskStatusId = defaultStatus.id
        )
        taskRepository.save(task)
        logger.info("Task '{}' added to project '{}'", task.name, project.name)
    }

    private fun handleTaskStatusCreated(event: TaskStatusCreatedEvent) {
        val project = findProjectOrThrow(event.projectId)
        val expectingStatus = taskStatusRepository.findByNameAndProjectId(event.taskStatusName, project.id)
        val taskStatus = if (expectingStatus != null) {
            logger.info("Status '{}' exists, updating their data.", event.taskStatusName)
            expectingStatus.apply {
                name = event.taskStatusName
                colour = event.taskStatusColour
            }
        } else {
            TaskStatusProjection(
                    id = event.taskStatusId,
                    name = event.taskStatusName,
                    colour = event.taskStatusColour,
                    projectId = project.id
            )
        }
        taskStatusRepository.save(taskStatus)
        logger.info("Task status '{}' added to project '{}'", taskStatus.name, project.name)
    }

    private fun handleTaskStatusAssigned(event: TaskStatusAssignedToTaskEvent) {
        val project = findProjectOrThrow(event.projectId)
        val task = findTaskOrThrow(event.taskId, event.projectId)
        val status = findStatusOrThrow(event.taskStatusId, project.id)
        val newTask = TaskProjection(
                id = task.id,
                name = task.name,
                projectId = task.projectId,
                taskStatusId = status.id
        )

        taskRepository.save(newTask)
        logger.info(
                "Task status '{}' assigned to task '{}' in project '{}'",
                event.taskStatusId, task.id, project.name
        )
    }

    private fun handleParticipantAdded(event: ParticipantAddedEvent) {
        val project = findProjectOrThrow(event.projectId)
        findUserOrThrow(event.participantUsername)
        val existingParticipant = participantRepository.findByProjectIdAndUsername(project.id, event.participantUsername)

        val participant = if (existingParticipant != null) {
            logger.info("Participant '{}' exists, updating their data.", event.participantUsername)
            existingParticipant.apply {
                fullName = event.participantFullName
                projectId = event.projectId
            }
        } else {
            ParticipantProjection(
                    id = event.participantId,
                    projectId = event.projectId,
                    username = event.participantUsername,
                    fullName = event.participantFullName
            )
        }

        participantRepository.save(participant)
        logger.info("Participant '{}' added to project '{}'", participant.username, project.name)
    }

    private fun findProjectOrThrow(projectId: UUID): ProjectProjection {
        return projectProjectionRepository.findById(projectId).orElseThrow {
            EntityNotFoundException("Project with ID '$projectId' not found")
        }.also {
            logger.info("Found project '{}'", it.name)
        }
    }

    private fun findDefaultStatusOrThrow(projectId: UUID): TaskStatusProjection {
        return taskStatusRepository.findByNameAndProjectId("Created", projectId) ?: throw EntityNotFoundException(
                "Default status 'Created' not found for project ID '$projectId'"
        ).also {
            logger.info("Found default status Created")
        }
    }

    private fun findStatusOrThrow(id: UUID, projectId: UUID): TaskStatusProjection {
        return taskStatusRepository.findByIdAndProjectId(id, projectId) ?: throw EntityNotFoundException(
                "Status with ID '$id' not found for project ID '$projectId'"
        ).also {
            logger.info("Found status '$id'")
        }
    }

    private fun findTaskOrThrow(taskId: UUID, projectId: UUID): TaskProjection {
        return taskRepository.findByIdAndProjectId(taskId, projectId) ?: throw EntityNotFoundException(
                "Task with ID '$taskId' not found in project ID '$projectId'"
        ).also {
            logger.info("Found task '$taskId'")
        }
    }

    private fun findUserOrThrow(userName: String): UserProjection {
        return userProjectionRepository.findUserProjectionByUsername(userName) ?: throw EntityNotFoundException(
                "User with username '$userName' not found"
        )
    }
}

