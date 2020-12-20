package com.group.worker;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SonarQubeWorkerTest {

    @Test
    void startSonarService() throws IOException {

        Config conf = ConfigFactory.load();
        String repoDir = conf.getString("repo.dir");
        String sonarQubeScannerBinDir = conf.getString("sonarqube.scanner.bin-dir");

        SonarQubeWorker sonarQubeWorker = new SonarQubeWorker(sonarQubeScannerBinDir,repoDir);
        sonarQubeWorker.executeScanning("1234");

        assert true;
    }
}