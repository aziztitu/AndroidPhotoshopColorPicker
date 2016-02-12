package com.azeesoft.lib.colorpicker;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

/**
 * Created by aziz titu2 on 2/9/2016.
 *
 * A modified {@link OrientedSeekBar} that draws the HueRange as its background
 */
public class HuePicker extends OrientedSeekBar {

    private static final int MIN_SIZE_DIP = 200;
    private boolean canUpdateHexVal=true;

    private static int minSizePx;
    private OnHuePickedListener onHuePickedListener;


    Context mContext;

    public HuePicker(Context context) {
        super(context);
        init(context);
    }

    public HuePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        minSizePx = (int) Stools.dipToPixels(context, MIN_SIZE_DIP);
        mContext = context;
//        setThumb(mContext.getResources().getDrawable(R.drawable.thumb));
//        measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
        //System.out.println("Width1: " + getMeasuredWidth());
        //System.out.println("Height1: " + getMeasuredHeight());

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                HuePicker.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                /*int width = HuePicker.this.getMeasuredWidth();
                int height = HuePicker.this.getMeasuredHeight();
                //System.out.println("Width2: " + width);
                //System.out.println("Height2: " + height);*/

                if(orientation==ORIENTATION_HORIZONTAL)
                    setProgressDrawable(new BitmapDrawable(BitmapsGenerator.getHueBitmap(getMeasuredWidth(), getMeasuredHeight())));
                else
                    setProgressDrawable(new BitmapDrawable(BitmapsGenerator.getHueBitmap(getMeasuredHeight(), getMeasuredWidth())));
            }
        });


        setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setHue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    public void setHue(float hue){
        if (onHuePickedListener != null)
            onHuePickedListener.onPicked(hue);
    }


    public void setOnHuePickedListener(OnHuePickedListener onHuePickedListener){
        this.onHuePickedListener=onHuePickedListener;
    }

    public boolean isCanUpdateHexVal() {
        return canUpdateHexVal;
    }

    public void setCanUpdateHexVal(boolean canUpdateHexVal) {
        this.canUpdateHexVal = canUpdateHexVal;
    }


    public interface OnHuePickedListener{
        void onPicked(float hue);
    }
}
