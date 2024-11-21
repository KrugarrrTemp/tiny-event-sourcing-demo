package ru.quipy.projections.repository;

import UserProjection
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserProjectionRepository : JpaRepository<UserProjection, UUID>