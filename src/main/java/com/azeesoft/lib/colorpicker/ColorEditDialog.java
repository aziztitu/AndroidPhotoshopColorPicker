package com.azeesoft.lib.colorpicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by aziz titu2 on 2/11/2016.
 *
 * A modified dialog that allows the users to edit the colors in HSV or RGB modes
 */
public class ColorEditDialog extends Dialog {

    public static final int MODE_HSV=1,MODE_RGB=2;

    private AppCompatButton doneButton,cancelButton;
    private TextView name1,name2,name3,suffix1,suffix2,suffix3;
    private EditText val1,val2,val3;

    private RelativeLayout colorEditorRoot;

    private OnColorEditedListener onColorEditedListener;

    private int eAlpha=255;

    public ColorEditDialog(Context context) {
        super(context);
        init(context);
    }

    public ColorEditDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialod_edit_color_root, null));

        colorEditorRoot=(RelativeLayout)findViewById(R.id.colorEditorRoot);

        doneButton=(AppCompatButton)findViewById(R.id.doneEditing);
        cancelButton=(AppCompatButton)findViewById(R.id.cancelEditing);
        name1=(TextView)findViewById(R.id.name1);
        name2=(TextView)findViewById(R.id.name2);
        name3=(TextView)findViewById(R.id.name3);
        suffix1=(TextView)findViewById(R.id.suffix1);
        suffix2=(TextView)findViewById(R.id.suffix2);
        suffix3=(TextView)findViewById(R.id.suffix3);
        val1=(EditText)findViewById(R.id.val1);
        val2=(EditText)findViewById(R.id.val2);
        val3=(EditText)findViewById(R.id.val3);

        setModeAndValues(MODE_HSV, "", "", "",255);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setBackgroundColor(int color){
        colorEditorRoot.setBackgroundColor(color);
    }

    public void setFontColor(int color){
        name1.setTextColor(color);
        name2.setTextColor(color);
        name3.setTextColor(color);
        suffix1.setTextColor(color);
        suffix2.setTextColor(color);
        suffix3.setTextColor(color);
        val1.setTextColor(color);
        val2.setTextColor(color);
        val3.setTextColor(color);
        val1.getBackground().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        val2.getBackground().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        val3.getBackground().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public void setDoneButtonColor(int color){
        doneButton.setTextColor(color);
    }

    public void setCancelButtonColor(int color){
        cancelButton.setTextColor(color);
    }

    /**
     * Sets the Mode to either HSV or RGB and enters the current values
     * @param mode MODE_HSV or MODE_RGB
     * @param v1 value1 - Hue[0,360] or Red[0,255]
     * @param v2 value2 - Sat[0,100] or Green[0,255]
     * @param v3 value3 - Val[0,100] or Blue[0,255]
     * @param existingAlpha Alpha[0,255]
     */
    public void setModeAndValues(int mode,String v1,String v2,String v3,int existingAlpha){
        if(existingAlpha<0 || existingAlpha>255)
            existingAlpha=255;

        eAlpha=existingAlpha;

        if(mode==MODE_RGB){
            name1.setText("Red: ");
            name2.setText("Green: ");
            name3.setText("Blue: ");
            suffix1.setText("");
            suffix2.setText("");
            suffix3.setText("");
            val1.setText(v1);
            val2.setText(v2);
            val3.setText(v3);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int red=Integer.parseInt(val1.getText().toString());
                        int green=Integer.parseInt(val2.getText().toString());
                        int blue=Integer.parseInt(val3.getText().toString());

                        if(red>=0 && red<=255 && green>=0 && green<=255 && blue>=0 && blue<=255){
                            if(onColorEditedListener!=null){
                                onColorEditedListener.onColorEdited(Color.argb(eAlpha, red, green, blue));
                            }

                            dismiss();
                            return;
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getContext(),"Enter values between 0 and 255",Toast.LENGTH_LONG).show();
                }
            });
        }else{
            name1.setText("Hue: ");
            name2.setText("Sat: ");
            name3.setText("Val: ");
            suffix1.setText("\u00b0");
            suffix2.setText("%");
            suffix3.setText("%");
            val1.setText(v1);
            val2.setText(v2);
            val3.setText(v3);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int hue=Integer.parseInt(val1.getText().toString());
                        int sat=Integer.parseInt(val2.getText().toString());
                        int val=Integer.parseInt(val3.getText().toString());

                        if(hue<0 || hue>360){
                            Toast.makeText(getContext(),"Hue should be between 0"+"\u00b0"+" and 360"+"\u00b0",Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(sat<0 || sat>100 || val<0 || val>100){
                            Toast.makeText(getContext(),"Sat and Val should be between 0% and 100%",Toast.LENGTH_LONG).show();
                            return;
                        }

                        float[] hsv=new float[]{hue,(sat*1f)/100,(val*1f)/100};

                        if(onColorEditedListener!=null){
                            onColorEditedListener.onColorEdited(Color.HSVToColor(eAlpha,hsv));
                        }

                        dismiss();
                        return;

                    } catch (NumberFormatException e) {
                        e.printStackTrace();

                        Toast.makeText(getContext(),"Enter numeric values",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void setOnColorEditedListener(OnColorEditedListener onColorEditedListener) {
        this.onColorEditedListener = onColorEditedListener;
    }

    public interface OnColorEditedListener{
        void onColorEdited(int color);
    }
}
