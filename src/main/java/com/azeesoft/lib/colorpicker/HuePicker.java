package com.azeesoft.lib.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

import java.lang.ref.WeakReference;

/**
 * Created by aziz titu2 on 2/9/2016.
 * <p>
 * A modified {@link OrientedSeekBar} that draws the HueRange as its background
 */
public class HuePicker extends OrientedSeekBar {

    private boolean canUpdateHexVal = true;

    private OnHuePickedListener onHuePickedListener;

    private BitmapGenerationFailedListener bitmapGenerationFailedListener;

    public HuePicker(Context context) {
        super(context);
        init(context);
    }

    public HuePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
//        setThumb(mContext.getResources().getDrawable(R.drawable.thumb));
//        measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
        //System.out.println("Width1: " + getMeasuredWidth());
        //System.out.println("Height1: " + getMeasuredHeight());

        final ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                /*int width = HuePicker.this.getMeasuredWidth();
                int height = HuePicker.this.getMeasuredHeight();
                //System.out.println("Width2: " + width);
                //System.out.println("Height2: " + height);*/
                reloadBitmap();
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

    public void setHue(float hue) {
        if (onHuePickedListener != null)
            onHuePickedListener.onPicked(hue);
    }

    public void setOnHuePickedListener(OnHuePickedListener onHuePickedListener) {
        this.onHuePickedListener = onHuePickedListener;
    }

    public boolean isCanUpdateHexVal() {
        return canUpdateHexVal;
    }

    public void setCanUpdateHexVal(boolean canUpdateHexVal) {
        this.canUpdateHexVal = canUpdateHexVal;
    }

    public void reloadBitmap() {
        new HueChanger(getMeasuredWidth(), getMeasuredHeight()).execute();
    }

    public void setBitmapGenerationFailedListener(BitmapGenerationFailedListener listener) {
        bitmapGenerationFailedListener = listener;
    }

    public interface BitmapGenerationFailedListener {
        void onBitmapGenerationFailedListener();
    }

    public interface OnHuePickedListener {
        void onPicked(float hue);
    }

    private class HueChanger extends AsyncTask<Float, Void, BitmapDrawable> {

        private WeakReference<Context> contextRef = new WeakReference<>(getContext());
        private int width;
        private int height;

        private HueChanger(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        protected BitmapDrawable doInBackground(Float... params) {
            boolean horizontal = orientation == ORIENTATION_HORIZONTAL;
            Bitmap bitmap = BitmapsGenerator.getHueBitmap(horizontal ? width : height, horizontal ? height : width);
            Context context = contextRef.get();
            return bitmap != null && context != null ? new BitmapDrawable(context.getResources(), bitmap) : null;
        }

        @Override
        protected void onPostExecute(BitmapDrawable bitmapDrawable) {
            if (bitmapDrawable != null)
                setProgressDrawable(bitmapDrawable);
            else
                bitmapGenerationFailedListener.onBitmapGenerationFailedListener();
        }
    }
}
