package com.Bollyflix

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.INFER_TYPE
import com.lagradost.cloudstream3.utils.loadExtractor

// 1. वेब सीरीज वाला सर्वर (GDFlix - नीला/लाल बटन वाला पेज)
class GDFlix : ExtractorApi() {
    override val name = "GDFlix"
    override val mainUrl = "https://new16.gdflix.net" 
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String, 
        referer: String?, 
        subtitleCallback: (SubtitleFile) -> Unit, 
        callback: (ExtractorLink) -> Unit
    ) {
        val doc = app.get(url).document
        
        // पेज पर मौजूद सभी बटनों को स्कैन करना
        doc.select("a.btn").forEach { 
            val link = it.attr("href")
            val text = it.text().lowercase()
            
            // अगर बटन में काम के सर्वर का नाम है
            if (text.contains("instant") || text.contains("direct") || text.contains("gofile") || text.contains("pixeldrain")) {
                loadExtractor(link, url, subtitleCallback, callback)
            }
        }
    }
}

// 2. मूवीज वाला सर्वर (LinksMod - नीले लिंक्स की लिस्ट वाला पेज)
class LinksMod : ExtractorApi() {
    override val name = "LinksMod"
    override val mainUrl = "https://linksmod.top"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String, 
        referer: String?, 
        subtitleCallback: (SubtitleFile) -> Unit, 
        callback: (ExtractorLink) -> Unit
    ) {
        val doc = app.get(url).document
        
        // पेज पर मौजूद सभी नीले लिंक्स (<a>) को स्कैन करना
        doc.select("a[href]").forEach { 
            val href = it.attr("href")
            
            // मूवी डाउनलोड करने वाले प्रमुख सर्वर
            if (href.contains("gofile.io") || href.contains("vikingfile") || href.contains("megaup") || href.contains("1fichier")) {
                loadExtractor(href, url, subtitleCallback, callback)
            }
        }
    }
}

// 3. Gofile सर्वर (जो दोनों जगह इस्तेमाल होता है)
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
        // Gofile का लिंक सीधे प्ले करने के लिए
        callback(
            ExtractorLink(
                this.name,
                "Gofile High Speed",
                url,
                url,
                com.lagradost.cloudstream3.utils.Qualities.Unknown.value,
                true
            )
        )
    }
}
