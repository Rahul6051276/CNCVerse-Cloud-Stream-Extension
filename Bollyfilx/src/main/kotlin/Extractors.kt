package com.Bollyflix

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.*

class GDFlix : ExtractorApi() {
    override val name = "GDFlix"
    override val mainUrl = "https://new16.gdflix.net"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit) {
        val doc = app.get(url).document
        doc.select("a.btn").forEach { 
            val link = it.attr("href")
            val text = it.text().lowercase()
            if (text.contains("instant") || text.contains("direct") || text.contains("gofile")) {
                loadExtractor(link, url, subtitleCallback, callback)
            }
        }
    }
}

class LinksMod : ExtractorApi() {
    override val name = "LinksMod"
    override val mainUrl = "https://linksmod.top"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit) {
        val doc = app.get(url).document
        doc.select("a[href]").forEach { 
            val href = it.attr("href")
            if (href.contains("gofile.io") || href.contains("vikingfile") || href.contains("megaup")) {
                loadExtractor(href, url, subtitleCallback, callback)
            }
        }
    }
}

class Gofile : ExtractorApi() {
    override val name = "Gofile"
    override val mainUrl = "https://gofile.io"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit) {
        callback(newExtractorLink(this.name, this.name, url, referer ?: "", Qualities.Unknown.value))
    }
}
