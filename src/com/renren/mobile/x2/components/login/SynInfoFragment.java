package com.renren.mobile.x2.components.login;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.RenrenChatApplication;
import com.renren.mobile.x2.base.BaseFragment;
import com.renren.mobile.x2.components.chat.message.Subject.DATA;
import com.renren.mobile.x2.components.home.HomeActivity;
import com.renren.mobile.x2.components.home.profile.ProfileFragment;
import com.renren.mobile.x2.components.home.profile.ProfileFragment.PhotoUploadSuccessListener;
import com.renren.mobile.x2.components.imageviewer.ImageViewActivity;
import com.renren.mobile.x2.components.imageviewer.PhotoUploadManager;
import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.img.ImageLoader;
import com.renren.mobile.x2.utils.img.ImageLoaderManager;

public class SynInfoFragment extends BaseFragment<DATA> {
	
	private View mView;
	private static Activity mActivity;

	public TextView mUserName;

	public TextView mGender;

	public TextView mBirthday;

	public static TextView mSchool;
	
	public static TextView mDepartment;

	public static TextView mEnrollYearTxt;

	public static FrameLayout mSchoolLayout;
	
	public FrameLayout mGenderLayout;

	public static FrameLayout mEnrollYearLayout;

	public static FrameLayout mDepartmentLayout;
	
	private FrameLayout mBirthdayLayout;
	private Button sys_info_jump_in;
	private ImageView mGenderArrow,mBirthdayArrow;
	private static ImageView mSchoolArrow;
	private static ImageView mDepartmentArrow;
	private static ImageView mEnrollArrow;
	
	//头像
	public ImageView mUserHeadImageView;
	private ImageLoader loader = ImageLoaderManager.get(
			ImageLoaderManager.TYPE_HEAD, mActivity);
	
	private static String mDepartmentId;
	private static String mDepartmentName;
	private static String mEnrollYear;
	private static String mSchoolId;
	private static String mSchoolName;
	private String birth_year;
	private String birth_month;
	private String birth_day;
	private String gender;
	private Handler mHandler = new Handler();
	private int birthIndex = 0;
	private int genderIndex;
	
	
	/**
	 * 用户个人信息
	 */
	private HashMap<String, String> profileInfo = new HashMap<String, String>();
	
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mActivity = (SynInfoActivity) getActivity();
		mView = View.inflate(mActivity, R.layout.syn_info, null);
		return mView;
	}

	private void initView() {
		
		mUserName = (TextView)mView.findViewById(R.id.userName_textview);
		mGender = (TextView)mView.findViewById(R.id.userGender_textview);
		mBirthday = (TextView)mView.findViewById(R.id.userBirthday_textview);
		mSchool = (TextView)mView.findViewById(R.id.userSchool_textview);
		mEnrollYearTxt = (TextView)mView.findViewById(R.id.userTime_textview);
		mDepartment = (TextView)mView.findViewById(R.id.userDepartment_textview);
		mSchoolLayout = (FrameLayout)mView.findViewById(R.id.user_school_layout);
		mGenderLayout = (FrameLayout)mView.findViewById(R.id.user_gender_layout);
		mDepartmentLayout = (FrameLayout)mView.findViewById(R.id.user_department_layout);
		mEnrollYearLayout = (FrameLayout)mView.findViewById(R.id.user_schoolTime_layout);
		mUserHeadImageView = (ImageView)mView.findViewById(R.id.edit_head_imageview);
		mBirthdayLayout = (FrameLayout)mView.findViewById(R.id.user_birthday_layout);
		sys_info_jump_in = (Button)mView.findViewById(R.id.sys_info_jump_in);
		
		mGenderArrow = (ImageView)mView.findViewById(R.id.gender_arrow);
		mBirthdayArrow = (ImageView)mView.findViewById(R.id.birthday_arrow);
		mSchoolArrow = (ImageView)mView.findViewById(R.id.school_arrow);
		mDepartmentArrow = (ImageView)mView.findViewById(R.id.department_arrow);
		mEnrollArrow = (ImageView)mView.findViewById(R.id.enroll_arrow);
		
		getTitleBar().hide();
		
		//选择学校
		mSchoolLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity,
						SelectCollegeActivity.class);  
				startActivity(intent);
			}
		});
		
		//选择生日
		mBirthdayLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createBirthdayDialog().show();
			}
		});
		
		//选择性别
		mGenderLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createGenderDialog().show();
			}
		});

		// 修改头像
		mUserHeadImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectHeadDialog(mActivity);
			}
		});
		
		// 点击进入按钮后。。。
		sys_info_jump_in.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// 先判断应该更改的信息是否都已经更改：生日信息、学校信息、入学年份
				
				
				// 向服务器更新个人资料，成功后跳到功能引导页面
				
				if(checkAllInfo()){
				// 如果生日没有，并且修改了，则赋值，并且检测是否为空。。。
					if(!TextUtils.isEmpty(birth_day)&& !TextUtils.isEmpty(birth_month)
							&& !TextUtils.isEmpty(birth_year)){
						profileInfo.put("birth_year", birth_year);
						profileInfo.put("birth_month", birth_month);
						profileInfo.put("birth_day", birth_day);
					}
			
				
				// 检测性别是否有值：如果默认有，就不可修改，默认没有，就可以修改，并参加参数中
				if(!TextUtils.isEmpty(gender)){
					profileInfo.put("gender", gender);
				}
				
				
				profileInfo.put("school_id", mSchoolId); // 学校ID
				profileInfo.put("school_department_id", mDepartmentId);   // 院系ID
				profileInfo.put("school_enroll_year", mEnrollYear);  // 入学年份
				
				HttpMasService.getInstance().completeUserInfo(profileInfo, response);
				// 应该在response成功后跳转到引导页，需和产品商量下
//				Intent intent = new Intent(mActivity, UserGuideActivity.class);
				Intent intent = new Intent(mActivity, HomeActivity.class);
				startActivity(intent);
				mActivity.finish();
				}else{
					CommonUtil.toast(R.string.syninfo_user_notice);
				}
				
				
			}
		});
		
		//初始化各个TextView的赋值
		LoginInfo info = LoginManager.getInstance().getLoginInfo();
		if(info!=null){
			if(TextUtils.isEmpty(info.mUserName)){
				mUserName.setTextAppearance(mActivity, R.style.T4);
				mUserName.setText(mActivity.getResources().getString(R.string.syninfo_user_hint_noUserName));
			}else{
				mUserName.setText(info.mUserName);
			}
			
			if(info.mGender==-1){
				mGender.setTextAppearance(mActivity, R.style.T4);
				mGender.setText(mActivity.getResources().getString(R.string.syninfo_user_hint_noGender));
			}else{
				gender = String.valueOf(info.mGender);
				mGender.setText(info.mGender==1?mActivity.getResources().getString(R.string.syninfo_user_hint_male)
						:mActivity.getResources().getString(R.string.syninfo_user_hint_female));
				mGenderLayout.setClickable(false);//测试关掉，以后要启用
				mGenderArrow.setVisibility(View.GONE);
			}
			
			
			if(TextUtils.isEmpty(info.mSchool)||TextUtils.isEmpty(info.mDepartment)||TextUtils.isEmpty(info.mEnrollyear)){
				mEnrollYearLayout.setVisibility(View.GONE);
				mDepartmentLayout.setVisibility(View.GONE);
				mSchoolLayout.setBackgroundResource(R.drawable.item_single_bg_selector);
				mSchool.setTextAppearance(mActivity, R.style.T4);
				mSchool.setText(mActivity.getResources().getString(R.string.syninfo_user_hint_noSchool));
			}else{
				mSchool.setText(info.mSchool);
				mSchoolName = info.mSchool;
				mSchoolLayout.setClickable(false);//测试时关掉，以后要启用
				mDepartment.setText(info.mDepartment);
				mDepartmentName = info.mDepartment;
				mEnrollYearTxt.setText(info.mEnrollyear);
				
				mSchoolId = info.mSchool_id;
				mDepartmentId = String.valueOf(info.mDepartmentid);
				mEnrollYear = info.mEnrollyear;
				
				mSchoolArrow.setVisibility(View.GONE);
				mDepartmentArrow.setVisibility(View.GONE);
				mEnrollArrow.setVisibility(View.GONE);
				
			}
			
			if(TextUtils.isEmpty(info.mBirthday)){
				mBirthday.setTextAppearance(mActivity, R.style.T4);
				mBirthday.setText(mActivity.getResources().getString(R.string.syninfo_user_hint_noBirthday));
				
			}else{
				mBirthdayLayout.setClickable(false);//测试时关掉，以后要启用
				mBirthday.setText(info.mBirthday);
				mBirthdayArrow.setVisibility(View.GONE);
				
			}
			
			
		}else{
			CommonUtil.log("login","info is null");
		}

	}

	protected Dialog createGenderDialog() {
		
		final String[] genderS = new String[]{mActivity.getResources().getString(R.string.syninfo_user_hint_female),
				mActivity.getResources().getString(R.string.syninfo_user_hint_male)};
	return new AlertDialog.Builder(mActivity)
      .setTitle(mActivity.getResources().getString(R.string.syninfo_user_gender_dialog_title))  
      .setItems(genderS, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				genderIndex = which;
				mGender.setText(genderS[genderIndex]);
				gender = String.valueOf(genderIndex);
         
			}
      }) 
     .create();
	}

	/**
	 * 检查所有信息是否都已填写，主要检查学校院系信息
	 * @return true,所有信息已填写；false,信息不完整
	 */
	protected boolean checkAllInfo() {

		if(!TextUtils.isEmpty(mSchool.getText()) && !TextUtils.isEmpty(mDepartment.getText())
				&&!TextUtils.isEmpty(mEnrollYearTxt.getText()) && !TextUtils.isEmpty(mUserName.getText())){
			return true;
		}
		return false;
	}

	private PhotoUploadSuccessListener listener;

	public void initPhotoListener() {
		listener = new PhotoUploadSuccessListener() {
			public void updateUI_Photo() {
				RenrenChatApplication.getUiHandler().post(new Runnable() {
					@Override
					public void run() {
						mUserHeadImageView.setImageBitmap(null);
						loadHeadImg(mUserHeadImageView, LoginManager
								.getInstance().getLoginInfo().mMediumUrl,
								false);

					}
				});
			}
		};
	}

	/**
	 * 下载头像
	 * @param imageView
	 * @param imageUrl
	 * @param isCover
	 */
	private void loadHeadImg(final View imageView, String imageUrl,
			final Boolean isCover) {
		loader.get(new ImageLoader.HttpImageRequest(imageUrl, true),
				new ImageLoader.UiResponse() {
					@Override
					public void uiSuccess(Bitmap mBitmap) {
						final Bitmap bitmap = mBitmap;
						RenrenChatApplication.getUiHandler().post(
								new Runnable() {
									@Override
									public void run() {
										((ImageView) imageView)
												.setImageBitmap(bitmap);
									}
								});
					}

					@Override
					public void failed() {
					}
				});

	}
	/** 选择修改头像 */
	public void selectHeadDialog(final Activity activity) {
		final String items[] = { mActivity.getResources().getString(R.string.profile_changehead_viewimg),
				mActivity.getResources().getString(R.string.profile_changehead_local),
				mActivity.getResources().getString(R.string.profile_changehead_camera) };
		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO
				Intent intent = null;
				switch (which) {
				case 0://查看大图
					ImageViewActivity.show(activity,LoginManager.getInstance().getLoginInfo().mOriginal_Url,false);
					break;
				case 1:// 拍照上传
					String fileName = "head_" + String.valueOf(System.currentTimeMillis());
					intent = PhotoUploadManager.getInstance().getTakePhotoIntent("/sixin/", fileName, ".jpg");
					activity.startActivityForResult(intent, PhotoUploadManager.REQUEST_CODE_HEAD_TAKE_PHOTO);
					break;
				case 2:// 本地上传
					activity.startActivityForResult(PhotoUploadManager.getInstance().getChooseFromGalleryIntent(),
							PhotoUploadManager.REQUEST_CODE_HEAD_CHOOSE_FROM_GALLERY);
					break;
				default:
					break;
				}
			}
		};
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
					PhotoUploadManager.getInstance().createUploadDialog(activity,
							mActivity.getResources().getString(R.string.profile_changehead), items,
							dialogClickListener);
			}
		});
	}

	private static final List<PhotoUploadSuccessListener> PHOTO_UPLOAD_SUCCESS_LISTENERS = new ArrayList<PhotoUploadSuccessListener>();

	public static void registerPhotoUploadSuccessListener(
			PhotoUploadSuccessListener listener) {
		if (listener != null
				&& !(PHOTO_UPLOAD_SUCCESS_LISTENERS.contains(listener))) {
			PHOTO_UPLOAD_SUCCESS_LISTENERS.add(listener);
		}
	}

	// public static void notifyAllPhotoUploadSuccessListeners() {
	// for (PhotoUploadSuccessListener listener :
	// PHOTO_UPLOAD_SUCCESS_LISTENERS)
	// listener.updateUI_Photo();
	// }
	//
	// public static void
	// unregisterPhotoUploadSuccessListener(PhotoUploadSuccessListener listener)
	// {
	// PHOTO_UPLOAD_SUCCESS_LISTENERS.remove(listener);
	// }

	
	/**
	 * 选择完院系信息后更新UI
	 * @param schoolId
	 * @param schoolName
	 * @param departmentId
	 * @param departmentName
	 * @param enrollYear
	 */
	public static void updateInfo(String schoolId, String schoolName, String departmentId, String departmentName, String enrollYear){
		CommonUtil.log("tag", "SynInfoFragment " + schoolId + schoolName.toString() + departmentId + departmentName + enrollYear);
		mSchool.setTextAppearance(mActivity, R.style.T17);
		mDepartment.setTextAppearance(mActivity, R.style.T17);
		mEnrollYearTxt.setTextAppearance(mActivity, R.style.T17);
		
		mSchoolId = schoolId;
		mSchoolName = schoolName;
		mDepartmentId = departmentId;
		mDepartmentName = departmentName;
		mEnrollYear = enrollYear;
		
		mSchoolLayout.setBackgroundResource(R.drawable.item_top_bg_selector);
		mSchool.setText(schoolName.toString());
		mSchoolArrow.setVisibility(View.GONE);
		
		mDepartmentLayout.setVisibility(View.VISIBLE);
		mDepartment.setText(departmentName);
		mDepartmentArrow.setVisibility(View.GONE);
		
		mEnrollYearLayout.setVisibility(View.VISIBLE);
		mEnrollYearTxt.setText(mEnrollYear);
		mEnrollArrow.setVisibility(View.GONE);
	}

	@Override
	protected void onPreLoad() {
		initView();

		initPhotoListener();

		ProfileFragment.registerPhotoUploadSuccessListener(listener);
		
//		 初始化载入用户的头像
		loadHeadImg(mUserHeadImageView, LoginManager
				.getInstance().getLoginInfo().mMediumUrl,
				false);
	}

	@Override
	protected void onFinishLoad(DATA data) {
		
	}

	@Override
	protected DATA onLoadInBackground() {
		return null;
	}

	@Override
	protected void onDestroyData() {
		
	}
	
	//更新用户资料返回的response,需要对应地更新数据库和LoginManger中的loginInfo
	INetResponse response = new INetResponse() {
		@Override
		public void response(final INetRequest req, JSONObject obj) {
			if (obj != null) {
				final JSONObject map = (JSONObject) obj;
				CommonUtil.log("wyf", "update profile info = " + map.toString());
//				mHandler.post(new Runnable() {
//					@Override
//					public void run() {
						if (Methods.checkNoError(req, map)) {
							
							LoginInfo info = LoginManager.getInstance().getLoginInfo();
							info.mSchool = mSchoolName;
							info.mSchool_id = mSchoolId;
							info.mDepartment = mDepartmentName;
							info.mDepartmentid = Integer.parseInt(mDepartmentId);
							info.mEnrollyear = mEnrollYear;
							info.mGender = Integer.parseInt(gender);
							LoginManager.getInstance().initLoginInfo(info);
							info.mBirthday = mBirthday.getText().toString().trim();
							LoginManager.getInstance().updateAccountInfoDB(info);
							
						}
//					}
//				});
			}
		}
	};
	
	private Dialog createBirthdayDialog(){
		Calendar c=Calendar.getInstance();
        return new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
            	Calendar tempCalendar = Calendar.getInstance();
            	long currentTime = tempCalendar.getTimeInMillis();
            	tempCalendar.set(year, monthOfYear, dayOfMonth);
            	long selectTime = tempCalendar.getTimeInMillis();
            	if(selectTime > currentTime){// 未来时间不可选取
            		CommonUtil.toast(R.string.syninfo_user_notice_timeSelect);
            	}else{
            		if(!(year == 0 && monthOfYear == 0 && dayOfMonth == 0)){
            			Calendar cal = Calendar.getInstance();
            			cal.set(Calendar.YEAR, year);
            			cal.set(Calendar.MONTH, monthOfYear);
            			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            			String tem = sdf.format(cal.getTime());
            			mBirthday.setTextAppearance(mActivity, R.style.T17);
            			mBirthday.setText(tem);
            			birth_day = String.valueOf(dayOfMonth);
            			birth_month = String.valueOf(monthOfYear);
            			birth_year = String.valueOf(year);
            		}
    				
            	}
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

	}

}
