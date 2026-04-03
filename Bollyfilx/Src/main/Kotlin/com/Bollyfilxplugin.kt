package com.Bollyflix  // यह नाम Bollyflix.kt वाले से मैच होना चाहिए

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class BollyflixPlugin: Plugin() {
    override fun load(context: Context) {
        // यहाँ हम Bollyflix वाली मशीन को ऐप में चालू कर रहे हैं
        registerMainAPI(Bollyflix()) 
    }
}
