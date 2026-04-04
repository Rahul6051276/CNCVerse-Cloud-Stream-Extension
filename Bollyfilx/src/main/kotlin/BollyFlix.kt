package com.Bollyflix

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class BollyFlix : MainAPI() { 
    // यह आपकी मुख्य वेबसाइट का पता है
    override var mainUrl = "https://bollyflix.frl"
    override var name = "BollyFlix"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    // 1. सर्च फंक्शन: जब आप ऐप में मूवी सर्च करेंगे
    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/?s=$query"
        val document = app.get(url).document
        return document.select("article.article").mapNotNull {
            it.toSearchResult()
        }
    }

    // 2. होमपेज: यहाँ आपकी पसंद की सभी कैटेगरी हैं
    override suspend fun getMainPage(page: Int, request: HomePageRequest): HomePageResponse {
        val items = mutableListOf<HomePageList>()
        val categories = listOf(
            Pair("Bollywood Movies", "$mainUrl/movies/bollywood/"),
            Pair("Hollywood Movies", "$mainUrl/movies/hollywood/"),
            Pair("South Hindi Dubbed", "$mainUrl/movies/south-hindi-dubbed/"),
            Pair("Hindi Dubbed Movies", "$mainUrl/movies/hindi-dubbed-movies-480p-720p/"),
            Pair("Web Series", "$mainUrl/web-series/")
        )

        categories.map { (name, url) ->
            val pagedUrl = if (page <= 1) url else "${url}page/$page/"
            val doc = app.get(pagedUrl).document
            val res = doc.select("article.article").mapNotNull { it.toSearchResult() }
            items.add(HomePageList(name, res))
        }
        return HomePageResponse(items)
    }

    // 3. लोड फंक्शन: मूवी का नाम और पोस्टर दिखाने के लिए
    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("h1.title")?.text()?.trim() ?: ""
        val poster = document.selectFirst("div.post-single-content img")?.attr("src")
        
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
        }
    }

    // 4. वीडियो लिंक: डाउनलोड बटन (class="dl") से लिंक निकालने के लिए
    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        document.select("a.dl").map {
            val href = it.attr("href")
            loadExtractor(href, data, subtitleCallback, callback)
        }
        return true
    }

    // यह हिस्सा वेबसाइट के HTML को ऐप के समझने लायक बनाता है
    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h2.title a")?.text()?.trim() ?: return null
        val href = this.selectFirst("h2.title a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("img")?.attr("src")

        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }
}
