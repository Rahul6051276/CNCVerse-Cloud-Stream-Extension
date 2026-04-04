package com.Bollyflix

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class BollyFlix : MainAPI() { 
    override var mainUrl = "https://bollyflix.frl"
    override var name = "BollyFlix"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/?s=$query"
        val document = app.get(url).document
        return document.select("article.article").mapNotNull { it.toSearchResult() }
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val items = mutableListOf<HomePageList>()
        val categories = listOf(
            Pair("Bollywood", "$mainUrl/movies/bollywood/"),
            Pair("Hollywood", "$mainUrl/movies/hollywood/"),
            Pair("Web Series", "$mainUrl/web-series/")
        )
        categories.forEach { (name, url) ->
            val pagedUrl = if (page <= 1) url else "${url}page/$page/"
            val res = app.get(pagedUrl).document.select("article.article").mapNotNull { it.toSearchResult() }
            items.add(HomePageList(name, res))
        }
        return newHomePageResponse(items)
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1.title")?.text()?.trim() ?: ""
        val poster = doc.selectFirst("div.post-single-content img")?.attr("src")
        return newMovieLoadResponse(title, url, TvType.Movie, url) { this.posterUrl = poster }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val doc = app.get(data).document
        doc.select("a.dl").map {
            val link = it.attr("href")
            loadExtractor(link, data, subtitleCallback, callback)
        }
        return true
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h2.title a")?.text()?.trim() ?: return null
        val href = this.selectFirst("h2.title a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("img")?.attr("src")
        return newMovieSearchResponse(title, href, TvType.Movie) { this.posterUrl = posterUrl }
    }
}
