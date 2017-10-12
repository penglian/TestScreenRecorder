package com.customview.taoqicar.testscreenrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnScreenBoot,btnRecorder;
    ImageView ivScreenBoot;

    private LinearLayout ll;
    private ScreenBootManage screenBootManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        screenBootManager = new ScreenBootManage(this);
    }


    private void initView() {
        btnRecorder = (Button) findViewById(R.id.btn_recorder);
        btnScreenBoot = (Button) findViewById(R.id.btn_screenboot);
        ivScreenBoot = (ImageView) findViewById(R.id.iv_screenboot);
        ll = (LinearLayout) findViewById(R.id.ll);
        btnScreenBoot.setOnClickListener(this);
        btnRecorder.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ScreenBootManage.REQUESTRESULT:
                    screenBootManager.onActivityResult(resultCode, data);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_screenboot:
                screenBoot();
                break;
            case R.id.btn_recorder:
                startActivity(new Intent(MainActivity.this,RecorderActivity.class));
                break;
            default:
                break;
        }
    }

    private void screenBoot() {
         screenBootManager.screenBoot(ivScreenBoot,ll);
    }


}
