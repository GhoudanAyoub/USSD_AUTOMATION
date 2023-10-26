package com.gws.common.utils

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.gws.ussd.common.R

object UssdNavigation {
    fun navigate(navController: NavController, action: NavDirections) {

        val appNavOptions = NavOptions.Builder().apply {
            setLaunchSingleTop(true)
            setEnterAnim(androidx.appcompat.R.anim.abc_fade_in)
            setExitAnim(androidx.appcompat.R.anim.abc_fade_out)
            setPopEnterAnim(androidx.appcompat.R.anim.abc_fade_in)
            setPopExitAnim(androidx.appcompat.R.anim.abc_fade_out)
        }.build()

        navController.navigate(directions = action, navOptions = appNavOptions)
    }
}
