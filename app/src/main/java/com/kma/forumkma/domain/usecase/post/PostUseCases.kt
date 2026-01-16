package com.kma.forumkma.domain.usecase.post

import com.kma.forumkma.core.base.BaseUseCase
import com.kma.forumkma.core.base.BaseUseCaseNoParams
import com.kma.forumkma.core.base.Resource
import com.kma.forumkma.domain.model.Post
import com.kma.forumkma.domain.repository.PostRepository

/**
 * UseCase: Lấy danh sách posts
 */
class GetPostsUseCase(
    private val postRepository: PostRepository
) : BaseUseCase<GetPostsUseCase.Params, Resource<List<Post>>>() {
    
    data class Params(
        val page: Int = 0,
        val limit: Int = 10,
        val search: String = ""
    )
    
    override suspend fun invoke(params: Params): Resource<List<Post>> {
        return postRepository.getAllPosts(params.page, params.limit, params.search)
    }
}

/**
 * UseCase: Lấy chi tiết post
 */
class GetPostDetailUseCase(
    private val postRepository: PostRepository
) : BaseUseCase<String, Resource<Post>>() {
    
    override suspend fun invoke(params: String): Resource<Post> {
        if (params.isBlank()) {
            return Resource.Error("Post ID không hợp lệ")
        }
        return postRepository.getPostById(params)
    }
}

/**
 * UseCase: Tạo post mới
 */
class CreatePostUseCase(
    private val postRepository: PostRepository
) : BaseUseCase<CreatePostUseCase.Params, Resource<Post>>() {
    
    data class Params(
        val title: String,
        val content: String,
        val groupId: String,
        val type: String = "TEXT",
        val resourceUrls: List<String> = emptyList()
    )
    
    override suspend fun invoke(params: Params): Resource<Post> {
        // Validation
        if (params.content.isBlank()) {
            return Resource.Error("Nội dung không được để trống")
        }
        
        if (params.groupId.isBlank()) {
            return Resource.Error("Vui lòng chọn nhóm")
        }
        
        return postRepository.createPost(
            title = params.title,
            content = params.content,
            groupId = params.groupId,
            type = params.type,
            resourceUrls = params.resourceUrls
        )
    }
}

/**
 * UseCase: Like/Unlike post
 */
class ToggleLikePostUseCase(
    private val postRepository: PostRepository
) : BaseUseCase<ToggleLikePostUseCase.Params, Resource<Unit>>() {
    
    data class Params(
        val postId: String,
        val isCurrentlyLiked: Boolean
    )
    
    override suspend fun invoke(params: Params): Resource<Unit> {
        return if (params.isCurrentlyLiked) {
            postRepository.unlikePost(params.postId)
        } else {
            postRepository.likePost(params.postId)
        }
    }
}
