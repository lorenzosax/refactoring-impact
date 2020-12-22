package com.group;

import org.refactoringminer.api.RefactoringType;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Utils {

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

}
