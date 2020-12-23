package com.group.worker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.StringTokenizer;

import com.group.pojo.sonar.Component;
import com.group.pojo.sonar.Measure;
import org.apache.log4j.Logger;
import org.buildobjects.process.ProcBuilder;

import com.google.gson.Gson;
import com.group.pojo.sonar.Analysis;

public class SonarQubeWorker {

	private static final Logger logger = Logger.getLogger(SonarQubeWorker.class);

	private static final String USER_AGENT = "Mozilla/5.0";

	private static final String COMPONENT_TREE_API = "/api/measures/component_tree?component=";
	private static final String PARAMETERS = "&metricKeys=sqale_index&qualifiers=FIL&ps=500&pageIndex=";

	private String baseUrl;
	private String sonarScannerDir;
	private String repoDir;
	private String project;

	public SonarQubeWorker(String sonarQubeServerBaseUrl, String sonarScannerDir, String repoDir) {
		this.baseUrl = sonarQubeServerBaseUrl;
		this.sonarScannerDir = sonarScannerDir;
		this.repoDir = repoDir;
		StringTokenizer stringTokenizer = new StringTokenizer(repoDir, "\\\\");
		while (stringTokenizer.hasMoreElements())
			this.project = stringTokenizer.nextToken();
	}

	public Analysis getAnalysisFor(String commitHash) throws IOException {

		String projectSonar = this.project.concat("_").concat(commitHash);

		return new Gson().fromJson(Objects.requireNonNull(httpGetRequest(projectSonar)).toString(), Analysis.class);

	}

	private StringBuffer httpGetRequest(String projectSonar) throws IOException {
		URL obj = new URL(this.baseUrl + COMPONENT_TREE_API + projectSonar + PARAMETERS);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		logger.info("GET Response Code :: " + responseCode + " for " + projectSonar);

		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response;
		} else {
			logger.info("GET request not worked for " + projectSonar);
		}
		return null;
	}

	public void executeScanning(String commitHashId) {
		logger.info("Run Sonar Scanner...");
		generatePropertiesFile(commitHashId);
		new ProcBuilder("cmd")
				.withWorkingDirectory(new File(this.repoDir))
				.withArg("/c")
				.withArg(this.sonarScannerDir + "\\sonar-scanner.bat")
				.withNoTimeout()
				.run();
		logger.info("Sona Scanner Done!");
	}

	public Integer extractTdFromComponent(Analysis analysis, String classPath) {
		if (analysis != null) {
			for (Component c : analysis.getComponents()) {
				if (c.getPath().equals(classPath)) {
					for (Measure m : c.getMeasures()) {
						if (m.getMetric().equals("sqale_index")) {
							return Integer.parseInt(m.getValue());
						}
					}
				}
			}
		}
		return 0;
	}

	private void generatePropertiesFile(String commitHashId) {

		try {
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(this.repoDir + "\\sonar-project.properties", false));
			writer.append("sonar.projectKey=");
			writer.append(this.project);
			writer.append("_");
			writer.append(commitHashId);
			writer.append("\n");
			writer.append("sonar.projectName=");
			writer.append(this.project);
			writer.append("_");
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