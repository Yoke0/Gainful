package com.yoke.gainful.feature.account.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.designsystem.components.BackNavigationIcon
import com.yoke.gainful.designsystem.components.GainfulScaffold
import com.yoke.gainful.designsystem.components.GainfulTopAppBar
import com.yoke.gainful.designsystem.components.LabeledPasswordField
import com.yoke.gainful.designsystem.components.LabeledTextField
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import gainful.core.designsystem.generated.resources.ic_user
import gainful.feature.account.generated.resources.Res
import gainful.feature.account.generated.resources.login_error_empty
import gainful.feature.account.generated.resources.login_error_invalid
import gainful.feature.account.generated.resources.login_hero_subtitle
import gainful.feature.account.generated.resources.login_hero_title
import gainful.feature.account.generated.resources.login_loading
import gainful.feature.account.generated.resources.login_no_account
import gainful.feature.account.generated.resources.login_password_hint
import gainful.feature.account.generated.resources.login_password_label
import gainful.feature.account.generated.resources.login_register_link
import gainful.feature.account.generated.resources.login_submit
import gainful.feature.account.generated.resources.login_title
import gainful.feature.account.generated.resources.login_username_hint
import gainful.feature.account.generated.resources.login_username_label
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import gainful.core.designsystem.generated.resources.Res as DsRes

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    LoginScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        onNavigateToRegister = onNavigateToRegister,
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    onBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    GainfulScaffold(
        appTopBar = {
            GainfulTopAppBar(
                title = stringResource(Res.string.login_title),
                navigationIcon = { BackNavigationIcon(onClick = onBack) },
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier =
                    Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GoldDim),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(DsRes.drawable.ic_user),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Gold,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.login_hero_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.login_hero_subtitle),
                fontSize = 14.sp,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            LabeledTextField(
                value = uiState.username,
                onValueChange = { onIntent(LoginIntent.SetUsername(it)) },
                label = stringResource(Res.string.login_username_label),
                placeholder = stringResource(Res.string.login_username_hint),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))

            LabeledPasswordField(
                value = uiState.password,
                onValueChange = { onIntent(LoginIntent.SetPassword(it)) },
                label = stringResource(Res.string.login_password_label),
                placeholder = stringResource(Res.string.login_password_hint),
                isPasswordVisible = uiState.isPasswordVisible,
                onToggleVisibility = { onIntent(LoginIntent.TogglePasswordVisibility) },
                modifier = Modifier.fillMaxWidth(),
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        when (uiState.error) {
                            LoginError.EMPTY_FIELDS -> stringResource(Res.string.login_error_empty)
                            LoginError.INVALID_CREDENTIALS -> stringResource(Res.string.login_error_invalid)
                        },
                    fontSize = 13.sp,
                    color = Color(0xFFE74C3C),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (uiState.isLoading) Gold.copy(alpha = 0.6f) else Gold)
                        .clickable(enabled = !uiState.isLoading) { onIntent(LoginIntent.Submit) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (uiState.isLoading) stringResource(Res.string.login_loading) else stringResource(Res.string.login_submit),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Background,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.login_no_account),
                    fontSize = 14.sp,
                    color = TextMuted,
                )
                Text(
                    text = stringResource(Res.string.login_register_link),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gold,
                    modifier =
                        Modifier
                            .clickable { onNavigateToRegister() }
                            .padding(start = 4.dp),
                )
            }
        }
    }
}
