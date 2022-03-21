package com.example.pdfeditormadtpeeps.Utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.example.pdfeditormadtpeeps.R;

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */

public class TextEditorDialogFragment extends DialogFragment {

    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_COLOR_CODE = "extra_color_code";
    public static final String EXTRA_COLOR_CODE2 = "extra_color_code2";
    public static final String EXTRA_COLOR_CODE3 = "extra_color_code3";
    public static final String FONT_STYLE = "font_style";
    public static final String FONT_ALIGNMENT = "font_alignment";
    private EditText mAddTextEditText;
    private TextView mAddTextDoneTextView;
    private InputMethodManager mInputMethodManager;
    private int mColorCode;
    private int mColorBg;
    private float mTextSize;
    private String mFontStyle;
    private String mFontAlignment;
    private TextEditor mTextEditor;
    Typeface typeface;

    public interface TextEditor {
        void onDone(String inputText, int colorCode, int colorBg, float txtFontSize, String txtFontStyle, String txtFontAlignment);
    }


    //Show dialog with provide text and text color
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                @NonNull String inputText,
                                                @ColorInt int colorCode,@ColorInt int bgColor, float txtFontSize, String txtFontStyle, String txtFontAlignment){
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        args.putInt(EXTRA_COLOR_CODE2, bgColor);
        args.putFloat(EXTRA_COLOR_CODE3,txtFontSize);
        args.putString(FONT_STYLE,txtFontStyle);
        args.putString(FONT_ALIGNMENT,txtFontAlignment);
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity) {
        return show(appCompatActivity, "", ContextCompat.getColor(appCompatActivity, R.color.colorWhite),ContextCompat.getColor(appCompatActivity, R.color.colorTextGrey),16,"Poppins","Left");
    }

    //Show dialog with default text input as empty and text color white


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_text_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddTextEditText = view.findViewById(R.id.add_text_edit_text);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv);

        //Setup the color picker for text color


        //This listener will change the text color when clicked on any color from picker


        mAddTextEditText.setText(getArguments().getString(EXTRA_INPUT_TEXT));
        mColorCode = getArguments().getInt(EXTRA_COLOR_CODE);
        mColorBg = getArguments().getInt(EXTRA_COLOR_CODE2);
        mTextSize = getArguments().getFloat(EXTRA_COLOR_CODE3);
        mFontStyle = getArguments().getString(FONT_STYLE);
        mFontAlignment = getArguments().getString(FONT_ALIGNMENT);
        mAddTextEditText.setTextColor(mColorCode);
        //mAddTextEditText.setBackgroundColor(mColorBg);
        mAddTextEditText.setTextSize(mTextSize);
        if (mFontStyle.equals("Poppins")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.poppinsregular);
        }  else if (mFontStyle.equals("Aladin Regular")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.aladinregular);
        }else if (mFontStyle.equals("Poppins Regular")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.poppinsregular);
        }else if (mFontStyle.equals("Satisfy")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.satisfy);
        }else if (mFontStyle.equals("Synemono")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.synemono);
        }else if (mFontStyle.equals("Electrolize")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.electrolize);
        }else if (mFontStyle.equals("Emily Scandy")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.emilyscandy);
        }else if (mFontStyle.equals("Arapey")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.arapey);
        }else if (mFontStyle.equals("Short Stack")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.shortstack);
        }else if (mFontStyle.equals("Aclonica")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.aclonica);
        }else if (mFontStyle.equals("Almendra")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.almendra);
        }else if (mFontStyle.equals("Bungee Shade")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.bungeeshade);
        }else if (mFontStyle.equals("Chonburi")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.chonburi);
        }else if (mFontStyle.equals("Doppio One")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.doppioone);
        }else if (mFontStyle.equals("Passero One")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.passeroone);
        }else if (mFontStyle.equals("Croissant One")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.croissantone);
        }else if (mFontStyle.equals("Poly")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.poly);
        }else if (mFontStyle.equals("Averialibre")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.averialibre);
        }else if (mFontStyle.equals("Philosopher")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.philosopher);
        }else if (mFontStyle.equals("Shanti")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.shanti);
        }else if (mFontStyle.equals("Faustina")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.faustina);
        }else if (mFontStyle.equals("Gudea")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.gudea);
        }else if (mFontStyle.equals("Public Sans")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.publicsans);
        }else if (mFontStyle.equals("Amaranth")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.amaranth);
        }else if (mFontStyle.equals("Itim")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.itim);
        }else if (mFontStyle.equals("Sansita")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.sansita);
        }else if (mFontStyle.equals("Scada")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.scada);
        }else if (mFontStyle.equals("B612")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.b612);
        }else if (mFontStyle.equals("Charm")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.charm);
        }else if (mFontStyle.equals("Mirza")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.mirza);
        }else if (mFontStyle.equals("Esteban")) {
            typeface = ResourcesCompat.getFont(getActivity(), R.font.esteban);
        }
        mAddTextEditText.setTypeface(typeface);



        if (mFontAlignment.equals("Left")){
            mAddTextEditText.setGravity(Gravity.LEFT);
        }
        else if (mFontAlignment.equals("Center")){
            mAddTextEditText.setGravity(Gravity.CENTER);
        }
        else if (mFontAlignment.equals("Right"))
        {
            mAddTextEditText.setGravity(Gravity.RIGHT);
        }

        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                dismiss();
                String inputText = mAddTextEditText.getText().toString();
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                    mTextEditor.onDone(inputText, mColorCode, mColorBg, mTextSize, mFontStyle, mFontAlignment);
                }
            }
        });

    }


    //Callback to listener if user is done with text editing
    public void setOnTextEditorListener(TextEditor textEditor) {
        mTextEditor = textEditor;
    }
}
