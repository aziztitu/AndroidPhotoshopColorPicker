package com.azeesoft.lib.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;


/**
 * Created by aziz titu2 on 2/10/2016.
 *
 * A modified {@link OrientedSeekBar} to select Opacity
 */
public class OpacityPicker extends OrientedSeekBar {

    private static final int MIN_SIZE_DIP = 200;

    private static int minSizePx;
    private OnOpacityPickedListener onOpacityPickedListener;

    private boolean canUpdateHexVal=true;
    Context mContext;


    public OpacityPicker(Context context) {
        super(context);
        init(context);
    }

    public OpacityPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OpacityPicker, 0, 0);

        try {
            if(a.getBoolean(R.styleable.OpacityPicker_cp_showOpacityBar, true)){
                setVisibility(View.VISIBLE);
            }else{
                setVisibility(View.GONE);
            }
        } finally {
            a.recycle();
        }
    }


    private void init(Context context) {
        minSizePx = (int) Stools.dipToPixels(context, MIN_SIZE_DIP);
        mContext = context;
        setMax(255);

//        setThumb(mContext.getResources().getDrawable(R.drawable.thumb));
//        measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
        //System.out.println("Width1: " + getMeasuredWidth());
        //System.out.println("Height1: " + getMeasuredHeight());

/*
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                OpacityPicker.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                *//*int width = HuePicker.this.getMeasuredWidth();
                int height = HuePicker.this.getMeasuredHeight();
                //System.out.println("Width2: " + width);
                //System.out.println("Height2: " + height);*//*

//                setProgressDrawable(new BitmapDrawable(BitmapsGenerator.getHueBitmap(getMeasuredWidth(), getMeasuredHeight())));
            }
        });*/


        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setOp(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void setOnOpacityPickedListener(OnOpacityPickedListener onOpacityPickedListener){
        this.onOpacityPickedListener=onOpacityPickedListener;
    }

    public void setOp(int opacity){
        if (onOpacityPickedListener != null)
            onOpacityPickedListener.onPicked(opacity);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
//        setOp(progress);
    }

    public boolean isCanUpdateHexVal() {
        return canUpdateHexVal;
    }

    public void setCanUpdateHexVal(boolean canUpdateHexVal) {
        this.canUpdateHexVal = canUpdateHexVal;
    }

    public interface OnOpacityPickedListener{
        void onPicked(int opacity);
    }
}
