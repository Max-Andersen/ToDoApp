package com.toloknov.summerschool.todoapp.ui.common.bottomsheet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.toloknov.summerschool.domain.model.ItemImportance
import com.toloknov.summerschool.theme.theme.PADDING_BIG
import com.toloknov.summerschool.theme.theme.PADDING_MEDIUM
import com.toloknov.summerschool.theme.theme.ToDoAppTheme
import com.toloknov.summerschool.theme.theme.textButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

data class BottomSheetItem(
    val itemName: String,
    val onItemClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportanceBottomSheetList(
    paddingValues: PaddingValues = PaddingValues(),
    sheetState: SheetState,
    scope: CoroutineScope,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = Color.Black.copy(alpha = 0.15f),
    onItemClick: (ItemImportance) -> Unit,
    onDismissRequest: () -> Unit
) {
    val importance = remember {
        ItemImportance.entries
    }

    var highAnimationStart by remember {
        mutableStateOf(false)
    }
    val alphaAnimatable = remember { Animatable(0.5f) }

    LaunchedEffect(highAnimationStart) {
        if (highAnimationStart) {
            scope.launch {
                alphaAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 800)
                )
                alphaAnimatable.animateTo(
                    targetValue = 0.5f,
                    animationSpec = tween(durationMillis = 800)
                )

                onItemClick(ItemImportance.HIGH)
                sheetState.hide()
                onDismissRequest()
            }
        }
    }


    ModalBottomSheet(
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        ),
        content = {
            Column(
                modifier = Modifier
                    .padding(
                        bottom = PADDING_BIG,
                        start = PADDING_BIG,
                        end = PADDING_BIG
                    )
            ) {
                importance.forEach { importanceItem ->
                    Button(
                        onClick = {
                            if (importanceItem != ItemImportance.HIGH) {
                                onItemClick(importanceItem)
                                scope.launch {
                                    sheetState.hide()
                                }
                                onDismissRequest()
                            } else {
                                highAnimationStart = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = Shapes().large,
                        colors = MaterialTheme.colorScheme.textButton
                    ) {
                        Text(
                            text = importanceItem.nameRu.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            },
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (importanceItem == ItemImportance.HIGH) Color.Red.copy(alpha = alphaAnimatable.value) else Color.Unspecified
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(PADDING_BIG + PADDING_MEDIUM))
        },
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.surfaceVariant) },
        containerColor = sheetBackgroundColor,
        contentColor = sheetContentColor,
        scrimColor = scrimColor,
        onDismissRequest = onDismissRequest,
        windowInsets = WindowInsets.navigationBars
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun BottomSheetPreview() {
    val sheetState = rememberModalBottomSheetState()
    LaunchedEffect(key1 = Unit) {
        sheetState.expand()
    }
    ToDoAppTheme {
        Scaffold { paddingValues ->
            val scope = rememberCoroutineScope()

            ImportanceBottomSheetList(
                paddingValues = paddingValues,
                sheetState = sheetState,
                scope = scope,
                onDismissRequest = { },
                onItemClick = {}
            )
        }
    }
}