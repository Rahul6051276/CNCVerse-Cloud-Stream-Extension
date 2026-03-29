package com.horis.cncverse

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
open class CNCVersePlugin: Plugin() {
    override fun load(context: Context) {
        // Original Storage and Context Initialization
        NetflixMirrorStorage.init(context.applicationContext)
        DisneyPlusProvider.context = context
        NetflixMirrorProvider.context = context
        PrimeVideoMirrorProvider.context = context
        HotStarMirrorProvider.context = context
        
        // Registering only the Original Providers
        registerMainAPI(NetflixMirrorProvider())
        registerMainAPI(PrimeVideoMirrorProvider())
        registerMainAPI(HotStarMirrorProvider())
        registerMainAPI(DisneyPlusProvider())
    }
}
