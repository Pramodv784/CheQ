package com.cheq.retail.ui.billPayments.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class Response{

	@SerializedName("TxnDetails")
	private String txnDetails;

	@SerializedName("cxDetails")
	private CxDetails2 cxDetails;

	@SerializedName("orderdetails")
	private Orderdetails orderdetails;

	@SerializedName("status")
	private boolean status;

	public String getTxnDetails(){
		return txnDetails;
	}

	public CxDetails2 getCxDetails(){
		return cxDetails;
	}

	public Orderdetails getOrderdetails(){
		return orderdetails;
	}

	public boolean isStatus(){
		return status;
	}
}