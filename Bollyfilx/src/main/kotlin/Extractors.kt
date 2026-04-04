package com.Bollyflix

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

// 1. वेब सीरीज के लिए (GDFlix - जो आपने स्क्रीनशॉट 1 में दिखाया)
class GDFlix : ExtractorApi() {
    override val name = "GDFlix"
    override val mainUrl = "https://new16.gdflix.net" 
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit) {
        val doc = app.get(url).document
        val title = doc.selectFirst("h5, .card-title")?.text() ?: "Video"
        
        doc.select("a.btn").forEach { 
            val link = it.attr("href")
            val text = it.text().lowercase()
            if (text.contains("instant") || text.contains("direct") || text.contains("gofile") || text.contains("pixeldrain")) {
                loadExtractor(link, url, subtitleCallback, callback)
            }
        }
    }
}

// 2. मूवीज के लिए (LinksMod - जो आपने स्क्रीनशॉट 2 में दिखाया)
class LinksMod : ExtractorApi() {
    override val name = "LinksMod"
    override val mainUrl = "https://linksmod.top"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit) {
        val doc = app.get(url).document
        // पेज पर मौजूद सभी नीले लिंक्स को स्कैन करना
        doc.select("a[href]").forEach { 
            val href = it.attr("href")
            if (href.contains("gofile.io") || href.contains("vikingfile") || href.contains("megaup")) {
                loadExtractor(href, url, subtitleCallback, callback)
            }
        }
    }
}

// Gofile Extractor (तेज़ स्पीड के लिए)
class Gofile : ExtractorApi() {
    override val name = "Gofile"
    override val mainUrl = "https://gofile.io"
    override suspend fun getUrl(url: String, referer: String?, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit) {
        // Gofile का बेसिक लॉजिक यहाँ रहेगा
        callback(newExtractorLink("Gofile", "High Speed", url, INFER_TYPE))
    }
}
