package com.ehsannarmani.apiprj_ehsan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ehsannarmani.apiprj_ehsan.viewModels.LocalThemeViewModel
import com.ehsannarmani.apiprj_ehsan.viewModels.ThemeViewModel

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ApiPrjEhsanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }


    val viewModel:ThemeViewModel = viewModel()
    val darkMode by viewModel.darkMode.collectAsState()


    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkMode
        }
    }

    val customColors = CustomColors(
        background = animateColorAsState(targetValue = if (darkMode) Color(0xff313131) else Color.White).value,
        lightBackground = animateColorAsState(targetValue = if (darkMode) Color(0xff454545) else Color(0xff919191)).value,
        darkBackground = animateColorAsState(targetValue = if (darkMode) Color(0xff252525) else Color(0xffeeeeee)).value,
        textColor = animateColorAsState(targetValue = if (darkMode) Color.White else Color(0xff313131)).value,
        active = animateColorAsState(targetValue = if (darkMode) Color.White else Color(0xff313131)).value,
        notActive = animateColorAsState(targetValue = if (darkMode) Color(0xff919191) else Color(0xff919191).copy(alpha = .6f)).value,
    )
    CompositionLocalProvider (
        LocalThemeViewModel provides viewModel,
        LocalCustomColors provides customColors
    ){
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

}
val LocalCustomColors = staticCompositionLocalOf<CustomColors> { error("") }
data class CustomColors(
    val background:Color,
    val lightBackground:Color,
    val darkBackground:Color,
    val textColor:Color,
    val active:Color,
    val notActive:Color
)