package ru.quipy.projections

import ru.quipy.projections.entity.UserProjection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserRegisteredEvent
import ru.quipy.projections.exceptions.EntityNotFoundException
import ru.quipy.projections.repository.UserProjectionRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Component
@AggregateSubscriber(
        aggregateClass = UserAggregate::class, subscriberName = "user-subs-stream"
)
class UserProjectionsService (private val userProjectionRepository: UserProjectionRepository) {


    val logger: Logger = LoggerFactory.getLogger(UserProjectionsService::class.java)

    @SubscribeEvent
    fun userRegisteredSubscriber(event: UserRegisteredEvent) {
        logger.info("User registered or updated: {}", event.username)

        val existingUser = userProjectionRepository.findUserProjectionByUsername(event.username)

        val userProjection = if (existingUser != null) {
            logger.info("User '{}' exists, updating their data.", event.username)
            existingUser.apply {
                fullName = event.fullName
            }
        } else {
            logger.info("User '{}' does not exist, creating a new record.", event.username)
            UserProjection(
                    userId = event.userId,
                    username = event.username,
                    fullName = event.fullName
            )
        }

        userProjectionRepository.save(userProjection)
        logger.info("User '{}' saved or updated successfully.", userProjection.username)
    }

    fun getUserByUserName(userName: String) : UserProjection? {
        val user = findUserOrThrow(userName)
        return user
    }

    private fun findUserOrThrow(userName: String): UserProjection {
        return userProjectionRepository.findUserProjectionByUsername(userName) ?: throw EntityNotFoundException(
                "User with username '$userName' not found"
        )
    }
}