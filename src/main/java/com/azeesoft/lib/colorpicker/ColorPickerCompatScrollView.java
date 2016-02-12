package com.azeesoft.lib.colorpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by aziz titu2 on 2/10/2016.
 *<p></p>
 * Custom scrollview to wrap the ColorPickers in order to manage the TouchEvents efficiently
 *<br/><br/>
 * This should also be set for the pickers using setColorPickerCompatScrollView() method of the pickers
 *<br/><br/>
 * Wrapping them in a normal scrollview will result in bugs
 */
public class ColorPickerCompatScrollView extends ScrollView {

    private boolean isScrollDisabled=false;

    public ColorPickerCompatScrollView(Context context) {
        super(context);
        isScrollDisabled=false;
    }

    public ColorPickerCompatScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        isScrollDisabled=false;
    }

    public ColorPickerCompatScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isScrollDisabled=false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isScrollDisabled)
            return false;
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * Use it to disable the scroll and allow child views to get touch events
     *<p></p>
     * Make sure to enable the scroll when the child view doesn't need to get the touch events
     *
     * @param b disable(true) / enable(false)
     */
    public void setScrollDisabled(boolean b){
        isScrollDisabled=b;
    }
}
