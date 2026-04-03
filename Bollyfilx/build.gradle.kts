import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.gradle.api.Project

// वर्जन नंबर 45 कर दिया है ताकि नया अपडेट लोड हो
val version = 45

cloudstream {
    // यहाँ आपका नाम
    authors = listOf("Rahul6051276")

    description = "Bollyflix"
    language = "hi"

    /**
     * Status: 1 (Ok)
     * */
    status = 1 

    // Bollyflix के हिसाब से कैटेगरी
    tvTypes = listOf(
        "Movie",
        "TvSeries",
        "Anime"
    )

    // आपका दिया हुआ आइकॉन लिंक
    iconUrl = "https://raw.githubusercontent.com/Rahul6051276/TVVVV/refs/heads/main/Icons/Bollyfilx.png"
    
    // मोबाइल और टीवी दोनों के लिए
    isCrossPlatform = false
}

// यह हिस्सा आपके 'BuildConfig' वाले एरर को ठीक करेगा
android {
    buildFeatures {
        buildConfig = true
    }
}
