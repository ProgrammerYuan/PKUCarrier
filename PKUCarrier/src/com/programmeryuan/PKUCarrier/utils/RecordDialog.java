package com.programmeryuan.PKUCarrier.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.programmeryuan.PKUCarrier.PKUCarrierApplication;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.interfaces.OnRecordSuccessListener;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

//import com.skd.androidrecording.audio.AudioRecordingHandler;
//import com.skd.androidrecording.audio.AudioRecordingThread;
//import com.skd.androidrecording.visualizer.VisualizerView;
//import com.skd.androidrecording.visualizer.renderer.BarGraphRenderer;

/**
 * Created by Michael on 2014/10/15.
 */
public class RecordDialog extends ZDialog {
    public boolean voice_interrupted = false;
    //    public Rect bound = null;
    public OnRecordSuccessListener l;
    public int status = 0;//0:ready 1:recording 2:cancel
    ImageView iv_micro;
    //    VisualizerView vv_wave;
    TextView tv_text;
    MediaRecorder recorder = null;
    String file_path;
    Activity caller;
    long start;
    Drawable drawableVol;
    long time = 0l;
    final long INTERVAL = 100;
    Handler handler;
    Runnable runnable;

    public RecordDialog(Context context) {
        super(context, R.layout.dialog_microphone);
        caller = (Activity) context;
//        file_path = context.getDir("voice_cache", Context.MODE_PRIVATE) + "/" + BApplication.getUserId() + "_" + System.currentTimeMillis() + ".amr";
        String fileDirPath = Environment.getExternalStorageDirectory() + "/bangyoung_filter";
        File fileDir = new File(fileDirPath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        file_path = fileDirPath + "/" + PKUCarrierApplication.getUserId() + "_" + System.currentTimeMillis() + ".amr";
        drawableVol = caller.getResources().getDrawable(R.drawable.record_level);
        clearDimAmount();
    }

    @Override
    protected void init() {
        super.init();

        iv_micro = (ImageView) root.findViewById(R.id.dialog_microphone_icon);
//        vv_wave = (VisualizerView) root.findViewById(R.id.dialog_microphone_wave);
        tv_text = (TextView) root.findViewById(R.id.dialog_microphone_text);
//        setupVisualizer();

        WindowManager.LayoutParams a = getWindow().getAttributes();
        a.gravity = Gravity.CENTER;
        a.dimAmount = 0.5f; // 添加背景遮盖

    }

    @Override
    public void resize() {

    }
    public void updateStatus() {
        updateStatus(0);
    }
    public void updateStatus(int vol) {
        drawableVol.setLevel(vol);
        switch (status) {
            case 0:
            case 1:
                tv_text.setText("手指上滑，取消发送");
                iv_micro.setImageDrawable(drawableVol);
                break;
            case 2:
                tv_text.setText("松开手指，取消发送");
                iv_micro.setImageResource(R.drawable.icon_microphone_cancel);
                break;
            default:
                break;
        }

    }

//    void setupVisualizer() {
//        Paint paint = new Paint();
//        paint.setStrokeWidth(5f);                     //set bar width
//        paint.setAntiAlias(true);
//        paint.setColor(Color.argb(200, 76, 193, 201)); //set bar color
////        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(2, paint, false);
////        vv_wave.addRenderer(barGraphRendererBottom);
//    }
    Timer recordTimer = null;
    VolumeTask volumeTask = null;
    public void startRecording() {
        Logger.out("startRecording()");
        status = 1;
        updateStatus(0);
        start = System.currentTimeMillis();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        String fileDirPath = Environment.getExternalStorageDirectory() + "/bangyoung_filter";
        File fileDir = new File(fileDirPath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        file_path = fileDirPath + "/" + PKUCarrierApplication.getUserId() + "_" + System.currentTimeMillis() + ".amr";
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(file_path);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (Exception e) {
            recorder.reset();
            recorder.release();
            recorder = null;
            status = 0;
            e.printStackTrace();
//            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
        if (handler == null) {
            handler = new Handler();
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                if (recorder != null) {
                    Logger.out("voice_interrupted");

                    voice_interrupted = true;
                    stopRecording();
                }
            }
        };
        handler.postDelayed(runnable, 60000);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (recorder != null) {
//                    Logger.out("voice_interrupted");
//
//                    voice_interrupted = true;
//                    stopRecording();
//                }
//
//            }
//        }, 60000);
        recordTimer = new Timer();
        volumeTask = new VolumeTask();
        recordTimer.schedule(volumeTask, 0, INTERVAL);

    }

    //    public File getRecordFile() {
//        return new File(file_path);
//    }
    public void cancelRecording() {
        try {
            if (volumeTask != null){
                volumeTask.cancel();
                volumeTask = null;
                recordTimer.cancel();
            }
            recorder.stop();
            recorder.release();
            recorder = null;
            status = 0;

            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
                Logger.out("remove runnable");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        l.execute(file_path, System.currentTimeMillis() - start);
        dismiss();
    }

    public void stopRecording() {
        try {
            if(volumeTask != null){
                volumeTask.cancel();
                volumeTask = null;
                recordTimer.cancel();
            }
            recorder.stop();
            recorder.release();
            recorder = null;
            status = 0;

            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
                Logger.out("remove runnable");
            }

            l.execute(file_path, System.currentTimeMillis() - start);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismiss();
    }


    private class VolumeTask extends TimerTask {

        public void run(){
            final int volume_level= recorder.getMaxAmplitude() / 4096;

            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable(){
                public void run(){
                    updateStatus(volume_level);
                }
            }; // This is your code
            mainHandler.post(myRunnable);
            time += INTERVAL;
            if (time >= 60 * 1000) {
                stopRecording();
                time = 0;
            }
        }
    };

}
