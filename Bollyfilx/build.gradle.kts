import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.gradle.api.Project

// इसे 48 कर देते हैं ताकि ऐप को नया अपडेट मिले
val versionNum = 48

cloudstream {
    // यहाँ 'version' लिखना ज़रूरी है, वरना वह -1 उठा लेता है
    version = versionNum 
    
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
    namespace = "com.Bollyflix" 
    
    buildFeatures {
        buildConfig = true
    }
}
