package com.renren.mobile.x2.components.publisher;

import java.io.FileNotFoundException;
import java.io.InputStream;
import com.renren.mobile.x2.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageEditActivity extends Activity {
	private final int NORMAL_DPI = 600;
	private final int HIGH_DPI = 1024;
	private ImageView mImagePreview;
	private Button mButtonFinish, mBHighQuality, mBLowQuality;
	private Button mBLRotate, mBRRotate;
	private TextView info;
	private byte[] uploadImgData;
	private Bitmap mImg;
	public int mPhotoQuality = NORMAL_DPI;
	public boolean isRotateCompleted = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_edit);
		iniView();
		new ImgProThread().start();
	}

	private void iniView() {
		mBHighQuality = (Button) findViewById(R.id.image_edit_high_quality);
		mBLowQuality = (Button) findViewById(R.id.image_edit_low_quality);
		mBLRotate = (Button) findViewById(R.id.image_edit_rotate_l);
		mBRRotate = (Button) findViewById(R.id.image_edit_rotate_r);
		mButtonFinish = (Button) findViewById(R.id.image_edit_finish);
		mImagePreview = (ImageView) findViewById(R.id.image_edit_preview);
		info = (TextView) findViewById(R.id.image_edit_info);
		mButtonFinish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
                data.putExtra("image_byte_data", uploadImgData);
                setResult(2, data);
                finish();
			}
		});
	}

	public class ImgProThread extends Thread {

		private Bitmap previewImg;

		@Override
		public void run() {
			Uri uri = null;
			// 得到原图
			try {
				uri = getIntent().getData();
				if(uri!=null&&uri.getPath()!=null){
					InputStream is = getContentResolver().openInputStream(uri);
					byte[] data = PublisherUtils.toByteArray(is);
					PublisherUtils.closeQuietly(is);
					mImg = optimizeBitmap(data, HIGH_DPI, HIGH_DPI);
					data = null;
				}else{
					byte[] data =getIntent().getByteArrayExtra("image_byte_data");
					mImg = optimizeBitmap(data, HIGH_DPI, HIGH_DPI);
					data = null;
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				finish();
				return;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				finish();
				return;
			} catch (Exception e) {
				finish();
				return;
			}
			// 处理预览图
			try {
              if (mImg != null) { // 压缩照片
                  mImg = scaleImg(mImg, HIGH_DPI);
                  previewImg = scaleImg(mImg, ImageEditActivity.this.getResources()
                          .getDisplayMetrics().widthPixels*2/3);
                  /*一些适配方法
                  if(mPhotoWidth != 0) {
                      previewImg = scaleImg(mImg, mPhotoWidth);
                  } else if (Variables.screenWidth > 0) {
                      previewImg = scaleImg(mImg, Variables.screenWidth * 2/3);
                  } else {
                      int imageWidth = (int) TypedValue.applyDimension(
                              TypedValue.COMPLEX_UNIT_DIP, 200, SecondActivity.this.getResources()
                              .getDisplayMetrics());
                      previewImg = scaleImg(mImg, imageWidth);
                  }
                  */
                  processPhotoQuality(mImg, mPhotoQuality );

              } else {
            	  //图片为空
                  finish();
                  return;
              }
          } catch (OutOfMemoryError e) {
              e.printStackTrace();
              finish();
              return;
          } catch (Exception e) {
        	  e.printStackTrace();
              finish();
              return;
          }
			// 其他处理
			runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    final Matrix matrix = new Matrix();

                    if (previewImg != null) {
                        mImagePreview.setImageBitmap(previewImg);
                    }

                    mBRRotate.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!isRotateCompleted) {
                                return;
                            }
                            isRotateCompleted = false;
                            matrix.setRotate(90);
                            previewImg = Bitmap.createBitmap(previewImg, 0, 0, previewImg.getWidth(), previewImg.getHeight(), matrix, true);
                            if (previewImg != null) {
                            	mImagePreview.setImageBitmap(previewImg);
                            }
                            new Thread() {
                                public void run() {
                                    if (mImg != null) {
                                        mImg = Bitmap.createBitmap(mImg, 0, 0, mImg.getWidth(), mImg.getHeight(), matrix, true);
                                        isRotateCompleted = true;
                                        processPhotoQuality(mImg, mPhotoQuality);
                                    }
                                };
                            }.start();
                            // 测试用
                            // processPhotoQuality(mImg, HIGH_DPI);
                        }
                    });

                    mBLRotate.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (!isRotateCompleted) {
                                return;
                            }
                            isRotateCompleted = false;
                            matrix.setRotate(-90);
                            previewImg = Bitmap.createBitmap(previewImg, 0, 0, previewImg.getWidth(), previewImg.getHeight(), matrix, true);
                            if (previewImg != null) {
                            	mImagePreview.setImageBitmap(previewImg);
                            }
                            new Thread() {
                                public void run() {
                                    if (mImg != null) {
                                        mImg = Bitmap.createBitmap(mImg, 0, 0, mImg.getWidth(), mImg.getHeight(), matrix, true);
                                        isRotateCompleted = true;
                                        processPhotoQuality(mImg, mPhotoQuality);
                                    }
                                };
                            }.start();
                        }
                    });
                }
            });

		}

	}

	// ************************Methods**************************
	/**
	 * 等比缩小图片
	 * 
	 * @param img
	 *            原图片
	 * @param quality
	 *            缩小以后的图片宽度
	 * @return
	 */
	private Bitmap scaleImg(Bitmap img, int quality) {

		int sHeight = 0;
		int sWidth = 0;
		// Log.v("@@@", "scaleImg------->1");
		Bitmap ret = null;
		if (img.getWidth() > quality) {
			sWidth = quality;
			sHeight = (quality * img.getHeight()) / img.getWidth();
			ret = Bitmap.createScaledBitmap(img, sWidth, sHeight, true);
		} else {
			ret = img;
		}
		return ret;
	}

	/**
	 * 处理图片质量
	 * 
	 * @param img
	 *            原图片
	 * @param quality
	 *            压缩后的图片宽度
	 */
	private void processPhotoQuality(Bitmap img, int quality) {
		if (img == null) {
			return;
		}
		Bitmap image = scaleImg(img, quality);
		uploadImgData = PublisherUtils.getByteFromPic(image, 80);
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// 显示图片大小
				info.setText(" size: " + (uploadImgData.length / 1024) + "k");
				
			}
		});
	}

	// 优化图片，从字节数组得到Bitmap
	public Bitmap optimizeBitmap(byte[] resource, int maxWidth, int maxHeight) {
		Bitmap result = null;
		int length = resource.length;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		result = BitmapFactory.decodeByteArray(resource, 0, length, options);
		int widthRatio = (int) Math.ceil(options.outWidth / maxWidth);
		int heightRatio = (int) Math.ceil(options.outHeight / maxHeight);
		if (widthRatio > 1 || heightRatio > 1) {
			if (widthRatio > heightRatio) {
				options.inSampleSize = widthRatio;
			} else {
				options.inSampleSize = heightRatio;
			}
		}
		options.inJustDecodeBounds = false;
		result = BitmapFactory.decodeByteArray(resource, 0, length, options);
		return result;
	}
}
