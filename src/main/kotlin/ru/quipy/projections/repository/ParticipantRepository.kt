package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.entity.ParticipantProjection
import java.util.*


@Repository
interface ParticipantRepository : JpaRepository<ParticipantProjection?, UUID> {
    fun findByProjectIdAndUsername(id: UUID, userName: String) : ParticipantProjection?
    fun findAllByProjectId(id: UUID) : List<ParticipantProjection>?
}
