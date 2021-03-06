package com.example.myandroidapp.remote

import com.example.myandroidapp.core.Constants
import com.example.myandroidapp.core.Api
import com.example.myandroidapp.core.Result

object AuthRepository {
    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
//        Constants.instance()?.deleteValueString("token")
        Api.tokenInterceptor.token = null
    }

    suspend fun login(
            username: String,
            password: String
    ): Result<TokenHolder> {
        val user = User(username, password)
        val result = RemoteAuthDataSource.login(user)
        if (result is Result.Success<TokenHolder>) {
            print("Success!!");
            setLoggedInUser(user, result.data)
            Constants.instance()?.storeValueString("token", result.data.token);
            Constants.instance()?.storeValueString("_id", result.data._id);
        }
        return result
    }

    private fun setLoggedInUser(
            user: User,
            tokenHolder: TokenHolder
    ) {
        AuthRepository.user = user
        Api.tokenInterceptor.token = tokenHolder.token
    }
}