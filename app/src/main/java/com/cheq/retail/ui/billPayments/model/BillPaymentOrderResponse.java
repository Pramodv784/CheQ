package com.cheq.retail.ui.billPayments.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class BillPaymentOrderResponse {

    @SerializedName("rzpCustomerId")
    private String rzpcustomerid;
    @SerializedName("cxDetails")
    private CxdetailsEntity cxdetails;
    @SerializedName("orderdetails")
    private OrderdetailsEntity orderdetails;
    @SerializedName("status")
    private boolean status;

    public String getRzpcustomerid() {
        return rzpcustomerid;
    }


    public void setRzpcustomerid(String rzpcustomerid) {
        this.rzpcustomerid = rzpcustomerid;
    }

    public CxdetailsEntity getCxdetails() {
        return cxdetails;
    }

    public void setCxdetails(CxdetailsEntity cxdetails) {
        this.cxdetails = cxdetails;
    }

    public OrderdetailsEntity getOrderdetails() {
        return orderdetails;
    }

    public void setOrderdetails(OrderdetailsEntity orderdetails) {
        this.orderdetails = orderdetails;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    @Keep
    public static class CxdetailsEntity {
        @SerializedName("panCard")
        private String pancard;
        @SerializedName("dateOfBirth")
        private String dateofbirth;
        @SerializedName("lastName")
        private String lastname;
        @SerializedName("firstName")
        private String firstname;
        @SerializedName("email")
        private String email;
        @SerializedName("__v")
        private int V;
        @SerializedName("createdAt")
        private String createdat;
        @SerializedName("updatedAt")
        private String updatedat;
        @SerializedName("isAutoPayEnabled")
        private boolean isautopayenabled;
        @SerializedName("isEmandateDone")
        private boolean isemandatedone;
        @SerializedName("whatsAppAccess")
        private boolean whatsappaccess;
        @SerializedName("deviceId")
        private String deviceid;
        @SerializedName("mobile")
        private String mobile;
        @SerializedName("isDeleted")
        private boolean isdeleted;
        @SerializedName("isActive")
        private boolean isactive;
        @SerializedName("_id")
        private String Id;

        public String getPancard() {
            return pancard;
        }

        public void setPancard(String pancard) {
            this.pancard = pancard;
        }

        public String getDateofbirth() {
            return dateofbirth;
        }

        public void setDateofbirth(String dateofbirth) {
            this.dateofbirth = dateofbirth;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getV() {
            return V;
        }

        public void setV(int V) {
            this.V = V;
        }

        public String getCreatedat() {
            return createdat;
        }

        public void setCreatedat(String createdat) {
            this.createdat = createdat;
        }

        public String getUpdatedat() {
            return updatedat;
        }

        public void setUpdatedat(String updatedat) {
            this.updatedat = updatedat;
        }

        public boolean getIsautopayenabled() {
            return isautopayenabled;
        }

        public void setIsautopayenabled(boolean isautopayenabled) {
            this.isautopayenabled = isautopayenabled;
        }

        public boolean getIsemandatedone() {
            return isemandatedone;
        }

        public void setIsemandatedone(boolean isemandatedone) {
            this.isemandatedone = isemandatedone;
        }

        public boolean getWhatsappaccess() {
            return whatsappaccess;
        }

        public void setWhatsappaccess(boolean whatsappaccess) {
            this.whatsappaccess = whatsappaccess;
        }

        public String getDeviceid() {
            return deviceid;
        }

        public void setDeviceid(String deviceid) {
            this.deviceid = deviceid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public boolean getIsdeleted() {
            return isdeleted;
        }

        public void setIsdeleted(boolean isdeleted) {
            this.isdeleted = isdeleted;
        }

        public boolean getIsactive() {
            return isactive;
        }

        public void setIsactive(boolean isactive) {
            this.isactive = isactive;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }
    }
    @Keep
    public static class OrderdetailsEntity {
        @SerializedName("created_at")
        private int createdAt;
        @SerializedName("notes")
        private List<String> notes;
        @SerializedName("attempts")
        private int attempts;
        @SerializedName("status")
        private String status;
        @SerializedName("receipt")
        private String receipt;
        @SerializedName("currency")
        private String currency;
        @SerializedName("amount_due")
        private int amountDue;
        @SerializedName("amount_paid")
        private int amountPaid;
        @SerializedName("amount")
        private int amount;
        @SerializedName("entity")
        private String entity;
        @SerializedName("id")
        private String id;

        @SerializedName("resp")
        private Resp resp;

        public int getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public List<String> getNotes() {
            return notes;
        }

        public void setNotes(List<String> notes) {
            this.notes = notes;
        }

        public int getAttempts() {
            return attempts;
        }

        public void setAttempts(int attempts) {
            this.attempts = attempts;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReceipt() {
            return receipt;
        }

        public void setReceipt(String receipt) {
            this.receipt = receipt;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public int getAmountDue() {
            return amountDue;
        }

        public void setAmountDue(int amountDue) {
            this.amountDue = amountDue;
        }

        public int getAmountPaid() {
            return amountPaid;
        }

        public void setAmountPaid(int amountPaid) {
            this.amountPaid = amountPaid;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Resp getResp() {
            return resp;
        }

        public void setResp(Resp resp) {
            this.resp = resp;
        }
    }
}