package id.andra.knowmyface.api

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null,
    val code: Int? = null
) {
    class Success<T>(
        data: T?
    ) : Resource<T>(
        data = data
    )

    class Error<T>(
        data: T? = null,
        error: Throwable?,
        code: Int? = null
    ) : Resource<T>(
        data = data,
        error = error,
        code = code
    )
}