package com.renren.mobile.x2.network.mas;


/**
 * @author dingwei.chen
 * @说明 MCS调用接口
 * @see com.common.mcs.HttpMcsService
 * */
public interface IMcsService {
	
	/*登录*/
	public void login(String account,String passwordMd5,  int captcha_needed, String captcha, long session,INetResponse response);
	
	/**
	 * 获取登录信息，用于票失效
	 * */
	public void getLoginInfo(INetResponse response);
	
	/**
	 * 修改密码
	 * @param response
	 * @param oldMd5 用户旧密码的32位md5小写
	 * @param newMd5 用户新密码的32位md5小写
	 * @param sessionId talk的sessionId
	 * @return
	 */
	public INetRequest modifyPassword(INetResponse response, String oldMd5, String newMd5, String sessionId);
	
	/**
	 * 国内版使用
	 * 注册，需name、密码、验证码:
	 * @param number 邮箱或者手机号
	 * @param verifycode
	 * @param captcha
	 * @param response
	 */
	public void register(String number, String pwd, String captcha, int gender, String name, INetResponse response);
	 
	/**
	 * 注销
	 * @param response
	 * @param sessionId talk的sessionId
	 * @return
	 */
	public INetRequest logout(INetResponse response, String sessionId);

    
}
