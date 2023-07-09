package com.cheq.retail.ui.dashboard.view.customview.custom_indicator;

import android.content.Context;

class ScreenUtils {


    static float dp2px(Context context, float dp) {
        float scale = 2.0f;
        if (context != null) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return dp * scale + 0.5f;
    }
}

