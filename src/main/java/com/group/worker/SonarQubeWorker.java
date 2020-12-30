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

import com.group.Utils;
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
	private String relativeSrcPath;
	private String project;

	/**
	 * Start SonarQube worker for scanning repository checkout to analyze
	 *
	 * @param  sonarQubeServerBaseUrl  SonarQube Server base url
	 * @param  sonarScannerDir absolute path of the directory that contains bins of SonarQube Scanner
	 * @param  repoDir absolute path of the directory that contains the repository to be analyzed
	 */
	public SonarQubeWorker(String sonarQubeServerBaseUrl, String sonarScannerDir, String repoDir, String relativeSrcPath) {
		this.baseUrl = sonarQubeServerBaseUrl;
		this.sonarScannerDir = sonarScannerDir;
		this.repoDir = repoDir;
		this.relativeSrcPath = relativeSrcPath;
		this.project = Utils.getProjectNameFromRepoDir(this.repoDir);
	}

	/**
	 * Start SonarQube worker for scanning repository checkout to analyze
	 *
	 * @param  commitHash  checkout commit to analyze
	 * @throws  IOException
	 * @return  a analysis object for specific commit
	 */
	public Analysis getAnalysisFor(String commitHash) throws IOException {
		// concat project name with relative hash
		String projectSonar = this.project.concat("_").concat(commitHash);

		return new Gson().fromJson(Objects.requireNonNull(httpGetRequest(projectSonar)).toString(), Analysis.class);

	}

	/**
	 * Building a GET request for get Sonar Scanner analysis from Sonar Server
	 *
	 * @param  projectSonar  project name
	 * @throws  IOException
	 * @return  a buffer that contains HTTP response
	 */
	private StringBuffer httpGetRequest(String projectSonar) throws IOException {
		String requestUrl = this.baseUrl + COMPONENT_TREE_API + projectSonar + PARAMETERS;
		logger.info("GET request: " + requestUrl);
		// URL for SonarQube server
		URL obj = new URL(requestUrl);
		// HTTP message composition
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		logger.info("GET Response Code : " + responseCode + " for " + projectSonar);

		if (responseCode == HttpURLConnection.HTTP_OK) { // success HTTP request
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
	/**
	 * Scanning a repository checkout throught Sonar Scanner
	 *
	 * @param  commitHash  checkout commit to analyze
	 */
	public void executeScanning(String commitHash) {
		logger.info("Run Sonar Scanner...");
		//variable path to the Sonar scanner depending on the operating system
		String sonarScannerScriptFilename = "sonar-scanner" + (Utils.isWindowsSystem ? ".bat" : "");
		generatePropertiesFile(commitHash);

			// throw command for Sonar scanner
		ProcBuilder procBuilder = new ProcBuilder(Utils.currentShell);
		procBuilder.withWorkingDirectory(new File(this.repoDir));
		if (Utils.isWindowsSystem) {
			procBuilder.withArg("/c");
		}
		procBuilder
				.withArg(Utils.preparePathOsBased(false, this.sonarScannerDir, sonarScannerScriptFilename))
				.withNoTimeout()
				.run();
		logger.info("Sonar Scanner Done!");
	}

	/**
	 * Scanning a repository checkout throught Sonar Scanner
	 *
	 * @param  analysis  analysis object that contains TD value for specified class
	 * @param  classPath class path for extract TD value for the specified class
	 * @return TD value for specified class
	 */
	public Integer extractTdFromComponent(Analysis analysis, String classPath) {
		if (analysis != null) {
			for (Component c : analysis.getComponents()) {
				if (Utils.getPackagesWithClassPath(c.getPath()).equals(classPath)) {
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

	/**
	 * Generate properties file in order to scanning a repository checkout
	 *
	 * @param  commitHash  checkout commit to analyze
	 */
	private void generatePropertiesFile(String commitHash) {

		try {
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(Utils.preparePathOsBased(false, this.repoDir, "sonar-project.properties"), false));
			// project key for sonar (name project + commitHash)
			writer.append("sonar.projectKey=");
			writer.append(this.project);
			writer.append("_");
			writer.append(commitHash);
			writer.append("\n");
			// project name for sonar (name project + commitHash)
			writer.append("sonar.projectName=");
			writer.append(this.project);
			writer.append("_");
			writer.append(commitHash);
			writer.append("\n");
			// project sources directory
			writer.append("sonar.sources=");
			writer.append(this.relativeSrcPath);
			writer.append("\n");
			// project binary directory
			writer.append("sonar.java.binaries=.");
			writer.append("\n");
			// disable SCM
			writer.append("sonar.scm.disabled=true");

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}