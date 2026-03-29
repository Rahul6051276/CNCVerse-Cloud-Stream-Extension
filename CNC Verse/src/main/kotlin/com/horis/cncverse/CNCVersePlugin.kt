package com.horis.cncverse

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import com.horis.cncverse.BollyflixProvider

@CloudstreamPlugin
open class CNCVersePlugin: Plugin() {
    override fun load(context: Context) {
        NetflixMirrorStorage.init(context.applicationContext)
        DisneyPlusProvider.context = context
        NetflixMirrorProvider.context = context
        PrimeVideoMirrorProvider.context = context
        HotStarMirrorProvider.context = context
        
        registerMainAPI(BollyflixProvider())
        registerMainAPI(NetflixMirrorProvider())
        registerMainAPI(PrimeVideoMirrorProvider())
        registerMainAPI(HotStarMirrorProvider())
        registerMainAPI(DisneyPlusProvider())
    }
}
