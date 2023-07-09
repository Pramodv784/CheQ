package com.cheq.retail.ui.main.model

import androidx.annotation.Keep
import com.cheq.retail.R
@Keep
class SettingData(
    var id:Int,
    var title:String,
    var subtitle:String,
    var drawable: Int,
    var emailCount: Int
    ) {


}
var listData = listOf(
    SettingData(1,"Personal Details","View your personal information", R.drawable.ic_personal_details,0),
    SettingData(2,"Transaction History","View your personal information", R.drawable.ic_transaction_history,0),

    SettingData(3,"Manage CheQ Safe","Two e-mail IDs linked",R.drawable.ic_cheq_safe,0),
   // SettingData(4,"Communications","Link your bank account to activate Autopay",R.drawable.ic_communications,0),
    SettingData(5,"Refer & Earn","Refer and Earn rewards",R.drawable.ic_refer_and_earn,0),


    /*SettingData(1,"Personal Details","View your personal information"),
    SettingData(2,"Transaction History","View your personal information"),

    SettingData(3,"Manage CheQ Safe","Two e-mail IDs linked"),
    SettingData(4,"Manage CheQ Autopay","Link your bank account to activate Autopay"),
    SettingData(5,"Refer & Earn","Refer and Earn rewards"),
    SettingData(6,"About CheQ","View all relevant policies"),
    SettingData(7,"Logout",""),*/

    )

var cardItem = listOf(
    SettingData(1,"HDFC","",R.drawable.ic_hdfc_logo,0),
    SettingData(1,"SBI","",R.drawable.ic_sbi_logo,0),
    SettingData(1,"ICICI","",R.drawable.ic_icici_logo,0),
    SettingData(1,"Axis","",R.drawable.ic_axis_logo,0),
)


