package io.jsql.taskRunners.common;


import io.jsql.taskRunners.common.Command;
import io.jsql.taskRunners.common.run.Process;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Klasa reprezentuje mechanizm parsowania plików
 */
public class Parser implements Runnable {
    // Zbiór wyników parsowania
    private ArrayList<String> values = new ArrayList<>();

    // Ścieżka do plików wyjściowych *.js
    private final String target;

    // Ścieżka do plików wejściowych
    private String src;

    // Unikalna lista wynikowa
    private Map<String, HashSet<String>> queries;

    // Lista z zawartością plików
    private Map<String, String> srciptFiles;

    private String wildcard = "*.js";

    // Konstruktor bazowy
//    public Parser(String dist)
//    {
//        this.dist = dist;
//        this.src = "*.js";
//        this.queries = new HashMap<>();
//
//        Test();
//    }

    // Konstruktor bazowy przeciążony o src
    public Parser(String src, String target) {
        this.src = src;
        this.target = target;
        this.queries = new HashMap<>();

        Test();
    }

    // Test method
    private void Test() {
        //TimeTestMethod("getFilesDirAIO", this::getFilesDirAIO, "getFilesDirScan", this::getFilesDirScan, 25);
    }

    // Metoda dokonująca parsowania wykrytych plików
    @Override
    public void run() {
        // Pobranie plików wraz z ich zawartością
        srciptFiles = getFilesContent();

        // Stats for debug mode only
        System.out.println("Sfl: " + srciptFiles.size());

        // Regex pattern
        Pattern pattern = Command.getPattern();

        // Find matches against pattern
        queries = srciptFiles.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                e -> e.getKey(),
                                e -> {
                                    Matcher matcher = pattern.matcher(e.getValue());
                                    HashSet<String> result = new HashSet<>();

                                    while (matcher.find()) {
                                        if (matcher.group(1) != null && !matcher.group(1).equals("null")) {
                                            result.add(matcher.group(1));
                                        } else if (matcher.group(2) != null && !matcher.group(2).equals("null")) {
                                            result.add(matcher.group(2));
                                        }
                                    }
                                    return result;
                                }
                        )
                );

        queries.entrySet().forEach(q -> {
            System.out.println("K: '" + q.getKey() + "'");
            System.out.println("V: '" + q.getValue() + "'\n");
        });

    }

    // 34% increase against non-parallel methods
    // 80% increase when accessing large collections
    private String getFileContent(String file) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(file));

        return bf.lines().parallel().collect(Collectors.joining());
    }

    // Method overloaded with file usage instead of string
    private String getFileContent(File file) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(file));

        return bf.lines().parallel().collect(Collectors.joining("\n"));
    }

    // Metoda zwraca mapę z nazwą pliku jako klucz i jego zawartością jako wartość
    private Map<String, String> getFilesContent() {
        try {
            return Arrays.stream(getFiles()).parallel()
                    .collect(Collectors.toMap(k -> k.getAbsolutePath(), v -> {
                        try {
                            return getFileContent(v);
                        } catch (Exception ex) {
                            throw new IllegalArgumentException("Exception during method 'getFilesContent': " + ex.toString());
                        }
                    }));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Input directory doesn't exist!");
        }
    }

    // Metoda zwraca nazwy plików znajdujących się w katalogu 'dist' z użyciem srca
 /*   private String[] getFileNames()
    {
        File dir = new File(dist);
        dir.mkdirs();

        FileFilter fileFilter = new srcFileFilter(src);
        File[] files = dir.listFiles(fileFilter);

        return Arrays.stream(files).map(f -> f.getAbsolutePath()).toArray(size -> new String[files.length]);
    }*/

    // Metoda zwraca pliki znajdujące się z katalogu 'src' do katalogu 'target'
    private File[] getFiles() {
        int separator = src.lastIndexOf('*');
        if (separator > -1) {
            this.wildcard = "*." + FilenameUtils.getExtension(src);
            System.out.println("wild: " + wildcard);
        }

        File srcFile = new File(src);
        if (srcFile.isFile() && src.contains("/")) {
            String dirPath = src.substring(0, src.lastIndexOf("/"));
            new File(dirPath).mkdirs();
        } else if (srcFile.isDirectory()) {
            System.out.println("there is a directory!");
            File[] files = new File(src).listFiles();
            for (File file : files) {
                Hasher hasher = new Hasher(Process.API_KEY, Process.USER, file.getAbsolutePath(), Process.TARGET);
            }

        }

        // Tworzenie katalogu wyjściowego
        //File dirOut = new File(target);
        //dirOut.mkdirs();


        //System.out.println("out dir : " + dirOut);

        if (src.endsWith(wildcard)) {

            if (!src.contains("/")) src = "/" + src;

            File dir = new File(FilenameUtils.getFullPath(src));
            System.out.println("input dir wildcard: " + dir);

            FileFilter fileFilter = new WildcardFileFilter(wildcard);

            return dir.listFiles(fileFilter);

        } else if (src.endsWith("." + FilenameUtils.getExtension(src)) && src.contains("/")) {

            File dir = new File(src);
            System.out.println("input dir extension: " + dir);

            if (!dir.exists()) {

                try {
                    dir.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return new File[]{dir};

        } else if (src.matches("^[^.]+$")) {
            // input without dot
            File dir = new File(src);
            System.out.println("input dir without dot: " + dir);

            FileFilter fileFilter = new WildcardFileFilter(wildcard);
            return dir.listFiles(fileFilter);

        } else {

            File dir = new File("/" + src);
            System.out.println("input dir Else: " + dir);

            if (!dir.exists()) {
/*
                try {
                    dir.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }

            return new File[]{dir};

        }

    }

    // Metoda służy do porównywania osiągnieć czasowych dwóch metod
    private void TimeTestMethod(String n1, Callable<Object> f1, String n2, Callable<Object> f2, int tests) {
        System.out.println("\nMethods time comparison:\t\t" + n1 + "\t\t" + n2);

        for (int i = 0; i < tests; ++i) {
            // Test parallelism vs non parallelism
            long m1_start = 0, m1_end = 0;
            long m2_start = 0, m2_end = 0;

            try {
                // 1st method time consumption init
                m1_start = System.currentTimeMillis();
                Object x = f1.call();
                m1_end = System.currentTimeMillis();

                // 2nd method time consumption init
                m2_start = System.currentTimeMillis();
                Object y = f2.call();
                m2_end = System.currentTimeMillis();
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.toString());
            } finally {

                // Results end comparison
                System.out.println(
                        "Test " + i + "\t\t\t\t\t\t\tMethod 1: " + (m1_end - m1_start) + " ms" +
                                "\t\tMethod 2: " + (m2_end - m2_start) + " ms"
                );
            }
        }
    }

    // Getter dla queries
    public Map<String, HashSet<String>> getQueries() {
        return queries;
    }

    // Getter dla srciptFiles
    public Map<String, String> getScriptFiles() {
        return srciptFiles;
    }
}