package com.customview.taoqicar.testscreenrecorder;

import android.app.Activity;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;

import java.io.IOException;

import static android.content.Context.MEDIA_PROJECTION_SERVICE;

/**
 * 屏幕录制
 * Created by penglian on 2017/10/12.
 */

public class ScreenRecorderManager {
    private MediaProjection mediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mediaProjectionManager;
    public static final int REQUESTVIDEO = 0x200;
    private int mScreenDensity;
    private final int DISPLAY_WIDTH = 1280;
    private final int DISPLAY_HEIGHT = 720;

    private boolean isCheck;
    private MediaRecorder mMediaRecorder;
    private MediaProjectionCallback mMediaProjectionCallback;
    private Activity activity;
    private final String FILE_PATH = "/sdcard/" + "video_" + System.currentTimeMillis() + ".mp4";
    public ScreenRecorderManager(Activity activity){
        this.activity = activity;
        init();
    }

    private void init() {
        mMediaRecorder = new MediaRecorder();
        mMediaProjectionCallback = new MediaProjectionCallback();

        mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(MEDIA_PROJECTION_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.densityDpi;
    }

    public void startRecorder(){
        initRecorder(FILE_PATH);
        prepareRecorder();
        if (null == mediaProjection) {
            Intent intent = new Intent(mediaProjectionManager.createScreenCaptureIntent());
            activity.startActivityForResult(intent, REQUESTVIDEO);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    private void initRecorder(String filePath) {
        /**
         *  视频编码格式：default，H263，H264，MPEG_4_SP
         获得视频资源：default，CAMERA
         音频编码格式：default，AAC，AMR_NB，AMR_WB
         获得音频资源：defalut，camcorder，mic，voice_call，voice_communication,
         voice_downlink,voice_recognition, voice_uplink
         输出方式：amr_nb，amr_wb,default,mpeg_4,raw_amr,three_gpp
         */
        //设置音频源
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置视频源：Surface和Camera 两种
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //设置视频输出格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置视频编码格式
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置音频编码格式
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //设置视频编码的码率
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        //设置视频编码的帧率
        mMediaRecorder.setVideoFrameRate(30);
        //设置视频尺寸大小
        mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        //设置视频输出路径
        mMediaRecorder.setOutputFile(filePath);

    }

    private void prepareRecorder() {
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            activity.finish();
        }
    }

    public void stopRecorder(){
        isCheck = true;
        stopRecord();
        mVirtualDisplay.release();
    }

    private void stopRecord() {
        try {
            mMediaRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMediaRecorder.reset();
    }

    private VirtualDisplay createVirtualDisplay() {
        /**
         * 创建虚拟画面
         * 第一个参数：虚拟画面名称
         * 第二个参数：虚拟画面的宽度
         * 第三个参数：虚拟画面的高度
         * 第四个参数：虚拟画面的标志
         * 第五个参数：虚拟画面输出的Surface
         * 第六个参数：虚拟画面回调接口
         */
        return mediaProjection
                .createVirtualDisplay("MainActivity", DISPLAY_WIDTH,
                        DISPLAY_HEIGHT, mScreenDensity,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        mMediaRecorder.getSurface()/*mImageReader.getSurface()*/,
                        null /* Callbacks */, null /* Handler */);
    }

    public void onDestroy(){
        if (isCheck) {
            isCheck = false;
        }

        if (null != mMediaRecorder) {
            // mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        if (null != mVirtualDisplay) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (null != mediaProjection) {
            mediaProjection.unregisterCallback(mMediaProjectionCallback);
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

    private final class MediaProjectionCallback extends MediaProjection.Callback {

        @Override
        public void onStop() {
            super.onStop();
            if (isCheck) {
                isCheck = false;
                stopRecord();
                mVirtualDisplay.release();
                mediaProjection.stop();
                mediaProjection.unregisterCallback(mMediaProjectionCallback);
                mediaProjection = null;
            }
        }
    }

    public void onActivityResult(int resultCode, Intent data){
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        mediaProjection.registerCallback(mMediaProjectionCallback, null);
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }
}
