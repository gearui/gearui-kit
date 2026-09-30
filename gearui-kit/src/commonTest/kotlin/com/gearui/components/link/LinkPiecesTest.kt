package com.gearui.components.link

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LinkPiecesTest {
    private val consent = "我已阅读并同意《用户协议》和《隐私政策》，并授权登录"
    private val links = listOf("《用户协议》", "《隐私政策》")

    @Test
    fun chineseSplitsPerCharacterAndLinksPerCharacterToo() {
        val pieces = linkPieces("我已阅读", emptyList())
        assertEquals(listOf("我", "已", "阅", "读"), pieces.map { it.text })
    }

    @Test
    fun everyCharacterOfALinkBelongsToIt() {
        val pieces = linkPieces(consent, links)
        val agreement = pieces.filter { it.link == "《用户协议》" }.joinToString("") { it.text }
        assertEquals("《用户协议》", agreement)
    }

    @Test
    fun openingPunctuationNeverEndsAPiece() {
        val pieces = linkPieces(consent, links)
        assertTrue(pieces.none { it.text.last() == '《' })
    }

    @Test
    fun closingPunctuationNeverStartsAPiece() {
        val pieces = linkPieces(consent, links)
        assertTrue(pieces.none { it.text.first() in "，》。" })
    }

    @Test
    fun aCommaAfterALinkRidesOnItInTheSentenceColour() {
        val pieces = linkPieces(consent, links)
        val last = pieces.last { it.link == "《隐私政策》" }
        assertEquals("，", last.trailing)
        assertTrue(pieces.none { it.link == null && it.text.startsWith("，") })
    }

    @Test
    fun latinWordsStayWhole() {
        val pieces = linkPieces("I agree to the Terms of Service.", listOf("Terms of Service"))
        assertEquals(listOf("I ", "agree ", "to ", "the ", "Terms of Service"), pieces.map { it.text })
        assertEquals(".", pieces.last().trailing)
    }

    @Test
    fun theWholeSentenceSurvives() {
        val pieces = linkPieces(consent, links)
        assertEquals(consent, pieces.joinToString("") { it.text + it.trailing })
    }

    @Test
    fun aLinkIsOnePieceSoItsButtonCoversThePhrase() {
        val pieces = linkPieces(consent, links)
        assertEquals(listOf("《用户协议》", "《隐私政策》"), pieces.filter { it.link != null }.map { it.text })
    }

    @Test
    fun aVeryLongLinkMayStillWrap() {
        val long = "《" + "很".repeat(20) + "》"
        val pieces = linkPieces("同意$long", listOf(long))
        assertTrue(pieces.count { it.link == long } > 1)
    }
}
