package com.renren.mobile.x2.components.login;

import com.renren.mobile.x2.components.login.LoginManager.LoginInfo;
import com.renren.mobile.x2.core.orm.ORM;
import com.renren.mobile.x2.db.table.AccountColumn;


public class LoginModel {
	
	public LoginModel () {
		
	}
	
	@ORM(mappingColumn=AccountColumn.LOGIN_ACCOUNT)
	public String mAccount = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_PASSWORD)
	public String mPassword = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_SESSIONKEY)
	public String mSessionKey = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_SECRETKEY)
	public String mSecretKey = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_HEADURL)
	public String mHeadUrl = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_USERID)
	public String mUserId = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_USERNAME)
	public String mUserName = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_TICKET)
	public String mTicket = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_HEAD_LARGE)
	public String mLargeUrl = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_HEAD_MEDIUM)
	public String mMediumUrl = null;
	
	@ORM(mappingColumn=AccountColumn.LOGIN_HEAD_ORIGINAL)
	public String mOriginal_Url = null;
	
	@ORM(mappingColumn=AccountColumn.BIND_INFO)
	public String mBindInfo = null;
	
	@ORM(mappingColumn=AccountColumn.IS_AUTO_LOGIN)
	public int mAutoLogin = 0;
	
	@ORM(mappingColumn=AccountColumn.IS_LAST_LOGIN)
	public int mLastLogin = 0;
	
	@ORM(mappingColumn=AccountColumn.PROFILE_GENDER)
	public int mGender = -1;
	
	@ORM(mappingColumn=AccountColumn.PROFILE_BIRTHDAY)
	public String mBirthday = null;
	
	@ORM(mappingColumn=AccountColumn.PROFILE_SCHOOL_ID)
	public String mSchool_id;
	
	@ORM(mappingColumn=AccountColumn.PROFILE_SCHOOL)
	public String mSchool = null;
	
	@ORM(mappingColumn=AccountColumn.PROFILE_DEPARTMENT)
	public String mDepartment = null;
	
	@ORM(mappingColumn=AccountColumn.PROFILE_DEPARTMENT_ID)
	public int mDepartmentid = 0;
	
	@ORM(mappingColumn=AccountColumn.PROFILE_ENROLL_YEAR)
	public String mEnrollyear = null;//employer
	
	@ORM(mappingColumn=AccountColumn.PROFILE_EMPLOYER)
	public String mEmployer = null;
	
	@ORM(mappingColumn=AccountColumn.PROFILE_PRIVATE)
	public int mPrivate = -1;
	
	@ORM(mappingColumn=AccountColumn.DOMAIN_NAME)
	public String mDomainName = null;
	
	@ORM(mappingColumn=AccountColumn.COVER_URL)
	public String mCoverUrl = null;
	
	@ORM(mappingColumn=AccountColumn.IS_FIRST_LOGIN)
	public int mIsFirstLogin = -1;
	
	public void parse(LoginInfo loginfo) {
		this.mAccount = loginfo.mAccount;
		this.mHeadUrl = loginfo.mHeadUrl;
		this.mPassword = loginfo.mPassword;
		this.mSecretKey = loginfo.mSecretKey;
		this.mSessionKey = loginfo.mSessionKey;
		this.mTicket = loginfo.mTicket;
		this.mUserId = loginfo.mUserId;
		this.mUserName = loginfo.mUserName;
		this.mLargeUrl = loginfo.mLargeUrl;
		this.mMediumUrl = loginfo.mMediumUrl;
		this.mOriginal_Url = loginfo.mOriginal_Url;
		this.mAutoLogin = loginfo.mAutoLogin;
		this.mLastLogin = loginfo.mLastLogin;
		this.mGender = loginfo.mGender;
		this.mBirthday = loginfo.mBirthday;
		this.mSchool = loginfo.mSchool;
		this.mSchool_id = loginfo.mSchool_id;
		this.mDepartment = loginfo.mDepartment;
		this.mDepartmentid = loginfo.mDepartmentid;
		this.mEnrollyear = loginfo.mEnrollyear;
		this.mPrivate = loginfo.mPrivate;
		this.mDomainName = loginfo.mDomainName;
		this.mCoverUrl = loginfo.mCoverUrl;
		this.mIsFirstLogin = loginfo.mIsFirstLogin;
	}
	
	public LoginInfo getLoginfo() {
		LoginInfo loginfo= new LoginInfo();
		loginfo.mAccount = this.mAccount;
		loginfo.mHeadUrl = this.mHeadUrl;
		loginfo.mPassword = this.mPassword;
		loginfo.mSecretKey = this.mSecretKey;
		loginfo.mSessionKey = this.mSessionKey;
		loginfo.mTicket = this.mTicket;
		loginfo.mUserId = this.mUserId;
		loginfo.mUserName = this.mUserName;
		loginfo.mLargeUrl = this.mLargeUrl;
		loginfo.mMediumUrl = this.mMediumUrl;
		loginfo.mOriginal_Url = this.mOriginal_Url;
		loginfo.mAutoLogin = this.mAutoLogin;
		loginfo.mLastLogin = this.mLastLogin;
		loginfo.mGender = this.mGender;
		loginfo.mBirthday = this.mBirthday;
		loginfo.mBirthdayDisPlay = this.mBirthday;
		loginfo.mSchool = this.mSchool;
		loginfo.mSchool_id = this.mSchool_id;
		loginfo.mDepartment = this.mDepartment;
		loginfo.mDepartmentid = this.mDepartmentid;
		loginfo.mEnrollyear = this.mEnrollyear;
		loginfo.mPrivate = this.mPrivate;
		loginfo.mDomainName = this.mDomainName;
		loginfo.mCoverUrl = this.mCoverUrl;
		loginfo.mIsFirstLogin = this.mIsFirstLogin;
		return loginfo;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("mAccount:").append(this.mAccount).append('\n');
		sb.append("mHeadUrl:").append(this.mHeadUrl).append('\n');
		sb.append("mPassword:").append(this.mPassword).append('\n');
		sb.append("mSecretKey:").append(this.mSecretKey).append('\n');
		sb.append("mSessionKey:").append(this.mSessionKey).append('\n');
		sb.append("mTicket:").append(this.mTicket).append('\n');
		sb.append("mUserId:").append(this.mUserId).append('\n');
		sb.append("mUserName:").append(this.mUserName).append('\n');
		sb.append("mLargeUrl:").append(this.mLargeUrl).append('\n');
		sb.append("mMediumUrl:").append(this.mMediumUrl).append('\n');
		sb.append("mOriginal_Url:").append(this.mOriginal_Url).append('\n');
		sb.append("mBindInfo:").append(this.mBindInfo).append('\n');
		sb.append("mAutoLogin:").append(this.mAutoLogin).append('\n');
		sb.append("mLastLogin:").append(this.mLastLogin).append('\n');
		sb.append("mGender:").append(this.mGender).append('\n');
		sb.append("mBirthday:").append(this.mBirthday).append('\n');
		sb.append("mSchool:").append(this.mSchool).append('\n');
		sb.append("mSchoolId:").append(this.mSchool_id).append('\n');
		sb.append("mPrivate:").append(this.mPrivate).append('\n');
		sb.append("mDomainName:").append(this.mDomainName).append('\n');
		sb.append("mCoverUrl:").append(this.mCoverUrl).append('\n');
		sb.append("mIsFirstLogin:").append(this.mIsFirstLogin).append('\n');
		
		return sb.toString();
	}
}
