package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.shaded.org.awaitility.Awaitility
import ru.quipy.controller.ProjectController
import ru.quipy.controller.UserController
import ru.quipy.projections.entity.UserProjection
import java.time.Duration
import java.util.UUID

@SpringBootTest
class TaskManagerTests {

    @Autowired
    private lateinit var userController: UserController

    @Autowired
    private lateinit var projectController: ProjectController

    @Test
    fun registerUserAndCompareWithReceivedUser() {
        val createdUser = userController.registerUser(
                "krugarrr",
                "sashulkaterentulka",
                "ppoklassniypredmet)")

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted {
                    val receivedUser = userController.getAccount(createdUser.username)
                    Assertions.assertNotNull(receivedUser)
                    Assertions.assertEquals(createdUser.username, receivedUser?.username)
                    Assertions.assertEquals(createdUser.userId, receivedUser?.userId)
                }
    }

    @Test
    fun createProjectAndCheckForValidEntity() {
        val project = projectController.createProject(
                "TaskManager",
                "krugarrr",
                "sashulkaterentulka",
                "Very cool and modern project made in USA, Omsk state"
        )
        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted {
                    val receivedProject = projectController.getProjectById(project.projectId)
                    Assertions.assertEquals("TaskManager", receivedProject?.name)
                    val author = receivedProject?.author
                    if (author is UserProjection) {
                        Assertions.assertEquals("krugarrr", author.username)
                        Assertions.assertEquals("sashulkaterentulka", author.fullName)
                    } else {
                        Assertions.fail("Author is not of type UserProjection")
                    }
                }
    }


    @Test
    fun checkProjectForAuthorAsFirstParticipant() {
        val project = projectController.createProject(
                "Megamanager",
                "krugarrr",
                "sashulkaterentulka",
                "Not so cool and modern project made in USA, Omsk state")

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted {
                    val receivedProject = projectController.getProjectById(project.projectId)
                    Assertions.assertNotNull(receivedProject)
                    Assertions.assertEquals(1, receivedProject?.participants?.count())
                    Assertions.assertEquals("krugarrr", receivedProject?.participants?.firstOrNull()?.username)
                }
    }

    @Test
    fun addParticipantToProject() {
        val project = projectController.createProject(
                "SuperManager",
                "krugarrr",
                "sashulkaterentulka",
                "Very cool and modern project made in USA, Omsk state")

        val createdUser = userController.registerUser(
                "pypkaed",
                "zarinkamandarinka",
                "ppoklassniypredmet)")


        projectController.addParticipant(
                project.projectId,
                createdUser.username,
                createdUser.fullName
        )

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted {
                    val projectWithUpdatedParticipants = projectController.getProjectById(project.projectId)
                    val newAddedParticipant = projectWithUpdatedParticipants?.participants?.filter { it.username == "pypkaed" }?.first()
                    Assertions.assertEquals("pypkaed", newAddedParticipant?.username)
                    Assertions.assertEquals("zarinkamandarinka", newAddedParticipant?.fullName)
                    Assertions.assertEquals(2, projectWithUpdatedParticipants?.participants?.count())
                }
    }

    @Test
    fun createTaskWithInitialStatusAndCheckForValidEntity() {
        val project = projectController.createProject(
                "GigaManager",
                "krugarrr",
                "sashulkaterentulka",
                "Very cool and modern project made in USA, Omsk state")

        Awaitility.await().atMost(Duration.ofSeconds(5))
        projectController.createTask(project.projectId, "Watch skibidi guide")

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted {
                    val task = projectController.findAllTasksWithConcreteStatus(project.projectId, "Created")?.first()
                    val defaultStatus = projectController.findAllStatuses(project.projectId)?.filter { it.name == "Created" }?.first()
                    Assertions.assertEquals(defaultStatus?.id, task?.taskStatusId)
                    Assertions.assertEquals("Created", defaultStatus?.name)
                    Assertions.assertEquals("Blue", defaultStatus?.colour)
                }

    }

//    @Test
//    fun createTaskAndAddStatus() {
//        val project = projectController.createProject(
//                "SigmaManager",
//                "krugarrr",
//                "sashulkaterentulka",
//                "Very cool and modern project made in USA, Omsk state")
//
//        Awaitility.await().atMost(Duration.ofSeconds(5))
//
//        projectController.createTask(project.projectId, "Бегит")
//        Awaitility.await().atMost(Duration.ofSeconds(5))
//        val task = projectController.findAllTasksWithConcreteStatus(project.projectId, "Created")?.first()
//
//
//        projectController.createTaskStatus(project.projectId, "Done", "Red")
//        Awaitility.await().atMost(Duration.ofSeconds(5))
//
//        val newStatus = projectController.findAllStatuses(project.projectId);
//
//       // projectController.createTaskStatus(project.projectId, newStatus!!.id, task!!.id)
//        Awaitility.await().atMost(Duration.ofSeconds(5))
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(10))
//                .untilAsserted {
//                    val updatedTask = projectController.findAllTasksWithConcreteStatus(project.id, "Done")?.first()
//
//                    val statusFromTask = projectController.findAllStatuses(project.projectId)?.first { it.name == "Done" }
//
//                            //Assertions.assertEquals(newStatus.id, updatedTask!!.taskStatusId)
//                    Assertions.assertEquals("Done", statusFromTask?.name)
//                    Assertions.assertEquals("Red", statusFromTask?.colour)
//                }
//    }
//
//    @Test
//    fun checkForValidEntityOfNewStatus() {
//        val project = projectController.createProject(
//                "TaskManager",
//                "krugarrr",
//                "sashulkaterentulka",
//                "Very cool and modern project made in USA, Omsk state")
//
//        projectController.createTask(project.projectId, "Watch skibidi guide")
//        val receivedProject = projectController.getProject(project.projectId)
//        val task = receivedProject?.tasks?.entries?.first()?.value
//
//        projectController.createTaskStatus(project.projectId, "Done", "Red")
//        val receivedProjectWithNewStatus = projectController.getProject(project.projectId)
//        val newTaskStatus = receivedProjectWithNewStatus?.taskStatuses?.values?.filter { it.name == "Done" }?.first()
//
//        //ну, простите уж)
//        if (newTaskStatus != null) {
//            if (task != null) {
//                projectController.createTaskStatus(project.projectId, newTaskStatus.id, task.id)
//            }
//        }
//
//        val receivedProjectWithNewTaskStatus = projectController.getProject(project.projectId)
//        val updatedTask = receivedProjectWithNewTaskStatus?.tasks?.entries?.first()?.value
//
//        Assertions.assertEquals(newTaskStatus?.id, updatedTask?.taskStatusesAssigned?.last())
//    }
//
//
//    @Test
//    fun createTaskAndAssignParticipant() {
//        val project = projectController.createProject(
//                "TaskManager",
//                "krugarrr",
//                "sashulkaterentulka",
//                "Very cool and modern project made in USA, Omsk state")
//
//
//        projectController.createTask(project.projectId, "Watch skibidi guide")
//        val receivedProject = projectController.getProject(project.projectId)
//        val task = receivedProject?.tasks?.entries?.first()?.value
//        val participant = project.participants.entries.first().value;
//        if (task != null) {
//            projectController.addParticipant(project.projectId, task.id, participant.id)
//        }
//        val receivedProjectWithUpdatedTask = projectController.getProject(project.projectId)
//        val updatedTask = receivedProjectWithUpdatedTask?.tasks?.entries?.first()?.value
//        Assertions.assertEquals(1, updatedTask?.performersAssigned?.count())
//        Assertions.assertNotEquals(task?.performersAssigned?.count(), updatedTask?.performersAssigned?.count())
//        Assertions.assertEquals(participant.id, updatedTask?.performersAssigned?.first())
//    }
//
//    @Test
//    fun createExistingStatus() {
//        val project = projectController.createProject(
//                "TaskManager",
//                "krugarrr",
//                "sashulkaterentulka",
//                "Very cool and modern project made in USA, Omsk state")
//
//
//        Assertions.assertThrows(
//                IllegalArgumentException::class.java) {
//            projectController.createTaskStatus(project.projectId, "Created", "Blue")
//        }
//    }
//
//    @Test
//    fun addExistingParticipant() {
//        val project = projectController.createProject(
//                "TaskManager",
//                "krugarrr",
//                "sashulkaterentulka",
//                "Very cool and modern project made in USA, Omsk state")
//
//
//        Assertions.assertThrows(
//                IllegalArgumentException::class.java) {
//            projectController.addParticipant(
//                    project.projectId,
//                    "krugarrr",
//                    "sashulkaterentulka"
//            )
//        }
//    }
//
//
//    @Test
//    fun subscribeNonExistingPaticipantToTask() {
//        val project = projectController.createProject(
//                "TaskManager",
//                "krugarrr",
//                "sashulkaterentulka",
//                "Very cool and modern project made in USA, Omsk state")
//
//
//        projectController.createTask(project.projectId, "Watch skibidi guide")
//        val createdUser = userController.registerUser(
//                "mistertvister",
//                "bivshiyministr",
//                "ppoklassniypredmet)")
//        val receivedProject = projectController.getProject(project.projectId)
//        val task = receivedProject?.tasks?.entries?.first()?.value
//        if (task != null) {
//            Assertions.assertThrows(
//                    IllegalArgumentException::class.java) {
//                projectController.addParticipant(project.projectId, task.id, createdUser.userId)
//            }
//
//        }
//    }
//
//    @Test
//    fun assignNonExistingStatus() {
//        val project = projectController.createProject(
//                "TaskManager",
//                "krugarrr",
//                "sashulkaterentulka",
//                "Very cool and modern project made in USA, Omsk state")
//
//
//        projectController.createTask(project.projectId, "Watch skibidi guide")
//        val receivedProject = projectController.getProject(project.projectId)
//        val task = receivedProject?.tasks?.entries?.first()?.value
//
//        if (task != null) {
//            Assertions.assertThrows(
//                    IllegalArgumentException::class.java) {
//                projectController.createTaskStatus(project.projectId, UUID.randomUUID(), task.id)
//            }
//
//        }
//    }
//
//    @Test
//    fun assignStatusToNonExistingTask() {
//        val project = projectController.createProject(
//                "TaskManager",
//                "krugarrr",
//                "sashulkaterentulka",
//                "Very cool and modern project made in USA, Omsk state")
//
//
//        projectController.createTask(project.projectId, "Watch skibidi guide")
//        projectController.createTaskStatus(project.projectId, "Done", "Red")
//        val receivedProjectWithNewStatus = projectController.getProject(project.projectId)
//        val newTaskStatus = receivedProjectWithNewStatus?.taskStatuses?.values?.filter { it.name == "Done" }?.first()
//          if (newTaskStatus != null) {
//            Assertions.assertThrows(
//                    IllegalArgumentException::class.java) {
//                projectController.createTaskStatus(project.projectId, newTaskStatus.id, UUID.randomUUID())
//            }
//
//        }
//    }

}
