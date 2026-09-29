package com.gearui.components.link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.gearui.foundation.motion.FeedbackDefaults
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.TextStyle
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.gestures.detectTapGestures
import com.tencent.kuikly.compose.foundation.layout.ExperimentalLayoutApi
import com.tencent.kuikly.compose.foundation.layout.FlowRow
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.semantics.Role
import com.tencent.kuikly.compose.ui.semantics.clearAndSetSemantics
import com.tencent.kuikly.compose.ui.semantics.contentDescription
import com.tencent.kuikly.compose.ui.semantics.onClick
import com.tencent.kuikly.compose.ui.semantics.role

/**
 * A sentence with tappable phrases in it: "I have read and agree to the 《Terms》 and
 * the 《Privacy Policy》", where each document opens on its own.
 *
 * [links] maps a phrase of [text] to what tapping it does; the first occurrence of each
 * phrase becomes the link. Translators supply the whole sentence and the phrases, so word
 * order stays theirs — no fragments glued together in code.
 *
 * Links take [linkColor] and no underline, the way consent lines are set in Chinese
 * apps, and dim while pressed; the rest of the sentence is [color] and reports taps to
 * [onTextClick].
 *
 * KuiklyUI draws a text natively and neither delivers taps to link annotations nor
 * reports character positions, so the sentence is laid out as a flow of pieces instead:
 * a Chinese character each, a Latin word each. Closing punctuation stays on the line of
 * the character before it and opening punctuation with the one after, so no line starts
 * with "，" or ends with "《". A screen reader hears the sentence once and each link as a
 * button.
 */
@Composable
fun LinkedText(
    text: String,
    links: Map<String, () -> Unit>,
    modifier: Modifier = Modifier,
    style: TextStyle = Theme.typography.bodySmall,
    color: Color = Theme.colors.mutedForeground,
    linkColor: Color = Theme.colors.primary,
    onTextClick: (() -> Unit)? = null,
) {
    LinkedTextFlow(text, links, modifier, style, color, linkColor, onTextClick, readSentence = true)
}

/**
 * [readSentence] false leaves the sentence unread by a screen reader — for a control that
 * already carries it as its name, as [com.gearui.components.checkbox.AgreementCheckbox]
 * does — while each link stays a button.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun LinkedTextFlow(
    text: String,
    links: Map<String, () -> Unit>,
    modifier: Modifier,
    style: TextStyle,
    color: Color,
    linkColor: Color,
    onTextClick: (() -> Unit)?,
    readSentence: Boolean,
) {
    val actions by rememberUpdatedState(links)
    val plainClick by rememberUpdatedState(onTextClick)
    val pieces = remember(text, links.keys) { linkPieces(text, links.keys) }
    var pressed by remember { mutableStateOf<String?>(null) }

    FlowRow(modifier = modifier) {
        pieces.forEachIndexed { index, piece ->
            val phrase = piece.link
            val firstOfLink = phrase != null && pieces.getOrNull(index - 1)?.link != phrase
            Row {
                Text(
                    text = piece.text,
                    style = style,
                    color = when {
                        phrase == null -> color
                        phrase == pressed -> linkColor.copy(alpha = linkColor.alpha * FeedbackDefaults.disabledOpacity)
                        else -> linkColor
                    },
                    modifier = Modifier
                        .pointerInput(phrase) {
                            detectTapGestures(
                                onPress = {
                                    pressed = phrase
                                    tryAwaitRelease()
                                    pressed = null
                                },
                                onTap = { if (phrase != null) actions[phrase]?.invoke() else plainClick?.invoke() },
                            )
                        }
                        .clearAndSetSemantics {
                            when {
                                firstOfLink -> {
                                    role = Role.Button
                                    contentDescription = phrase!!
                                    onClick(label = null) { actions[phrase]?.invoke(); true }
                                }
                                index == 0 && readSentence -> contentDescription = text
                                else -> Unit
                            }
                        },
                )
                if (piece.trailing.isNotEmpty()) {
                    Text(
                        text = piece.trailing,
                        style = style,
                        color = color,
                        modifier = Modifier.clearAndSetSemantics { },
                    )
                }
            }
        }
    }
}

/** A run of the sentence laid out as one piece; [link] is the phrase it belongs to. */
internal data class LinkPiece(val text: String, val link: String?, val trailing: String = "")

private const val ClosingPunctuation = "，。、；：？！）》」』】〉”’,.;:?!)]}"
private const val OpeningPunctuation = "（《「『【〈“‘([{"

/**
 * Splits [text] into pieces that may wrap between them: each CJK character alone, each
 * Latin word with its trailing spaces, punctuation kept on the side it belongs to, and
 * never across the edge of a link.
 */
internal fun linkPieces(text: String, phrases: Collection<String>): List<LinkPiece> {
    val owner = arrayOfNulls<String>(text.length)
    phrases.forEach { phrase ->
        val start = text.indexOf(phrase)
        if (phrase.isEmpty() || start < 0) return@forEach
        for (i in start until start + phrase.length) owner[i] = phrase
    }
    val pieces = mutableListOf<LinkPiece>()
    val current = StringBuilder()
    var currentLink: String? = null
    var glueNext = false

    fun flush() {
        if (current.isNotEmpty()) pieces += LinkPiece(current.toString(), currentLink)
        current.clear()
    }

    text.forEachIndexed { i, c ->
        val link = owner[i]
        val latin = c.code < 0x2E80 && !c.isWhitespace() && c !in ClosingPunctuation && c !in OpeningPunctuation
        val sameLink = link == currentLink
        val attachToPrevious = sameLink && current.isNotEmpty() && (
            c in ClosingPunctuation ||
                c.isWhitespace() ||
                glueNext ||
                (latin && current.last().let { it.code < 0x2E80 && !it.isWhitespace() && it !in ClosingPunctuation })
            )
        if (!attachToPrevious) {
            flush()
            currentLink = link
        }
        current.append(c)
        glueNext = c in OpeningPunctuation
    }
    flush()
    // Closing punctuation right after a link is not part of it, but must not start a line:
    // it rides on the link's piece, in the sentence's colour.
    val merged = mutableListOf<LinkPiece>()
    pieces.forEach { piece ->
        val previous = merged.lastOrNull()
        val lead = piece.text.takeWhile { it in ClosingPunctuation }
        if (previous != null && lead.isNotEmpty() && previous.link != piece.link && previous.trailing.isEmpty()) {
            merged[merged.lastIndex] = previous.copy(trailing = lead)
            val rest = piece.text.drop(lead.length)
            if (rest.isNotEmpty()) merged += piece.copy(text = rest)
        } else {
            merged += piece
        }
    }
    return merged
}
