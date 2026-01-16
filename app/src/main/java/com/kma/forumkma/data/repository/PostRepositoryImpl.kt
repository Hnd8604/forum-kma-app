package com.kma.forumkma.data.repository

import com.kma.forumkma.core.base.Resource
import com.kma.forumkma.core.network.RetrofitClient
import com.kma.forumkma.data.mapper.GroupMapper
import com.kma.forumkma.data.mapper.PostMapper
import com.kma.forumkma.data.remote.api.PostApiService
import com.kma.forumkma.data.remote.dto.post.CreatePostRequestDto
import com.kma.forumkma.data.remote.dto.post.InteractionRequestDto
import com.kma.forumkma.data.remote.dto.post.UpdatePostRequestDto
import com.kma.forumkma.domain.model.Group
import com.kma.forumkma.domain.model.Post
import com.kma.forumkma.domain.repository.PostRepository

/**
 * Implementation của PostRepository
 */
class PostRepositoryImpl : PostRepository {
    
    private val postApiService: PostApiService by lazy {
        RetrofitClient.createService<PostApiService>()
    }
    
    override suspend fun getAllPosts(page: Int, limit: Int, search: String): Resource<List<Post>> {
        return try {
            val response = postApiService.getAllPosts(page, limit, search)
            if (response.isSuccess && response.data != null) {
                val posts = PostMapper.toDomainList(response.data.items)
                Resource.Success(posts)
            } else {
                Resource.Error(response.message.ifBlank { "Không thể tải bài viết" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun getPostById(id: String): Resource<Post> {
        return try {
            val response = postApiService.getPostById(id)
            if (response.isSuccess && response.data != null) {
                val post = PostMapper.toDomain(response.data)
                Resource.Success(post)
            } else {
                Resource.Error(response.message.ifBlank { "Không tìm thấy bài viết" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun createPost(
        title: String,
        content: String,
        groupId: String,
        type: String,
        resourceUrls: List<String>
    ): Resource<Post> {
        return try {
            val request = CreatePostRequestDto(
                title = title,
                content = content,
                groupId = groupId,
                type = type,
                resourceUrls = resourceUrls
            )
            val response = postApiService.createPost(request)
            if (response.isSuccess && response.data != null) {
                val post = PostMapper.toDomain(response.data)
                Resource.Success(post)
            } else {
                Resource.Error(response.message.ifBlank { "Không thể tạo bài viết" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun updatePost(
        id: String,
        title: String?,
        content: String?,
        type: String?,
        resourceUrls: List<String>?
    ): Resource<Post> {
        return try {
            val request = UpdatePostRequestDto(
                title = title,
                content = content,
                type = type,
                resourceUrls = resourceUrls
            )
            val response = postApiService.updatePost(id, request)
            if (response.isSuccess && response.data != null) {
                val post = PostMapper.toDomain(response.data)
                Resource.Success(post)
            } else {
                Resource.Error(response.message.ifBlank { "Không thể cập nhật bài viết" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun deletePost(id: String): Resource<Unit> {
        return try {
            val response = postApiService.deletePost(id)
            if (response.isSuccess) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message.ifBlank { "Không thể xóa bài viết" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun getPostsByAuthor(authorId: String, page: Int, limit: Int): Resource<List<Post>> {
        return try {
            val response = postApiService.getPostsByAuthor(authorId, page, limit)
            if (response.isSuccess && response.data != null) {
                val posts = PostMapper.toDomainList(response.data.items)
                Resource.Success(posts)
            } else {
                Resource.Error(response.message.ifBlank { "Không thể tải bài viết" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun getPostsByGroup(groupId: String, page: Int, limit: Int): Resource<List<Post>> {
        return try {
            val response = postApiService.getPostsByGroup(groupId, page, limit)
            if (response.isSuccess && response.data != null) {
                val posts = PostMapper.toDomainList(response.data.items)
                Resource.Success(posts)
            } else {
                Resource.Error(response.message.ifBlank { "Không thể tải bài viết" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun likePost(postId: String): Resource<Unit> {
        return try {
            val request = InteractionRequestDto(
                targetId = postId,
                targetType = "POST",
                type = "LIKE"
            )
            val response = postApiService.addInteraction(request)
            if (response.isSuccess) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message.ifBlank { "Không thể thích bài viết" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun unlikePost(postId: String): Resource<Unit> {
        return try {
            val response = postApiService.removeInteraction(postId, "POST")
            if (response.isSuccess) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message.ifBlank { "Không thể bỏ thích bài viết" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun getGroups(page: Int, limit: Int): Resource<List<Group>> {
        return try {
            val response = postApiService.getAllGroups(page, limit)
            if (response.isSuccess && response.data != null) {
                val groups = GroupMapper.toDomainList(response.data.items)
                Resource.Success(groups)
            } else {
                Resource.Error(response.message.ifBlank { "Không thể tải danh sách nhóm" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
}
