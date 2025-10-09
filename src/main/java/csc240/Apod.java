package csc240;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



//https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY

@JsonIgnoreProperties(ignoreUnknown = true)
public class Apod {
    public String date;
    public String title;
    public String explanation;
    public String url;
    public String copyright;

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();


    

    public static Apod fetchApod() throws Exception {
        String apodUrl = "https://api.nasa.gov/planetary/apod?api_key=t8iXZdJAmJYb2NBfMmMHIetpSIAGkTWZvWkqgeoC";
        Request request = new Request.Builder().url(apodUrl).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                {throw new Exception("Failed: " + response);}
            return mapper.readValue(response.body().string(), Apod.class);
        }
    }
}


