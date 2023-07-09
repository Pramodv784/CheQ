package com.cheq.retail.ui.billPayments.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class CxDetails{

	@SerializedName("lastName")
	private String lastName;

	@SerializedName("isAutoPayEnabled")
	private boolean isAutoPayEnabled;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("dateOfBirth")
	private String dateOfBirth;

	@SerializedName("isEmandateDone")
	private boolean isEmandateDone;

	@SerializedName("isActive")
	private boolean isActive;

	@SerializedName("deviceId")
	private String deviceId;

	@SerializedName("panUpdated")
	private boolean panUpdated;

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("firstName")
	private String firstName;

	@SerializedName("isDeleted")
	private boolean isDeleted;

	@SerializedName("__v")
	private int V;

	@SerializedName("whatsAppAccess")
	private boolean whatsAppAccess;

	@SerializedName("_id")
	private String id;

	@SerializedName("fcmToken")
	private String fcmToken;

	@SerializedName("email")
	private String email;

	@SerializedName("updatedAt")
	private String updatedAt;

	public String getLastName(){
		return lastName;
	}

	public boolean isIsAutoPayEnabled(){
		return isAutoPayEnabled;
	}

	public String getMobile(){
		return mobile;
	}

	public String getDateOfBirth(){
		return dateOfBirth;
	}

	public boolean isIsEmandateDone(){
		return isEmandateDone;
	}

	public boolean isIsActive(){
		return isActive;
	}

	public String getDeviceId(){
		return deviceId;
	}

	public boolean isPanUpdated(){
		return panUpdated;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getFirstName(){
		return firstName;
	}

	public boolean isIsDeleted(){
		return isDeleted;
	}

	public int getV(){
		return V;
	}

	public boolean isWhatsAppAccess(){
		return whatsAppAccess;
	}

	public String getId(){
		return id;
	}

	public String getFcmToken(){
		return fcmToken;
	}

	public String getEmail(){
		return email;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}
}