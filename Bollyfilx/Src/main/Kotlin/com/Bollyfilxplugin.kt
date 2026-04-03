package com.Bollyflix 

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class BollyflixPlugin: Plugin() {
    override fun load(context: Context) {
        // API और Extractor दोनों को एक साथ रजिस्टर करना
        registerMainAPI(Bollyflix()) 
        registerExtractorAPI(VikingFile())
    }
}
