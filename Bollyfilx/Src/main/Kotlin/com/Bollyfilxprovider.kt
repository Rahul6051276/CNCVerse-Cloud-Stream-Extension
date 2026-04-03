package com.Bollyflix 

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class Bollyflix : MainAPI() {
    override var mainUrl: String = "https://bollyflix.com.mx" 
    override var name = "Bollyflix"
    override val hasMainPage = true
    override var lang = "hi"
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override val mainPage = mainPageOf(
        "" to "Latest Movies",
        "category/bollywood-movies/" to "Bollywood",
        "category/hollywood-movies/" to "Hollywood",
        "category/south-hindi-dubbed/" to "South Hindi",
        "category/web-series/" to "Web Series"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page <= 1) "$mainUrl/${request.data}" else "$mainUrl/${request.data}page/$page/"
        val document = app.get(url).document
        val items = document.select("div.post-content, article, div.box").mapNotNull { it.toSearchResult() }
        return newHomePageResponse(request.name, items)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val aTag = selectFirst("h2 a, h3 a") ?: return null
        val img = selectFirst("img") ?: return null
        val href = fixUrl(aTag.attr("href"))
        val title = aTag.text().trim().replace("Download ", "") 
        val posterUrl = fixUrlNull(img.attr("data-src") ?: img.attr("src"))
        
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("h1")?.text()?.trim() ?: "Bollyflix"
        val poster = document.selectFirst("div.post-content img, div.featured-thumbnail img")?.attr("src")
        val description = document.select("div.entry-content p").first()?.text()?.trim() ?: ""

        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = description
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        // ViKiNG FiLE और GDFlix के बटनों को ढूँढना
        document.select("a[href*='vikingfile'], a[href*='gdflix'], a[href*='link']").forEach { button ->
            val link = button.attr("href")
            loadExtractor(link, data, subtitleCallback, callback)
        }
        return true
    }
}
