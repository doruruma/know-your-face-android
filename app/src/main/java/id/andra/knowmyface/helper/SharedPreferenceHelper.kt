package id.andra.knowmyface.helper

import android.content.Context
import android.content.SharedPreferences
import id.andra.knowmyface.model.User

object SharedPreferenceHelper {

    private const val PREF_NAME = "KNOW_MY_FACE"
    private const val TOKEN = "token"
    private const val REFRESH_TOKEN = "refresh_token"
    private const val USER = "user"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(context: Context, user: User) {
        val moshi = MoshiHelper.createMoshi()
        val userAdapter = moshi.adapter(User::class.java)
        val userJson = userAdapter.toJson(user)
        getSharedPreferences(context).edit().putString(USER, userJson).apply()
    }

    fun getUser(context: Context): User? {
        val moshi = MoshiHelper.createMoshi()
        val userAdapter = moshi.adapter(User::class.java)
        val userJson = getSharedPreferences(context).getString(USER, null) ?: return null
        return userAdapter.fromJson(userJson)
    }

    fun saveToken(context: Context, value: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(TOKEN, value).apply()
    }

    fun getToken(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(TOKEN, null)
    }

    fun saveRefreshToken(context: Context, value: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(REFRESH_TOKEN, value).apply()
    }

    fun getRefreshToken(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(REFRESH_TOKEN, null)
    }

}