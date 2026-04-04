package com.Bollyflix

import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin

@CloudstreamPlugin
class BollyflixProvider: BasePlugin() {
    override fun load() {
        // यह आपके इंजन को रजिस्टर करता है
        registerMainAPI(BollyFlix())
        
        // वीडियो चलाने के लिए जरूरी Extractors
        registerExtractorAPI(Filesdl())
        registerExtractorAPI(GDFlix())
        registerExtractorAPI(HubCloud())
        registerExtractorAPI(Gofile())
        registerExtractorAPI(SharedDrive())
    }
}
