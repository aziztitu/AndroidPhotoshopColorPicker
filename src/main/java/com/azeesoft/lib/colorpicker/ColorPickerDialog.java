package com.azeesoft.lib.colorpicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



/**
 * Created by aziz titu2 on 2/10/2016.
 *<p></p>
 * A modified dialog that contains the color picking tools within itself
 *<br/><br/>
 * Use createColorPickerDialog(Context context) or createColorPickerDialog(Context context,int theme) to instantiate this class
 * <br/><br/>
 * To create custom themes, create a new style with any of the ColorPicker themes as parent and use the following attributes:<br/><br/>
 * cp_showOpacityBar: boolean<br/>
 * cp_showHexaDecimalValue: boolean<br/>
 * cp_showColorComponentsInfo: boolean<br/>
 * cp_backgroundColor: color<br/>
 * cp_hexaDecimalTextColor: color<br/>
 * cp_colorComponentsTextColor: color<br/>
 * cp_positiveActionTextColor: color<br/>
 * cp_negativeActionTextColor: color<br/>
 * cp_sliderThumbColor: color<br/>
 */
public class ColorPickerDialog extends Dialog {

    /**
     * Default resource for the Light themed Color Picker
     */
    public static final int LIGHT_THEME=R.style.ColorPicker_Light;

    /**
     * Default resource for the Dark themed Color Picker
     */
    public static final int DARK_THEME= R.style.ColorPicker_Dark;



    private HuePicker huePicker;
    private OpacityPicker opacityPicker;
    private SatValPicker satValPicker;
    private LinearLayout colorPreviewBox,oldColorPreviewBox;
    private EditText hexVal;
    private TextView hex,hue,sat,val,red,green,blue,alpha;
    private ImageView hsvEditIcon,rgbEditIcon;
    private AppCompatButton pickButton,cancelButton;
    private RelativeLayout colorComponents,hexHolder,hsv,rgb;
    private ColorPickerCompatScrollView colorPickerCompatScrollView;
    private ColorPickerCompatHorizontalScrollView colorPickerCompatHorizontalScrollView;
    private ColorPickerRootView colorPickerRootView;
    private ColorEditDialog colorEditDialog;


    private int initColor;
    private boolean skipHexValChange=false;

    private int mColor=Color.parseColor("#ffffffff");
    private String mHexVal="#ffffffff";

    private OnColorPickedListener onColorPickedListener;
    private OnClosedListener onClosedListener;

    private ColorPickerDialog(Context context) {
        super(context);
        init(context);
    }

    private ColorPickerDialog(Context context, int theme) {
        super(context,theme);
        init(context);
    }

    /**
     * Creates a Light themed ColorPickerDialog
     *<br/><br/>
     * To create a Dark themed ColorPickerDialog, use ColorPickerDialog.createColorPickerDialog(context,ColorPickerDialog.DARK_THEME);
     *
     * <br/><br/>
     * To create custom themes, create a new style with any of the ColorPicker themes as parent and use the following attributes:<br/><br/>
     * cp_showOpacityBar: boolean<br/>
     * cp_showHexaDecimalValue: boolean<br/>
     * cp_showColorComponentsInfo: boolean<br/>
     * cp_backgroundColor: color<br/>
     * cp_hexaDecimalTextColor: color<br/>
     * cp_colorComponentsTextColor: color<br/>
     * cp_positiveActionTextColor: color<br/>
     * cp_negativeActionTextColor: color<br/>
     * cp_sliderThumbColor: color<br/>
     *
     * @param context Context
     * @return Light themed ColorPickerDialog
     */
    public static ColorPickerDialog createColorPickerDialog(Context context){
        return new ColorPickerDialog(new ContextThemeWrapper(context,LIGHT_THEME), LIGHT_THEME);
    }

    /**
     * Creates a Custom themed ColorPickerDialog
     *<br/><br/>
     * To create a light themed ColorPickerDialog, use ColorPickerDialog.createColorPickerDialog(context,ColorPickerDialog.LIGHT_THEME);
     *<br/><br/>
     * To create a dark themed ColorPickerDialog, use ColorPickerDialog.createColorPickerDialog(context,ColorPickerDialog.DARK_THEME);
     *<br/><br/>
     * To create custom themes, create a new style with any of the ColorPicker themes as parent and use the following attributes:<br/><br/>
     * cp_showOpacityBar: boolean<br/>
     * cp_showHexaDecimalValue: boolean<br/>
     * cp_showColorComponentsInfo: boolean<br/>
     * cp_backgroundColor: color<br/>
     * cp_hexaDecimalTextColor: color<br/>
     * cp_colorComponentsTextColor: color<br/>
     * cp_positiveActionTextColor: color<br/>
     * cp_negativeActionTextColor: color<br/>
     * cp_sliderThumbColor: color<br/>
     *
     * @param context Context
     * @param theme theme resId
     * @return Custom themed ColorPickerDialog
     */
    public static ColorPickerDialog createColorPickerDialog(Context context,int theme){
        return new ColorPickerDialog(new ContextThemeWrapper(context,theme), theme);
    }

    @Override
    public void show() {
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        super.show();
        reloadLastColor();
        if(opacityPicker.getVisibility()!=View.VISIBLE)
            opacityPicker.setProgress(255);
//        init(getContext());
    }

    @Override
    public void dismiss() {
        super.dismiss();
        initColor=getLastColor(getContext());
        if(onClosedListener !=null){
            onClosedListener.onClosed();
        }
    }

    private void init(final Context context){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_root, null));
        setTitle("Pick a color");

        initColor=getLastColor(context);

        colorEditDialog=new ColorEditDialog(context);
        colorEditDialog.setOnColorEditedListener(new ColorEditDialog.OnColorEditedListener() {
            @Override
            public void onColorEdited(int color) {
                setCurrentColor(color);
            }
        });

        huePicker=(HuePicker)findViewById(R.id.hueBar);
        opacityPicker=(OpacityPicker)findViewById(R.id.opacityBar);
        satValPicker=(SatValPicker)findViewById(R.id.satValBox);
        colorPreviewBox=(LinearLayout)findViewById(R.id.colorPreviewBox);
        oldColorPreviewBox=(LinearLayout)findViewById(R.id.oldColorPreviewBox);
        hexHolder=(RelativeLayout)findViewById(R.id.hexHolder);
        pickButton=(AppCompatButton)findViewById(R.id.pickButton);
        cancelButton=(AppCompatButton)findViewById(R.id.cancelButton);
        colorComponents=(RelativeLayout)findViewById(R.id.colorComponents);
        hsv=(RelativeLayout)findViewById(R.id.hsv);
        rgb=(RelativeLayout)findViewById(R.id.rgb);
        colorPickerRootView=(ColorPickerRootView)findViewById(R.id.colorPickerRoot);
        hexVal=(EditText)findViewById(R.id.hexVal);



        View hScrollView=findViewById(R.id.scrollView);

        if(hScrollView instanceof ColorPickerCompatScrollView)
            colorPickerCompatScrollView=(ColorPickerCompatScrollView)hScrollView;
        else if(hScrollView instanceof ColorPickerCompatHorizontalScrollView)
            colorPickerCompatHorizontalScrollView=(ColorPickerCompatHorizontalScrollView)hScrollView;

        hexVal.setImeOptions(EditorInfo.IME_ACTION_GO);
        hexVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//
                if (skipHexValChange) {
                    skipHexValChange = false;
                } else {
                    hexValTyped(s.toString());
                }
            }
        });

        hex=(TextView)findViewById(R.id.hex);
        hue=(TextView)findViewById(R.id.hue);
        sat=(TextView)findViewById(R.id.sat);
        val=(TextView)findViewById(R.id.val);
        red=(TextView)findViewById(R.id.red);
        green=(TextView)findViewById(R.id.green);
        blue=(TextView)findViewById(R.id.blue);
        alpha=(TextView)findViewById(R.id.alpha);
        hsvEditIcon=(ImageView)findViewById(R.id.hsvEditIcon);
        rgbEditIcon=(ImageView)findViewById(R.id.rgbEditIcon);

        huePicker.setOnHuePickedListener(new HuePicker.OnHuePickedListener() {
            @Override
            public void onPicked(float hue) {
                satValPicker.refreshSatValPicker(hue);
                ColorPickerDialog.this.hue.setText("H: " + (int) hue + " \u00b0");
            }
        });

        huePicker.setMax(360);
        huePicker.setProgress(0);
        huePicker.setColorPickerCompatScrollView(colorPickerCompatScrollView);
        huePicker.setColorPickerCompatHorizontalScrollView(colorPickerCompatHorizontalScrollView);

        satValPicker.setOnColorSelectedListener(new SatValPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color, String hexVal) {
                refreshPreviewBox(color, opacityPicker.getProgress(), satValPicker.isCanUpdateHexVal());
                satValPicker.setCanUpdateHexVal(true);
            }
        });
        satValPicker.setColorPickerCompatScrollView(colorPickerCompatScrollView);
        satValPicker.setColorPickerCompatHorizontalScrollView(colorPickerCompatHorizontalScrollView);


        opacityPicker.setOnOpacityPickedListener(new OpacityPicker.OnOpacityPickedListener() {
            @Override
            public void onPicked(int opacity) {
                //System.out.println("Opacity: " + opacity);
                ColorDrawable colorDrawable = (ColorDrawable) colorPreviewBox.getBackground();
                if (colorDrawable == null)
                    return;

                int color = colorDrawable.getColor();
                refreshPreviewBox(color, opacity, opacityPicker.isCanUpdateHexVal());
                opacityPicker.setCanUpdateHexVal(true);
            }
        });
        opacityPicker.setColorPickerCompatScrollView(colorPickerCompatScrollView);
        opacityPicker.setColorPickerCompatHorizontalScrollView(colorPickerCompatHorizontalScrollView);

        hsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String h = getPlainComponentValue(hue.getText().toString());
                    String s = getPlainComponentValue(sat.getText().toString());
                    String v = getPlainComponentValue(val.getText().toString());
                    int a = Integer.parseInt(getPlainComponentValue(alpha.getText().toString()));
                    colorEditDialog.setModeAndValues(ColorEditDialog.MODE_HSV, h, s, v, a);
                    colorEditDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String r=getPlainComponentValue(red.getText().toString());
                    String g=getPlainComponentValue(green.getText().toString());
                    String b=getPlainComponentValue(blue.getText().toString());
                    int a=Integer.parseInt(getPlainComponentValue(alpha.getText().toString()));
                    colorEditDialog.setModeAndValues(ColorEditDialog.MODE_RGB,r,g,b,a);
                    colorEditDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onColorPickedListener != null)
                    onColorPickedListener.onColorPicked(mColor, mHexVal);

                Stools.saveLastColor(getContext(), mHexVal);

                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

//        reloadLastColor();

        applyTheme();

    }

    private String getPlainComponentValue(String s){
        s=s.split(":",2)[1];
        s=s.replaceAll("%","");
        s=s.replaceAll("\u00b0","");
        return s.replaceAll(" ","");
    }

    private void hexValTyped(String s){
        //System.out.println("Hexa typed: " + s);
        try {
            int color=Color.parseColor("#"+s);
            if(opacityPicker.getVisibility()!=View.VISIBLE){
                if(s.length()==8){
                    s=s.substring(2);
                    color=Color.parseColor("#" + s);

                }
            }
            setCurrentColor(color,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyTheme(){
        if(colorPickerRootView.isFLAG_SHOW_HEX()){
            showHexaDecimalValue();
        }else{
            hideHexaDecimalValue();
        }

        if(colorPickerRootView.isFLAG_SHOW_COLOR_COMPS()){
            showColorComponentsInfo();
        }else{
            hideColorComponentsInfo();
        }

        int hexColor=colorPickerRootView.getFLAG_HEX_COLOR();
        setHexaDecimalTextColor(hexColor);

        int compsColor=colorPickerRootView.getFLAG_COMPS_COLOR();
        //System.out.println("CompsColor: " + compsColor);
        setColorComponentsTextColor(compsColor);

        Drawable hsvIcon=getContext().getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp);
        Drawable rgbIcon=getContext().getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp);
        hsvEditIcon.setImageDrawable(Stools.tintDrawable(hsvIcon,compsColor));
        rgbEditIcon.setImageDrawable(Stools.tintDrawable(rgbIcon,compsColor));

        setPositiveActionText(colorPickerRootView.getFLAG_POS_ACTION_TEXT());
        setNegativeActionText(colorPickerRootView.getFLAG_NEG_ACTION_TEXT());
        setPositiveActionTextColor(colorPickerRootView.getFLAG_POSITIVE_COLOR());
        setNegativeActionTextColor(colorPickerRootView.getFLAG_NEGATIVE_COLOR());


        int sliderThumbColor = colorPickerRootView.getFLAG_SLIDER_THUMB_COLOR();

        setSliderThumbColor(sliderThumbColor);


        colorEditDialog.setBackgroundColor(colorPickerRootView.getFLAG_BACKGROUND_COLOR());
        colorEditDialog.setFontColor(colorPickerRootView.getFLAG_COMPS_COLOR());
        colorEditDialog.setDoneButtonColor(colorPickerRootView.getFLAG_POSITIVE_COLOR());
        colorEditDialog.setCancelButtonColor(colorPickerRootView.getFLAG_NEGATIVE_COLOR());

    }

    private void reloadLastColor(){
        reloadLastColor(initColor);
    }

    private void reloadLastColor(int current_color){
        String lastHexVal=Stools.loadLastColor(getContext());
        if(lastHexVal!=null){
            //System.out.println("LastColor: "+lastHexVal);
            int lastColor=Color.parseColor(lastHexVal);
            oldColorPreviewBox.setBackgroundColor(lastColor);
        }
        setCurrentColor(current_color);
    }

    private void setCurrentColor(int color){
        setCurrentColor(color, true);
    }

    private void setCurrentColor(int color,boolean updateHexVal){
        float[] hsv=new float[3];
        Color.colorToHSV(color, hsv);

//        huePicker.setCanUpdateHexVal(updateHexVal);
        satValPicker.setCanUpdateHexVal(updateHexVal);
        opacityPicker.setCanUpdateHexVal(updateHexVal);


        satValPicker.setSaturationAndValue(hsv[1], hsv[2], false);
        if(huePicker.getProgress()!=(int)hsv[0])
            huePicker.setProgress((int) hsv[0]);
        else
            satValPicker.refreshSatValPicker(huePicker.getProgress());

        //System.out.println("Ash: "+hsv[1]+" "+hsv[2]);
        opacityPicker.setProgress(Color.alpha(color));
    }

    private void refreshPreviewBox(int color,int opacity,boolean updateHexVal){
        color= Color.argb(opacity, Color.red(color), Color.green(color), Color.blue(color));
        colorPreviewBox.setBackgroundColor(color);

        mHexVal="#"+Integer.toHexString(color);

        //System.out.println("Retrieved Color: " + color + " (#" + mHexVal + ")");

        mColor=color;

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        sat.setText("S: " + (int) (hsv[1] * 100) + " %");
        val.setText("V: " + (int) (hsv[2] * 100) + " %");

        if(updateHexVal)
            setHexValText(mHexVal);

        red.setText("R: " + Color.red(color));
        green.setText("G: " + Color.green(color));
        blue.setText("B: " + Color.blue(color));
        alpha.setText("A: " + Color.alpha(color));
    }

    private void setHexValText(String s){
        s=s.replace("#","");
        skipHexValChange=true;
        hexVal.setText(s);
    }


    /**
     *  Register a callback to be invoked when the user picks a color by tapping the positive action
     *
     * @param onColorPickedListener Listener to call when color is picked
     */
    public void setOnColorPickedListener(OnColorPickedListener onColorPickedListener){
        this.onColorPickedListener=onColorPickedListener;
    }


    /**
     *  Register a callback to be invoked when the user closes the dialog
     *
     * @param onClosedListener Listener to call when dialog is closed
     */
    public void setOnClosedListener(OnClosedListener onClosedListener){
        this.onClosedListener = onClosedListener;
    }


    /**
     * Gets the last picked color as a String in hexadecimal form (Eg: #ff000000)
     *
     * @param context Context
     * @return Returns the last picked color as a hexadecimal String(Eg: #ff000000) or null if the last picked color doesn't exist
     */
    public static String getLastColorAsHexa(Context context){
        return Stools.loadLastColor(context);
    }


    /**
     * Gets the last picked color as an int
     *
     * @param context Context
     * @return Returns the last picked color or Transparent color if the last picked color doesn't exist
     */
    public static int getLastColor(Context context){
        String lastColorHex=Stools.loadLastColor(context);
        if(lastColorHex==null)
            return Color.parseColor("#00ffffff");
        else
            return Color.parseColor(lastColorHex);
    }

    /**
     * Gets the currently selected color
     * @return Returns the currently selected color in the ColorPicker
     */
    public int getCurrentColor(){
        return mColor;
    }

    /**
     * Gets the currently selected color as a String in hexadecimal form (Eg: #ff000000)
     * @return Returns the currently selected color in the ColorPicker as a hexadecimal String (Eg: #ff000000)
     */
    public String getCurrentColorAsHexa(){
        return mHexVal;
    }

    /**
     * Sets a pre-selected color in the ColorPicker when the dialog opens
     *
     * @param color Hexadecimal String form of the color to be pre-selected in the ColorPicker when the dialog opens
     */
    public void setInitialColor(String hexVal){
        setInitialColor(Color.parseColor(hexVal));
    }


    /**
     * Sets a pre-selected color in the ColorPicker when the dialog opens
     *
     * @param color Color to be pre-selected in the ColorPicker when the dialog opens
     */
    public void setInitialColor(int color){
        initColor=color;
    }

    /**
     * Sets the last color and current color in the ColorPicker
     *
     * @param color Color to be applied to the last color and current color in the ColorPicker
     */
    public void setLastColor(int color){
        setLastColor("#"+Integer.toHexString(color));
    }

    /**
     * Sets the last color and current color in the ColorPicker
     *
     * @param hexVal Hexadecimal String form of the color to be applied to the last color and current color in the ColorPicker
     */
    public void setLastColor(String hexVal){
        Stools.saveLastColor(getContext(), hexVal);
        initColor=Color.parseColor(hexVal);
        reloadLastColor();
    }

    /**
     * Show the OpacityBar in the ColorPicker
     */
    public void showOpacityBar(){
        opacityPicker.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the OpacityBar in the ColorPicker
     */
    public void hideOpacityBar(){
        opacityPicker.setVisibility(View.GONE);
    }

    /**
     * Show the Hexadecimal value in the ColorPicker
     */
    public void showHexaDecimalValue(){
        hexHolder.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the Hexadecimal value in the ColorPicker
     */
    public void hideHexaDecimalValue(){
        hexHolder.setVisibility(View.GONE);
    }

    /**
     * Show the Color Components Information (Hue, Saturation, Value, Red, Gren, Blue, Alpha) in the ColorPicker
     */
    public void showColorComponentsInfo(){
        colorComponents.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the Color Components Information (Hue, Saturation, Value, Red, Gren, Blue, Alpha) in the ColorPicker
     */
    public void hideColorComponentsInfo(){
        colorComponents.setVisibility(View.GONE);
    }

    /**
     * Sets a Background color for the dialog
     * @param color Color to use as background for the dialog
     */
    public void setBackgroundColor(int color){
        colorPickerRootView.setBackgroundColor(color);
    }

    /**
     * Sets the color of the hexadecimal value
     * @param color Color to use for the hexadecimal value's text
     */
    public void setHexaDecimalTextColor(int color){
        hex.setTextColor(color);
        hexVal.setTextColor(color);
        hexVal.getBackground().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * Sets the color of the Color components info
     * @param color Color to use for the color components info's text
     */
    public void setColorComponentsTextColor(int color){
        hue.setTextColor(color);
        sat.setTextColor(color);
        val.setTextColor(color);
        red.setTextColor(color);
        green.setTextColor(color);
        blue.setTextColor(color);
        alpha.setTextColor(color);
    }

    /**
     * Sets the text value for the Positive action (Default is "Pick")
     * @param s String to use as Positive action's text
     */
    public void setPositiveActionText(String s){
        pickButton.setText(s);
    }

    /**
     * Sets the color of the Positive action's text
     * @param color Color to use for the Positive action's text
     */
    public void setPositiveActionTextColor(int color){
        pickButton.setTextColor(color);
    }

    /**
     * Sets the text value for the Negative action (Default is "Cancel")
     * @param s String to use as Negative action's text
     */
    public void setNegativeActionText(String s){
        cancelButton.setText(s);
    }

    /**
     * Sets the color of the Negative action's text
     * @param color Color to use for the Negative action's text
     */
    public void setNegativeActionTextColor(int color) {
        cancelButton.setTextColor(color);
    }

    /**
     * Sets the color of the Slider's thumb of both HuePicker and OpacityPicker
     * @param color Color to use for the Slider's thumb of HuePicker and OpacityPicker
     */
    public void setSliderThumbColor(int color){
        Drawable hueThumbDrawable=getContext().getResources().getDrawable(R.drawable.slider_thumb);
        Drawable opacityThumbDrawable=getContext().getResources().getDrawable(R.drawable.slider_thumb);

        hueThumbDrawable=Stools.tintDrawable(hueThumbDrawable,color);
        opacityThumbDrawable=Stools.tintDrawable(opacityThumbDrawable,color);
        huePicker.setThumb(hueThumbDrawable);
        opacityPicker.setThumb(opacityThumbDrawable);
    }

    /**
     * Interface definition for a callback to be invoked when the user picks a color by tapping the positive action
     */
    public interface OnColorPickedListener{
        /**
         * Called when the user picks a color by tapping the positive action
         * @param color Color picked by the user
         * @param hexVal Color picked by the user in hexadecimal form
         */
        void onColorPicked(int color, String hexVal);
    }

    /**
     * Interface definition for a callback to be invoked when the user closes the dialog
     */
    public interface OnClosedListener {
        /**
         * Called when the user closes the dialog
         */
        void onClosed();
    }
}
