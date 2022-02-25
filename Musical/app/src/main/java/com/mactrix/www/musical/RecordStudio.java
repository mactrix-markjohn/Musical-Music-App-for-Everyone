package com.mactrix.www.musical;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import java.io.IOException;

/**
 * Created by Mactrix on 2/21/2018.
 */

public class RecordStudio {
    MediaRecorder mediaRecorder;
    boolean isRecording=false;
    boolean pause=false;
    boolean resume = false;
    Context context;
    public RecordStudio(Context context){
        this.context = context;
        mediaRecorder = new MediaRecorder();

    }

    public boolean hasMicrophone(){
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void record(final String path){
        try {

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(path);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            isRecording=true;
        } catch (IOException e) {
            mediaRecorder = new MediaRecorder();
        }catch (IllegalStateException e){

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(path);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.start();
            isRecording=true;
        }catch (Exception e){
            mediaRecorder = new MediaRecorder();
        }
        try {
            mediaRecorder.start();
        }catch (IllegalStateException e){
            noticeDialog();
            Intent intent = new Intent("recordstop");
            context.sendBroadcast(intent);
        }



    }

    public void noticeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setIcon(R.drawable.cancelhead);
        builder.setMessage("Hello, Sorry for the Sudden notice. This Song has cause an Error in the Recording Engine of this App, and So therefore this app can not record your voice.\n\nSorry for the Inconvenience...  ");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("recordnotice");
                context.sendBroadcast(intent);
            }
        });
        builder.create().show();
    }
    public boolean checkVersion(){
        return Build.VERSION.SDK_INT>= Build.VERSION_CODES.N;
    }

    public void pause(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder.pause();
            isRecording = false;
        }
    }

    public void resume(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder.resume();
            isRecording=true;

        }
    }

    public void stop(){
        try {

            mediaRecorder.stop();
            isRecording=false;
        }catch (IllegalStateException e) {

            mediaRecorder = new MediaRecorder();
        }
    }

    public boolean isRecording(){

        return isRecording;
    }

    public void setOutput(String path){


    }

    public void setMaxDuration(int max){
        mediaRecorder.setMaxDuration(max);
    }

    public void reset(){
        mediaRecorder.reset();
    }



}
