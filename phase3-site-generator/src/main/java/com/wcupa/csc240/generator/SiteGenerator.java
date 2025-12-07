package com.wcupa.csc240.generator;

import java.io.IOException;
import java.nio.file.*;

public class SiteGenerator {

    // TO run: CMD,  
    //cd into root: CSC240-SemesterProject 
    // mvn -pl phase3-site-generator -Dexec.mainClass=com.wcupa.csc240.generator.SiteGenerator org.codehaus.mojo:exec-maven-plugin:3.1.0:java
    private static final Path OUTPUT_DIR =
            Paths.get("phase3-site-generator/target/site/").toAbsolutePath();

    private static final Path TEMPLATE_DIR =
            Paths.get("phase3-site-generator/src/main/resources/");

    public static void main(String[] args) {
        new SiteGenerator().run();
    }

    public void run() {
        try {
            Files.createDirectories(OUTPUT_DIR);

            copyTemplate("index_template.html", "index.html");
            copyTemplate("summary_template.html", "summary.html");
            copyTemplate("combined_template.html", "combined.html");

            copyRawFile("chart.js");
            copyRawFile("summary.js");
            copyRawFile("combined.js");
            copyRawFile("style.css");

            System.out.println("Site generated → http://localhost/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyTemplate(String srcFile, String destFile) throws IOException {
        String html = Files.readString(TEMPLATE_DIR.resolve(srcFile));
        Files.writeString(OUTPUT_DIR.resolve(destFile), html);
        System.out.println("Created " + destFile);
    }

    private void copyRawFile(String file) throws IOException {
        Files.copy(
                TEMPLATE_DIR.resolve(file),
                OUTPUT_DIR.resolve(file),
                StandardCopyOption.REPLACE_EXISTING
        );
        System.out.println("Copied " + file);
    }
}
