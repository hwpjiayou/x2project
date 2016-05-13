package com.renren.mobile.x2.utils.voice;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renren.mobile.x2.R;
import com.renren.mobile.x2.components.home.feed.FeedModel;
import com.renren.mobile.x2.components.login.LoginManager;
import com.renren.mobile.x2.components.publisher.FilterActivity;
import com.renren.mobile.x2.network.mas.HttpMasService;
import com.renren.mobile.x2.network.mas.INetRequest;
import com.renren.mobile.x2.network.mas.INetResponse;
import com.renren.mobile.x2.network.mas.RequestQueueManager;
import com.renren.mobile.x2.network.mas.UGC;
import com.renren.mobile.x2.network.mas.UGCImgModel;
import com.renren.mobile.x2.network.mas.UGCManager;
import com.renren.mobile.x2.network.mas.UGCModel;
import com.renren.mobile.x2.network.mas.UGCTagModel;
import com.renren.mobile.x2.utils.CommonUtil;
import com.renren.mobile.x2.utils.Methods;
import com.renren.mobile.x2.utils.ViewMapUtil;
import com.renren.mobile.x2.utils.ViewMapping;
import com.renren.mobile.x2.utils.log.Logger;
import com.renren.mobile.x2.utils.voice.PlayerThread.PlayRequest;

@ViewMapping(ID = R.layout.voicetest)
public class VoiceTestActivity extends Activity {
	
//	@ViewMapping(ID = R.id.gzip)
//	public Button gzip;
//	@ViewMapping(ID = R.id.cover)
	public Button cover;
	@ViewMapping(ID = R.id.imgpost)
	public Button imgpost;
	@ViewMapping(ID = R.id.others)
	public Button others;
	@ViewMapping(ID = R.id.topten)
	public Button topten;
	@ViewMapping(ID = R.id.neighbors)
	public Button neighbors;
	@ViewMapping(ID = R.id.locate)
	public Button locate;
	@ViewMapping(ID = R.id.profile)
	public Button profile;
	
	@ViewMapping(ID = R.id.page)
	public Button page;
	@ViewMapping(ID = R.id.pageText)
	public TextView pageText;
	
	
	/**
	 * @since 2012-10-30 
	 * */
	@ViewMapping(ID = R.id.school)
	public EditText school;
	@ViewMapping(ID = R.id.sschool)
	public Button sschool;
	@ViewMapping(ID = R.id.department)
	public EditText department;
	@ViewMapping(ID = R.id.sdepartment)
	public Button sdepartment;
	@ViewMapping(ID = R.id.update)
	public Button update;
	
	/**
	 * @since 2012-11-01
	 * */
	@ViewMapping(ID = R.id.sendpost)
	public Button sendpost;
	@ViewMapping(ID = R.id.deletepost)
	public Button deletepost;
	@ViewMapping(ID = R.id.sendcomment)
	public Button sendcomment;
	@ViewMapping(ID = R.id.deletecomment)
	public Button deletecomment;
	@ViewMapping(ID = R.id.getcomment)
	public Button getcomment;
	@ViewMapping(ID = R.id.deletelike)
	public Button deletelike;
	@ViewMapping(ID = R.id.sendlike)
	public Button sendlike;
	@ViewMapping(ID = R.id.getfeed)
	public Button getfeed;
	@ViewMapping(ID = R.id.getfeeds)
	public Button getfeeds;
	@ViewMapping(ID = R.id.myfeeds)
	public Button myfeeds;
	
	@ViewMapping(ID = R.id.getOutlineIds)
	public Button getOutlineIds;
	
	@ViewMapping(ID = R.id.recoverQueueFromFile)
	public Button recoverQueueFromFile;
	
	@ViewMapping(ID = R.id.say)
	public Button say;
	@ViewMapping(ID = R.id.shutup)
	public Button shutup;
	@ViewMapping(ID = R.id.play)
	public Button play;
	
	@ViewMapping(ID = R.id.content)
	public LinearLayout content;
	
	@ViewMapping(ID = R.id.send)
	public Button sendvoice;
	@ViewMapping(ID = R.id.getvoice)
	public Button getvoice;
	@ViewMapping(ID = R.id.uploadp)
	public Button uploadp;
	@ViewMapping(ID = R.id.uploadh)
	public Button uploadh;
	
	@ViewMapping(ID = R.id.textSend)
	public EditText textSend;
	@ViewMapping(ID = R.id.getImg)
	public Button getImg;
	
	@ViewMapping(ID = R.id.sendfeed)
	public Button sendfeed;
	
	@ViewMapping(ID = R.id.sendofflinefeed)
	public Button sendofflinefeed;
	
	@ViewMapping(ID = R.id.getOutlineNum)
	public Button getOutlineNum;
	
	public static String sessionKey= "";
	
	public static String feedId = "2853";
	public String schoolId = "";
	public static String commentID = "";
	public static String afterId ="";

	Logger logger = new Logger("NCS");
	
	private String mFileName = "";
	private int mVoiceLength;
	String fileUrl="";
	String voiceUrl = "";	
	
	
	public static final String tag = "jason";
	
	public static final String ugc = "{\"content\":[{\"type\":\"text\",\"content\":{\"text\":\"我是要成为海贼王的男人！！！\"}}]}";
	
	public String ugcForImg = "";
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ViewMapUtil.viewMapping(this));
		schoolId = LoginManager.getInstance().getLoginInfo().mSchool_id;
		Log.d(tag,"mschoolId:"+schoolId);
		
//		gzip.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				//HttpMasService.getInstance().getSchoolFeeds(schoolId, 15, "", "","gz", feedsResponse);
//			}});
		
//		cover.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				sendCover();
//				
//			}});
		
		imgpost.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(ugcForImg)) {
					CommonUtil.toast("请先上传图片,本页面有这个传图片按钮");
					return;
				}
				JSONObject object;
				try {
					object = new JSONObject(ugcForImg);
					
					HttpMasService.getInstance().sendPost(object, schoolId, response1, false);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}});
		
		others.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				HttpMasService.getInstance().getSomeOnesFeed("1256043999", "", 10, "", response1);
			}});
		page.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				HttpMasService.getInstance().getSchoolFeeds(schoolId, 10, afterId, "post,feed_id", feedsResponse1);
			}});
		
//		voicenew.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				if (TextUtils.isEmpty(feedId)) {
//					CommonUtil.toast("feedId is empty ,please send a post");
//				} else {
//					 UGC feed = makeFeedRequests();
//					 UGCManager.getInstance().sendUGC(feed);
//				}
//			}});
//		
//		sendvoicecomment.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				if (TextUtils.isEmpty(feedId)) {
//					CommonUtil.toast("feedId is empty ,please send a post");
//				} else {
//					 UGC feed = makeFeedRequests();
//					 UGCManager.getInstance().sendUGC(feed);
//				}
//			}});
		
		profile.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String id = LoginManager.getInstance().getLoginInfo().mUserId;
				Log.d(tag,"my uid:" + id);
				HttpMasService.getInstance().getProfile(id, response1);
			}});
		
		topten.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HttpMasService.getInstance().getTopTen("5002","comments_size,post,", response1);
			}});
		neighbors.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HttpMasService.getInstance().getNeighbors(255000000l, 255000000l, "", 0, 10, 0, 0, response1);
			}});
		
		locate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				try {
					JSONObject object = new JSONObject(ugc);
					Log.d(tag,object.toString());
					//.getMyLocation(39.9588409l, 116.434287l, 0, 0, response1);
					HttpMasService.getInstance().getMyLocation(255000000l, 255000000l,"", 0, 0, response1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}});
		
		sendpost.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				try {
//					if (TextUtils.isEmpty(fileUrl)){
//						CommonUtil.toast("feedId is empty ,Empty");
//						return;
//					}
					//String ugc = "{\"content\":[{\"type\":\"voice\",\"content\":{\"voice_url\":\""+ fileUrl +"\",\"duration\":\"60\"}}]}";
					JSONObject object = new JSONObject(ugc);
					Log.d(tag,"jaja" + object.toString());
					HttpMasService.getInstance().sendPost(object, schoolId, postresponse, false);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}});
		
		deletepost.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(feedId)) {
					CommonUtil.toast("请先发帖子");
				} else {
					HttpMasService.getInstance().deletePost(schoolId, "106", "106", dpostresponse);
				}
			}});
		
		sendcomment.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try {
					JSONObject content = new JSONObject(ugc);
					if (TextUtils.isEmpty(feedId)) {
						CommonUtil.toast("请先发帖子");
					} else {
						HttpMasService.getInstance().postComment(schoolId, feedId, content, response1);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}});
		
		deletecomment.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HttpMasService.getInstance().deleteComment(schoolId, feedId, "1351258534605", response1);
			}});
		getcomment.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HttpMasService.getInstance().getPostComments(schoolId, feedId, 10, "", "", response1);
			}});
		
		sendlike.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HttpMasService.getInstance().postLike(schoolId, feedId, response1);
			}});
		deletelike.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				HttpMasService.getInstance().deleteLike(schoolId, feedId, feedId, response1);
			}});
		
		getfeed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.d(tag,feedId);
				if (TextUtils.isEmpty(feedId)) {
					CommonUtil.toast("请先发帖子");
				} else {
					HttpMasService.getInstance().getFeedByID(schoolId, feedId, "post,comments,comments_count,like_count", response1);
				}
			}});
		getfeeds.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				HttpMasService.getInstance().getSchoolFeeds(schoolId, 15, "", "", feedsResponse);
			}});
		
		myfeeds.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				HttpMasService.getInstance().getMyFeeds(10, "", "", response1);
			}});
		
		
		update.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HashMap<String ,String> map = new HashMap<String, String>();
//				String uid  = LoginManager.getInstance().getLoginInfo().mUserId;
//				map.put("user_id", uid);
				map.put("birth_year", "1990");
				map.put("birth_month", "07");
				map.put("birth_day", "08");
				map.put("school_enroll_year", "2009");
				map.put("school_id","1002");
				map.put("school_department_id","1333");
				map.put("gender", "0");
				map.put("name", "璐璐");
				HttpMasService.getInstance().completeUserInfo(map, response1);
			}});
		sschool.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String q = school.getText().toString();
				if (TextUtils.isEmpty(q)) {
					CommonUtil.toast("empty shool");
				} else {
					HttpMasService.getInstance().searchSchool(q, response1 , false);
				}
				
			}});
		
		sdepartment.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String schoolId = department.getText().toString();
				if (TextUtils.isEmpty(schoolId)) {
					CommonUtil.toast("empty department");
				} else {
					HttpMasService.getInstance().searchDepartment(schoolId, response1, false);
				}
				
			}});
		
		say.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				createVoiceName();
				StartRecording();
			}});
		shutup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				endRecording();
			}
			
		});
		play.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				File file = new File(mFileName);
				Log.d(tag, "from play : " + mFileName);
				if (file.exists() && file.length() > 0) {
					final PlayRequest request = new PlayRequest();
					request.mAbsVoiceFileName = mFileName;
					if(PlayerThread.getInstance().forceToPlay(request)){
						PlayerThread.getInstance().onAddPlay(request);
					};
				}
			}	
		});
		
		sendvoice.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				send();
			}});
		getvoice.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getvoice();
			}});
		
		uploadp.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sendImg();
			}});
		uploadh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				isUploadHead = true;
				showPhotoDialog(false);
			}});
		textSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				textSend.setText("");
			}
		});
		getImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showPhotoDialog(false);
			}
		});
		
		sendfeed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				 UGC feed = makeFeedRequests();
				 UGCManager.getInstance().sendUGC(feed);
			}});
		
		sendofflinefeed.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				RequestQueueManager.getInstance().sendOutlineUGCs();
			}});
		
		getOutlineNum.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int requestQueueSize = RequestQueueManager.getInstance().getRequestQueueSize();
				int secondQueueSize = RequestQueueManager.getInstance().getSecondQueueSize();
				int blockedFeedNum = UGCManager.getInstance().getBlockedUGCNum();
				CommonUtil.toast("feed offline pool size is : " + blockedFeedNum + "/" +requestQueueSize + "-" + secondQueueSize);
			}});
		
		getOutlineIds.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<Long> idList = new ArrayList<Long>();
				RequestQueueManager.getInstance().getOutLineUGCIds(idList);
				logger.d("idList:"+idList.toString()+"|"+"size:"+idList.size());
				CommonUtil.toast(idList.toString());
				CommonUtil.toast("size:"+idList.size());
			}
		});
		
		recoverQueueFromFile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RequestQueueManager.getInstance().recoverQueue();
			}
		});
	}
	
	private String strFilePath;
	// request code
	private static final int REQUESTCODE_TAKEPHOTO = 0;
	private static final int REQUESTCODE_SELECTPHOTO = 1;
	private static final int REQUESTCODE_EDITPHOTO = 2;
	private static final int REQUESTCODE_FILTER = 3;
	private boolean isUploadHead = false;
	
	/**
	 * 照片dialog
	 */
	private void showPhotoDialog(boolean isExistPhoto) {
		CharSequence[] items;
		if(isExistPhoto){
			//已经存在图片的情况下 三个选项
			items = new CharSequence[] {getResources().getString(R.string.publisher_dialog_take),getResources().getString(R.string.publisher_dialog_select_from_album),getResources().getString(R.string.publisher_dialog_delete)};
		}else{
			//不存在图片的情况下 两个选项
			items = new CharSequence[] {getResources().getString(R.string.publisher_dialog_take),getResources().getString(R.string.publisher_dialog_select_from_album)};
		}
		
		AlertDialog dlg = new AlertDialog.Builder(VoiceTestActivity.this).setTitle(getResources().getString(R.string.publisher_dialog_title)).setItems(items, new
		DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// 这里item是根据选择的方式，
				// 在items数组里面定义了两种方式，拍照的下标为1所以就调用拍照方法
				//对路径的一个预处理
				if(item==0||item==1){
					//拍照
					Log.d("pa","拍照 path");
					// 将拍照得到的图片首先保存在SD卡指定的位置中
					String strPath = Environment.getExternalStorageDirectory().toString()+"/x2/upload";
					//文件夹的检测和创建
					File path = new File(strPath);
					if (!path.exists())
						path.mkdirs();
					Log.d("pa","拍照 nomedia");
					//.nomedia文件的检测和创建
					File noMedia = new File(strPath+"/.nomedia");
					if(!noMedia.exists()){
						try {
							noMedia.createNewFile();
						} catch (IOException ignore) {
						}
					}
					// TODO 有待修改，指定的目录和随机的名字？
					String strFileName = System.currentTimeMillis()+".jpg";
					Log.d("pa","拍照 filename:"+strFileName);
					strFilePath = strPath + "/" + strFileName;
				}
				switch(item){
				case 0:
					File file = new File(strFilePath);
					Uri uri = Uri.fromFile(file);
					Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
					getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					Log.d("pa","拍照 start");
					startActivityForResult(getImageByCamera, REQUESTCODE_TAKEPHOTO);
					break;
				case 1:
					//从相册中选取
					Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
					getImage.addCategory(Intent.CATEGORY_OPENABLE);
					getImage.setType("image/jpeg");
					startActivityForResult(getImage, REQUESTCODE_SELECTPHOTO);
					break;
				case 2:
					//删除当前准备上传的图片
					mbyteImage = null;
					//默认图片
					break;
				}
				
			}
		}).create();
		dlg.show();
	}

	Bitmap testBitmap;
	private byte[] mbyteImage;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		logger.d("requestCode:"+requestCode);
		switch (requestCode) {
		case REQUESTCODE_SELECTPHOTO:
			try {
				// 获得图片的uri
				Uri originalUri = data.getData();
				Intent sIntent = new Intent();
				sIntent.setData(originalUri);
				sIntent.putExtra("destination", strFilePath);
				sIntent.setClass(getApplicationContext(), FilterActivity.class);
				startActivityForResult(sIntent, REQUESTCODE_FILTER);
			} catch (Exception ignore) {
			}
			break;
		case REQUESTCODE_TAKEPHOTO:
			// 通过文件的存在性判断拍照是否成功
			try {
				File file = new File(strFilePath);
				if (file.exists()) {
					Intent sIntent = new Intent();
					sIntent.putExtra("path", strFilePath);
					sIntent.putExtra("destination", strFilePath);
					sIntent.setClass(getApplicationContext(), FilterActivity.class);
					startActivityForResult(sIntent, REQUESTCODE_FILTER);
				}
			} catch (Exception ignore) {
			}
			break;
		case REQUESTCODE_FILTER:
			CommonUtil.toast("here filter");
			boolean success = false;
			if (data != null) {
				success = data.getBooleanExtra("success", false);
			}
			if (success) {
				testBitmap = BitmapFactory.decodeFile(strFilePath);
				mbyteImage = Bitmap2Bytes(testBitmap);
			} else {
				mbyteImage = null;
			}
			logger.d("@REQUEST_FILTER set mCameraView bitmap");
			if (isUploadHead) {
				sendHeadImg();
				isUploadHead = false;
			}
			break;
		}
	}
	
	private INetResponse postresponse = new INetResponse(){

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				//CommonUtil.toast("success:"+data.toString());
				String feedIdd = data.optString("feed_id");
				VoiceTestActivity.feedId = feedIdd;
				CommonUtil.toast("success: feedId is " + feedId);
				Log.d(tag,feedId +"|" + data.toString());
			}else{
				CommonUtil.toast("error:"+data.toString());

			}
		}
		
	};
	
	private INetResponse dpostresponse = new INetResponse(){

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				CommonUtil.toast("success:"+data.toString());
				feedId = "";
				CommonUtil.toast("success: delete post,请重新发帖,以便于测试" );
				Log.d(tag,data.toString()+":delete");
			}else{
				CommonUtil.toast("error:"+data.toString());

			}
		}
		
	};
	
	private INetResponse response1 = new INetResponse(){

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			logger.d("response1:"+ data.toString());
			if(Methods.checkNoError(req, data)){
				//toast("success:"+data.toString());
				CommonUtil.toast("success:"+data.toString());
				Log.d(tag, data.toString());
			}else{
				//toast("error"+data.toString());
				CommonUtil.toast("error:"+data.toString());

			}
		}
		
	};
	
	private INetResponse feedsResponse = new INetResponse() {
		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				Log.d(tag,data.toString());
//				JSONArray array = data.optJSONArray("feeds");
//				JSONObject object = null;
//				int length = array.length();
//				for (int i = 0 ; i < array.length() ; i ++) {
//					object = array.optJSONObject(i);
//					FeedModel model = new FeedModel(object);
//					Log.d(tag,"feedId|client_time:" + model.getFeedId()+"_"+model.getmPostContentModel().clientTime);
//					if (i == (length - 1))
//						feedId = String.valueOf(model.getFeedId());
//				}
			}else{
				CommonUtil.toast("error: " + data.toString());
			}
		}
	};
	
	private INetResponse feedsResponse1 = new INetResponse() {
		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				Log.d(tag,"page:" +data.toString());
				JSONArray array = data.optJSONArray("feeds");
				JSONObject object = null;
				int length = array.length();
				for (int i = 0 ; i < array.length() ; i ++) {
					object = array.optJSONObject(i);
					FeedModel model = new FeedModel(object);
					//Log.d(tag,"feedId|client_time:" + model.getFeedId()+"_"+model.getmPostContentModel().clientTime);
					Map<String , UGCModel> models = model.getmPostContentModel().contentInfo;
//					if (models.containsKey(UGCModel.UGCType.IMG)) {
//						UGCImgModel tmp = (UGCImgModel) models.get(UGCModel.UGCType.IMG);
//						Log.d(tag, tmp.mOriginalUrl);
//					}
					Log.d(tag,"feedId|client_time:" + model.getFeedId()+"_"+model.getmPostContentModel().clientTime);
					if (i == (length - 1)) {
						afterId = String.valueOf(model.getFeedId());
						Log.d(tag, "afterId:" + afterId);
						VoiceTestActivity.this.runOnUiThread(new Runnable(){
							@Override
							public void run() {
								pageText.setText("afterId:" + afterId);
							}});
					}	
				}
			}else{
				CommonUtil.toast("error: " + data.toString());
			}
		}
	};
	
//	FeedResponse feedresponse = new FeedResponse(response1);
	
	public UGC makeFeedRequests() {
		INetRequest imageRequest = null;
		if (testBitmap != null) {
			byte[] bytes = Bitmap2Bytes(testBitmap);
			imageRequest = UGCManager.getInstance().getImageUploadRequest(bytes);
		}
		INetRequest voiceRequest = this.getVoiceRequest();
		UGCTagModel tagModel = new UGCTagModel(6);
		//UGCPlaceModel placeModel = new UGCPlaceModel("阶梯教室", 36.12323, 126.12323);
		String text = textSend.getText().toString();
		if (TextUtils.isEmpty(text)) {
			text = null;
		}
		INetRequest textRequest = UGCManager.getInstance().getUGCUploadRequest(text, tagModel, null);
		UGC feed = new UGC(schoolId ,UGC.UGC_TYPE_FEED, Integer.parseInt(feedId) ,voiceRequest, imageRequest, textRequest, response1);
		
		return feed;
	}
	

	public void sendCover() {
		Bitmap test = BitmapFactory.decodeResource(getResources(), R.drawable.test_cover_2);
		byte[] bytes = Bitmap2Bytes(test);
		HttpMasService.getInstance().uploadCover(bytes, 0.633, "特效" ,response1);
		
	}
	
	
	
	public void sendImg() {
		Bitmap test = BitmapFactory.decodeResource(getResources(), R.drawable.test_cover_2);
		byte[] bytes = Bitmap2Bytes(test);
		HttpMasService.getInstance().uploadPhoto(sendImgResponse, bytes);
		
	}
	
	
	private INetResponse sendImgResponse = new INetResponse(){

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				CommonUtil.toast("success:"+data.toString());
				UGCImgModel model = new UGCImgModel(data);
				ugcForImg = model.buildTest().toString();
			}else{
				CommonUtil.toast("error:"+data.toString());
			}
		}
		
	};
	
	
	public void sendHeadImg() {
		Log.d(tag,"sendHeadImg");
		if (mbyteImage != null && mbyteImage.length > 0 ) {
			HttpMasService.getInstance().uploadHeadPhoto(response1, 80, 80, 400, 400, mbyteImage);
			Log.d(tag,"sendHeadImg imagebytes not null");
		} else {
			Log.d(tag,"sendHeadImg imagebytes null");
			CommonUtil.toast("头像数据为空,请重新获取");
		}
	}
	
	private byte[] Bitmap2Bytes(Bitmap bitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            return bytes;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
	
	public INetRequest getVoiceRequest() {
		if(!TextUtils.isEmpty(mFileName) && mFileName.length() > 0 ) {
			Log.d(tag,"send");
			File a = new File(mFileName);
			byte[] bytes = new byte[(int)a.length()];
			FileInputStream fis;
			try {
				fis = new FileInputStream(a);
				fis.read(bytes);
				fis.close();
				Log.d(tag,"length" + a.length() +"|" + bytes.length);
			} catch(Exception e){
				Log.d(tag,"sss exception");
				e.printStackTrace();
			}
			
			long toId =      3000010698L;
			
			return UGCManager.getInstance().getVoiceUploadRequest(toId, "1", 1, "end", mVoiceLength, bytes);
		}
		return null;
	}
	
//	public void sendNewVoice() {
//		if(!TextUtils.isEmpty(mFileName) && mFileName.length() > 0 ) {
//			Log.d(tag,"send");
//			File a = new File(mFileName);
//			byte[] bytes = new byte[(int)a.length()];
//			FileInputStream fis;
//			try {
//				fis = new FileInputStream(a);
//				fis.read(bytes);
//				fis.close();
//				Log.d(tag,"length" + a.length() +"|" + bytes.length);
//			} catch(Exception e){
//				Log.d(tag,"sss exception");
//				e.printStackTrace();
//			}
//			
//			long toId =      3000010698L;
//			
//			HttpMasService.getInstance().pos
//		}
//	}
	
	public void send() {
		if(!TextUtils.isEmpty(mFileName) && mFileName.length() > 0 ) {
			Log.d(tag,"send");
			File a = new File(mFileName);
			byte[] bytes = new byte[(int)a.length()];
			FileInputStream fis;
			try {
				fis = new FileInputStream(a);
				fis.read(bytes);
				fis.close();
				Log.d(tag,"length" + a.length() +"|" + bytes.length);
			} catch(Exception e){
				Log.d(tag,"sss exception");
				e.printStackTrace();
			}
			
			long toId =      3000010698L;
			
			HttpMasService.getInstance().postVoice(toId, "1", 1, "end", mVoiceLength, bytes, voiceresponse);
		}
	}
	
	private INetResponse voiceresponse = new INetResponse(){

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				if (data.has("file_url"))
					fileUrl = data.optString("file_url");
				Log.d(tag,"success:" + data.toString());
				CommonUtil.toast("success:"+data.toString());
			}else{
				CommonUtil.toast("error:"+data.toString());
			}
		}
		
	};
	
	
	private INetResponse response = new INetResponse(){

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				if (data.has("file_url")) {
					fileUrl = data.optString("file_url");
					Log.d(tag,"success:" + data.toString());
					CommonUtil.toast("success:"+data.toString());
				}
					
			}else{
				CommonUtil.toast("error:"+data.toString());
			}
		}
		
	};
	
	
	
	private INetResponse downloadvoice = new INetResponse(){

		@Override
		public void response(INetRequest req, JSONObject obj) {
			JSONObject data = (JSONObject)obj;
			if(Methods.checkNoError(req, data)){
				if (data.has("url")) {
					Log.d(tag, "from download voice response:" + fileUrl);
					fileUrl = data.optString("url");
					byte[] voice = (byte[]) data.opt("voice");
					try {
						createVoiceName();
						FileOutputStream fos = new FileOutputStream(mFileName);
						fos.write(voice);
						fos.close();
					} catch (FileNotFoundException e) {
						Log.d(tag,"download voice response error not found");
						e.printStackTrace();
					} catch (IOException e) {
						Log.d(tag,"download voice response error IO ");
						e.printStackTrace();
					}
					Log.d(tag,"success:" + data.toString());
					CommonUtil.toast("success:"+data.toString());
				}
					
			}else{
				CommonUtil.toast("error:"+data.toString());
			}
		}
		
	};
	
	private void StartRecording() {
		VoiceManager.getInstance().record(mFileName);
	}
	
	private void endRecording() {
		VoiceManager.getInstance().stopRecord(true);
		mVoiceLength = Math.round(VoiceManager.getInstance().getVoiceLength()/1000);
		if (mVoiceLength < 1) {
			mVoiceLength = 1;
		}
		logger.d("voice---length:"+mVoiceLength);
	}
	
	public void getvoice() {
		if (TextUtils.isEmpty(fileUrl)) {
			CommonUtil.toast("请上传语音,获取语音文件");
			return;
		}
		Log.d(tag, "from get voice :" + fileUrl);
		HttpMasService.getInstance().getVoice(fileUrl, downloadvoice);
		
	}

	private String createVoiceName(){
		String rootDir = "/sdcard/x2/voice/";
		File dir = new File(rootDir);
		if (!dir.exists()) {
			dir.mkdirs();
			Log.d(tag,"makedir");
		}
		this.mFileName = rootDir + System.currentTimeMillis();
		return mFileName;
	}
}
