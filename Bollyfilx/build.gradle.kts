import com.lagradost.cloudstream3.gradle.CloudstreamExtension

// 'v' छोटा होना चाहिए और '31' कोट्स में, यही नियम है
version = "31"

configure<CloudstreamExtension> {
    language = "hi"
    description = "Movies and Series upto 4K"
    authors = listOf("Rahul6051276")

    /**
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 1
    tvTypes = listOf(
        "TvSeries",
        "Movie",
        "AsianDrama",
        "Anime"
    )

    // आपकी अपनी रिपॉजिटरी का आइकॉन लिंक
    iconUrl = "https://raw.githubusercontent.com/Rahul6051276/CNCVerse-Cloud-Stream-Extension/master/Bollyfilx/icon.png"
}
