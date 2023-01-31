package com.sample.bleintegration

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sample.bleintegration.ui.theme.Shapes
import com.sample.bleintegration.ui.theme.Teal200

@Composable
fun SimpleButton(
    buttonLabel: String,
    backgroundColor: Color = Teal200,
    enableButton: Boolean = true,
    onClick: ()->Unit,
    modifier: Modifier = Modifier
        .height(64.dp)
        .requiredWidth(200.dp)
)
{
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = Shapes.large,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            disabledBackgroundColor = Color.Gray,
        ),
        enabled = enableButton,
    ) {
        Text(
            text = buttonLabel,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}