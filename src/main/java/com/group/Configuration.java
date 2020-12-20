package com.group;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

public class Configuration {
    private static final Configuration instance = new Configuration();

    private String repoDir;
    private boolean refactoringMinerDetectBetweenCommits;
    private String refactoringMinerStartCommitId;
    private String refactoringMinerEndCommitId;
    private boolean writeRefactoringMinerOutputOnFile;
    private String designiteDir;
    private String sonarQubeServerBaseUrl;
    private String sonarQubeScannerBinDir;
    private String resultsDir;

    private boolean isWindowsSystem;

    private Configuration() {
        Config conf = ConfigFactory.load();
        repoDir = conf.getString("repo.dir");
        refactoringMinerDetectBetweenCommits = conf.getBoolean("refactoring-miner.detect-between-commits");
        refactoringMinerStartCommitId = conf.getString("refactoring-miner.start-commit-id");
        refactoringMinerEndCommitId = conf.getString("refactoring-miner.end-commit-id");
        writeRefactoringMinerOutputOnFile = conf.getBoolean("refactoring-miner.write-on-file");
        designiteDir = conf.getString("designite.dir");
        sonarQubeServerBaseUrl = conf.getString("sonarqube.server.base-url");
        sonarQubeScannerBinDir = conf.getString("sonarqube.scanner.bin-dir");
        resultsDir = conf.getString("results.dir");

        this.isWindowsSystem = StringUtils.containsIgnoreCase(conf.getString("os.name"), "windows");
    }

    public static Configuration getInstance() {
        return instance;
    }

    public String getRepoDir() {
        return repoDir;
    }

    public boolean isRefactoringMinerDetectBetweenCommits() {
        return refactoringMinerDetectBetweenCommits;
    }

    public String getRefactoringMinerStartCommitId() {
        return refactoringMinerStartCommitId;
    }

    public String getRefactoringMinerEndCommitId() {
        return refactoringMinerEndCommitId;
    }

    public boolean isWriteRefactoringMinerOutputOnFile() {
        return writeRefactoringMinerOutputOnFile;
    }

    public String getDesigniteDir() {
        return designiteDir;
    }

    public String getSonarQubeServerBaseUrl() {
        return sonarQubeServerBaseUrl;
    }

    public String getSonarQubeScannerBinDir() {
        return sonarQubeScannerBinDir;
    }

    public String getResultsDir() {
        return resultsDir;
    }

    public boolean isWindowsSystem() {
        return isWindowsSystem;
    }
}
