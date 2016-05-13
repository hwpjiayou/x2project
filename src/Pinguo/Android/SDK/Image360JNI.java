package Pinguo.Android.SDK;

import android.R.bool;

public class Image360JNI {
	static {
	    System.loadLibrary("imagesdk");
	}
	
	public native int test(int Value);
	 
	public native boolean Verify(String sdkVerifyKey);

	public native boolean SetImageFromJpegData(byte[] arrJpeg,int Length,int MaxPixelCount);

	public native boolean SetImageFromRgbaIntArray(byte[] arrRGBA,int Length,int Width,int Height);

	public native int[] ProcessEffectImage(String Params,boolean ChangeOrg);

	public native int GetImageWidth();
	public native int GetImageHeight();
	public native int[] GetImageData();

	public native void FreeImage();
}
