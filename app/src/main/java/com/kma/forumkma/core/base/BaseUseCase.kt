package com.kma.forumkma.core.base

/**
 * Base UseCase với input và output
 * @param Params: Input parameters
 * @param Result: Output result
 */
abstract class BaseUseCase<in Params, out Result> {
    
    /**
     * Execute the use case
     */
    abstract suspend operator fun invoke(params: Params): Result
}

/**
 * UseCase không cần parameters
 */
abstract class BaseUseCaseNoParams<out Result> {
    abstract suspend operator fun invoke(): Result
}

/**
 * Sealed class để wrap kết quả từ UseCase/Repository
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    
    val isSuccess: Boolean
        get() = this is Success
    
    val isError: Boolean
        get() = this is Error
    
    val isLoading: Boolean
        get() = this is Loading
}

/**
 * Extension functions cho Resource
 */
fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) {
        action(data)
    }
    return this
}

fun <T> Resource<T>.onError(action: (String) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(message)
    }
    return this
}

fun <T> Resource<T>.onLoading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        action()
    }
    return this
}
