package com.renren.mobile.x2.components.home.nearbyfriends;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.renren.mobile.x2.components.chat.face.ICanTalkable;


import android.graphics.Bitmap;
import android.util.Log;
/***
 * 附近人的数据，添加的时候会进行排序
 * @author xiaochao.zheng
 *
 */
public class NearByFData {
	private final static int CHINESE =1;
	private final static int PINYIN =0;
	public int length;
	private ArrayList<NearByFNnode> list = new ArrayList<NearByFData.NearByFNnode>();
	public NearByFData() {
		
	}
	/***
	 * 添加一条附近的人的信息
	 * @param bm 位图
	 * @param signature 状态
	 * @param uid 用户id
	 * @param firstindex 姓名索引，比如 张三  会存成 zs
	 * @param username 用户姓名
	 */
	public void add(Bitmap bm, String signature,long uid, String firstindex,String username,int g){
		this.softbyUserNameIndexandAdd(new NearByFNnode(bm, signature,uid, firstindex, username,g) );
		this.length=this.list.size();
	}
	/***
	 * 添加一条附近的人的信息, 确保添加进去的不为空
	 * @param url 头像的地址
	 * @param signature 状态
	 * @param uid 用户id
	 * @param firstindex 姓名索引，比如 张三  会存成 zs
	 * @param username 用户姓名
	 */
	public void add(String url, String signature,long uid, String firstindex,String username,int g){
//		this.list.add(new NearByFNnode(url, signature,uid, firstindex, username));
		this.softbyUserNameIndexandAdd(new NearByFNnode(url, signature,uid, firstindex, username, g) );
		this.length = this.list.size();
		for(int i = 0 ; i < list.size();++i){
			Log.d("list","info "+list.get(i).userName+ "  " + list.get(i).userNameHeadchar);
			
		}
	}
	/****
	 * 清空 数据
	 */
	public void clear(){
		this.list.clear();
		this.length=0;
	}
	
	/***
	 * 复制一个
	 */
	@Override
	protected NearByFData clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		NearByFData data = new NearByFData();
		data.list = (ArrayList<NearByFNnode>) this.list.clone();
		data.length = this.length;
		return data;
	}
	private void add(NearByFNnode node ){
		this.list.add(node);
		this.length= list.size();
	}
	/***
	 * 添加的时候进行排序
	 * @param node
	 */
	private void softbyUserNameIndexandAdd(NearByFNnode node){
			if(list.size()==0){
				list.add(node);
				return;
			}
			if((int)node.userFirstindex < (int)list.get(0).userFirstindex){
				list.add(0, node);
				return;
			}
			for(ListIterator<NearByFNnode> iter = list.listIterator(); iter.hasNext();){
				int c = (int)iter.next().userFirstindex;
				if((int)node.userFirstindex < c){
					iter.previous();
					iter.add(node);
					return;
				}
			}
			list.add(node);
			return ;
	}
	public NearByFData(String nameindex){
		
	}
	/****
	 * 返回节点
	 * @param position
	 * @return
	 */
	public NearByFNnode getNode(int position){
		if(list!=null && position < list.size()){
			return list.get(position);
		}
		return null;
	}
	
	/***
	 * 获取相应位置的位图
	 * @param position
	 * @return
	 */
	public Bitmap getBitmap(int position){
		if(position<length){
			return this.list.get(position).bitmap;
		}
		return null;
	}
	/***
	 * 判断是否含有位图
	 * @param position
	 * @return
	 */
	public boolean ishasbitmap(int position){
		if(position<length){
			return this.list.get(position).bitmap!=null;
		}else{
			return false;
		}
		
	}
	/***
	 * 获取用户名
	 * @param position
	 * @return
	 */
	public String getUsername(int position){
		if(position < length){
			return this.list.get(position).userName;
		}
		return null;
	}
	/***
	 * 获取用户的状态
	 * @param position
	 * @return
	 */
	public String getSignature(int position ){
		if(position < length){
			return this.list.get(position).mSignature;
		}
		return null;
	}
	
	public int getGender(int position){
		if(position < length){
			return this.list.get(position).gender;
		}
		return -1;
	}
	
	public int getDistance(int position){
//		if(position <length){
////			return this.list.get(position).
//		}
		return 150;
	}
	public long getTime(int position){
		return System.currentTimeMillis();
	}
	
	/**
	 * 获取头像的地址
	 * @param position
	 * @return
	 */
	public String getImageUrl(int position){
		if(position < length){
			return this.list.get(position).bitmapurl;
		}
		return null;
	}
	/***
	 * 按照index搜索数据，返回一个新的NearByFdata
	 * @param index 搜索索引
	 * @return NearByFData
	 */
	public NearByFData getDataWithIndex(String index){
		ErrLog.Print("search index " + index);
		if(index==""){
			return this;
		}
		int nameType = this.getNameIndexType(index);
		NearByFData data = new NearByFData();
		if(nameType == CHINESE){
			for(int i = 0; i < this.length;++i){
				if (list.get(i).userName.substring(0,
						index.length() <= list.get(i).userName.length() ? 
								index.length() : list.get(i).userName.length()).contains(
						index)) 
				{
					NearByFNnode node = this.list.get(i);
					data.add(node);
				}
			}
		}else if( nameType == PINYIN){
			for(int i = 0; i < this.length;++i){
				ErrLog.Print("username haedchar "+ this.list.get(i).userNameHeadchar);
				if (list.get(i).userNameHeadchar.substring(0,
						index.length() <= list.get(i).userNameHeadchar.length() ? 
								index.length() : list.get(i).userNameHeadchar.length()).contains(
						index)) 
				{
					NearByFNnode node = this.list.get(i);
					data.add(node);
				}
			}
		}
		ErrLog.Print(" user name " + data.getUsername(0));
		return data;
	}
	public NearByFData getDatawithgender(int gender){
		if(gender == -1){
			return this;
		}
		NearByFData data = new NearByFData();
		for(int i = 0;i < this.length;++i){
			if(this.list.get(i).gender==gender){
				data.add(list.get(i));
			}
		}
		Log.d("zxc","getdata with gender " + data.length);
		return data;
	}
	/****
	 * 获取是否是孩子还是拼音
	 * @param name_index
	 * @return
	 */
	private int getNameIndexType(String name_index) {
		Log.d("fff", name_index);
		int count = 0;
		String regEx = "[\u4E00-\u9FA5\uFE30-\uFFA0]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(name_index);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				count = count + 1;
			}
		}
		if (count > 0) {
			return CHINESE;
		}
		return PINYIN;
	}
	public  class NearByFNnode implements ICanTalkable{
		protected Bitmap bitmap;//头像
		protected String bitmapurl;
		protected String mSignature;//个性签名
		protected String userName;//用户姓名
		protected String userNameHeadchar;
		protected long userId;
		protected int gender;//1代表男性，0代表女性
		private char userFirstindex;
		public NearByFNnode() {
			assert userNameHeadchar!=null;
		}
		public NearByFNnode(Bitmap bm){
			assert userNameHeadchar!=null;
			this.bitmap= bm;
		}
		public NearByFNnode(NearByFNnode node){
			this.bitmap = node.bitmap;
			this.bitmapurl = node.bitmapurl;
			this.mSignature = node.mSignature;
			this.userNameHeadchar = node.userNameHeadchar;
			this.userId=node.userId;
			this.userName = node.userName;
			assert userNameHeadchar!=null;
			this.userFirstindex = this.userNameHeadchar.charAt(0);
		}
		public NearByFNnode(String imgurl,String signature ,long uid, String uname_headchar,String username,int g){
			this.mSignature = signature;
			this.bitmapurl =imgurl;
			this.userId = uid;
			this.userNameHeadchar= uname_headchar;
			this.userName = username;
			this.gender= g;
			assert userNameHeadchar!=null;
			if(userNameHeadchar!= null){
				this.userFirstindex = this.userNameHeadchar.charAt(0);
			}
		}
		public NearByFNnode(Bitmap bm, String signature,long uid, String uname_headchar,String username,int g){
			this.bitmap=bm;
			this.mSignature=signature;
			this.userId = uid;
			this.userNameHeadchar= uname_headchar;
			this.userName = username;
			this.gender = g;
			assert userNameHeadchar!=null;
			
			this.userFirstindex = this.userNameHeadchar.charAt(0);
		}
		
		public Bitmap getBitmap(){
			if(this.bitmap ==null){
				ErrLog.Print("bitmap is null in NearByFData");
			}
			return bitmap;
		}
		public String getSignature(){
			if(this.mSignature==null){
				ErrLog.Print("Signature is null in NearByFData");
			}
			return mSignature;
		}
		/* (non-Javadoc)
		 * @see com.renren.mobile.x2.components.chat.RenRenChatActivity.CanTalkable#getUId()
		 */
		@Override
		public long getUId() {
			return userId;
		}
		/* (non-Javadoc)
		 * @see com.renren.mobile.x2.components.chat.RenRenChatActivity.CanTalkable#getName()
		 */
		@Override
		public String getName() {
			return userName;
		}
		/* (non-Javadoc)
		 * @see com.renren.mobile.x2.components.chat.RenRenChatActivity.CanTalkable#getHeadUrl()
		 */
		@Override
		public String getHeadUrl() {
			return bitmapurl;
		}
	}
}
