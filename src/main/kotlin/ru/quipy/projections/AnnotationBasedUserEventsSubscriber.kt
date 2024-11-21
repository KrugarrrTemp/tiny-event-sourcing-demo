package ru.quipy.projections

import UserProjection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserRegisteredEvent
import ru.quipy.projections.repository.UserProjectionRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "user-subs-stream"
)
class AnnotationBasedUserEventsSubscriber (private val userProjectionRepository: UserProjectionRepository) {


    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedUserEventsSubscriber::class.java)

    @SubscribeEvent
    fun userRegisteredSubscriber(event: UserRegisteredEvent) {
        logger.info("User registered: {}", event.username)

        val userProjection = UserProjection(
                userId =  event.userId,
                username =  event.username,
                fullName =  event.fullName,
                createdAt = event.createdAt
        )
        userProjectionRepository.save(userProjection)
    }
}