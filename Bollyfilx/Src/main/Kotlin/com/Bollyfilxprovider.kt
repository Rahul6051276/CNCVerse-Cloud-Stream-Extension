package com.horis.cncverse

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import com.lagradost.cloudstream3.utils.Qualities
import org.jsoup.nodes.Element

class BollyflixProvider : MainAPI() {
    override var mainUrl = "https://bollyflix.frl"
    override var name = "Bollyflix Professional"
    override val hasMainPage = true
    override var lang = "hi"
    override val hasQuickSearch = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override val mainPage = mainPageOf(
        "category/bollywood-movies/" to "Bollywood",
        "category/hollywood-movies/" to "Hollywood",
        "category/dual-audio-hindi-dubbed-movies/" to "Dual Audio",
        "category/web-series/" to "Web Series"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page <= 1) "$mainUrl/${request.data}" else "$mainUrl/${request.data}page/$page/"
        val doc = app.get(url).document
        val home = doc.select("article").mapNotNull { toResult(it) }
        return newHomePageResponse(request.name, home, true)
    }

    private fun toResult(post: Element): SearchResponse? {
        val title = post.selectFirst("h2.entry-title")?.text() ?: return null
        val url = post.selectFirst("a")?.attr("href") ?: return null
        val posterUrl = post.selectFirst("img")?.attr("src") ?: ""
        return newMovieSearchResponse(title, url, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1.entry-title")?.text() ?: return null
        val poster = doc.selectFirst("img.attachment-sociallyviral-featuredbig")?.attr("src")
        val plot = doc.selectFirst(".entry-content p")?.text()
        
        // HDhub4u style TV type detection
        val tvType = if (url.contains("web-series")) TvType.TvSeries else TvType.Movie

        return newMovieLoadResponse(title, url, tvType, url) {
            this.posterUrl = poster
            this.plot = plot
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.get(data).document
        // Advanced link detection for Direct MGT, BusyCDN, and GDFlix
        doc.select("a[href*='gdflix'], a[href*='busycdn'], a[href*='mkv'], a[href*='direct']").forEach { 
            val link = it.attr("href")
            if (link.contains(".mkv") || link.contains(".mp4") || link.contains("direct")) {
                callback.invoke(
                    ExtractorLink(this.name, "Direct Server [MGT]", link, "", Qualities.P720.value, false)
                )
            } else {
                loadExtractor(link, data, subtitleCallback, callback)
            }
        }
        return true
    }
}
