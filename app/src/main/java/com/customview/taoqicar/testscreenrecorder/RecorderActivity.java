package com.customview.taoqicar.testscreenrecorder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RecorderActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRecorder, btnStopRecorder;
    private String[] arr = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private List<String> list = new ArrayList<>();
    private ScreenRecorderManager screenRecoderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        checkPermissions();
        initView();
        initData();
    }

    private void initData() {
        screenRecoderManager = new ScreenRecorderManager(this);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < arr.length; i++) {
                if (ContextCompat.checkSelfPermission(this,
                        arr[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    list.add(arr[i]);
                }
            }

            if (list.size() == 0) {
                return;
            }

            ActivityCompat.requestPermissions(RecorderActivity.this,
                    list.toArray(new String[list.size()]),
                    1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
            }
        }
    }

    private void initView() {
        btnStopRecorder = (Button) findViewById(R.id.btn_stop_recorder);
        btnRecorder = (Button) findViewById(R.id.btn_recorder);
        btnRecorder.setOnClickListener(this);
        btnStopRecorder.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ScreenRecorderManager.REQUESTVIDEO:
                    //录屏操作
                    screenRecoderManager.onActivityResult(resultCode, data);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_recorder:
                startRecorder();
                break;
            case R.id.btn_stop_recorder:
                stopRecorder();
                Toast.makeText(RecorderActivity.this, "视频录制完毕", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void startRecorder() {
        screenRecoderManager.startRecorder();
    }

    private void stopRecorder() {
        screenRecoderManager.stopRecorder();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenRecoderManager.onDestroy();
    }
}
