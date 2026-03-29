package com.horis.cncverse

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import com.lagradost.cloudstream3.utils.Qualities

open class BollyflixProvider : MainAPI() {
    override var mainUrl = "https://bollyflix.frl"
    override var name = "Bollyflix Custom"
    override val hasMainPage = true
    override var lang = "hi"
    override val hasQuickSearch = true

    override val mainPage = mainPageOf(
        "movies/bollywood/" to "Bollywood",
        "movies/south-hindi-dubbed/" to "South Hindi Dubbed",
        "movies/dual-audio-movies/" to "Dual Audio",
        "movies/hindi-dubbed-movies-480p-720p/" to "Hindi Dubbed",
        "movies/hollywood/" to "Hollywood",
        "web-series/netflix/" to "Netflix",
        "web-series/amazon-prime-video/" to "Amazon Prime",
        "web-series/hotstar/" to "Hotstar",
        "web-series/disney/" to "Disney Plus",
        "web-series/korean-drama/" to "Korean Drama"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page <= 1) "$mainUrl/${request.data}" else "$mainUrl/${request.data}page/$page/"
        val document = app.get(url).document
        val home = document.select("article").mapNotNull {
            val title = it.selectFirst("h2.entry-title")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val posterUrl = it.selectFirst("img")?.attr("src") ?: ""
            newMovieSearchResponse(title, href, TvType.Movie) { this.posterUrl = posterUrl }
        }
        return newHomePageResponse(request.name, home)
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        val title = document.selectFirst("h1.entry-title")?.text() ?: return null
        val poster = document.selectFirst("img.attachment-sociallyviral-featuredbig")?.attr("src")
        
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        
        document.select("a[href*='gdflix'], a[href*='busycdn'], a[href*='pixeldrain'], a[href*='mkv'], a[href*='direct']").forEach { 
            val link = it.attr("href")

            if (link.contains(".mkv") || link.contains(".mp4") || link.contains("direct")) {
                callback.invoke(
                    ExtractorLink(
                        this.name,
                        "Direct Server [MGT]",
                        link,
                        "",
                        Qualities.P720.value,
                        false
                    )
                )
            } else {
                loadExtractor(link, data, subtitleCallback, callback)
            }
        }
        return true
    }
}
