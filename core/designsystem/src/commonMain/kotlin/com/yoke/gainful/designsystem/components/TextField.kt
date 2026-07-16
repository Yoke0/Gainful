package com.yoke.gainful.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.GainfulTheme
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import gainful.core.designsystem.generated.resources.Res
import gainful.core.designsystem.generated.resources.ic_eye_closed
import gainful.core.designsystem.generated.resources.ic_eye_open
import org.jetbrains.compose.resources.painterResource

private val InputShape = RoundedCornerShape(8.dp)

@Composable
fun LabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    focusedBorderColor: Color = Gold,
    unfocusedBorderColor: Color = Border,
    imeAction: ImeAction = ImeAction.Next,
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(InputShape)
                    .background(Surface)
                    .border(1.dp, if (isFocused) focusedBorderColor else unfocusedBorderColor, InputShape)
                    .onFocusChanged { isFocused = it.isFocused },
            enabled = true,
            readOnly = false,
            textStyle = TextStyle(color = TextPrimary, fontSize = 15.sp),
            cursorBrush = SolidColor(Gold),
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 15.sp,
                                color = TextMuted,
                            )
                        }
                        innerTextField()
                    }
                }
            },
        )
    }
}

@Composable
fun LabeledPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    focusedBorderColor: Color = Gold,
    unfocusedBorderColor: Color = Border,
    imeAction: ImeAction = ImeAction.Done,
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(InputShape)
                    .background(Surface)
                    .border(1.dp, if (isFocused) focusedBorderColor else unfocusedBorderColor, InputShape)
                    .onFocusChanged { isFocused = it.isFocused },
            enabled = true,
            readOnly = false,
            textStyle = TextStyle(color = TextPrimary, fontSize = 15.sp),
            cursorBrush = SolidColor(Gold),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
            singleLine = true,
            decorationBox = { innerTextField ->
                RowWithTrailing(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                    trailing = {
                        Icon(
                            painter =
                                painterResource(
                                    if (isPasswordVisible) {
                                        Res.drawable.ic_eye_closed
                                    } else {
                                        Res.drawable.ic_eye_open
                                    },
                                ),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .size(24.dp)
                                    .clickable { onToggleVisibility() },
                            tint = TextMuted,
                        )
                    },
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 15.sp,
                            color = TextMuted,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun RowWithTrailing(
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        trailing()
    }
}

@Preview
@Composable
private fun LabeledTextFieldPreview() {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }

    GainfulTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(text = "LabeledTextField", fontSize = 13.sp, color = TextSecondary)

            LabeledTextField(
                value = text1,
                onValueChange = { text1 = it },
                label = "Username",
                placeholder = "Enter username",
            )

            Text(text = "LabeledPasswordField", fontSize = 13.sp, color = TextSecondary)

            LabeledPasswordField(
                value = text2,
                onValueChange = { text2 = it },
                label = "Password",
                placeholder = "Enter password",
                isPasswordVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
            )

            Text(text = "Error state", fontSize = 13.sp, color = TextSecondary)

            LabeledTextField(
                value = "",
                onValueChange = {},
                label = "Confirm password",
                placeholder = "Re-enter password",
                focusedBorderColor = Color(0xFFE74C3C),
                unfocusedBorderColor = Color(0xFFE74C3C),
            )
        }
    }
}
