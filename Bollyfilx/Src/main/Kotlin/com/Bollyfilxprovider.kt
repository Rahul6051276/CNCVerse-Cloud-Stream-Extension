package com.Bollyfilx // आपके फोल्डर के नाम के अनुसार

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.network.CloudflareKiller
import com.lagradost.cloudstream3.base64Decode
import org.json.JSONObject
import kotlinx.coroutines.runBlocking

class BollyflixProvider : MainAPI() {
    // यहाँ अपनी वेबसाइट का वर्किंग डोमेन डालें
    override var mainUrl = "https://bollyflix.frl" 
    override var name = "BollyFlix"
    override val hasMainPage = true
    override var lang = "hi"
    private val cinemeta_url = "https://aiometadata.elfhosted.com/stremio/9197a4a9-2f5b-4911-845e-8704c520bdf7/meta"
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries,
        TvType.AsianDrama,
        TvType.Anime
    )

    init {
        runBlocking {
            basemainUrl?.let {
                mainUrl = it
            }
        }
    }

    companion object {
        val basemainUrl: String? by lazy {
            runBlocking {
                try {
                    // यहाँ 'Saurabh' को हटाकर 'Rahul6051276' कर दिया गया है
                    // सुनिश्चित करें कि आपकी रिपॉजिटरी में 'urls.json' फाइल मौजूद हो
                    val response = app.get("https://raw.githubusercontent.com/Rahul6051276/CNCVerse-Cloud-Stream-Extension/master/Bollyflix/urls.json")
                    val jsonObject = JSONObject(response.text)
                    jsonObject.optString("bollyflix")
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private val cfKiller by lazy { CloudflareKiller() }

    override val mainPage = mainPageOf(
        "" to "Home",
        "/movies/bollywood/" to "Bollywood Movies",
        "/movies/hollywood/" to "Hollywood Movies",
        "/anime/" to "Anime"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val fullUrl = if (page == 1) "${mainUrl}${request.data}" else "${mainUrl}${request.data}page/$page/"
        val document = app.get(fullUrl, interceptor = cfKiller).document
        val home = document.select("div.post-cards > article").mapNotNull {
            it.toSearchResult()
        }
        return newHomePageResponse(request.name, home)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.select("a").attr("title").replace("Download ", "")
        val href = this.select("a").attr("href")
        val posterUrl = this.select("img").attr("src")
    
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String, page: Int): List<SearchResponse> {
        val document = app.get("$mainUrl/search/$query/page/$page/", interceptor = cfKiller).document
        return document.select("div.post-cards > article").mapNotNull { it.toSearchResult() }
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url, interceptor = cfKiller).document
        val title = document.selectFirst("h1.entry-title")?.text()?.replace("Download ", "") ?: ""
        val posterUrl = document.selectFirst("meta[property=og:image]")?.attr("content")
        val plot = document.selectFirst("div.entry-content p")?.text()

        return if (url.contains("web-series")) {
            newTvSeriesLoadResponse(title, url, TvType.TvSeries, emptyList()) {
                this.posterUrl = posterUrl
                this.plot = plot
            }
        } else {
            newMovieLoadResponse(title, url, TvType.Movie, url) {
                this.posterUrl = posterUrl
                this.plot = plot
            }
        }
    }
}
