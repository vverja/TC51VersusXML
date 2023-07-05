package com.vereskul.tc51versusxml.data.repository

import android.util.Log
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.data.database.UsersDAO
import com.vereskul.tc51versusxml.data.database.entities.asDomainModel
import com.vereskul.tc51versusxml.data.network.ApiFactory
import com.vereskul.tc51versusxml.domain.UserRepository
import com.vereskul.tc51versusxml.domain.models.LoginResult
import com.vereskul.tc51versusxml.domain.models.UsersModel
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
            }
        }

    override fun getDefaultUser(): Flow<UsersModel>  = flow {
        usersDAO.getUser()?.let {
            emit(it.asDomainModel())
        }

    }
}