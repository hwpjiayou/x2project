package com.renren.mobile.x2.utils;

import static android.util.Log.v;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.res.AssetManager;

import com.renren.mobile.x2.utils.JsonMappingUtils.JsonMapping;

public class JsonDemo {

	public static String TAG = JsonDemo.class.getSimpleName();
	
	public static class LoginRenrenResult{
		@JsonMapping(Tag="session_key")
		public String sessionKey;

		@JsonMapping(Tag={"secret_key"})
		public int secretKey;
		
		@JsonMapping(Tag="is_first_login")
		public int isFirstLogin;
		
		@JsonMapping(Tag="fill_stage")
		public int fillStage;
		
		@JsonMapping(Tag="bind_info", IsList=true, ItemClass=BindInfo.class)
		public ArrayList<BindInfo> list;

		@JsonMapping(Tag={"profile_info", "user_id"})
		public int userID;
		
		@JsonMapping(Tag={"profile_info", "domain_name"})
		public String domainName;
		
		@JsonMapping(Tag={"profile_info", "name"})
		public int name;
		
		@JsonMapping(Tag={"profile_info", "birth_day", "year"})
		public int year;
		
		@JsonMapping(Tag={"profile_info", "birth_day", "yeara"})
		public int nullData;
		
		@JsonMapping(Tag={"profile_info", "birth_day", "month"})
		public int month;
		
		@JsonMapping(Tag={"profile_info", "birth_day", "day"})
		public int day;
		
		@JsonMapping(Tag={"profile_info", "school"}, IsList=true, ItemClass=School.class)
		public ArrayList<School> schools = new ArrayList<School>();

		@Override
		public String toString() {
			return "LoginRenrenResult [sessionKey=" + sessionKey
					+ ", secretKey=" + secretKey + ", isFirstLogin="
					+ isFirstLogin + ", fillStage=" + fillStage + ", list="
					+ list + ", userID=" + userID + ", domainName="
					+ domainName + ", name=" + name + ", year=" + year
					+ ", nullData=" + nullData + ", month=" + month + ", day="
					+ day + ", schools=" + schools + "]";
		}
	}
	
	public static class BindInfo{
		@JsonMapping(Tag = "bind_id")
		public String bindId;

		@JsonMapping(Tag="type")
		public String type;

		@JsonMapping(Tag="name")
		public String name;
		
		@JsonMapping(Tag="page")
		public String page;

		@Override
		public String toString() {
			return "BindInfo [bindId=" + bindId + ", type=" + type + ", name="
					+ name + ", page=" + page + "]";
		}
	}
	
	public static class School{
		@JsonMapping(Tag="id")
		public int id;
		
		@JsonMapping(Tag="name")
		public String name;

		@Override
		public String toString() {
			return "School [id=" + id + ", name=" + name + "]";
		}
	}
	
	public static void demo(Context context){
		AssetManager assetManager = context.getAssets();
		try {
			InputStream is = assetManager.open("json");
			StringBuffer sb = new StringBuffer();
			byte[] bytes = new byte[128];
			while ((is.read(bytes)) != -1) {
				sb.append(new String(bytes));
			}
			JSONTokener jsonParser = new JSONTokener(sb.toString());

			JSONObject jo = (JSONObject) jsonParser.nextValue();
			LoginRenrenResult d = new LoginRenrenResult();
			JsonMappingUtils.map(d, jo);
			v(TAG, d.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
