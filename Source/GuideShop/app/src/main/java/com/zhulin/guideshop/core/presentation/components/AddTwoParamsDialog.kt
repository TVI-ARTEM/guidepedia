package com.zhulin.guideshop.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.zhulin.guideshop.ui.theme.Rubik


@Composable
fun AddTwoParamsDialog(
    title: String,
    hintFirst: String,
    hintSecond: String,
    onDismissRequest: () -> Unit,
    onConfirm: (first: String, second: String) -> Unit
) {
    var inputFirst by remember { mutableStateOf(TextFieldValue("")) }
    var inputSecond by remember { mutableStateOf(TextFieldValue("")) }
    var hintVisibleFirst by remember { mutableStateOf(false) }
    var hintVisibleSecond by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            FilledIconButton(
                onClick = { onConfirm(inputFirst.text, inputSecond.text) },
                shape = CircleShape,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xFFFF4040)),
                )
            }
        },
        shape = RoundedCornerShape(10.dp),
        title = {
            Text(text = title, fontFamily = Rubik, fontWeight = FontWeight.Medium)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TransparentHintTextField(
                    text = inputFirst,
                    hint = hintFirst,
                    onValueChange = {
                        if (!it.text.contains("\n")) {
                            inputFirst = it
                        }
                    },
                    onFocusChange = {
                        hintVisibleFirst = !it.isFocused && inputFirst.text.isEmpty()
                    },
                    isHintVisible = hintVisibleFirst,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Blue.copy(alpha = 0.1f))
                        .padding(10.dp)
                        .fillMaxWidth(0.95f)
                )

                TransparentHintTextField(
                    text = inputSecond,
                    hint = hintSecond,
                    onValueChange = {
                        if (!it.text.contains("\n")) {
                            inputSecond = it
                        }
                    },
                    onFocusChange = {
                        hintVisibleSecond = !it.isFocused && inputSecond.text.isEmpty()
                    },
                    isHintVisible = hintVisibleSecond,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Blue.copy(alpha = 0.1f))
                        .padding(10.dp)
                        .fillMaxWidth(0.95f)
                )
            }

        }
    )
}
