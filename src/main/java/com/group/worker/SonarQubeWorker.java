package com.group.worker;

import com.google.gson.Gson;
import com.group.pojo.sonar.Analysis;
import org.buildobjects.process.ProcBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SonarQubeWorker {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String uriApi = "http://localhost:9000/api/measures/component_tree?component=moneytransfer45864560&metricKeys=sqale_index&qualifiers=FIL&ps=500&pageIndex=";

    private String sonarScannerDir;
    private String repoDir;

    public SonarQubeWorker(String sonarScannerDir, String repoDir) {
        this.sonarScannerDir = sonarScannerDir;
        this.repoDir = adjustPath(repoDir);
    }

    private String adjustPath(String repoDir) {
        return repoDir.replaceAll("\\\\", "\\\\\\\\");
    }

    private static void sendGET() throws IOException {
        URL obj = new URL(uriApi);
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
        preparePropertiesFile(commitHashId);
        new ProcBuilder("sonar-scanner.bat")
                .withWorkingDirectory(new File(this.sonarScannerDir)).withNoTimeout().run();
    }

    private void preparePropertiesFile(String commitHashId) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.sonarScannerDir + "\\sonar-project.properties", false));
            writer.append("sonar.projectKey=project");
            writer.append(commitHashId);
            writer.append("\n");
            writer.append("sonar.projectName=project");
            writer.append(commitHashId);
            writer.append("\n");
            writer.append("sonar.sources=");
            writer.append(this.repoDir);
            writer.append("\\\\src\n");
            writer.append("sonar.java.binaries=.");
            //writer.append(this.repoDir);
            writer.append("\n");
            writer.append("sonar.scm.disabled=true");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}