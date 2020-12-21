package com.group;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;

public class Configuration {
    private static final Configuration instance = new Configuration();

    private final String repoDir;
    private final String refactoringMinerBranchToAnalyze;
    private final boolean refactoringMinerDetectBetweenCommits;
    private final String refactoringMinerStartCommitId;
    private final String refactoringMinerEndCommitId;
    private final boolean writeRefactoringMinerOutputOnFile;
    private final String designiteDir;
    private final String sonarQubeServerBaseUrl;
    private final String sonarQubeScannerBinDir;
    private final String resultsDir;

    private final boolean isWindowsSystem;

    private Configuration() {
        Config conf = ConfigFactory.load();
        repoDir = conf.getString("repo.dir");
        refactoringMinerBranchToAnalyze = conf.getString("refactoring-miner.branch-to-analyze");
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

    public String getRefactoringMinerBranchToAnalyze() {
        return refactoringMinerBranchToAnalyze;
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
