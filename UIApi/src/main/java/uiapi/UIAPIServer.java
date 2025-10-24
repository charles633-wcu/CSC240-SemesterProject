package uiapi;



import java.nio.file.Files;
import java.nio.file.Paths;

private static String renderHtmlSummary(Map<String, Object> summary) throws IOException {
    String template = Files.readString(Paths.get("src/main/resources/templates/summary.html"));

    String date = (String) summary.getOrDefault("date", "N/A");
    Map<String, Object> tempData = (Map<String, Object>) summary.get("temperatureData");
    Object temp = (tempData != null) ? tempData.get("temperature") : "N/A";
    int total = ((Number) summary.getOrDefault("totalIncidents", 0)).intValue();

    String perpsBySex = mapToHtmlList((Map<String, Object>) summary.get("perpsBySex"));
    String vicsBySex = mapToHtmlList((Map<String, Object>) summary.get("vicsBySex"));
    String perpsByAge = mapToHtmlList((Map<String, Object>) summary.get("perpsByAgeRange"));
    String vicsByAge = mapToHtmlList((Map<String, Object>) summary.get("vicsByAgeRange"));

    return template
            .replace("{{date}}", date)
            .replace("{{total}}", String.valueOf(total))
            .replace("{{temp}}", String.valueOf(temp))
            .replace("{{perpsBySex}}", perpsBySex)
            .replace("{{vicsBySex}}", vicsBySex)
            .replace("{{perpsByAge}}", perpsByAge)
            .replace("{{vicsByAge}}", vicsByAge);
}
