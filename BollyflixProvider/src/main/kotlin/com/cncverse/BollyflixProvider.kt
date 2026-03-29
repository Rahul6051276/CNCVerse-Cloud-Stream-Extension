package com.cncverse

import com.lagradost.cloudstream3.*

class BollyflixProvider : MainAPI() { 
    override var mainUrl = "https://bollyflix.frl"
    override var name = "Bollyflix Custom"
    override val hasMainPage = true
    override var lang = "hi"

    override val mainPage = mainPageOf(
        "category/bollywood-movies/" to "🎬 Bollywood",
        "category/dual-audio-hindi-dubbed-movies/" to "🌐 Dual Audio",
        "category/web-series/" to "📺 Web Series"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page <= 1) "$mainUrl/${request.data}" else "$mainUrl/${request.data}page/$page/"
        val document = app.get(url).document
        val home = document.select("article").mapNotNull {
            val title = it.selectFirst("h2.entry-title")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("src") ?: ""
            newMovieSearchResponse(title, href, TvType.Movie) { this.posterUrl = poster }
        }
        return newHomePageResponse(request.name, home)
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        val title = document.selectFirst("h1.entry-title")?.text() ?: return null
        val poster = document.selectFirst("img.attachment-sociallyviral-featuredbig")?.attr("src")
        return newMovieLoadResponse(title, url, TvType.Movie, url) { this.posterUrl = poster }
    }
}
