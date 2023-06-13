package com.vereskul.tc51versusxml.domain.models

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: UsersModel? = null,
    val error: Int? = null
)