package com.yoke.gainful.feature.account.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoke.gainful.designsystem.components.BackNavigationIcon
import com.yoke.gainful.designsystem.components.GainfulScaffold
import com.yoke.gainful.designsystem.components.GainfulTopAppBar
import com.yoke.gainful.designsystem.components.LabeledPasswordField
import com.yoke.gainful.designsystem.components.LabeledTextField
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.GainGreen
import com.yoke.gainful.designsystem.theme.GainRed
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import gainful.core.designsystem.generated.resources.ic_check
import gainful.core.designsystem.generated.resources.ic_user_add
import gainful.feature.account.generated.resources.Res
import gainful.feature.account.generated.resources.error_agreement_required
import gainful.feature.account.generated.resources.error_nickname_length
import gainful.feature.account.generated.resources.error_password_mismatch
import gainful.feature.account.generated.resources.error_password_too_short
import gainful.feature.account.generated.resources.register_agreement_and
import gainful.feature.account.generated.resources.register_agreement_prefix
import gainful.feature.account.generated.resources.register_confirm_password_hint
import gainful.feature.account.generated.resources.register_confirm_password_label
import gainful.feature.account.generated.resources.register_email_hint
import gainful.feature.account.generated.resources.register_email_label
import gainful.feature.account.generated.resources.register_error_empty
import gainful.feature.account.generated.resources.register_has_account
import gainful.feature.account.generated.resources.register_hero_subtitle
import gainful.feature.account.generated.resources.register_hero_title
import gainful.feature.account.generated.resources.register_loading
import gainful.feature.account.generated.resources.register_login_link
import gainful.feature.account.generated.resources.register_nickname_hint
import gainful.feature.account.generated.resources.register_nickname_label
import gainful.feature.account.generated.resources.register_nickname_length
import gainful.feature.account.generated.resources.register_password_hint
import gainful.feature.account.generated.resources.register_password_label
import gainful.feature.account.generated.resources.register_privacy_policy
import gainful.feature.account.generated.resources.register_submit
import gainful.feature.account.generated.resources.register_terms_of_service
import gainful.feature.account.generated.resources.register_title
import gainful.feature.account.generated.resources.strength_fair
import gainful.feature.account.generated.resources.strength_label
import gainful.feature.account.generated.resources.strength_medium
import gainful.feature.account.generated.resources.strength_strong
import gainful.feature.account.generated.resources.strength_weak
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import gainful.core.designsystem.generated.resources.Res as DsRes

private const val TERMS_URL = "https://gainful.app/terms"
private const val PRIVACY_URL = "https://gainful.app/privacy"

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            onRegisterSuccess()
        }
    }

    RegisterScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        onNavigateToLogin = onNavigateToLogin,
    )
}

@Composable
private fun RegisterScreen(
    uiState: RegisterUiState,
    onIntent: (RegisterIntent) -> Unit,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    GainfulScaffold(
        appTopBar = {
            GainfulTopAppBar(
                title = stringResource(Res.string.register_title),
                navigationIcon = { BackNavigationIcon(onClick = onBack) },
            )
        },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                    painter = painterResource(DsRes.drawable.ic_user_add),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Gold,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.register_hero_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.register_hero_subtitle),
                fontSize = 14.sp,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            LabeledTextField(
                value = uiState.nickname,
                onValueChange = { onIntent(RegisterIntent.SetNickname(it)) },
                label = stringResource(Res.string.register_nickname_label),
                placeholder = stringResource(Res.string.register_nickname_hint),
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(Res.string.register_nickname_length),
                fontSize = 12.sp,
                color = TextMuted,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                value = uiState.email,
                onValueChange = { onIntent(RegisterIntent.SetEmail(it)) },
                label = stringResource(Res.string.register_email_label),
                placeholder = stringResource(Res.string.register_email_hint),
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Next,
            )

            Spacer(modifier = Modifier.height(16.dp))

            LabeledPasswordField(
                value = uiState.password,
                onValueChange = { onIntent(RegisterIntent.SetPassword(it)) },
                label = stringResource(Res.string.register_password_label),
                placeholder = stringResource(Res.string.register_password_hint),
                isPasswordVisible = uiState.isPasswordVisible,
                onToggleVisibility = { onIntent(RegisterIntent.TogglePasswordVisibility) },
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Next,
            )

            if (uiState.password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                PasswordStrengthIndicator(strength = uiState.passwordStrength)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LabeledPasswordField(
                value = uiState.confirmPassword,
                onValueChange = { onIntent(RegisterIntent.SetConfirmPassword(it)) },
                label = stringResource(Res.string.register_confirm_password_label),
                placeholder = stringResource(Res.string.register_confirm_password_hint),
                isPasswordVisible = uiState.isPasswordVisible,
                onToggleVisibility = { onIntent(RegisterIntent.TogglePasswordVisibility) },
                modifier = Modifier.fillMaxWidth(),
                focusedBorderColor = if (uiState.passwordMismatch) GainRed else Gold,
                unfocusedBorderColor = if (uiState.passwordMismatch) GainRed else Border,
            )
            if (uiState.passwordMismatch) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.error_password_mismatch),
                    fontSize = 12.sp,
                    color = GainRed,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onIntent(RegisterIntent.ToggleAgreement) }
                        .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (uiState.agreedToTerms) Gold else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (uiState.agreedToTerms) Gold else Border,
                                shape = RoundedCornerShape(4.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (uiState.agreedToTerms) {
                        Icon(
                            painter = painterResource(DsRes.drawable.ic_check),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Background,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.register_agreement_prefix),
                        fontSize = 12.sp,
                        color = TextMuted,
                    )
                    Text(
                        text = stringResource(Res.string.register_terms_of_service),
                        fontSize = 12.sp,
                        color = Gold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { uriHandler.openUri(TERMS_URL) },
                    )
                    Text(
                        text = stringResource(Res.string.register_agreement_and),
                        fontSize = 12.sp,
                        color = TextMuted,
                    )
                    Text(
                        text = stringResource(Res.string.register_privacy_policy),
                        fontSize = 12.sp,
                        color = Gold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { uriHandler.openUri(PRIVACY_URL) },
                    )
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        when (uiState.error) {
                            RegisterError.EMPTY_FIELDS -> stringResource(Res.string.register_error_empty)
                            RegisterError.NICKNAME_LENGTH -> stringResource(Res.string.error_nickname_length)
                            RegisterError.PASSWORD_MISMATCH -> stringResource(Res.string.error_password_mismatch)
                            RegisterError.PASSWORD_TOO_SHORT -> stringResource(Res.string.error_password_too_short)
                            RegisterError.AGREEMENT_REQUIRED -> stringResource(Res.string.error_agreement_required)
                        },
                    fontSize = 13.sp,
                    color = GainRed,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (uiState.isLoading) Gold.copy(alpha = 0.6f) else Gold)
                        .clickable(enabled = !uiState.isLoading) { onIntent(RegisterIntent.Submit) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text =
                        if (uiState.isLoading) {
                            stringResource(
                                Res.string.register_loading,
                            )
                        } else {
                            stringResource(Res.string.register_submit)
                        },
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
                    text = stringResource(Res.string.register_has_account),
                    fontSize = 14.sp,
                    color = TextMuted,
                )
                Text(
                    text = stringResource(Res.string.register_login_link),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gold,
                    modifier =
                        Modifier
                            .clickable { onNavigateToLogin() }
                            .padding(start = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PasswordStrengthIndicator(strength: PasswordStrength) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            repeat(4) { index ->
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (index < strength.level) {
                                    when (strength) {
                                        PasswordStrength.WEAK -> GainRed
                                        PasswordStrength.FAIR -> GainRed
                                        PasswordStrength.MEDIUM -> Gold
                                        PasswordStrength.STRONG -> GainGreen
                                    }
                                } else {
                                    Border
                                },
                            ),
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text =
                stringResource(Res.string.strength_label) +
                    stringResource(
                        when (strength) {
                            PasswordStrength.WEAK -> Res.string.strength_weak
                            PasswordStrength.FAIR -> Res.string.strength_fair
                            PasswordStrength.MEDIUM -> Res.string.strength_medium
                            PasswordStrength.STRONG -> Res.string.strength_strong
                        },
                    ),
            fontSize = 12.sp,
            color = TextMuted,
        )
    }
}
