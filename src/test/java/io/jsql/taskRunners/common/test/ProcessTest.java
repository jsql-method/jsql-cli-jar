package io.jsql.taskRunners.common.test;

import io.jsql.taskRunners.common.process.FileAnalyser;
import io.jsql.taskRunners.common.process.Hasher;
import io.jsql.taskRunners.common.run.ProcessFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProcessTest {

    @Test
    void testCollectingQueries() {
        System.out.println("STARTING TEST");
        System.out.println("CREATING JavaScript data");



        String newTestDirName = "testDir_collecting_" + new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date());
        File srcDir = new File("testDirBase");
        File destDir = new File("testResult/" + newTestDirName);

        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("STARTING processing");

        int totalQueries = 12; //bo się powtarza where query = test repo

        String[] args = {"436g45544f5g6657fg546gn6fd45g6f54g6gh5", "member1", "testResult/" + newTestDirName};

        ProcessFactory processFactory = new ProcessFactory(args);

        processFactory.hasher.iterateAndFindQueries();

        //Assert.assertEquals(processFactory.hasher.querySet.size() as Long, totalQueries as Long);


        System.out.println("TEST OK");

    }

    @Test
    void testProcessJavaScript() {

//        println 'STARTING TEST'
//        println 'CREATING JavaScript data'
//
//        def newTestDirName = 'testDir_' + new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date())
//        File srcDir = new File('testDirBase');
//        File destDir = new File('testResult/' + newTestDirName);
//
//        FileUtils.copyDirectory(srcDir, destDir);
//
//        println 'STARTING processing'
//
//        Object[] args = ['436g45544f5g6657fg546gn6fd45g6f54g6gh5', 'member1', 'testResult/' + newTestDirName]
//        ProcessFactory processFactory = new ProcessFactory(args)
//        processFactory.start()
//
//
//        processFactory.hasher.querySet = []
//
//        println 'testProcessJavaScript iterating';
//
//
//        processFactory.hasher.iterateFiles({
//
//            String body = FileAnalyser.getFile(it)
//            processFactory.hasher.findingReplacementsClosure(body)
//
//        })
//
//
//
//        def queriesHashsInJavaScript = processFactory.hasher.querySet.toSet()
//        queriesHashsInJavaScript = queriesHashsInJavaScript as String[]
//        queriesHashsInJavaScript.sort()
//
//        println 'queries hashs from JavaScript : ' + queriesHashsInJavaScript
//
//        def queriesHashsFromServer = processFactory.hasher.queryHashMap.collect { it.token } as String[]
//        queriesHashsFromServer.sort()
//
//        println 'queries hashs from server : ' + queriesHashsFromServer
//
//
//        Assert.assertArrayEquals(queriesHashsInJavaScript, queriesHashsFromServer)
//
//        println 'TEST OK'


    }

}
