package com.Bollyflix

import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin

@CloudstreamPlugin
class BollyflixProvider: BasePlugin() {
    override fun load() {
        registerMainAPI(BollyFlix())
        registerExtractorAPI(GDFlix())
        registerExtractorAPI(LinksMod())
        registerExtractorAPI(Gofile())
    }
} 
