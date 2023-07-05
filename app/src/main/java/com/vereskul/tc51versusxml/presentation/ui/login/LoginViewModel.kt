package com.vereskul.tc51versusxml.presentation.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.domain.models.LoginResult
import com.vereskul.tc51versusxml.domain.usecases.users_case.GetDefaultUserUseCase
import com.vereskul.tc51versusxml.domain.usecases.users_case.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val getDefaultUserUseCase: GetDefaultUserUseCase
) : ViewModel() {
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _defaultUserName = MutableLiveData<String>()
    val defaultUserName: LiveData<String>
        get() = _defaultUserName

    init {
        getDefaultUsername()
    }
    fun login(username: String, password: String) {
        viewModelScope.launch {
            loginUseCase(username, password).collect{
                Log.d("LoginViewModel", "$it")
                _loginResult.value = it
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

    private fun getDefaultUsername() = viewModelScope.launch {
        getDefaultUserUseCase().collect{
            _defaultUserName.value = it.displayName?:""
        }
    }
}