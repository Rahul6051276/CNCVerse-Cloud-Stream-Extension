// use an integer for version numbers
version = 1 // पहली बार बना रहे हैं तो 1 से शुरू करें

cloudstream {
    // यहाँ आप अपना नाम (Rahul6051276) लिख सकते हैं
    authors = listOf(" राहुल6051276")

    /**
    * Status: 1 (Ok)
    * 0: Down
    * 1: Ok
    * 2: Slow
    * 3: Beta only
    * */
    status = 1 

    tvTypes = listOf(
        "Movie",
        "TvSeries"
    )
    
    // भाषा 'hi' (Hindi) ही रहने दें
    language = "hi"

    // Bollyflix का प्रोफेशनल आइकन
    iconUrl = "https://raw.githubusercontent.com/Rahul6051276/icons/main/bollyflix.png"

    isCrossPlatform = false
}
