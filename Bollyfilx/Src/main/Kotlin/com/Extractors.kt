package com.Bollyflix

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.INFER_TYPE
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.utils.getQualityFromName
import com.lagradost.cloudstream3.utils.loadExtractor
import com.lagradost.cloudstream3.utils.newExtractorLink
import org.json.JSONObject
import java.net.URI

class HubCloud : ExtractorApi() {
    override val name = "Hub-Cloud"
    override val mainUrl = "https://hubcloud.foo"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val document = app.get(url).document
        // डाउनलोड बटन ढूंढना
        val href = document.selectFirst("#download")?.attr("href") ?: url
        
        val res = app.get(href).document
        val quality = getIndexQuality(res.selectFirst("div.card-header")?.text())

        res.select("a.btn").forEach { element ->
            val link = element.attr("href")
            val label = element.text().lowercase()
            
            if (link.contains("http")) {
                callback(
                    newExtractorLink(
                        "Bollyflix Server",
                        "Bollyflix [HubCloud] $label",
                        link,
                        INFER_TYPE
                    ) { this.quality = quality }
                )
            }
        }
    }

    private fun getIndexQuality(str: String?): Int {
        return Regex("(\\d{3,4})[pP]").find(str ?: "")?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?: Qualities.P720.value
    }
}

class Gofile : ExtractorApi() {
    override val name = "Gofile"
    override val mainUrl = "https://gofile.io"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        // Gofile का लॉजिक आपकी पुरानी फाइल जैसा ही रहेगा
        // यह सीधे लिंक को एक्स्ट्रेक्ट करता है
        loadExtractor(url, referer, subtitleCallback, callback)
    }
}
