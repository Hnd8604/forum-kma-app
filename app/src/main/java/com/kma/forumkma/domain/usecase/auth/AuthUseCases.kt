package com.kma.forumkma.domain.usecase.auth

import com.kma.forumkma.core.base.BaseUseCase
import com.kma.forumkma.core.base.Resource
import com.kma.forumkma.domain.model.User
import com.kma.forumkma.domain.repository.AuthRepository

/**
 * UseCase: Đăng ký tài khoản mới
 */
class RegisterUseCase(
    private val authRepository: AuthRepository
) : BaseUseCase<RegisterUseCase.Params, Resource<User>>() {
    
    data class Params(
        val username: String,
        val email: String,
        val password: String,
        val confirmPassword: String,
        val displayName: String
    )
    
    override suspend fun invoke(params: Params): Resource<User> {
        // Validation
        if (params.username.isBlank()) {
            return Resource.Error("Username không được để trống")
        }
        
        if (params.username.length < 3) {
            return Resource.Error("Username phải có ít nhất 3 ký tự")
        }
        
        if (params.email.isBlank()) {
            return Resource.Error("Email không được để trống")
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(params.email).matches()) {
            return Resource.Error("Email không hợp lệ")
        }
        
        if (params.password.isBlank()) {
            return Resource.Error("Mật khẩu không được để trống")
        }
        
        if (params.password.length < 6) {
            return Resource.Error("Mật khẩu phải có ít nhất 6 ký tự")
        }
        
        if (params.password != params.confirmPassword) {
            return Resource.Error("Mật khẩu xác nhận không khớp")
        }
        
        return authRepository.register(
            username = params.username.trim(),
            email = params.email.trim(),
            password = params.password,
            displayName = params.displayName.trim().ifBlank { params.username }
        )
    }
}

/**
 * UseCase: Đăng xuất
 */
class LogoutUseCase(
    private val authRepository: AuthRepository
) : BaseUseCase<Unit, Resource<Unit>>() {
    
    override suspend fun invoke(params: Unit): Resource<Unit> {
        return authRepository.logout()
    }
}

/**
 * UseCase: Lấy thông tin user hiện tại
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) : BaseUseCase<Unit, Resource<User?>>() {
    
    override suspend fun invoke(params: Unit): Resource<User?> {
        return authRepository.getCurrentUser()
    }
}
