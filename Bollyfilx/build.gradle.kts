import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.gradle.api.Project

// इसे 51 कर देते हैं ताकि ऐप को एकदम ताज़ा अपडेट मिले
val versionNum = 51

cloudstream {
    // यहाँ वर्जन अपडेट करना सबसे ज़रूरी है
    version = versionNum 
    
    authors = listOf("Rahul6051276")
    description = "Bollyflix Working Plugin v-51"
    language = "hi"
    status = 1 

    tvTypes = listOf(
        "Movie",
        "TvSeries"
    )

    // आपका आइकॉन लिंक एकदम सही है
    iconUrl = "https://raw.githubusercontent.com/Rahul6051276/TVVVV/refs/heads/main/Icons/Bollyfilx.png"
    isCrossPlatform = false
}

android {
    namespace = "com.Bollyflix" 
    
    buildFeatures {
        buildConfig = true
    }
}
