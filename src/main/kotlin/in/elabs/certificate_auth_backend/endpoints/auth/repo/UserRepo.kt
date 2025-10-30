package `in`.elabs.certificate_auth_backend.endpoints.auth.repo

import `in`.elabs.certificate_auth_backend.endpoints.auth.model.UserModel
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepo: JpaRepository<UserModel, Long>