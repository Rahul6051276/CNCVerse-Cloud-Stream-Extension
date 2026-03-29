package com.cncverse

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class BollyflixProvider : MainAPI() {
    override var name = "Bollyflix"
    override var mainUrl = "https://bollyflix.frl"
    override val hasMainPage = true
    override var lang = "hi"
    override val supportedTypes = setOf(TvType.Movie)

    override val mainPage = mainPageOf(
        "" to "Latest Movies"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get(mainUrl).document

        val home = document.select("article").map {
            val title = it.select("h2").text()
            val link = it.select("a").attr("href")
            val poster = it.select("img").attr("src")

            newMovieSearchResponse(title, link, TvType.Movie) {
                this.posterUrl = poster
            }
        }

        return newHomePageResponse(request.name, home)
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document

        val title = document.select("h1").text()
        val poster = document.select("img").attr("src")

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

        val links = document.select("a").mapNotNull {
            val href = it.attr("href")

            if (
                href.contains("download") ||
                href.contains("watch") ||
                href.contains("play") ||
                href.contains("server")
            ) {
                href
            } else null
        }

        for (link in links) {
            loadExtractor(link, data, subtitleCallback, callback)
        }

        return true
    }
}
