package com.kma.forumkma.domain.usecase.auth

import com.kma.forumkma.core.base.BaseUseCase
import com.kma.forumkma.core.base.Resource
import com.kma.forumkma.domain.model.User
import com.kma.forumkma.domain.repository.AuthRepository

/**
 * UseCase cho Login
 * Single Responsibility: Chỉ xử lý logic login
 * 
 * Note: Add @Inject annotation when Hilt is configured
 */
class LoginUseCase(
    private val authRepository: AuthRepository
) : BaseUseCase<LoginUseCase.Params, Resource<User>>() {
    
    data class Params(
        val username: String,
        val password: String
    )
    
    override suspend fun invoke(params: Params): Resource<User> {
        // Validation
        if (params.username.isBlank()) {
            return Resource.Error("Username không được để trống")
        }
        
        if (params.password.isBlank()) {
            return Resource.Error("Password không được để trống")
        }
        
        if (params.password.length < 6) {
            return Resource.Error("Password phải có ít nhất 6 ký tự")
        }
        
        // Call repository
        return authRepository.login(
            username = params.username.trim(),
            password = params.password
        )
    }
}
