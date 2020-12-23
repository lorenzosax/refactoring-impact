package com.group;

import org.refactoringminer.api.RefactoringType;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Utils {

    private static boolean isWindowsSystem = Configuration.getInstance().isWindowsSystem();

    public static final Map<String, Map<RefactoringType, Boolean>> allowedSmellWithRefactoringTypes = new HashMap<String, Map<RefactoringType, Boolean>>() {{
        put("Long Method", new HashMap<RefactoringType, Boolean>() {{
            put(RefactoringType.EXTRACT_OPERATION, true);
        }});
        put("Long Parameter List", new HashMap<RefactoringType, Boolean>() {{
            put(RefactoringType.EXTRACT_CLASS, true);
            put(RefactoringType.MERGE_PARAMETER, true);
            put(RefactoringType.REMOVE_PARAMETER, true);
        }});
        put("Insufficient Modularization", new HashMap<RefactoringType, Boolean>() {{
            put(RefactoringType.EXTRACT_CLASS, true);
            put(RefactoringType.EXTRACT_SUBCLASS, true);
            put(RefactoringType.EXTRACT_INTERFACE, true);
        }});
        put("Multifaceted Abstraction", new HashMap<RefactoringType, Boolean>() {{
            put(RefactoringType.EXTRACT_CLASS, true);
            put(RefactoringType.EXTRACT_SUBCLASS, true);
            put(RefactoringType.EXTRACT_INTERFACE, true);
        }});
    }};

    public static final Map<RefactoringType, Boolean> refactoringsConsidered = new HashMap<RefactoringType, Boolean>() {{
        put(RefactoringType.EXTRACT_OPERATION, true);
        put(RefactoringType.MERGE_PARAMETER, true);
        put(RefactoringType.REMOVE_PARAMETER, true);
        put(RefactoringType.EXTRACT_CLASS, true);
        put(RefactoringType.EXTRACT_SUBCLASS, true);
        put(RefactoringType.EXTRACT_INTERFACE, true);
    }};

    static final Pattern srcMainJavaPattern = Pattern.compile("^src/(main|test)/java/(.*)");
    static final Pattern srcPattern = Pattern.compile("^src/(.*)");

    public static String preparePathOsBased(boolean withLastSlash, String... subPaths) {
        StringBuilder path = new StringBuilder();
        for (String subPath : subPaths) {
            if (subPath != null)
                path.append(subPath).append(isWindowsSystem ? "\\" : "/");
        }
        if (!withLastSlash) {
            path.deleteCharAt(path.length()-1);
        }
        return path.toString();
    }

    public static String getProjectNameFromRepoDir(String repoDir) {
        StringTokenizer stringTokenizer = new StringTokenizer(repoDir, isWindowsSystem ? "\\\\" : "/");
        String projectName = null;
        while (stringTokenizer.hasMoreElements())
            projectName = stringTokenizer.nextToken();
        return projectName;
    }

}
