package com.malayspeak.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Google Iconic Brand Colors (Blue, Red, Yellow, Green)
val GoogleBlue = Color(0xFF4285F4)
val GoogleRed = Color(0xFFEA4335)
val GoogleYellow = Color(0xFFFBBC05)
val GoogleGreen = Color(0xFF34A853)

val GoogleColors = listOf(
    GoogleBlue,
    GoogleRed,
    GoogleYellow,
    GoogleGreen,
    GoogleBlue // Seamless loop
)

/**
 * Animated Gradient Glow Border modifier for Jetpack Compose.
 * Uses Brush.sweepGradient with Google colors rotating from 0f to 360f.
 */
fun Modifier.animatedGoogleGlowBorder(
    rotationAngle: Float,
    borderWidth: Dp = 2.dp,
    shapeCornerRadius: Dp = 14.dp,
    innerCornerRadius: Dp = 12.dp,
    backgroundColor: Color
): Modifier = this
    .clip(RoundedCornerShape(shapeCornerRadius))
    .drawBehind {
        rotate(rotationAngle) {
            drawCircle(
                brush = Brush.sweepGradient(GoogleColors),
                radius = size.maxDimension
            )
        }
    }
    .padding(borderWidth)
    .background(
        color = backgroundColor,
        shape = RoundedCornerShape(innerCornerRadius)
    )

/**
 * Google AI Mode Search Bar with continuous animated gradient glow border.
 * 
 * Requirements fulfilled:
 * 1. Brush.sweepGradient() with multi-color Google gradient (Blue, Red, Yellow, Green).
 * 2. rememberInfiniteTransition and animateFloat rotating angle 0f -> 360f.
 * 3. Thin background glow wrapper acting as an animated border behind OutlinedTextField.
 * 4. Border is exactly 2dp thick.
 * 5. Background is solid black in Dark mode, solid white in Light mode for full legibility.
 * 6. Minimalist Black & White aesthetic preserved everywhere else.
 */
@Composable
fun GoogleAIGlowSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "စကားစု ရှာဖွေပါ (Burmese, English, Malay)...",
    onClearQuery: () -> Unit = { onQueryChange("") }
) {
    // 2. Animated rotation angle 0f to 360f
    val infiniteTransition = rememberInfiniteTransition(label = "GoogleAIGlowBorderTransition")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glowAngle"
    )

    // Solid container surface color based on theme
    val surfaceColor = MaterialTheme.colorScheme.surface

    // 3. Thin background container with 2dp border reveal
    Box(
        modifier = modifier.animatedGoogleGlowBorder(
            rotationAngle = rotationAngle,
            borderWidth = 2.dp,
            shapeCornerRadius = 14.dp,
            innerCornerRadius = 12.dp,
            backgroundColor = surfaceColor
        )
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearQuery) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = surfaceColor,
                unfocusedContainerColor = surfaceColor,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
