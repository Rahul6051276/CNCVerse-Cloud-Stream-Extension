package com.Bollyfilx

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class BollyflixPlugin: Plugin() {
    override fun load(context: Context) {
        // यहाँ 'Bollyflix()' वही नाम है जो आपने अपनी Provider फाइल में रखा है
        registerMainAPI(Bollyflix()) 
    }
}
