package csc240;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Represents a Wikipedia summary for a given object.
 * API: https://en.wikipedia.org/api/rest_v1/#/Page%20content/get_page_summary__title_
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WikiSummary {
    public String title;
    public String extract; // Summary text
    //public String content_urls; // URL info may be nested

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Fetches the Wikipedia summary for a given object name.
     * @param objectName Name of the celestial object or term
     * @return WikiSummary object
     * @throws Exception if API call or JSON parsing fails
     */
    public static WikiSummary fetchWikiSummary(String objectName) throws Exception {
        // Encode object name for URL (replace spaces with underscores)
        String encodedName = objectName.replace(" ", "_");
        String url = "https://en.wikipedia.org/api/rest_v1/page/summary/" + encodedName;

        Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "APOD-Wiki-App/1.0 cwang633@wcupa.edu")
                    .header("Accept", "application/json")
                    .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) 
                {throw new Exception("Failed Wiki request: " + response);}
            return mapper.readValue(response.body().string(), WikiSummary.class);
        }

        
    }
}
