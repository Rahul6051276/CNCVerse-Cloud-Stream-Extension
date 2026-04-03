package com.Bollyfilx

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import com.lagradost.cloudstream3.base64Decode
import com.lagradost.cloudstream3.LoadResponse.Companion.addImdbUrl
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.network.CloudflareKiller
import com.google.gson.Gson
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class BollyflixProvider : MainAPI() {
    override var mainUrl = "https://bollyflix.frl"
    override var name = "BollyFlix"
    override val hasMainPage = true
    override var lang = "hi"
    val cinemeta_url = "https://aiometadata.elfhosted.com/stremio/9197a4a9-2f5b-4911-845e-8704c520bdf7/meta"
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
                    val response = app.get("https://raw.githubusercontent.com/SaurabhKaperwan/Utils/refs/heads/main/urls.json")
                    val json = response.text
                    val jsonObject = JSONObject(json)
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

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val document = if(page == 1) {
            app.get("${mainUrl}${request.data}", interceptor = cfKiller).document
        }
        else {
            app.get(request.data + "page/" + page, interceptor = cfKiller).document
        }
        val home = document.select("div.post-cards > article").mapNotNull {
            it.toSearchResult()
        }
        return newHomePageResponse(request.name, home)
    }

    private suspend fun bypass(id: String): String {
        val url = "https://web.sidexfee.com/?id=$id"
        val document = app.get(url).text
        val encodeUrl = Regex("""link":"([^"]+)""").find(document) ?. groupValues ?. get(1) ?: ""
        return base64Decode(encodeUrl.replace("\\/", "/"))
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.select("a").attr("title").replace("Download ", "")
        val href = this.select("a").attr("href")
        val posterUrl = this.select("img").attr("src")
    
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String, page: Int): SearchResponseList? {
        val document = app.get("$mainUrl/search/$query/page/$page/", interceptor = cfKiller).document
        val results = document.select("div.post-cards > article").mapNotNull { it.toSearchResult() }
        val hasNext = if(results.isEmpty()) false else true
        return newSearchResponseList(results, hasNext)
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url, interceptor = cfKiller).document
        var title = document.selectFirst("title")?.text()?.replace("Download ", "").toString()
        var posterUrl = document.selectFirst("meta[property=og:image]")?.attr("content").toString()
        var description = document.selectFirst("span#summary")?.text().toString()
        val tvtype = if(title.contains("Series") || url.contains("web-series")) {
            "series"
        }
        else {
            "movie"
        }
        val imdbUrl = document.selectFirst("div.imdb_left > a")?.attr("href")
        val responseData = if (!imdbUrl.isNullOrEmpty()) {
            try {
                val imdbId = imdbUrl.substringAfter("title/").substringBefore("/")
                app.get("$cinemeta_url/$tvtype/$imdbId.json").parsedSafe<ResponseData>()

            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        var cast: List<String> = emptyList()
        var genre: List<String> = emptyList()
