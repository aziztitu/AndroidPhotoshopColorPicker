package com.azeesoft.lib.colorpicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by aziz titu2 on 2/9/2016.
 */
public class Stools {

    private final static String SP_KEY_LAST_COLOR="lastColor";

    /**
     * Converts Density pixels(dp) to Pixels
     * @param context Context
     * @param dipValue dp value to convert
     * @return Pixels
     */
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    /**
     * Saves the color in SharedPreferences as the last picked color
     *
     * @param context Context
     * @param hexaVal Color to store in hexadecimal form (Eg: #ff000000 or #000000)
     */
    public static void saveLastColor(Context context,String hexaVal){
        try {
            Color.parseColor(hexaVal);
            SharedPreferences sharedPreferences=context.getSharedPreferences("colpick", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString(SP_KEY_LAST_COLOR,hexaVal);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the color stored in Shared Preferences as the last picked color or null if it doesn't exist
     * @param context Context
     * @return Last picked color in hexadecimal form (Eg: #ff000000 or #000000) or null if it doesn't exist
     */
    public static String loadLastColor(Context context){
        try {
            SharedPreferences sharedPreferences=context.getSharedPreferences("colpick", Context.MODE_PRIVATE);
            String s=sharedPreferences.getString(SP_KEY_LAST_COLOR,null);
            Color.parseColor(s);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Applies a tint on the drawable with the given color
     * @param drawable Drawable to apply the tint on
     * @param color Color to apply as tint on the drawable
     * @return Tinted drawable
     */
    public static Drawable tintDrawable(Drawable drawable,int color){
        int red   = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue  = color & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }

        return drawable;
    }

}
