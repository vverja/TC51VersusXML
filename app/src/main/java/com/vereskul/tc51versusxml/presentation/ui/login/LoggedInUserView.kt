package com.vereskul.tc51versusxml.presentation.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String?,
    val warehouse: String?
    //... other data fields that may be accessible to the UI
)