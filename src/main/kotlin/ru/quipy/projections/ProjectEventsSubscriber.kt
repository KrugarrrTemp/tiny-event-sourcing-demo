package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.projections.entity.ProjectProjection
import ru.quipy.projections.entity.TaskEntity
import ru.quipy.projections.entity.TaskStatusEntity
import ru.quipy.projections.repository.ProjectProjectionRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectEventsSubscriber(
        private val projectProjectionRepository: ProjectProjectionRepository
) {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

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
        }
    }

    private fun handleProjectCreated(event: ProjectCreatedEvent) {
        logger.info("Project created: {}", event.projectName)
        val project = ProjectProjection(
                id = event.projectId,
                name = event.projectName,
                description = event.description,
                authorUserName = event.authorUsername
        )

        projectProjectionRepository.save(project)
        logger.info("Project '{}' saved with ID '{}'", project.name, project.id)
    }

    private fun handleTaskCreated(event: TaskCreatedEvent) {
        val project = findProjectOrLogError(event.projectId) ?: return

        val task = TaskEntity(
                id = event.taskId,
                name = event.taskName,
                project = project
        )

        project.tasks.add(task)
        projectProjectionRepository.save(project)
        logger.info("Task '{}' added to project '{}'", task.name, project.name)
    }

    private fun handleTaskStatusCreated(event: TaskStatusCreatedEvent) {
        val project = findProjectOrLogError(event.projectId) ?: return

        val taskStatus = TaskStatusEntity(
                id = event.taskStatusId,
                name = event.taskStatusName,
                colour = event.taskStatusColour,
                project = project
        )

        project.taskStatuses.add(taskStatus)
        projectProjectionRepository.save(project)
        logger.info("Task status '{}' added to project '{}'", taskStatus.name, project.name)
    }

    private fun handleTaskStatusAssigned(event: TaskStatusAssignedToTaskEvent) {
        val project = findProjectOrLogError(event.projectId) ?: return

        val task = project.tasks.find { it.id == event.taskId }
        if (task == null) {
            logger.error("Task with ID '{}' not found in project '{}'", event.taskId, project.name)
            return
        }

        if (project.taskStatuses.none { it.id == event.taskStatusId }) {
            logger.error("Task status with ID '{}' not found in project '{}'", event.taskStatusId, project.name)
            return
        }

        task.taskStatusesAssigned.add(event.taskStatusId)
        projectProjectionRepository.save(project)
        logger.info(
                "Task status '{}' assigned to task '{}' in project '{}'",
                event.taskStatusId, task.id, project.name
        )
    }

    private fun findProjectOrLogError(projectId: UUID): ProjectProjection? {
        return projectProjectionRepository.findById(projectId).orElse(null)?.also {
            logger.info("Found project '{}'", it.name)
        } ?: run {
            logger.error("Project with ID '{}' not found", projectId)
            null
        }
    }
}
