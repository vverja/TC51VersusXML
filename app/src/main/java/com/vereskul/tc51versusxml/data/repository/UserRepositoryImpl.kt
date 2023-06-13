package com.vereskul.tc51versusxml.data.repository

import android.util.Log
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.data.database.UsersDAO
import com.vereskul.tc51versusxml.data.database.entities.asDomainModel
import com.vereskul.tc51versusxml.data.network.ApiFactory
import com.vereskul.tc51versusxml.data.network.ApiFactory.apiService
import com.vereskul.tc51versusxml.data.network.ApiService
import com.vereskul.tc51versusxml.domain.UserRepository
import com.vereskul.tc51versusxml.domain.models.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val usersDAO: UsersDAO
):UserRepository {
    override suspend fun login(
        username: String,
        password: String
    ): Flow<LoginResult> = flow {
            try {
                    ApiFactory.register(username, password)
                    val apiService = ApiFactory.apiService
                    val userInfo = apiService.getUserInfo()
                    Log.d("UserRepositoryImpl", "from network ${userInfo.toString()}")
                    usersDAO.insertUser(userInfo.convertToDataBaseModel(password))
                } catch (e: Exception) {
                    Log.e("UserRepositoryImpl", e.message.toString())
                }
            usersDAO.getUserByNameAndPassword(username, password).collect{usersList ->
                if (usersList.isNotEmpty()){
                    emit(LoginResult(success = usersList.first().asDomainModel()))
                }else{
                    emit(LoginResult(error = R.string.login_failed))
                }
                Log.d("UserRepositoryImpl", "from db !!!!")
            }
            Log.d("UserRepositoryImpl", "After local emit")
        }
    private suspend fun localLogin(username: String, password: String): LoginResult {
        var loginResult = LoginResult(error = R.string.login_failed)
        usersDAO.getUserByNameAndPassword(username, password)
            .collect{usersList ->
                    if (usersList.isNotEmpty()){
                        loginResult = LoginResult(success = usersList.first().asDomainModel())
                    }
                    Log.d("UserRepositoryImpl", "from db $loginResult")
                }
            return loginResult
        }


}