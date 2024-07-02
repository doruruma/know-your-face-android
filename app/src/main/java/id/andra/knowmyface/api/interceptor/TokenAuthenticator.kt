package id.andra.knowmyface.api.interceptor

import android.content.Context
import id.andra.knowmyface.api.repository.UserRepository
import id.andra.knowmyface.helper.SharedPreferenceHelper
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val context: Context
) : Authenticator {

    private val userRepository: UserRepository = UserRepository()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401)
            return null
        val refreshToken = SharedPreferenceHelper.getRefreshToken(context)
        val refreshTokenResponse = runBlocking {
            userRepository.refreshToken(refreshToken.orEmpty())
        }
        if (refreshTokenResponse.data != null) {
            SharedPreferenceHelper.saveToken(
                context = context,
                value = refreshTokenResponse.data.token
            )
            SharedPreferenceHelper.saveRefreshToken(
                context = context,
                value = refreshTokenResponse.data.refreshToken
            )
            return response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshTokenResponse.data.token}")
                .build()
        }
        return null
    }

}