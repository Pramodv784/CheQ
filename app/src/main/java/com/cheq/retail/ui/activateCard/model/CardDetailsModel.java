package com.cheq.retail.ui.activateCard.model;

import androidx.annotation.Keep;

import com.cheq.retail.ui.main.model.DashBoardResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class CardDetailsModel {

    @SerializedName("message")
    private String message;
    @SerializedName("bank")
    private BankEntity bank;
    @SerializedName("iin")
    private IinEntity iin;
    @SerializedName("status")
    private Boolean status;
    @SerializedName("success")
    private boolean success;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BankEntity getBank() {
        return bank;
    }

    public void setBank(BankEntity bank) {
        this.bank = bank;
    }

    public IinEntity getIin() {
        return iin;
    }

    public void setIin(IinEntity iin) {
        this.iin = iin;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Keep
    public static class BankEntity {
        @SerializedName("perc")
        private int perc;
        @SerializedName("logoPath")
        private String logopath;
        @SerializedName("__v")
        private int V;
        @SerializedName("createdAt")
        private String createdat;
        @SerializedName("updatedAt")
        private String updatedat;
        @SerializedName("IFSC_Prefix")
        private String ifscPrefix;
        @SerializedName("logo")
        private String logo;
        @SerializedName("ocrBankName")
        private String ocrbankname;
        @SerializedName("bureauBankName")
        private String bureaubankname;
        @SerializedName("originalBankName")
        private String originalbankname;
        @SerializedName("isDeleted")
        private boolean isdeleted;
        @SerializedName("isActive")
        private boolean isactive;
        @SerializedName("_id")
        private String Id;
        @SerializedName("logoWithName")
        private String logoWithName;
        @SerializedName("bankName")
        private String bankName;
        @SerializedName("outerGridGradientColors")
        private BankOuterGradient outerGridGradientColors;

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getLogoWithName() {
            return logoWithName;
        }

        public void setLogoWithName(String logoWithName) {
            this.logoWithName = logoWithName;
        }

        public int getPerc() {
            return perc;
        }

        public void setPerc(int perc) {
            this.perc = perc;
        }

        public String getLogopath() {
            return logopath;
        }

        public void setLogopath(String logopath) {
            this.logopath = logopath;
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

        public String getIfscPrefix() {
            return ifscPrefix;
        }

        public void setIfscPrefix(String ifscPrefix) {
            this.ifscPrefix = ifscPrefix;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getOcrbankname() {
            return ocrbankname;
        }

        public void setOcrbankname(String ocrbankname) {
            this.ocrbankname = ocrbankname;
        }

        public String getBureaubankname() {
            return bureaubankname;
        }

        public void setBureaubankname(String bureaubankname) {
            this.bureaubankname = bureaubankname;
        }

        public String getOriginalbankname() {
            return originalbankname;
        }

        public void setOriginalbankname(String originalbankname) {
            this.originalbankname = originalbankname;
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

        public BankOuterGradient getOuterGridGradientColors() {
            return outerGridGradientColors;
        }

        public void setOuterGridGradientColors(BankOuterGradient outerGridGradientColors) {
            this.outerGridGradientColors = outerGridGradientColors;
        }
    }

    @Keep
    public static class IinEntity {
        @SerializedName("authentication_types")
        private List<AuthenticationTypesEntity> authenticationTypes;
        @SerializedName("recurring")
        private RecurringEntity recurring;
        @SerializedName("emi")
        private EmiEntity emi;
        @SerializedName("international")
        private boolean international;
        @SerializedName("issuer_name")
        private String issuerName;
        @SerializedName("issuer_code")
        private String issuerCode;
        @SerializedName("sub_type")
        private String subType;
        @SerializedName("type")
        private String type;
        @SerializedName("network")
        private String network;
        @SerializedName("entity")
        private String entity;
        @SerializedName("iin")
        private String iin;

        public List<AuthenticationTypesEntity> getAuthenticationTypes() {
            return authenticationTypes;
        }

        public void setAuthenticationTypes(List<AuthenticationTypesEntity> authenticationTypes) {
            this.authenticationTypes = authenticationTypes;
        }

        public RecurringEntity getRecurring() {
            return recurring;
        }

        public void setRecurring(RecurringEntity recurring) {
            this.recurring = recurring;
        }

        public EmiEntity getEmi() {
            return emi;
        }

        public void setEmi(EmiEntity emi) {
            this.emi = emi;
        }

        public boolean getInternational() {
            return international;
        }

        public void setInternational(boolean international) {
            this.international = international;
        }

        public String getIssuerName() {
            return issuerName;
        }

        public void setIssuerName(String issuerName) {
            this.issuerName = issuerName;
        }

        public String getIssuerCode() {
            return issuerCode;
        }

        public void setIssuerCode(String issuerCode) {
            this.issuerCode = issuerCode;
        }

        public String getSubType() {
            return subType;
        }

        public void setSubType(String subType) {
            this.subType = subType;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        public String getIin() {
            return iin;
        }

        public void setIin(String iin) {
            this.iin = iin;
        }
    }

    @Keep
    public static class AuthenticationTypesEntity {
        @SerializedName("type")
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @Keep
    public static class RecurringEntity {
        @SerializedName("available")
        private boolean available;

        public boolean getAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }

    @Keep
    public static class EmiEntity {
        @SerializedName("available")
        private boolean available;

        public boolean getAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }

    @Keep
    public static class BankOuterGradient {
        @SerializedName("gOne")
        private String gOne;
        @SerializedName("gTwo")
        private String gTwo;

        @SerializedName("gThree")
        private String gThree;


        public String getgOne() {
            return gOne;
        }

        public void setgOne(String gOne) {
            this.gOne = gOne;
        }

        public String getgTwo() {
            return gTwo;
        }

        public void setgTwo(String gTwo) {
            this.gTwo = gTwo;
        }

        public String getgThree() {
            return gThree;
        }

        public void setgThree(String gThree) {
            this.gThree = gThree;
        }
    }


}
