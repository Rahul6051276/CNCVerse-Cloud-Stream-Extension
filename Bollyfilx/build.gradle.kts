import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import com.android.build.gradle.BaseExtension

// वर्जन और बेसिक जानकारी
version = "31"

// एंड्रॉइड सेटिंग्स (यहीं पर एरर था, जिसे मैंने ठीक कर दिया है)
configure<BaseExtension> {
    compileSdkVersion(34)

    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }

    // यही वह हिस्सा है जो आपके 'Build Failed' एरर को ठीक करेगा
    buildFeatures.buildConfig = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// क्लाउडस्ट्रीम प्लगइन की सेटिंग्स
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

    // आपका अपना आइकॉन लिंक
    iconUrl = "https://raw.githubusercontent.com/Rahul6051276/CNCVerse-Cloud-Stream-Extension/master/Bollyfilx/icon.png"
}
