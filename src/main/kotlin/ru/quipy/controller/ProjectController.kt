package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskStatusAssignedToTaskEvent
import ru.quipy.api.ParticipantAddedEvent
import ru.quipy.api.PerformerAddedToTaskEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.projections.ProjectProjectionsService
import ru.quipy.projections.entity.ParticipantProjection
import ru.quipy.projections.entity.ProjectProjection
import ru.quipy.projections.entity.TaskProjection
import ru.quipy.projections.entity.TaskStatusProjection
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val gatewayService: GatewayService,

) {
    @PostMapping("create")
    fun createProject(
        @RequestParam(required = true, value = "projectName") projectName: String,
        @RequestParam(required = true, value = "authorUsername") authorUsername: String,
        @RequestParam(required = true, value = "authorFullName") authorFullName: String,
        @RequestParam(required = false, defaultValue = "", value = "description") description: String) : ProjectCreatedEvent {
            return projectEsService.create {
                it.create(UUID.randomUUID(), projectName, authorUsername, authorFullName, description)
            }

    }

    @GetMapping("/{projectId}")
    fun getProjectById(@PathVariable projectId: UUID) : ProjectWithParticipants? {
        return gatewayService.getProjectWithParticipants(projectId)
    }

    @GetMapping("/author/{userName}")
    fun findProjectsByAuthor(@PathVariable userName: String) : List<ProjectProjection>? {
        return gatewayService.getProjectsByAuthor(userName)
    }

    @GetMapping("/participant/{projectId}/{userName}")
    fun findParticipant(@PathVariable projectId: UUID, @PathVariable userName: String) : ParticipantProjection? {
        return gatewayService.findParticipant(projectId, userName)
    }

    @GetMapping("/status/{projectId}")
    fun findAllStatuses(@PathVariable projectId: UUID) : List<TaskStatusProjection>? {
        return gatewayService.findAllStatuses(projectId)
    }

    @GetMapping("/task/{projectId}/{statusName}")
    fun findAllTasksWithConcreteStatus(@PathVariable projectId: UUID, @PathVariable statusName: String) : List<TaskProjection>? {
        return gatewayService.findAllTasksWithConcreteStatus(projectId, statusName)
    }



    @PostMapping("/{projectId}/tasks/create")
    fun createTask(@PathVariable projectId: UUID, @RequestParam(required = true, value = "taskName") taskName: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName)
        }
    }

    @PostMapping("/{projectId}/taskStatuses/create")
    fun createTaskStatus(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "taskStatusName") taskStatusName: String,
        @RequestParam(required = true, value = "taskStatusColour") taskStatusColour: String) : TaskStatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTaskStatus(name = taskStatusName, colour = taskStatusColour)
        }
    }

    @PostMapping("/{projectId}/taskStatuses/assign")
    fun createTaskStatus(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "taskStatusId") taskStatusId: UUID,
        @RequestParam(required = true, value = "taskId") taskId: UUID) : TaskStatusAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignTaskStatusToTask(taskStatusId = taskStatusId, taskId = taskId)
        }
    }


    @PostMapping("/{projectId}/participants/add")
    fun addParticipant(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "participantUsername") participantUsername: String,
        @RequestParam(required = true, value = "participantFullName") participantFullName: String) : ParticipantAddedEvent {
        return projectEsService.update(projectId) {
            it.addParticipant(participantUsername = participantUsername, participantFullName = participantFullName)
        }
    }

    @PostMapping("/{projectId}/task/performers/add")
    fun addParticipant(
        @PathVariable projectId: UUID,
        @RequestParam(required = true, value = "taskId") taskId: UUID,
        @RequestParam(required = true, value = "participantId") participantId: UUID) : PerformerAddedToTaskEvent {
        return projectEsService.update(projectId) {
            it.addPerformerToTask(taskId = taskId, participantId = participantId)
        }
    }
}