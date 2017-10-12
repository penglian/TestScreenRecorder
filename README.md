# TestScreenRecorder
录屏（SDK5.0以上）与截屏


屏幕截屏：
1：通过 getSystemService（）得到MediaProjectionManager服务； 
2：通过MediaProjectionManager创建一个屏幕捕捉意图； 
3：通过startActivityForResult开启该意图； 
4：创建ImageReader、MediaProjection对象，然后通过mediaProjection.createVirtualDisplay（）创建一个VirtualDisplay对象，并把mImageReader.getSurface()作为createVirtualDisplay（）第六个参数； 
5： 通过ImageReader.acquireLatestImage()得到Image;通过Image得到Bitmap.createBitmap(）的参数；

屏幕录制的步骤：
1：通过 getSystemService（）得到MediaProjectionManager服务； 
2、创建MediaRecorder对象，并把音视频的编码、输出方式设置好（有时我们可能会遇到设置音频后，程序运行会报错！）； 准备录制； 
2：通过MediaProjectionManager创建一个屏幕捕捉意图； 
3：通过startActivityForResult开启该意图； 
4：创建MediaProjection对象，然后通过mediaProjection.createVirtualDisplay（）创建一个VirtualDisplay对象，并把mMediaRecorder.getSurface()作为createVirtualDisplay（）第六个参数；
