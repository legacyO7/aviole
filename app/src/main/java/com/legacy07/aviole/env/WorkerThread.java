package com.legacy07.aviole.env;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

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
    Activity context;


    final int pid = getPid(mProcess);
    ProgressDialog pd = null;
    public WorkerThread(Activity context, String cwd, String fileToExecute, String[] args) {
        this.context=context;
        this.cwd=cwd;
        this.fileToExecute=fileToExecute;
        this.args=args;


        if(context!=null){
            pd=new ProgressDialog(context);
            pd.setTitle("Processing request");
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage("Connecting to aviole system");
            pd.show();
        }

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
        final Bundle result = new Bundle();


        Thread errThread = new Thread() {
            @Override
            public void run() {
                InputStream stderr = mProcess.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stderr, StandardCharsets.UTF_8));
                String line;
                try {
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
            while ((line = reader.readLine()) != null) {
                logger( "[" + pid + "] stdout: " + line);
                if(line.contains("[download]")){

                    String finalLine = line;
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalLine.contains("% of ")&&!finalLine.contains("100% of ")){
                                logger((finalLine.substring(finalLine.indexOf('%')-3, finalLine.indexOf('%')-2).replaceAll("[^0-9]", "").trim()));
                                pd.setProgress(Integer.parseInt(finalLine.substring(finalLine.indexOf('%')-5, finalLine.indexOf('%')-2).replaceAll("[^0-9]", "").trim()));

                            }
                            else if(finalLine.contains("Destination:")){
                                pd.dismiss();
                                pd.setTitle("Downloading media");
                                pd.setIndeterminate(false);
                                pd.setMessage(finalLine.split("Destination: ")[1]);
                                pd.show();
                                logger("destination found");
                            }

                        }
                    });



                }
                outResult.append(line).append('\n');
            }
            pd.cancel();
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
