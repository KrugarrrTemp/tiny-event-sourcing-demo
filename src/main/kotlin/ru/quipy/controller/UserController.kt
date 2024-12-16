package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.register
import ru.quipy.api.UserRegisteredEvent
import ru.quipy.projections.UserProjectionsService
import ru.quipy.projections.entity.UserProjection
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val userProjectionService: UserProjectionsService
) {

    @PostMapping("/register")
    fun registerUser(
        @RequestParam(required = true, value = "username") username: String,
        @RequestParam(required = true, value = "fullName") fullName: String,
        @RequestParam(required = true, value = "password") password: String) : UserRegisteredEvent {
            return userEsService.create() { 
                it.register(UUID.randomUUID(), username, fullName, password)
             }
    }


    @GetMapping("/{userName}")
    fun getAccount(@PathVariable("userName") username: String): UserProjection? {
        return userProjectionService.getUserByUserName(username)
    }
}