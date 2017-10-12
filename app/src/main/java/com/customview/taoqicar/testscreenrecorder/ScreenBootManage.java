package com.customview.taoqicar.testscreenrecorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.nio.ByteBuffer;

import static android.content.Context.MEDIA_PROJECTION_SERVICE;

/**
 * 截屏管理
 * Created by penglian on 2017/10/12.
 */

public class ScreenBootManage {
    private Activity activity;
    public static final int REQUESTRESULT = 0x100;
    private MediaProjection mediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mediaProjectionManager;
    private int mWidth;
    private int mHeight;
    private int mScreenDensity;
    private ImageReader mImageReader;
    public ScreenBootManage(Activity activity){
        this.activity = activity;
        init();
    }

    private void init() {
        mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(MEDIA_PROJECTION_SERVICE);
        Display display = activity.getWindowManager().getDefaultDisplay();
        mWidth = display.getWidth();
        mHeight = display.getHeight();
        DisplayMetrics outMetric = new DisplayMetrics();
        display.getMetrics(outMetric);
        mScreenDensity = (int) outMetric.density;
        Intent intent = new Intent(mediaProjectionManager.createScreenCaptureIntent());
        activity.startActivityForResult(intent,REQUESTRESULT);
    }

    public void onActivityResult(int resultCode, Intent data){
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        mVirtualDisplay = mediaProjection.createVirtualDisplay("mediaprojection", mWidth, mHeight,
                mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
    }

    /**
     * 对ImageReocder获取的图片进行处理
     * @param iv
     */
    public void screenBoot(ImageView iv, LinearLayout ll){
        Image image = mImageReader.acquireLatestImage();
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width+rowPadding/pixelStride, height,
                Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,width, height);
        image.close();
        iv.setImageBitmap(bitmap);
        ll.setBackgroundColor(0x0f0);
    }
}
