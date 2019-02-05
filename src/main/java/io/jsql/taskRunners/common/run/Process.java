package io.jsql.taskRunners.common.run;


import io.jsql.taskRunners.common.ErrMsg;
import io.jsql.taskRunners.common.FileWatcher;
import io.jsql.taskRunners.common.Hasher;

import java.io.*;

public class Process {

    public static boolean DEBUG = true;

    // Args
    public static String API_KEY = null;
    public static String USER = null;
    public static String SRC = null;
    public static String TARGET = null;
    public static String WATCH = "";
    public static String DEV_KEY_DIR = null;

    public static void main(String[] args) throws InterruptedException {
        ErrMsg error = new ErrMsg();
        try {
            long m1_start = 0, m1_end = 0;
            long m2_start = 0, m2_end = 0;
            // Args
//        String apiKey = null;
//        String user = null;
//        String inputSource = null;
//        String outputDestination = null;
            // Argument conditional
            if (args.length < 3) {
                throw new IllegalArgumentException("Not enough arguments supplied!");
            }

            if(args.length > 5){
                throw new IllegalArgumentException("Too many arguments supplied!");
            }

            m1_start = System.currentTimeMillis();
            // Assign non-varying data
            API_KEY = args[0];

            System.out.println("args.length : "+args.length);

            for(String ar : args){
                System.out.println("arg: "+ar);
            }
            if (args.length == 5) {
                DEV_KEY_DIR = args[4];
            }

            //Read member key from file
            File file = null;

            System.out.println("DEV_KEY_DIR :"+DEV_KEY_DIR);
            if(DEV_KEY_DIR == null){
                file =  new File("./jsql");
            }else{
                file = new File(DEV_KEY_DIR+"/jsql");
            }

            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String st;
                while ((st = br.readLine()) != null)
                    USER = st;
            } catch (IOException e) {
                e.printStackTrace();
                ErrMsg.error("bad API");
            } catch (IllegalArgumentException e) {
                e.getMessage();
            }
            file = null;

            // default values
            SRC = "*.js";
            TARGET = "/";

            // Source specified
            SRC = args[1];

            SRC = SRC.replace("?","*");

            // Destination directory specified
            TARGET = args[2];

            File targetDir = null;
            if (Process.TARGET.contains(".")) {
                String targetDirs = Process.TARGET.substring(0, Process.TARGET.lastIndexOf("/") + 1);
                targetDir = new File(targetDirs);

            } else {
                targetDir = new File(Process.TARGET);
            }

            targetDir.mkdirs();

// Is Watcher turn on
            if (args.length >= 4) {
                WATCH = args[3];
            }

            WATCH = WATCH.trim();


            /* Printout */
            System.out.println("Api key: " + API_KEY);
            System.out.println("Member Key: " + USER);

// Hashing algorithm instance

            Hasher hasher = new Hasher(API_KEY, USER, SRC, TARGET);
            hasher = null;

            m1_end = System.currentTimeMillis();
            if (args.length >= 4) {
// Results end comparison
                System.out.println(
                        "Test \t\t\t\t\t\t\tMethod 1: " + (m1_end - m1_start) + " ms"
                );
                FileWatcher fileWatcher = new FileWatcher(WATCH);

            }
        } catch (Exception e) {
            ErrMsg.error(e.getMessage());
            e.printStackTrace();
        }

    }


}


