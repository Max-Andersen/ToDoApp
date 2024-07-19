package com.toloknov.summerschool.todoapp.ui.divkit

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.getSystemService
import com.toloknov.summerschool.todoapp.databinding.AboutScreenBinding
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.picasso.PicassoDivImageLoader

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.systemBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Screen(onBackClick)
    }
}

@Composable
private fun Screen(
    onBackClick: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            with(context) {
                val layoutInflater = context.getSystemService<LayoutInflater>()
                val binding = AboutScreenBinding.inflate(layoutInflater!!)
                val assetReader = AssetReader(this)

                val divJson = assetReader.read("about_screen.json")
                val templatesJson = divJson.optJSONObject("templates")
                val cardJson = divJson.getJSONObject("card")

                val divContext = Div2Context(
                    baseContext = getActivity()!!,
                    configuration = createDivConfiguration(onBackClick),
                    lifecycleOwner = lifecycleOwner,
                )

                val divView = Div2ViewFactory(divContext, templatesJson).createView(cardJson)
                binding.root.addView(divView)

                binding.root
            }
        }
    )
}

private fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

private fun Context.createDivConfiguration(backClick : () -> Unit): DivConfiguration {
    val imageLoader = PicassoDivImageLoader(this)
    val configuration = DivConfiguration.Builder(imageLoader)
        .visualErrorsEnabled(true)
        .actionHandler(DivActionHandler(backClick))
        .build()
    return configuration
}