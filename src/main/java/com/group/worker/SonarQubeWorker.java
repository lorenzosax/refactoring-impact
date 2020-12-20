package com.group.worker;

import com.google.gson.Gson;
import com.group.pojo.sonar.Analysis;
import org.buildobjects.process.ProcBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SonarQubeWorker {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String COMPONENT_TREE_API = "/api/measures/component_tree?component=moneytransfer45864560&metricKeys=sqale_index&qualifiers=FIL&ps=500&pageIndex=";

    private String baseUrl;
    private String sonarScannerDir;
    private String repoDir;

    public SonarQubeWorker(String sonarQubeServerBaseUrl, String sonarScannerDir, String repoDir) {
        this.baseUrl = sonarQubeServerBaseUrl;
        this.sonarScannerDir = sonarScannerDir;
        this.repoDir = repoDir;
    }

    private void sendGET() throws IOException {
        URL obj = new URL(this.baseUrl + COMPONENT_TREE_API);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Analysis an = new Gson().fromJson(response.toString(), Analysis.class);

            System.out.println(an.toString());
        } else {
            System.out.println("GET request not worked");
        }

    }

    public void executeScanning(String commitHashId) {
        System.out.println("Run Sonar Scanner...");
        generatePropertiesFile(commitHashId);
        new ProcBuilder("cmd")
                .withWorkingDirectory(new File(this.repoDir))
                .withArg("/c")
                .withArg(this.sonarScannerDir + "\\sonar-scanner.bat")
                .withNoTimeout()
                .run();
        System.out.println("Sona Scanner Done!");
    }

    private void generatePropertiesFile(String commitHashId) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.repoDir + "\\sonar-project.properties", false));
            writer.append("sonar.projectKey=project_");
            writer.append(commitHashId);
            writer.append("\n");
            writer.append("sonar.projectName=project_");
            writer.append(commitHashId);
            writer.append("\n");
            writer.append("sonar.sources=src");
            writer.append("\n");
            writer.append("sonar.java.binaries=.");
            writer.append("\n");
            writer.append("sonar.scm.disabled=true");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}