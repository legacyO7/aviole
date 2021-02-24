package com.legacy07.aviole.env;

import android.content.Intent;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.legacy07.aviole.misc.GlobalVariablesKt.outputText;
import static com.legacy07.aviole.env.HttpsTrustManagerKt.logger;
import static com.legacy07.aviole.env.executeTermuxAction.getPid;

public class WorkerThread implements Runnable{
    final StringBuilder outResult = new StringBuilder();
    final StringBuilder errResult = new StringBuilder();

    private List listeners = new ArrayList();
    private List results;
    Process mProcess;
    String cwd,fileToExecute;
    String[] args;


    final int pid = getPid(mProcess);

    public WorkerThread(String cwd, String fileToExecute, String[] args) {
        this.cwd=cwd;
        this.fileToExecute=fileToExecute;
        this.args=args;
    }

    @Override
    public void run() {

        String[] env = executeTermuxAction.buildEnvironment(false, cwd);
        final String[] progArray = executeTermuxAction.setupProcessArgs(fileToExecute, args);
        final String processDescription = Arrays.toString(progArray);

        Process process;
        try {
            process = Runtime.getRuntime().exec(progArray, env, new File(cwd));
        } catch (IOException e) {
            mProcess = null;
            // TODO: Visible error message?
            logger( "Failed running background job: " + processDescription);
            return;
        }

        mProcess = process;

        logger( "[" + pid + "] starting: " + processDescription);
        InputStream stdout = mProcess.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));
        String line;
        outputText="";
        final Bundle result = new Bundle();


        Thread errThread = new Thread() {
            @Override
            public void run() {
                InputStream stderr = mProcess.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stderr, StandardCharsets.UTF_8));
                String line;
                try {
                    // FIXME: Long lines.
                    while ((line = reader.readLine()) != null) {
                        errResult.append(line).append('\n');
                        logger( "[" + pid + "] stderr: " + line);
                    }
                } catch (IOException e) {
                    // Ignore.
                }
            }
        };
        errThread.start();


        try {
            // FIXME: Long lines.
            while ((line = reader.readLine()) != null) {
                logger( "[" + pid + "] stdout: " + line);
                outResult.append(line).append('\n');
            }
            outputText=line;
        } catch (IOException e) {
            logger( "Error reading output $e");
        }

        try {
            int exitCode = mProcess.waitFor();
            //service.onBackgroundJobExited(BackgroundJob.this);
            if (exitCode == 0) {
                logger( "[" + pid + "] exited normally");
            } else {
                logger("[" + pid + "] exited with code: " + exitCode);
            }

            result.putString("stdout", outResult.toString());
            result.putInt("exitCode", exitCode);

           errThread.join();
           result.putString("stderr", errResult.toString());

            Intent data = new Intent();
            data.putExtra("result", result);
            results = new ArrayList();
            results.add(outResult.toString());
            results.add(errResult.toString());
            notifyListeners();


        } catch (InterruptedException e) {
            // Ignore
        }

    }

    public void notifyListeners() {
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            WorkerListener listener = (WorkerListener) iter.next();
            listener.workDone(this);
        }
    }

    public void registerWorkerListener(WorkerListener listener) {
        listeners.add(listener);
    }

    public List getResults() {
        return results;
    }

}
