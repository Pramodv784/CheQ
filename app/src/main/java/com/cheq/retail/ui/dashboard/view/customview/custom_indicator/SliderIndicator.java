package com.cheq.retail.ui.dashboard.view.customview.custom_indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.cheq.retail.R;

import javax.annotation.Nullable;

public class SliderIndicator extends View {

    private int itemCount = 0;

    private float selectedWidth = 0f;

    private float unselectedWidth = 0f;

    private float dia = 0f;

    private float space = 0f;

    private float shadowRadius = 0f;

    private RectF rectf = new RectF();

    private Paint paint;

    private float lastPositionOffset = 0;

    private int firstVisiblePosition = 0;

    private int selectedIndicatorColor = 0xffffffff;

    private int unselectedIndicatorColor = 0xffffffff;

    private int shadowColor = 0x88000000;

    private boolean isAnimation = true;

    private boolean isShadow = true;

    private OnViewPagerPageChangeListener pageChangeListener;
    private ViewPager2.OnPageChangeCallback onPageChangeCallback;
   private MutableLiveData<String> toDoItemCount=new MutableLiveData<String>();


    public SliderIndicator(Context context) {
        this(context, null);
    }

    public SliderIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MutableLiveData<String> getTodoItemCount(){

       return toDoItemCount;
    }

    public SliderIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {

        //默认值
        selectedWidth = ScreenUtils.dp2px(getContext(), 5f);
        unselectedWidth = ScreenUtils.dp2px(getContext(), 5f);
        dia = ScreenUtils.dp2px(getContext(), 5f);
        space = ScreenUtils.dp2px(getContext(), 5f);
        shadowRadius = ScreenUtils.dp2px(getContext(), 2f);
        setWillNotDraw(false);

        // Load attributes
        TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SliderIndicator, defStyle, 0);

        selectedIndicatorColor = a.getColor(com.cheq.retail.R.styleable.SliderIndicator_selectedIndicatorColor, selectedIndicatorColor);
        unselectedIndicatorColor = a.getColor(R.styleable.SliderIndicator_unselectedIndicatorColor, selectedIndicatorColor);
        shadowColor = a.getColor(R.styleable.SliderIndicator_shadowColor, shadowColor);
        selectedWidth = a.getDimension(R.styleable.SliderIndicator_selectedWidthDimension, selectedWidth);
        unselectedWidth = a.getDimension(R.styleable.SliderIndicator_unselectedWidthDimension, unselectedWidth);
        dia = a.getDimension(R.styleable.SliderIndicator_diaDimension, dia);
        space = a.getDimension(R.styleable.SliderIndicator_spaceDimension, space);
        shadowRadius = a.getDimension(R.styleable.SliderIndicator_shadowRadiusDimension, shadowRadius);
        isAnimation = a.getBoolean(R.styleable.SliderIndicator_isAnimation, isAnimation);
        isShadow = a.getBoolean(R.styleable.SliderIndicator_isShadow, isShadow);
        a.recycle();

        if (isShadow) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(selectedIndicatorColor);
        paint.setStyle(Paint.Style.FILL);
        if (isShadow) {
            paint.setShadowLayer(shadowRadius, shadowRadius / 2, shadowRadius / 2, shadowColor);
        }
    }

    public void setSelectedIndicatorColor(int selectedIndicatorColor, int unselectedIndicatorColor) {
        this.selectedIndicatorColor = selectedIndicatorColor;
        this.unselectedIndicatorColor = unselectedIndicatorColor;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        if (heightMeasureSpec == 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.AT_MOST);
        }
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            int width = 0;
            int height = 0;
            if (itemCount != 0) {
                if (isShadow) {
                    width = (int) ((itemCount - 1) * (space + unselectedWidth) + selectedWidth + shadowRadius);
                    height = (int) (dia + shadowRadius);
                } else {
                    width = (int) ((itemCount - 1) * (space + unselectedWidth) + selectedWidth);
                    height = (int) dia;
                }
            }
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || itemCount == 0) {
            return;
        }

        for (int i = 0; i < itemCount; i++) {
            int left;
            int right;
            int color;
            if (i < firstVisiblePosition) {
                left = (int) (i * (unselectedWidth + space));
                right = (int) (left + unselectedWidth);
                color = unselectedIndicatorColor;
            } else if (i == firstVisiblePosition) {
                left = (int) (i * (unselectedWidth + space));
                right = (int) (left + unselectedWidth + (selectedWidth - unselectedWidth) * (1 - lastPositionOffset));
                color = ColorUtils.getScaleRadioColor(1 - lastPositionOffset, selectedIndicatorColor, unselectedIndicatorColor);
            } else if (i == firstVisiblePosition + 1) {
                left = (int) ((i - 1) * (space + unselectedWidth) + unselectedWidth + (selectedWidth - unselectedWidth) * (1 - lastPositionOffset) + space);
                right = (int) (i * (space + unselectedWidth) + selectedWidth);
                color = ColorUtils.getScaleRadioColor(lastPositionOffset, selectedIndicatorColor, unselectedIndicatorColor);
            } else {
                left = (int) ((i - 1) * (unselectedWidth + space) + (selectedWidth + space));
                right = (int) ((i - 1) * (unselectedWidth + space) + (selectedWidth + space) + unselectedWidth);
                color = unselectedIndicatorColor;
            }

            int top = (int) 0f;
            int bottom = (int) dia;

            rectf.left = left;
            rectf.top = top;
            rectf.right = right;
            rectf.bottom = bottom;
            paint.setColor(color);
            canvas.drawRoundRect(rectf, dia / 2, dia / 2, paint);
        }
    }

    /**
     * 绑定viewPager
     *
     * @param viewPager
     */
    public void setupWithViewPager(ViewPager viewPager) {

        if (viewPager.getAdapter() == null) {
            throw new IllegalArgumentException("viewPager adapter not be null");
        }

        itemCount = viewPager.getAdapter().getCount();

        if (pageChangeListener == null) {
            pageChangeListener = new OnViewPagerPageChangeListener();
        }

        viewPager.removeOnPageChangeListener(pageChangeListener);
        viewPager.addOnPageChangeListener(pageChangeListener);
        requestLayout();
    }

    public void setupWithViewPager2(ViewPager2 viewPager2) {
        if (viewPager2.getAdapter() == null) {
            throw new IllegalArgumentException("viewPager2 adapter not be null");
        }

        itemCount = viewPager2.getAdapter().getItemCount();

        if (onPageChangeCallback == null) {
            onPageChangeCallback = new OnViewPageChangeCallback();
        }

        viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback);
        viewPager2.registerOnPageChangeCallback(onPageChangeCallback);

        requestLayout();
    }

    private class OnViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (isAnimation) {
                firstVisiblePosition = position;
                lastPositionOffset = positionOffset;
                invalidate();
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (!isAnimation) {
                firstVisiblePosition = position;
                invalidate();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class OnViewPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            toDoItemCount.postValue(""+(position+1));
            if (isAnimation) {

                firstVisiblePosition = position;
                lastPositionOffset = positionOffset;
                invalidate();
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (!isAnimation) {
                firstVisiblePosition = position;
                invalidate();
            }
        }
    }
}
