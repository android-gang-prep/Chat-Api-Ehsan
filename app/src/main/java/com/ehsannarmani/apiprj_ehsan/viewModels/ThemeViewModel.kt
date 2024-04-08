package com.ehsannarmani.apiprj_ehsan.viewModels

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ThemeViewModel: ViewModel() {

    private val _darkMode = MutableStateFlow(false)
    val darkMode = _darkMode.asStateFlow()

    fun setDarkMode(darkMode:Boolean) = _darkMode.update { darkMode }

}

val LocalThemeViewModel = staticCompositionLocalOf<ThemeViewModel> { error("") }