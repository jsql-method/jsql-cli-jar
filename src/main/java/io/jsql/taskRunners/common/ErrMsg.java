package io.jsql.taskRunners.common;

import io.jsql.taskRunners.common.run.Process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class ErrMsg {
    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_YELLOW = "\u001B[33m";
    static final String ANSI_GREEN = "\u001B[32m";
    private String errPath;

    public ErrMsg() {

        if(!Process.DEBUG){
            PrintStream output = null;
            this.errPath = "debug/jsql-debug-" + System.currentTimeMillis() + ".txt";
            Boolean file = new File("debug").mkdir();
            deleteEmptyErrors();
            try {
                output = new PrintStream(new FileOutputStream(errPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.setErr(output);
        }

    }

    private void deleteEmptyErrors() {
       // Boolean deleted = new File("debug/jsql-debug-1540974323232.txt").delete();
        File[] files = new File("debug").listFiles();
        for (File file: files) {
            if (!(file.length()>0)){
              Boolean bool = file.delete();
            }
        }
    }

    public static void error(String message) {
        System.out.println(ANSI_RED + message + ANSI_RESET);
    }

    public static void warning(String message) {
        System.out.println(ANSI_YELLOW + message + ANSI_RESET);
    }

    public static void success(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }

    public String getErrPath() {
        return errPath;
    }
}
