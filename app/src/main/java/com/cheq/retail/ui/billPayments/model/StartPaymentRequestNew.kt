package com.cheq.retail.ui.billPayments.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class StartPaymentRequestNew(

    @field:SerializedName("rewardUsed") val rewardUsed: Boolean? = null,

    @field:SerializedName("cvv") val cvv: String? = null,

    @field:SerializedName("debit_card_network") val debitCardNetwork: String? = null,

    @field:SerializedName("upiPackageName") val upiPackageName: String? = null,

    @field:SerializedName("paymentMode") val paymentMode: String? = null,

    @field:SerializedName("chip_remaining") val chipRemaining: Int? = null,


    @field:SerializedName("chip_amount") val chipAmount: Int? = null,

    @field:SerializedName("expiry_year") val expiryYear: String? = null,

    @field:SerializedName("netBankingObject") val netBankingObject: NetBankingObject? = null,

    @field:SerializedName("products") val products: List<ProductsItem?>? = null,

    @field:SerializedName("number") val number: String? = null,

    @field:SerializedName("expiry_month") val expiryMonth: String? = null,

    @field:SerializedName("total_amount") val totalAmount: String? = null,

    @field:SerializedName("name") val name: String? = null,

    @field:SerializedName("payTogether") val payTogether: Boolean? = null,

    @field:SerializedName("chip_used") val chipUsed: Int? = null,

    @field:SerializedName("DCBankmaster_id") val DCBankmaster_id: String? = null,

    @field:SerializedName("saveDebitCard") val saveDebitCard: Boolean? = null,

    @field:SerializedName("dcToken") val dcToken: String? = null,

    @field:SerializedName("debit_card_bank_name") val debitCardBankName: String? = null,

    @field:SerializedName("payment_instrument") val payment_instrument: String? = null,
) : Serializable

@Keep
data class ProductsItem(

    @field:SerializedName("bankMasterId") val bankMasterId: String? = null,

    @field:SerializedName("amount") val amount: String? = null,

    @field:SerializedName("productID") val productID: String? = null,

    @field:SerializedName("InstitutionId") val institutionId: String? = null,

    @field:SerializedName("billStaus") val billStaus: String? = null,

    @field:SerializedName("billID") val billID: String? = null,

    @field:SerializedName("plainCard") val plainCard: Boolean? = null,

    @field:SerializedName("pan") val pan: String? = null,

    @field:SerializedName("productType") val productType: String? = null
) : Serializable

@Keep
data class NetBankingObject(

    @field:SerializedName("IFSC_Prefix") val iFSCPrefix: String? = null,

    @field:SerializedName("logo") val logo: String? = null,

    @field:SerializedName("bankName") val bankName: String? = null,

    @field:SerializedName("id") val id: String? = null
) : Serializable

/*
export class StartTxnDTOV3 {

	payTogether: Boolean;

	products: any;

	// [

	//   {

	//     productID: string,

	//     amount: string,

	//     bankMasterId: string,

	//     InstitutionId: string,

	//     billID: string,

	//     billStaus: string, // min/full/custom

	//     plainCard: Boolean,

	//     pan: string, // when plain card/loan?/bnpl?

	//     productType: string // CC/Loan/BNPL

	//     billData: any;

	//     billTxnData: any

	//   }

	// ];

	total_amount: number;

	rewardUsed: true;

	chip_used: number;

	chip_amount: number;

	chip_remaining: number;

	paymentMode: string; //card/upi/net banking / REWARD / SAVED_CARD

	saveDebitCard : boolean;

	dcToken : string;

	cvv: string; //card

	number: string; // card

	expiry_month: string; // card

	expiry_year: string; //card

	debit_card_network: string; // card

	debit_card_bank_name: string; // card

	upiPackageName: string; //upi

	netBankingObject: { //netbanking

		bankName: string;

		IFSC_Prefix: string;

		logo: string;

		_id: string;

	};

	name: string;

	DCBankmaster_id: string;

}*/
