package com.cheq.retail.ui.billPayments.model;

import androidx.annotation.Keep;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@Keep
public class Resp{

	@SerializedName("amount")
	private int amount;

	@SerializedName("amount_paid")
	private int amountPaid;

	@SerializedName("notes")
	private List<Object> notes;

	@SerializedName("created_at")
	private int createdAt;

	@SerializedName("amount_due")
	private int amountDue;

	@SerializedName("currency")
	private String currency;

	@SerializedName("receipt")
	private String receipt;

	@SerializedName("id")
	private String id;

	@SerializedName("entity")
	private String entity;

	@SerializedName("offer_id")
	private Object offerId;

	@SerializedName("status")
	private String status;

	@SerializedName("attempts")
	private int attempts;

	public int getAmount(){
		return amount;
	}

	public int getAmountPaid(){
		return amountPaid;
	}

	public List<Object> getNotes(){
		return notes;
	}

	public int getCreatedAt(){
		return createdAt;
	}

	public int getAmountDue(){
		return amountDue;
	}

	public String getCurrency(){
		return currency;
	}

	public String getReceipt(){
		return receipt;
	}

	public String getId(){
		return id;
	}

	public String getEntity(){
		return entity;
	}

	public Object getOfferId(){
		return offerId;
	}

	public String getStatus(){
		return status;
	}

	public int getAttempts(){
		return attempts;
	}
}