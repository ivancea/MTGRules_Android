package com.ivancea.MTGRules.presentation.common

import android.annotation.SuppressLint
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import java.util.regex.Pattern

private const val LINK_ANNOTATION_KEY = "LINK"

@Composable
fun LinkedText(
    text: String,
    style: TextStyle = TextStyle.Default,
    pattern: Pattern,
    onClick: (String) -> Unit
) {
    val annotatedString = makeAnnotatedString(text, pattern)

    NonConsumingClickableText(
        annotatedString,
        style = style,
        onClick = {
            annotatedString.getStringAnnotations(
                tag = LINK_ANNOTATION_KEY,
                start = it,
                end = it
            ).firstOrNull()?.let { annotation ->
                onClick(annotation.item)
            }
        },
        shouldConsume = {
            annotatedString.getStringAnnotations(
                tag = LINK_ANNOTATION_KEY,
                start = it,
                end = it
            ).firstOrNull() != null
        }
    )
}

@Composable
@ReadOnlyComposable
@SuppressLint("ComposableNaming")
private fun makeAnnotatedString(text: String, pattern: Pattern): AnnotatedString {
    val annotatedString = AnnotatedString.Builder(text)

    val matcher = pattern.matcher(text)

    while (matcher.find()) {
        annotatedString.addStringAnnotation(
            tag = LINK_ANNOTATION_KEY,
            annotation = matcher.group(),
            start = matcher.start(),
            end = matcher.end()
        )
        annotatedString.addStyle(
            SpanStyle(
                color = MaterialTheme.colors.primaryVariant,
                textDecoration = TextDecoration.Underline
            ),
            matcher.start(),
            matcher.end()
        )
    }

    return annotatedString.toAnnotatedString()
}