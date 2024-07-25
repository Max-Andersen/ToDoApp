package com.toloknov.summerschool.todoapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.toloknov.summerschool.domain.model.ApplicationTheme
import com.toloknov.summerschool.todoapp.R
import com.toloknov.summerschool.todoapp.ui.common.dropdown.ButtonWithExposedDropDownMenu

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val avatarId by viewModel.avatarId.collectAsStateWithLifecycle()
    val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()

    val themes = remember {
        ApplicationTheme.entries.toList()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(com.toloknov.summerschool.theme.theme.PADDING_BIG)
                .padding(top = com.toloknov.summerschool.theme.theme.PADDING_BIG * 2)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://avatars.yandex.net/get-yapic/$avatarId/islands-retina-middle")
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.avatar_placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(84.dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                ButtonWithExposedDropDownMenu(
                    modifier = Modifier.fillMaxWidth(0.65f),
                    listOfParameters = themes.map { it.nameRu },
                    label = "Тема приложения",
                    selectedParameter = appTheme.nameRu,
                    onItemClick = {
                        viewModel.changeTheme(it)
                    }
                )

            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(onClick = viewModel::logout, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Выйти")
            }

        }
    }
}
