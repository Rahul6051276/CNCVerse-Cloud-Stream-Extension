import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.gradle.api.Project

// वर्जन 47 एकदम सही है
val version = 47

cloudstream {
    authors = listOf("Rahul6051276")
    description = "Bollyflix"
    language = "hi"
    status = 1 

    tvTypes = listOf(
        "Movie",
        "TvSeries",
        "Anime"
    )

    iconUrl = "https://raw.githubusercontent.com/Rahul6051276/TVVVV/refs/heads/main/Icons/Bollyfilx.png"
    isCrossPlatform = false
}

android {
    // यह लाइन डालना ज़रूरी है ताकि ऐप को पता चले कि फोल्डर कहाँ है
    namespace = "com.Bollyflix" 
    
    buildFeatures {
        buildConfig = true
    }
}
