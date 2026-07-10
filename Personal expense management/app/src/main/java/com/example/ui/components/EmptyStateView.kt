package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EmptyStateView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(130.dp)
                .padding(bottom = 12.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            drawCircle(
                color = GreenPrimary.copy(alpha = 0.25f),
                radius = 14f,
                center = Offset(canvasWidth * 0.24f, canvasHeight * 0.24f)
            )
            drawCircle(
                color = GreenPrimary.copy(alpha = 0.12f),
                radius = 20f,
                center = Offset(canvasWidth * 0.24f, canvasHeight * 0.24f),
                style = Stroke(width = 3f)
            )

            drawCircle(
                color = GreenPrimary.copy(alpha = 0.45f),
                radius = 10f,
                center = Offset(canvasWidth * 0.74f, canvasHeight * 0.16f)
            )

            val walletWidth = canvasWidth * 0.52f
            val walletHeight = canvasHeight * 0.42f
            val walletX = (canvasWidth - walletWidth) / 2f
            val walletY = (canvasHeight - walletHeight) / 2f + 10f

            drawRoundRect(
                color = GreenPrimary.copy(alpha = 0.08f),
                topLeft = Offset(walletX, walletY),
                size = Size(walletWidth, walletHeight),
                cornerRadius = CornerRadius(24f, 24f)
            )
            drawRoundRect(
                color = GreenPrimary.copy(alpha = 0.7f),
                topLeft = Offset(walletX, walletY),
                size = Size(walletWidth, walletHeight),
                cornerRadius = CornerRadius(24f, 24f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            val flapWidth = walletWidth * 0.45f
            val flapHeight = walletHeight * 0.38f
            val flapX = walletX + walletWidth - flapWidth + 5f
            val flapY = walletY + (walletHeight - flapHeight) / 2f - 3f

            drawRoundRect(
                color = Color.White,
                topLeft = Offset(flapX, flapY),
                size = Size(flapWidth, flapHeight),
                cornerRadius = CornerRadius(14f, 14f)
            )
            drawRoundRect(
                color = GreenPrimary.copy(alpha = 0.85f),
                topLeft = Offset(flapX, flapY),
                size = Size(flapWidth, flapHeight),
                cornerRadius = CornerRadius(14f, 14f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            drawCircle(
                color = GreenPrimary,
                radius = 7f,
                center = Offset(flapX + flapWidth * 0.5f, flapY + flapHeight * 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "هنوز هیچ تراکنشی ثبت نشده است.",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "برای شروع تراکنش جدید، روی دکمه (+) ضربه بزنید.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary.copy(alpha = 0.55f),
            fontSize = 12.sp
        )
    }
}
