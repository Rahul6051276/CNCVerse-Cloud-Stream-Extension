package com.cncverse

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class CNCVersePlugin : Plugin() {
    override fun load(context: Context) {

        CineTvProvider.context = context
        registerMainAPI(CineTvProvider())

        registerMainAPI(BollyflixProvider())
    }
}
