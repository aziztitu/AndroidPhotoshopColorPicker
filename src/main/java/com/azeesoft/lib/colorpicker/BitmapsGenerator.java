package com.azeesoft.lib.colorpicker;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by aziz titu2 on 2/10/2016.
 *<p></p>
 * Base class for generating Bitmaps for the Color Pickers
 */
public class BitmapsGenerator {


    /**Gets a rectangular Bitmap representing the hue range (0-360)
     * @param width width of the HuePicker
     * @param height height of the HuePicker
     * @return A rectangular Bitmap representing the hue range (0-360)
     */
    public static Bitmap getHueBitmap(int width,int height) {
        //System.out.println("Width2: " + width);
        //System.out.println("Height2: " + height);

        Bitmap hueBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            float hue = 0;
            if (width > height) {
                hue = (x * 360f) / width;
            }
            for (int y = 0; y < height; y++) {
                if (width <= height) {
                    hue = (y * 360f) / height;
                }

                float[] hsv = new float[]{hue, 1, 1};

                hueBitmap.setPixel(x, y, Color.HSVToColor(hsv));
            }
        }

        return hueBitmap;

    }


    /**Gets a Rectangular Bitmap representing the Gradient of Sat and Val copied from the given BitmapDrawable for the given Hue
     * @param bitmapDrawable A BitmapDrawable to use as a base for the gradient of Sat and Val
     * @param hue Value of Hue to use for the bitmap generation of SatVal Gradient bitmap
     * @param width Width of the SatValPicker
     * @param height Height of the SatValPicker
     * @param skipCount Number of pixels to skip when generating Bitmap (increasing this results in faster bitmap generation but reduces bitmap quality)
     * @return A Rectangular Bitmap representing the Gradient of Sat and Val copied from the given BitmapDrawable for the given Hue
     */
    @Deprecated
    public static Bitmap getSatValBitmapFrom(BitmapDrawable bitmapDrawable,float hue, int width, int height, int skipCount){
        int[] pixels=new int[width*height];
        Bitmap bitmap=bitmapDrawable.getBitmap();
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for(int i=0;i<pixels.length;i++){
            float[] hsv=new float[3];
            Color.colorToHSV(pixels[i],hsv);
            hsv[0]=hue;
            pixels[i]=Color.HSVToColor(hsv);
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     *Gets a Rectangular Bitmap representing the Gradient of Sat and Val for the given Hue
     * @param hue Value of Hue to use for the bitmap generation of SatVal Gradient bitmap
     * @param width Width of the SatValPicker
     * @param height Height of the SatValPicker
     * @param skipCount Number of pixels to skip when generating Bitmap (increasing this results in faster bitmap generation but reduces bitmap quality)
     * @return A Rectangular Bitmap representing the Gradient of Sat and Val for the given Hue
     */
    public static Bitmap getSatValBitmap(float hue, int width, int height, int skipCount) {
//        //System.out.println("Width2: " + width);
//        //System.out.println("Height2: " + height);

        Bitmap hueBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int[] colors = new int[width * height];
        int pix = 0;
        for (int y = 0; y < height; y+=skipCount) {
            for (int x = 0; x < width; x+=skipCount) {

                if(pix>=(width*height))
                    break;

                float sat = (x) / (float)width;
                float val = ((height - y)) / (float)height;

                float[] hsv = new float[]{hue, sat, val};

                int color= Color.HSVToColor(hsv);
                for(int m=0;m<skipCount;m++){
                    if(pix>=(width*height))
                        break;

//                    //System.out.println("Filling...");

                    if((x+m)<width) {/*
                        //System.out.println(x+n);
                        //System.out.println(width);*/
                        colors[pix] = color;
                        pix++;
                    }
                }
            }

            for(int n=0;n<skipCount;n++){
                if(pix>=(width*height))
                    break;

//                    //System.out.println("Filling...");

                for (int x = 0; x < width; x++) {
                    colors[pix]=colors[pix-width];
                    pix++;
                }
            }
        }

        hueBitmap.setPixels(colors, 0, width, 0, 0, width, height);
        return hueBitmap;

    }
}
