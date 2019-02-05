package io.jsql.taskRunners.common;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashSet;

import static io.jsql.taskRunners.common.run.Process.*;

public class FileWatcher {
    private File fileSrc = null;
    private File fileTarget = null;
    HashSet<String> source = new HashSet<>();
    Hasher hasher=null;

    public FileWatcher(String watch) {
        int i = 1;
        while (watch.equalsIgnoreCase("true") || watch.equals("1")) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HashSet<String> targets = getTargets();
            HashSet<String> source = getSources(SRC);
            watch(source, targets);
            source.clear();
            fileSrc = null;
            fileTarget = null;
            hasher=null;
        }
    }

    private HashSet<String> getTargets() {
        File[] targetArray = new File(TARGET).listFiles();
        HashSet<String> targets = new HashSet<>();
        for (File file : targetArray) {
            String path = file.getPath();
            targets.add(path);
        }
        return targets;
    }

    private HashSet<String> getSources(String sourcePath) {
        File[] files = new File(sourcePath).listFiles();
        String test = FilenameUtils.getExtension(sourcePath);

        if (test.length() > 0 && sourcePath.contains("*")) {
            files = new File(new File(sourcePath).getParent()).listFiles();
        }
        if (files == null) {
            File file1 = new File(sourcePath);
            String str = file1.getPath();
            source.add(str.replace('\\', '/'));
            return source;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                getSources(file.getPath());

            } else {
                String str = file.getPath();
                if (str.endsWith(test)) {
                    source.add(str.replace('\\', '/'));
                }
            }

        }
        return source;
    }

    private void watch(HashSet<String> srcMap, HashSet<String> targetSet) {
        for (String srcPath : srcMap) {
            boolean found = false;

            for (String target : targetSet) {
                found = comparison(srcPath, target);
                if (found) {
                    if (fileSrc.lastModified() > fileTarget.lastModified()) {
                        System.out.println("turning on hasher!");
                       hasher = new Hasher(API_KEY, USER, srcPath, TARGET);

                    }
                    break;
                }
            }
            if (!found) {
                hasher = new Hasher(API_KEY, USER, srcPath, TARGET);
            }
        }
    }

    private boolean comparison(String srcPath, String target) {
        fileSrc = new File(srcPath);
        fileTarget = new File(target);
        return fileSrc.getName().equals(fileTarget.getName());

    }
}
