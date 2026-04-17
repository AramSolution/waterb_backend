package egovframework.com.ext.oauth.service;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class OAuthUniversalUser {
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getLoginip() {
		return loginIp;
	}

	public void setLoginip(String loginIp) {
		this.loginIp = loginIp;
	}

	public Date getLastlogin() {
		return lastLogin;
	}

	public void setLastlogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		if (phoneNumber != null && phoneNumber.startsWith("+82 ")) {
			phoneNumber = phoneNumber.replace("+82 ", "0");
		}
		if (phoneNumber != null) {
			this.phoneNumber = phoneNumber.replace("-", "");
		} else {
			this.phoneNumber = "";
		}
	}

	public String getCertCi() {
		return certCi;
	}

	public void setCertCi(String certCi) {
		this.certCi = certCi;
	}

	private String uid;

	private String email;
	private String userId;
	private String userName;
	private String nickName;

	private String serviceName;

	private String loginIp;
	private Date lastLogin;
	private String profileImage = "";

	private String phoneNumber = "";

	private String certCi = "";



	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
