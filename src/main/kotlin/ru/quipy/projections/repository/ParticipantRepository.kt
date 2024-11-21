package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.entity.ParticipantEntity
import java.util.*


@Repository
interface ParticipantRepository : JpaRepository<ParticipantEntity?, UUID>
