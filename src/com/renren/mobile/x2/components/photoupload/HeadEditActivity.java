package com.renren.mobile.x2.components.photoupload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.components.home.profile.ProfileFragment;
import com.renren.mobile.x2.components.imageviewer.PhotoUploadManager;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * author yuchao.zhang
 * <p/>
 * description 本类为，上传图片逻辑的最后一步，即展示页面
 * 目前支持放大缩小，移动功能！
 */
public class HeadEditActivity extends Activity {

	private Handler handler = new Handler();
    /**
     * 取消按钮
     */
    private Button cancelButton;
    /**
     * 确定按钮
     */
    private Button okButton;
    /**
     * 顶部工具栏
     */
    private LinearLayout mTitleBar;
    /**
     * 底部工具栏
     */
    private LinearLayout mOperationBar;
    /**
     * NB的可缩放、可移动、多功能控件
     */
    private HeadEditImageView mImageView;
    /**
     * 正在加载的等待框
     */
    private FrameLayout mLoadingLayout;
    /**
     * 位图
     */
    private Bitmap mBitmap;
    /**
     * 当前模式
     */
    private int mCurrentMode;
    /**
     * 全屏 浏览模式
     */
    private static final int MODE_FULL_SCREEN = 0;
    /**
     * 非全屏 显示工具栏模式
     */
    private static final int MODE_SHOW_TOOL_BAR = 1;
    /**
     * 用来上传的数组
     */
    private byte[] bytes;
    /**
     * 登录数据
     */
    private LoginInfo loginInfo;
    /**
     * 当前屏幕分辨率
     */
    private float density;
    /**
     * 选取框的DP值
     */
    private int selectedFrameDp = 268;
    /**
     * 是否正在上传图片 策略：正在上传图片时，不允许再次上传
     */
    private boolean isUploading = false;
    /**
     * 正在上传的Dialog
     */
    private ProgressDialog uploadingDialog;
    /**
     * 初始化时的缩放比
     */
    private float scaleByInit;

    private MyThread mThread;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.head_edit);

        getExtras();

        initView();

        initEvents();

        setMode(MODE_SHOW_TOOL_BAR);

        setImage();
    }

    @Override
    protected void onDestroy() {

        if (mImageView.getBitmap() != null && !mImageView.getBitmap().isRecycled()) {

            mImageView.setBitmapNull();
        }
        bytes = null;
        PhotoUploadManager.getInstance().bytes = null;
        System.gc();
        super.onDestroy();
    }

    private void getExtras() {

//        Uri uri = (Uri) this.getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
//        mBitmap = PhotoUploadManager.getInstance().getBitmap(HeadEditActivity.this, uri);
//        bytes = Bitmap2Bytes(mBitmap);

        /* 取得当前屏幕分辨率 */
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;
    }

    private void initView() {

        mTitleBar = (LinearLayout) findViewById(R.id.head_edit_title_bar);
        mOperationBar = (LinearLayout) findViewById(R.id.head_edit_operation_bar);
        cancelButton = (Button) findViewById(R.id.head_edit_cancel_button);
        okButton = (Button) findViewById(R.id.head_edit_ok_button);
        mImageView = (HeadEditImageView) findViewById(R.id.head_edit_image_view);
        mLoadingLayout = (FrameLayout) findViewById(R.id.head_edit_loading_layout);
    }

    private void initEvents() {

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isUploading) {

                    CommonUtil.toast("正在上传图片，请稍后再试!");//正在上传图片，请稍后再试!
                    return;
                }

                final INetResponse uploadResponse = new INetResponse() {
                    @Override
                    public void response(INetRequest req, JSONObject obj) {

//                        SystemUtil.log("HeadUpload", "HeadEditActivity upload response obj = " + jv.toString());

                        if (Methods.checkNoError(req, obj)) {

                            RenrenChatApplication.getUiHandler().post(new Runnable() {
                                @Override
                                public void run() {

                                	CommonUtil.toast("上传成功");//"上传成功"
                                }
                            });

                            /* 更新当前登录用户的内存中数据 */
                            loginInfo = LoginManager.getInstance().getLoginInfo();
                            try{

                            	loginInfo.mMediumUrl = obj.getString("medium_url");
                            	loginInfo.mLargeUrl = obj.getString("large_url");
                            	loginInfo.mOriginal_Url = obj.getString("original_url");
                            } catch(Exception e){
                            	
                            	e.printStackTrace();
                            }

                            /* 调用用户资料页面观察者，实时更新头像  */
                            ProfileFragment.notifyAllPhotoUploadSuccessListeners();

                            /* 更新当前登录用户的数据库 */
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                	
                                	LoginManager.getInstance().updateAccountInfoDB(loginInfo);
                                }
                            }).start();

                            finish();
                        } else {

                            RenrenChatApplication.getUiHandler().post(new Runnable() {
                                @Override
                                public void run() {

                                    CommonUtil.toast("上传失败");//上传失败
                                }
                            });
                        }
                    
                        isUploading = false;
                        RenrenChatApplication.getUiHandler().post(new Runnable() {
                            @Override
                            public void run() {

                                if (null != uploadingDialog && uploadingDialog.isShowing()) {

                                    uploadingDialog.dismiss();
                                }
                            }
                        });
                    }
                };

                float distanceX = mImageView.getMoveHorizontal();
                float distanceY = mImageView.getMoveVertical();
                float frameMarinLeft = (mImageView.getScreenWidth() - selectedFrameDp * density) / 2;
                float frameMarginTop = (mImageView.getScreenHeight() - selectedFrameDp * density) / 2;
                if (distanceX > frameMarinLeft) {
                    distanceX = frameMarinLeft;
                }
                if (distanceY > frameMarginTop) {
                    distanceY = frameMarginTop;
                }
                float frameSize = selectedFrameDp * density;

                final int left = (int) ((frameMarinLeft - distanceX) / (mImageView.getScale()));
                final int top = (int) ((frameMarginTop - distanceY) / (mImageView.getScale()));
                final int width = (int) (frameSize / (mImageView.getScale()));
                final int height = (int) (frameSize / (mImageView.getScale()));
//                SystemUtil.log("HeadUpload", "left = "+left+" top = "+top+" width = "+width);

                RenrenChatApplication.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {

                        CommonUtil.toast("正在上传头像");//"正在上传头像"
                    }
                });
                isUploading = true;
                uploadingDialog = new ProgressDialog(HeadEditActivity.this);
                uploadingDialog.setCanceledOnTouchOutside(false);
                uploadingDialog.setMessage("正在上传");//"正在上传"
                uploadingDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        bytes = mImageView.getBytes();
                        HttpMasService.getInstance().uploadHeadPhoto(uploadResponse, left, top, width, height, bytes);
                    }
                }).start();
            }
        });

        findViewById(R.id.head_edit_turn_left_image_view).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mImageView.turnLeft();
            }
        });

        findViewById(R.id.head_edit_turn_right_image_view).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mImageView.turnRight();
            }
        });
    }

    /**
     * 显示图片
     */
    private void setImage() {

//        if (null == mBitmap) {
//
//            mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_image_loading);
//        }
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        mImageView.setScreenWidth(screenWidth);
        mImageView.setScreenHeight(screenHeight);

        float marginLeft = (screenWidth - density * selectedFrameDp)/2;
        float marginRight = (screenWidth - density * selectedFrameDp)/2;
        float marginTop = (screenHeight - density * selectedFrameDp)/2;
        float marginBottom = (screenHeight - density * selectedFrameDp)/2;
        mImageView.setMarginLeft(marginLeft);
        mImageView.setMarginRight(marginRight);
        mImageView.setMarginTop(marginTop);
        mImageView.setMarginBottom(marginBottom);
//        SystemUtil.log("HeadUpload", "marginLeft = "+marginLeft+" marginTop = "+marginTop);
//        mImageView.setImageWidth(mBitmap.getWidth());
//        mImageView.setImageHeight(mBitmap.getHeight());
        mImageView.setDensity(density);
//        SystemUtil.log("HeadUpload", "screenWith = "+getWindowManager().getDefaultDisplay().getWidth()+" screenHeight = "+getWindowManager().getDefaultDisplay().getHeight()
//        +" density = "+density);
//        mImageView.setImageBitmap(mBitmap);

//        RenRenChatActivity.sHandler.post(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
        mThread = new MyThread(new Runnable() {
            @Override
            public void run() {

                Uri uri = (Uri) HeadEditActivity.this.getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
                mBitmap = PhotoUploadManager.getInstance().getBitmap(HeadEditActivity.this, uri);

                if(null != mBitmap && !mBitmap.isRecycled()){

                    bytes = Bitmap2Bytes(mBitmap);

                    if (!isBitmapCanUse()) {

                    	handler.post(new Runnable() {
                            @Override
                            public void run() {

                                CommonUtil.toast("你选的图片太小了啊，亲，要选"+selectedFrameDp * density + "长度以上的图片哦！");//"你选的图片太小了啊，亲，要选"     "长度以上的图片哦！"
                            }
                        });
                        finish();
                    } else {

//                        SystemUtil.log("HeadUpload", "bitmap.getWidth = " + mBitmap.getWidth() + " bitmap.getHeight = " + mBitmap.getHeight());

                        mImageView.setImageWidth(mBitmap.getWidth());
                        mImageView.setImageHeight(mBitmap.getHeight());
                        mImageView.setBytes(bytes);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                mImageView.setImageBitmap(mBitmap);
                                mLoadingLayout.setVisibility(View.GONE);
                            }
                        });
                        HeadEditImageView.isLoadingBitmap = false;
                    }
                }
            }
        });
        mThread.start();
    }

    private boolean isBitmapCanUse() {

        return (mBitmap.getWidth() >= selectedFrameDp * density) && (mBitmap.getHeight() >= selectedFrameDp * density);
    }

    /**
     * 切换模式
     */
    private void switchMode() {

        setMode(MODE_FULL_SCREEN + MODE_SHOW_TOOL_BAR + mCurrentMode);
    }

    /**
     * 设置模式
     *
     * @param mode 当前模式
     */
    private void setMode(int mode) {

        if (mode == MODE_FULL_SCREEN) {

            mTitleBar.setVisibility(View.INVISIBLE);
            mOperationBar.setVisibility(View.INVISIBLE);
        } else if (mode == MODE_SHOW_TOOL_BAR) {

            mTitleBar.setVisibility(View.VISIBLE);
            mOperationBar.setVisibility(View.VISIBLE);
        }
        mCurrentMode = mode;
    }

    /**
     * 图片转化为Byte数组
     *
     * @param bitmap 需要转化为数据的位图
     * @return 上传给服务器的数据
     */
    private byte[] Bitmap2Bytes(Bitmap bitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try{

            bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            return bytes;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public class MyThread extends Thread{

        public MyThread(Runnable runnable){

            super(runnable);
        }

        public void stopDraw() {

            if (mBitmap != null && !mBitmap.isRecycled()) {

                mBitmap.recycle();
                mBitmap = null;
            }
        }
    }
}
