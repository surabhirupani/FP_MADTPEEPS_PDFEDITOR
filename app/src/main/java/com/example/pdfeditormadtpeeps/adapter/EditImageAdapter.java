package com.example.pdfeditormadtpeeps.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.example.pdfeditormadtpeeps.R;
import com.example.pdfeditormadtpeeps.Utility.TextEditorDialogFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;

public class EditImageAdapter  extends PagerAdapter implements OnPhotoEditorListener,
        View.OnClickListener
   {
    private final Context mContext;
    private final ArrayList<String> mPreviewItems;
    private final LayoutInflater mInflater;
    private PhotoEditorView photoEditorView;
    Button iv_save;
    PhotoEditor mPhotoEditor;
    Typeface typeface;
    TextEditorDialogFragment textEditorDialogFragment;
    View layout;
    Uri mSaveImageUri;
    private ProgressDialog mProgressDialog;
    String path;
    ImageView img_Undo,img_Redo;


    public EditImageAdapter(Context context, ArrayList<String> previewItems) {
        mContext = context;
        mPreviewItems = previewItems;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        layout = mInflater.inflate(R.layout.pdf_preview_item, view, false);
        photoEditorView = layout.findViewById(R.id.photoEditorView);
        iv_save = layout.findViewById(R.id.iv_save);
        img_Undo = layout.findViewById(R.id.img_Undo);
        img_Redo = layout.findViewById(R.id.img_Redo);
        //Toast.makeText(mContext,"Position"+position,Toast.LENGTH_LONG).show();
        Log.d("position", String.valueOf(position));

        mPhotoEditor = new PhotoEditor.Builder(mContext, photoEditorView)
                .setPinchTextScalable(true)
                .build();
        mPhotoEditor.setOnPhotoEditorListener(this);
        path = mPreviewItems.get(position);
        File fileLocation = new File(path);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.fromFile(fileLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPhotoEditor.clearAllViews();
        photoEditorView.getSource().setImageBitmap(transformBitmapTo2048px(bitmap));
        view.addView(layout);
        iv_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });

        img_Undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.undo();
            }
        });
        img_Redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.redo();
            }
        });
        return layout;
    }

   @RequiresApi(api = Build.VERSION_CODES.O)
   @SuppressLint("MissingPermission")
   public void saveImage() {
        showLoading("Saving...");
       File file = new File(path);
       try {
           file.createNewFile();

           SaveSettings saveSettings = new SaveSettings.Builder()
                   .setClearViewsEnabled(true)
                   .setTransparencyEnabled(true)
                   .build();

           mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
               @Override
               public void onSuccess(@NonNull String imagePath) {
                   hideLoading();
                   mSaveImageUri = Uri.fromFile(new File(imagePath));
                   photoEditorView.getSource().setImageURI(mSaveImageUri);
               }

               @Override
               public void onFailure(@NonNull Exception exception) {
                   hideLoading();
                   Toast.makeText(mContext, "Failed to save Image", Toast.LENGTH_LONG).show();
               }
           });
       }
       catch (IOException e) {
           e.printStackTrace();
           hideLoading();
            Log.d("EXCEPTION", e.getMessage());
       }
   }

   protected void showLoading(@NonNull String message) {
       mProgressDialog = new ProgressDialog(mContext);
       mProgressDialog.setMessage(message);
       mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       mProgressDialog.setCancelable(false);
       mProgressDialog.show();
   }

   protected void hideLoading() {
       if (mProgressDialog != null) {
           mProgressDialog.dismiss();
       }
   }

   public void onEraser(){
       mPhotoEditor.brushEraser();
   }

    @Override
    public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

   public void onColorChanged(int colorCode) {
          mPhotoEditor.setBrushColor(colorCode);
          //Toast.makeText(mContext,"Dipti"+colorCode,Toast.LENGTH_LONG).show();
   }
   public void onOpacityChanged(int opacity) {
          mPhotoEditor.setOpacity(opacity);

   }
   public void onBrushSizeChanged(int brushSize) {
          mPhotoEditor.setBrushSize(brushSize);

   }
   public void onTextChange(int color, int bgColor, float fontSize, String txtFontStyle, String txtFontAlignment) {
       Log.d("COLOR", String.valueOf(color));
       TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show((AppCompatActivity) mContext, "", color,bgColor,fontSize,txtFontStyle, txtFontAlignment);
       textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
           @Override
           public void onDone(String inputText, int colorCode, int colorBg, float txtFontSize, String txtFontStyle, String txtFontAlignment) {
               final TextStyleBuilder styleBuilder = new TextStyleBuilder();
               styleBuilder.withTextColor(colorCode);
               styleBuilder.withBackgroundColor(colorBg);
               styleBuilder.withTextSize(txtFontSize);

               if (txtFontStyle.equals("Poppins")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.poppinsregular);
               } else if (txtFontStyle.equals("Aladin Regular")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.aladinregular);
               }else if (txtFontStyle.equals("Poppins Regular")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.poppinsregular);
               }else if (txtFontStyle.equals("Satisfy")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.satisfy);
               }else if (txtFontStyle.equals("Synemono")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.synemono);
               }else if (txtFontStyle.equals("Electrolize")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.electrolize);
               }else if (txtFontStyle.equals("Emily Scandy")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.emilyscandy);
               }else if (txtFontStyle.equals("Arapey")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.arapey);
               }else if (txtFontStyle.equals("Short Stack")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.shortstack);
               }else if (txtFontStyle.equals("Aclonica")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.aclonica);
               }else if (txtFontStyle.equals("Almendra")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.almendra);
               }else if (txtFontStyle.equals("Bungee Shade")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.bungeeshade);
               }else if (txtFontStyle.equals("Chonburi")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.chonburi);
               }else if (txtFontStyle.equals("Doppio One")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.doppioone);
               }else if (txtFontStyle.equals("Passero One")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.passeroone);
               }else if (txtFontStyle.equals("Croissant One")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.croissantone);
               }else if (txtFontStyle.equals("Poly")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.poly);
               }else if (txtFontStyle.equals("Averialibre")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.averialibre);
               }else if (txtFontStyle.equals("Philosopher")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.philosopher);
               }else if (txtFontStyle.equals("Shanti")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.shanti);
               }else if (txtFontStyle.equals("Faustina")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.faustina);
               }else if (txtFontStyle.equals("Gudea")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.gudea);
               }else if (txtFontStyle.equals("Public Sans")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.publicsans);
               }else if (txtFontStyle.equals("Amaranth")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.amaranth);
               }else if (txtFontStyle.equals("Itim")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.itim);
               }else if (txtFontStyle.equals("Sansita")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.sansita);
               }else if (txtFontStyle.equals("Scada")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.scada);
               }else if (txtFontStyle.equals("B612")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.b612);
               }else if (txtFontStyle.equals("Charm")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.charm);
               }else if (txtFontStyle.equals("Mirza")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.mirza);
               }else if (txtFontStyle.equals("Esteban")) {
                   typeface = ResourcesCompat.getFont(mContext, R.font.esteban);
               }
               styleBuilder.withTextFont(typeface);

               if (txtFontAlignment.equals("Left")){
                   styleBuilder.withGravity(Gravity.LEFT);
               }
               else if (txtFontAlignment.equals("Center")){
                   styleBuilder.withGravity(Gravity.CENTER);
               }
               else if (txtFontAlignment.equals("Right")){
                   styleBuilder.withGravity(Gravity.RIGHT);
               }

               mPhotoEditor.addText(inputText, styleBuilder);
           }
       });

   }

   public  Bitmap transformBitmapTo2048px(Bitmap source){
       if(source.getHeight() <= 2048 && source.getWidth() <= 2048)
           return source;

       int targetWidth;
       int targetHeight;

       double aspectRatio = (double) source.getHeight() / (double) source.getWidth();

       if(source.getWidth() >= source.getHeight()){
           targetWidth = 2048;
           targetHeight = (int)(2048 * aspectRatio);
       } else {
           targetHeight = 2048;
           targetWidth = (int)(2048 / aspectRatio);
       }

       Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
       if (result != source) {
//            Same bitmap is returned if sizes are the same
           source.recycle();
       }
       return result;
   }
   @Override
    public int getCount() {
        return mPreviewItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format(mContext.getResources().getString(R.string.showing_image),
                position + 1, mPreviewItems.size());
    }

    public void setData(ArrayList<String> images) {
        mPreviewItems.clear();
        mPreviewItems.addAll(images);
    }

   @Override
   public void onClick(View v) {

   }

   @Override
   public void onEditTextChangeListener(View rootView, String text, int colorCode) {

   }

   @Override
   public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

   }

   @Override
   public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

   }

   @Override
   public void onStartViewChangeListener(ViewType viewType) {

   }

   @Override
   public void onStopViewChangeListener(ViewType viewType) {

   }
}
