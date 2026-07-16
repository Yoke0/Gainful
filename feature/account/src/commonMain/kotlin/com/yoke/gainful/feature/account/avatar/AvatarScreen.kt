package com.yoke.gainful.feature.account.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.yoke.gainful.designsystem.components.BackNavigationIcon
import com.yoke.gainful.designsystem.components.GainfulScaffold
import com.yoke.gainful.designsystem.components.GainfulTopAppBar
import com.yoke.gainful.designsystem.theme.Background
import com.yoke.gainful.designsystem.theme.Border
import com.yoke.gainful.designsystem.theme.Gold
import com.yoke.gainful.designsystem.theme.GoldDim
import com.yoke.gainful.designsystem.theme.Surface
import com.yoke.gainful.designsystem.theme.TextMuted
import com.yoke.gainful.designsystem.theme.TextPrimary
import com.yoke.gainful.designsystem.theme.TextSecondary
import com.yoke.gainful.file.decodeImageBitmap
import com.yoke.gainful.file.rememberImagePickerLauncher
import gainful.core.designsystem.generated.resources.ic_upload
import gainful.feature.account.generated.resources.Res
import gainful.feature.account.generated.resources.avatar_confirm
import gainful.feature.account.generated.resources.avatar_current_label
import gainful.feature.account.generated.resources.avatar_error_image_process
import gainful.feature.account.generated.resources.avatar_error_save
import gainful.feature.account.generated.resources.avatar_error_upload
import gainful.feature.account.generated.resources.avatar_preset_label
import gainful.feature.account.generated.resources.avatar_title
import gainful.feature.account.generated.resources.avatar_upload_format
import gainful.feature.account.generated.resources.avatar_upload_hint
import gainful.feature.account.generated.resources.avatar_upload_label
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import gainful.core.designsystem.generated.resources.Res as DsRes

private val PRESET_AVATARS =
    listOf(
        "\uD83D\uDE0E",
        "\uD83E\uDD20",
        "\uD83E\uDD8A",
        "\uD83D\uDC31",
        "\uD83D\uDC3B",
        "\uD83E\uDD84",
        "\uD83D\uDC38",
        "\uD83D\uDC28",
        "\uD83E\uDD81",
        "\uD83D\uDC2F",
        "\uD83E\uDC88",
        "\uD83D\uDC09",
    )

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarScreen(
    viewModel: AvatarViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val imagePickerLauncher =
        rememberImagePickerLauncher { bytes, fileName ->
            if (bytes != null && fileName != null) {
                viewModel.onIntent(AvatarIntent.SelectImage(bytes, fileName))
            }
        }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onBack()
        }
    }

    GainfulScaffold(
        appTopBar = {
            GainfulTopAppBar(
                title = stringResource(Res.string.avatar_title),
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

            // Current avatar / preview
            val previewBytes = uiState.previewImageBytes
            val avatarUrl = uiState.avatarUrl
            if (previewBytes != null) {
                val bitmap =
                    remember(previewBytes) {
                        decodeImageBitmap(previewBytes)
                    }
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .border(3.dp, Gold, CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else if (avatarUrl != null) {
                AsyncImage(
                    model = "http://192.168.31.47:8080$avatarUrl",
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .border(3.dp, Gold, CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier =
                        Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .border(3.dp, Gold, CircleShape)
                            .background(Surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = uiState.selectedEmoji ?: "\uD83D\uDE0E",
                        fontSize = 48.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.avatar_current_label),
                fontSize = 13.sp,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Preset avatars section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(Res.string.avatar_preset_label),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PRESET_AVATARS.forEach { emoji ->
                        val isSelected = emoji == uiState.selectedEmoji && uiState.previewImageBytes == null
                        Box(
                            modifier =
                                Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoldDim else Surface)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Gold else Border,
                                        shape = CircleShape,
                                    )
                                    .clickable { viewModel.onIntent(AvatarIntent.SelectPreset(emoji)) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 28.sp,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Upload section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(Res.string.avatar_upload_label),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .background(Surface)
                            .clickable { imagePickerLauncher.launch() },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            painter = painterResource(DsRes.drawable.ic_upload),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = TextMuted,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.avatar_upload_hint),
                            fontSize = 14.sp,
                            color = TextSecondary,
                        )
                        Text(
                            text = stringResource(Res.string.avatar_upload_format),
                            fontSize = 12.sp,
                            color = TextMuted,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Error message
            if (uiState.error != null) {
                Text(
                    text =
                        when (uiState.error!!) {
                            AvatarError.IMAGE_PROCESS -> stringResource(Res.string.avatar_error_image_process)
                            AvatarError.SAVE -> stringResource(Res.string.avatar_error_save)
                            AvatarError.UPLOAD -> stringResource(Res.string.avatar_error_upload)
                        },
                    fontSize = 13.sp,
                    color = com.yoke.gainful.designsystem.theme.GainRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Confirm button
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Gold)
                        .clickable { viewModel.onIntent(AvatarIntent.Confirm) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.avatar_confirm),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Background,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
