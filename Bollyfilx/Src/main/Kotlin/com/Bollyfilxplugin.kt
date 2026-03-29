package com.horis.cncverse

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.extractors.StreamTape
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import com.lagradost.cloudstream3.app

@CloudstreamPlugin
class BollyflixPlugin: Plugin() {
    override fun load(context: Context) {
        // पुराने प्रोवाइडर्स का रजिस्ट्रेशन
        NetflixMirrorStorage.init(context.applicationContext)
        DisneyPlusProvider.context = context
        NetflixMirrorProvider.context = context
        PrimeVideoMirrorProvider.context = context
        HotStarMirrorProvider.context = context

        // Bollyflix और उसके सभी Extractors का रजिस्ट्रेशन
        registerMainAPI(BollyflixProvider())
        
        // ये Extractors मूवी लिंक्स को 'डिकोड' करने में मदद करेंगे
        registerExtractorAPI(StreamTape())
        // भविष्य में आप यहाँ नए Extractors जोड़ सकते हैं
    }

    companion object {
        // यहाँ आप अपनी GitHub रिपॉजिटरी का लिंक डाल सकते हैं जहाँ से डोमेन अपडेट होगा
        private const val DOMAINS_URL = "https://raw.githubusercontent.com/Rahul6051276/domains/main/domains.json"
        var cachedDomains: Domains? = null

        suspend fun getDomains(forceRefresh: Boolean = false): Domains? {
            if (cachedDomains == null || forceRefresh) {
                try {
                    cachedDomains = app.get(DOMAINS_URL).parsedSafe<Domains>()
                } catch (e: Exception) {
                    return null
                }
            }
            return cachedDomains
        }

        data class Domains(
            @JsonProperty("bollyflix")
            val bollyflix: String,
        )
    }
}
