package com.horis.cncverse

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
open class CNCVersePlugin: Plugin() {
    override fun load(context: Context) {
        // यहाँ हमने सभी पुराने और नए Bollyflix को एक साथ जोड़ दिया है
        NetflixMirrorStorage.init(context.applicationContext)
        DisneyPlusProvider.context = context
        NetflixMirrorProvider.context = context
        PrimeVideoMirrorProvider.context = context
        HotStarMirrorProvider.context = context
        
        // 1. आपका नया Bollyflix
        registerMainAPI(BollyflixProvider())
        
        // 2. आपके पुराने प्रोवाइडर्स
        registerMainAPI(NetflixMirrorProvider())
        registerMainAPI(PrimeVideoMirrorProvider())
        registerMainAPI(HotStarMirrorProvider())
        registerMainAPI(DisneyPlusProvider())
    }
}
