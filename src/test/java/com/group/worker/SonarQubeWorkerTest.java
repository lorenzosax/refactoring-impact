package com.group.worker;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.group.pojo.sonar.Analysis;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SonarQubeWorkerTest {

	/*@Test
	void startSonarService() throws IOException {

		Config conf = ConfigFactory.load();
		String repoDir = conf.getString("repo.dir");
		String sonarQubeServerBaseUrl = conf.getString("sonarqube.server.base-url");
		String sonarQubeScannerBinDir = conf.getString("sonarqube.scanner.bin-dir");

		SonarQubeWorker sonarQubeWorker = new SonarQubeWorker(sonarQubeServerBaseUrl, sonarQubeScannerBinDir, repoDir);
		sonarQubeWorker.executeScanning("TEST");

		assert true;
	}*/

	@Test
	void getAnalysisRequestTest() throws IOException {

		Config conf = ConfigFactory.load();
		String repoDir = conf.getString("repo.dir");
		String sonarQubeServerBaseUrl = conf.getString("sonarqube.server.base-url");
		String sonarQubeScannerBinDir = conf.getString("sonarqube.scanner.bin-dir");

		SonarQubeWorker sonarQubeWorker = new SonarQubeWorker(sonarQubeServerBaseUrl, sonarQubeScannerBinDir, repoDir);
		Analysis analysis =sonarQubeWorker.getAnalysisFor("9ad9f468c083840b8946732c8896d842a4d7a5a2");
		
		
		assert true;
	}
}