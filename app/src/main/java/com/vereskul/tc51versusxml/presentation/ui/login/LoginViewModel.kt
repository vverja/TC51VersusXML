package com.vereskul.tc51versusxml.presentation.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.network.ApiFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel() : ViewModel() {
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        //val result = loginRepository.login(username, password)
        ApiFactory.register(username, password)
        ApiFactory.apiService?.let { apiService ->
            viewModelScope.launch {
                try {
                    val userInfo = apiService.getUserInfo().convertToModel()
                    _loginResult.value = LoginResult( success = LoggedInUserView(
                                    userInfo.displayName,
                                    userInfo.stockName
                                )
                            )
                        Log.d("LoginViewModel", userInfo.displayName?:"")
                }catch (e: RuntimeException){
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                    Log.e("LoginViewModel",e.message?:"")
                }
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank()
    }

    override fun onCleared() {
        super.onCleared()
    }
}