package ru.quipy.projections.repository;

import org.apache.catalina.User
import ru.quipy.projections.entity.UserProjection
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserProjectionRepository : JpaRepository<UserProjection, UUID> {
    fun findUserProjectionByUsername(username: String) : UserProjection?
}