package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ResqOrange
import com.example.ui.theme.ResqRed
import com.example.ui.theme.ResqRedLight

@Composable
fun SosCountdownButton(
    isCountingDown: Boolean,
    secondsRemaining: Int,
    progress: Float,
    isSilent: Boolean,
    onTapTrigger: () -> Unit,
    onCancelCountdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isCountingDown) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isCountingDown) 400 else 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .size(240.dp)
            .testTag("sos_button_container"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (isCountingDown) ResqOrange.copy(alpha = 0.5f) else ResqRed.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Progress ring canvas during countdown
        if (isCountingDown) {
            Canvas(modifier = Modifier.size(210.dp)) {
                drawArc(
                    color = ResqOrange.copy(alpha = 0.3f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = ResqOrange,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Inner Action Circle
        Surface(
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, radius = 90.dp),
                    onClick = {
                        if (isCountingDown) onCancelCountdown() else onTapTrigger()
                    }
                )
                .testTag("sos_interactive_button"),
            shape = CircleShape,
            color = if (isCountingDown) ResqOrange else ResqRed,
            shadowElevation = 16.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isCountingDown) {
                        Text(
                            text = "$secondsRemaining",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "TAP TO CANCEL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancel SOS",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "SOS Button",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SOS",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = if (isSilent) "SILENT MODE" else "1-TAP EMERGENCY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.85f),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
