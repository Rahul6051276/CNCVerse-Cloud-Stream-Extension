package com.horis.cncverse

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.base64Decode
import com.lagradost.cloudstream3.extractors.PixelDrain
import com.lagradost.cloudstream3.newSubtitleFile
import com.lagradost.cloudstream3.utils.*
import java.net.URI
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

// यह क्लास HubCloud जैसे जटिल सर्वर से लिंक निकालेगी
open class HubCloud : ExtractorApi() {
    override val name = "Hub-Cloud"
    override var mainUrl = "https://hubcloud.club" // इसे आप अपनी जरूरत अनुसार बदल सकते हैं
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val doc = app.get(url).document
        // यहाँ से वह सभी 'Download' और 'Stream' बटन को स्कैन करेगा
        doc.select("a.btn").forEach { element ->
            val link = element.attr("href")
            val label = element.text().lowercase()
            
            if (link.contains("http")) {
                loadExtractor(link, subtitleCallback, callback)
            }
        }
    }
}

// AES Decryption के लिए मददगार क्लास
object AesHelper {
    private const val TRANSFORMATION = "AES/CBC/PKCS5PADDING"

    fun decryptAES(inputHex: String, key: String, iv: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        val ivSpec = IvParameterSpec(iv.toByteArray(Charsets.UTF_8))
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        return String(cipher.doFinal(inputHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()))
    }
}
