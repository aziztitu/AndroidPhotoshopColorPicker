package com.azeesoft.lib.colorpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by aziz titu2 on 2/11/2016.
 *<p></p>
 * A modified {@link AppCompatSeekBar} which can be used both Horizontally as well as Vertically
 *<p></p>
 * To specify the orientation:
 *<p></p>
 * Java:<p></p>
 *      setOrientation(int orientation);<p></p>
 *
 * XML:<p></p>
 *      attribute 'orientation'<p></p>
 *
 *      Use<p></p>
 *          orientation=horizontal<p></p>
 *
 *          or<p></p>
 *
 *          orientation=vertical<p></p>
 */
public class OrientedSeekBar extends AppCompatSeekBar {

    protected static final int ORIENTATION_HORIZONTAL=1,ORIENTATION_VERTICAL=2;

    protected int orientation=ORIENTATION_HORIZONTAL;

    private OnSeekBarChangeListener seekBarChangeListener;
    private ColorPickerCompatScrollView colorPickerCompatScrollView;
    private ColorPickerCompatHorizontalScrollView colorPickerCompatHorizontalScrollView;


    public OrientedSeekBar(Context context) {
        super(context);
    }

    public OrientedSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.OrientedSeekBar, 0, 0);

        try {
            orientation = a.getInt(R.styleable.OrientedSeekBar_orientation, ORIENTATION_HORIZONTAL);
        } finally {
            a.recycle();
        }
    }



    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener){
        this.seekBarChangeListener = mListener;
        super.setOnSeekBarChangeListener(mListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(orientation==ORIENTATION_HORIZONTAL){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        }else{
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);
            setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(orientation==ORIENTATION_HORIZONTAL)
            super.onSizeChanged(w, h, oldw, oldh);
        else
            super.onSizeChanged(h, w, oldh, oldw);
    }


    @Override
    protected synchronized void onDraw(Canvas c) {
        if(orientation!=ORIENTATION_HORIZONTAL){

            c.rotate(-90);
            c.translate(-getHeight(), 0);
        }
        super.onDraw(c);

        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(orientation==ORIENTATION_HORIZONTAL)
        {
            return super.onTouchEvent(event);
        }


        //Only if orientation is vertical
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
//                is = true;
                int i = 0;
                i = getMax() - (int) Math.ceil((getMax() * event.getY() / getHeight()));
                setProgress(i);
                //setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onStartTrackingTouch(this);
                }

                disableScroll();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int i = 0;
                i = getMax() - (int) Math.ceil((getMax() * event.getY() / getHeight()));
                setProgress(i);
                //setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);

                disableScroll();
                break;
            }
            case MotionEvent.ACTION_UP: {
                int i = 0;
//                is = false;
                i = getMax() - (int) Math.ceil((getMax() * event.getY() / getHeight()));
                setProgress(i);
                //setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onStopTrackingTouch(this);
                }
                enableScroll();
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onStopTrackingTouch(this);
                }
                enableScroll();
                break;
            default:
                enableScroll();
        }
        return true;
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(seekBarChangeListener!=null)
                    seekBarChangeListener.onStartTrackingTouch(this);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_MOVE:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                seekBarChangeListener.onProgressChanged(this, getMax() - (int) (getMax() * event.getY() / getHeight()), true);
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("Seek bar UP");
                seekBarChangeListener.onStopTrackingTouch(this);
                break;

        }
        return true;
    }*/

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);

        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    private void disableScroll(){
        if(colorPickerCompatScrollView!=null)
            colorPickerCompatScrollView.setScrollDisabled(true);

        if(colorPickerCompatHorizontalScrollView!=null)
            colorPickerCompatHorizontalScrollView.setScrollDisabled(true);
    }

    private void enableScroll(){
        if(colorPickerCompatScrollView!=null)
            colorPickerCompatScrollView.setScrollDisabled(false);


        if(colorPickerCompatHorizontalScrollView!=null)
            colorPickerCompatHorizontalScrollView.setScrollDisabled(false);
    }

    public void setColorPickerCompatScrollView(ColorPickerCompatScrollView colorPickerCompatScrollView){
        this.colorPickerCompatScrollView=colorPickerCompatScrollView;
    }

    public void setColorPickerCompatHorizontalScrollView(ColorPickerCompatHorizontalScrollView colorPickerCompatHorizontalScrollView){
        this.colorPickerCompatHorizontalScrollView=colorPickerCompatHorizontalScrollView;
    }

    /**
     * Sets the orientation of the seekbar
     * @param orientation Use ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL (Default is ORIENTATION_HORIZONTAL)
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
