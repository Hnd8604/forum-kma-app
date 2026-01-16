package com.kma.forumkma.domain.repository

import com.kma.forumkma.core.base.Resource
import com.kma.forumkma.domain.model.Group
import com.kma.forumkma.domain.model.Post

/**
 * Repository interface cho Posts
 */
interface PostRepository {
    
    /**
     * Lấy danh sách tất cả posts
     */
    suspend fun getAllPosts(page: Int = 0, limit: Int = 10, search: String = ""): Resource<List<Post>>
    
    /**
     * Lấy chi tiết post theo ID
     */
    suspend fun getPostById(id: String): Resource<Post>
    
    /**
     * Tạo post mới
     */
    suspend fun createPost(
        title: String,
        content: String,
        groupId: String,
        type: String = "TEXT",
        resourceUrls: List<String> = emptyList()
    ): Resource<Post>
    
    /**
     * Cập nhật post
     */
    suspend fun updatePost(
        id: String,
        title: String?,
        content: String?,
        type: String? = null,
        resourceUrls: List<String>? = null
    ): Resource<Post>
    
    /**
     * Xóa post
     */
    suspend fun deletePost(id: String): Resource<Unit>
    
    /**
     * Lấy posts của user
     */
    suspend fun getPostsByAuthor(authorId: String, page: Int = 0, limit: Int = 10): Resource<List<Post>>
    
    /**
     * Lấy posts trong group
     */
    suspend fun getPostsByGroup(groupId: String, page: Int = 0, limit: Int = 10): Resource<List<Post>>
    
    /**
     * Like post
     */
    suspend fun likePost(postId: String): Resource<Unit>
    
    /**
     * Unlike post
     */
    suspend fun unlikePost(postId: String): Resource<Unit>
    
    /**
     * Lấy danh sách groups
     */
    suspend fun getGroups(page: Int = 0, limit: Int = 20): Resource<List<Group>>
}
