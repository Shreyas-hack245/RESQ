package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LiveRadarView(
    activeVolunteerCount: Int,
    safeZoneName: String = "Home Residence",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, ResqCardBorder, RoundedCornerShape(16.dp))
            .testTag("live_radar_view"),
        shape = RoundedCornerShape(16.dp),
        color = ResqCardBg
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Radar Grid Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.height / 2f - 20.dp.toPx()

                // Concentric circles
                drawCircle(
                    color = ResqEmerald.copy(alpha = 0.15f),
                    radius = maxRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = ResqEmerald.copy(alpha = 0.25f),
                    radius = maxRadius * 0.66f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = ResqEmerald.copy(alpha = 0.35f),
                    radius = maxRadius * 0.33f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                // Crosshairs
                drawLine(
                    color = ResqEmerald.copy(alpha = 0.2f),
                    start = Offset(center.x, center.y - maxRadius),
                    end = Offset(center.x, center.y + maxRadius),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = ResqEmerald.copy(alpha = 0.2f),
                    start = Offset(center.x - maxRadius, center.y),
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 1.dp.toPx()
                )

                // Animated Radar Arc
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            ResqEmerald.copy(alpha = 0.05f),
                            ResqEmerald.copy(alpha = 0.45f)
                        ),
                        center = center
                    ),
                    startAngle = sweepAngle,
                    sweepAngle = 60f,
                    useCenter = true,
                    topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                    size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
                )

                // User Center Marker
                drawCircle(
                    color = ResqRed,
                    radius = 7.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = center
                )

                // Simulated Nearby Volunteers
                val volunteerOffsets = listOf(
                    Offset(center.x - maxRadius * 0.4f, center.y - maxRadius * 0.3f),
                    Offset(center.x + maxRadius * 0.5f, center.y - maxRadius * 0.2f),
                    Offset(center.x + maxRadius * 0.2f, center.y + maxRadius * 0.5f)
                )

                volunteerOffsets.take(activeVolunteerCount).forEach { pos ->
                    drawCircle(
                        color = ResqEmerald,
                        radius = 5.dp.toPx(),
                        center = pos
                    )
                }
            }

            // Top Status Bar Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(ResqCardBg.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = null,
                    tint = ResqEmeraldLight,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = "GPS LOCK ACTIVE • LAT 37.7749 L N 122.4194",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ResqTextPrimary
                )
            }

            // Bottom Badges Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ResqEmerald.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ResqEmerald)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = ResqEmeraldLight,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SAFE ZONE: $safeZoneName",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ResqEmeraldLight
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ResqOrange.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ResqOrange)
                ) {
                    Text(
                        text = "$activeVolunteerCount VOLUNTEERS NEARBY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ResqOrangeLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
