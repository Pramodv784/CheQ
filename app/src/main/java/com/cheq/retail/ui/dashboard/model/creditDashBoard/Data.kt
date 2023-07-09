package com.cheq.retail.ui.dashboard.model.creditDashBoard

import androidx.annotation.Keep
import com.cheq.retail.ui.dashboard.model.homedashboad.BalanceDueWidget
import com.cheq.retail.ui.dashboard.model.homedashboad.CreditHealthWidget
import com.cheq.retail.ui.dashboard.model.homedashboad.RewardWidget
@Keep
data class CreditDashBoardResponse(val bureauReport: CreditHealthWidget?)