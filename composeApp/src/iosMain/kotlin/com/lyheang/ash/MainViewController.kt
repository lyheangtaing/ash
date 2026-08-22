package com.lyheang.ash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

private var rootViewController: UIViewController? = null

fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        Box {
            App()
        }
    }.also {
        rootViewController = it
    }
}

internal fun presentingViewController(): UIViewController? {
    var controller = rootViewController ?: return null
    while (controller.presentedViewController != null) {
        controller = controller.presentedViewController ?: break
    }
    return controller
}
