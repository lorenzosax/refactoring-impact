package com.group;

public class Main {

    public static void main(String[] args) {
        Process process = new Process();
        try {
            process.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
