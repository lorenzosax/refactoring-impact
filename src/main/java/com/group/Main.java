package com.group;

import org.apache.log4j.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        Process process = new Process();
        try {
            process.start();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
