package com.azeesoft.lib.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by aziz titu2 on 2/9/2016.
 */
public class SatValPicker extends ViewGroup {

    private static final int SKIP_COUNT_DIP = 2;
    private static final int MIN_SIZE_DIP = 200;

    private int skipCount = SKIP_COUNT_DIP;
    private boolean firstDraw, pendingUpdateSatVal = false;
    private boolean canUpdateHexVal = true;
    private int minSizePx, mWidth, mHeight;
    private float mHue = 0, mSat = 0, mVal = 1, pointerX, pointerY;
    private OnColorSelectedListener onColorSelectedListener;
    private ImageView thumb;
    private ColorPickerCompatScrollView colorPickerCompatScrollView;
    private ColorPickerCompatHorizontalScrollView colorPickerCompatHorizontalScrollView;

    public SatValPicker(Context context) {
        super(context);
        init(context);
    }

    public SatValPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SatValPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        maxHeight = 0;
        curLeft = childLeft;
        curTop = childTop;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                return;

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();
            //wrap is reach to the end
            if (curLeft + curWidth >= childRight) {
                curLeft = childLeft;
                curTop += maxHeight;
                maxHeight = 0;
            }
            //do the layout
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
            //store the max height
            if (maxHeight < curHeight)
                maxHeight = curHeight;
            curLeft += curWidth;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Constrain to square
        final int w = MeasureSpec.getSize(widthMeasureSpec);
        final int h = MeasureSpec.getSize(heightMeasureSpec);
        final int modeW = MeasureSpec.getMode(widthMeasureSpec);
        final int modeH = MeasureSpec.getMode(heightMeasureSpec);
        int size;
        if (modeW == MeasureSpec.UNSPECIFIED) {
            size = h;
        } else if (modeH == MeasureSpec.UNSPECIFIED) {
            size = w;
        } else {
            size = Math.min(w, h);
        }
        size = Math.max(size, minSizePx);
        setMeasuredDimension(size, size / 2);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        //System.out.println("init sat val measures: " + mWidth + "," + mHeight);
        if (firstDraw) {
            firstDraw = false;
            refreshSatValPicker(mHue);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void init(Context context) {
        skipCount = (int) Stools.dipToPixels(context, SKIP_COUNT_DIP);
        minSizePx = (int) Stools.dipToPixels(context, MIN_SIZE_DIP);
        firstDraw = true;
        pendingUpdateSatVal = false;

        thumb = new ImageView(context);
        thumb.setImageResource(R.drawable.thumb);

        thumb.setPivotX(Stools.dipToPixels(getContext(), 6));
        thumb.setPivotY(Stools.dipToPixels(getContext(), 6));

        addView(thumb);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //System.out.println("DOWN\n----");
                placePointer(event.getX(), event.getY());
                disableScroll();
                return true;
            case MotionEvent.ACTION_MOVE:
                //System.out.println("MOVE\n----");
                placePointer(event.getX(), event.getY());
                disableScroll();
                return true;
        }
        enableScroll();
        return false;
//        return super.onTouchEvent(event);
    }

    private void disableScroll() {
        if (colorPickerCompatScrollView != null)
            colorPickerCompatScrollView.setScrollDisabled(true);

        if (colorPickerCompatHorizontalScrollView != null)
            colorPickerCompatHorizontalScrollView.setScrollDisabled(true);
    }

    private void enableScroll() {
        if (colorPickerCompatScrollView != null)
            colorPickerCompatScrollView.setScrollDisabled(false);


        if (colorPickerCompatHorizontalScrollView != null)
            colorPickerCompatHorizontalScrollView.setScrollDisabled(false);
    }

    private void placePointer(float x, float y) {
        if (mWidth > 0 && mHeight > 0) {
            if (x < 0)
                x = 0;
            else if (x > mWidth)
                x = mWidth;

            if (y < 0)
                y = 0;
            else if (y > mHeight)
                y = mHeight;

            thumb.setX(x - Stools.dipToPixels(getContext(), 6));
            thumb.setY(y - Stools.dipToPixels(getContext(), 6));
            int thumbResId = y < mHeight / 2 ? R.drawable.thumb : R.drawable.thumb_white;
            thumb.setImageDrawable(getContext().getResources().getDrawable(thumbResId, getContext().getTheme()));

            retrieveColorAt(x, y);

        }
        //System.out.println("AshX: " + x);
        //System.out.println("AshY: " + y);
    }

    private void retrieveColorAt(float x, float y) {

        pointerX = x;
        pointerY = y;

        mSat = (x) / (float) mWidth;
        mVal = ((mHeight - y)) / (float) mHeight;

        onColorRetrieved(mHue, mSat, mVal);
    }

    private void onColorRetrieved(float hue, float sat, float val) {
        int color = Color.HSVToColor(new float[]{hue, sat, val});

        if (onColorSelectedListener != null) {
            onColorSelectedListener.onColorSelected(color, "#" + Integer.toHexString(color));
        }
    }

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }

    public void setSaturationAndValue(float sat, float val) {
        setSaturationAndValue(sat, val, true);
    }

    public void setSaturationAndValue(float sat, float val, boolean apply) {
        //System.out.println("Ash Sat Val: "+ sat+" " + val);
        //System.out.println("Ash mWidth mHeight: "+ mWidth+" "+mHeight);
        if ((mWidth <= 0 || mHeight <= 0) || !apply) {
            mSat = sat;
            mVal = val;
            pendingUpdateSatVal = true;
        } else
            placePointer(sat * mWidth, mHeight - (val * mHeight));
    }

    public void refreshSatValPicker(float hue) {
        mHue = hue;


        //System.out.println("Refreshing with Hue: "+hue);

//        mCanvas.drawBitmap(getSatValBitmap(hue), 0, 0, null);

        if (mWidth > 0 && mHeight > 0)
            new SatValChanger().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, hue);
    }

    public void setColorPickerCompatScrollView(ColorPickerCompatScrollView colorPickerCompatScrollView) {
        this.colorPickerCompatScrollView = colorPickerCompatScrollView;
    }

    public void setColorPickerCompatHorizontalScrollView(ColorPickerCompatHorizontalScrollView colorPickerCompatHorizontalScrollView) {
        this.colorPickerCompatHorizontalScrollView = colorPickerCompatHorizontalScrollView;
    }

    public boolean isCanUpdateHexVal() {
        return canUpdateHexVal;
    }

    public void setCanUpdateHexVal(boolean canUpdateHexVal) {
        this.canUpdateHexVal = canUpdateHexVal;
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color, String hexVal);
    }

    private class SatValChanger extends AsyncTask<Float, Void, BitmapDrawable> {

        private WeakReference<Context> contextRef = new WeakReference<>(getContext());
        private float hue;

        @Override
        protected BitmapDrawable doInBackground(Float... params) {
//                //System.out.println("sat val measures: "+mWidth+","+mHeight);
            hue = params[0];
            Bitmap bitmap = BitmapsGenerator.getSatValBitmap(hue, mWidth, mHeight, skipCount);
            Context context = contextRef.get();
            return bitmap != null && context != null ? new BitmapDrawable(context.getResources(), bitmap) : null;
        }

        @Override
        protected void onPostExecute(BitmapDrawable bitmapDrawable) {
//                if(Math.abs(hue-mHue)<40)
            if (bitmapDrawable != null)
                setBackground(bitmapDrawable);

            if (pendingUpdateSatVal)
                placePointer(mSat * mWidth, mHeight - (mVal * mHeight));
            else
                retrieveColorAt(pointerX, pointerY);
        }
    }
}
