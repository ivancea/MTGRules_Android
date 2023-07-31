package com.ivancea.MTGRules.presentation.common

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.coroutineScope


/**
 * Copy of [androidx.compose.foundation.text.ClickableText], but not consuming
 * the click event if the lambda returns false
 */
@Composable
fun NonConsumingClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: (Int) -> Unit,
    shouldConsume: (Int) -> Boolean = { true }
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        coroutineScope {
            awaitEachGesture {
                val down = awaitFirstDown()

                val shouldConsumeResult = layoutResult.value?.let { layoutResult ->
                    shouldConsume(layoutResult.getOffsetForPosition(down.position))
                } ?: false

                if (!shouldConsumeResult) {
                    return@awaitEachGesture
                }

                down.consume()

                var upOrCancel: PointerInputChange? = null
                try {
                    // wait for first tap up or long press
                    upOrCancel = withTimeout(viewConfiguration.longPressTimeoutMillis) {
                        waitForUpOrCancellation()
                    }
                } catch (_: PointerEventTimeoutCancellationException) {
                }

                if (upOrCancel != null) {
                    upOrCancel.consume()
                    layoutResult.value?.let { layoutResult ->
                        onClick(layoutResult.getOffsetForPosition(upOrCancel.position))
                    }
                }
            }
        }

        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                onClick(layoutResult.getOffsetForPosition(pos))
            }
        }
    }

    BasicText(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = style,
        inlineContent = inlineContent,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        }
    )
}