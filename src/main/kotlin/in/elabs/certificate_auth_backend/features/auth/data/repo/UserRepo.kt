package `in`.elabs.certificate_auth_backend.features.auth.data.repo

import `in`.elabs.certificate_auth_backend.features.auth.data.model.UserModel
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepo: JpaRepository<UserModel, Long>{
    fun findByEmail(email: String): MutableList<UserModel>
    fun findByUserName(userName: String): MutableList<UserModel>
}