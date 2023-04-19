package com.zhulin.guideshop.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.zhulin.guideshop.R


val Rubik = FontFamily(
    Font(R.font.rubik_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.rubik_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.rubik_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.rubik_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(R.font.rubik_medium_italic, weight = FontWeight.Medium, style = FontStyle.Italic),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Rubik,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 24.sp
    ),

    )