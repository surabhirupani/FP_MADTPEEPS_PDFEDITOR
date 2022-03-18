package com.example.pdfeditormadtpeeps.Utility;


import static com.example.pdfeditormadtpeeps.Utility.Constants.AUTHORITY_APP;
import static com.example.pdfeditormadtpeeps.Utility.Constants.pdfDirectory;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;


import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.FileOutputStream;


public class ImageUtils {

    public String mImageScaleType;

    private static class SingletonHolder {
        static final ImageUtils INSTANCE = new ImageUtils();
    }

    public static ImageUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Calculates the optimum size for an image, such that it scales to fit whilst retaining its aspect ratio
     *
     * @param originalWidth  the original width of the image
     * @param originalHeight the original height of the image
     * @param documentSize   a rectangle specifying the width and height that the image must fit within
     * @return a rectangle that provides the scaled width and height of the image
     *

    /**
     * Creates a rounded bitmap from any bitmap
     *
     * @param bmp - input bitmap
     * @return - output bitmap
     */
    public Bitmap getRoundBitmap(Bitmap bmp) {
        int width = bmp.getWidth(), height = bmp.getHeight();
        int radius = Math.min(width, height); // set the smallest edge as radius.
        Bitmap bitmap;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            bitmap = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / 1.0f),
                    (int) (bmp.getHeight() / 1.0f), false);
        } else {
            bitmap = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(radius / 2f + 0.7f, radius / 2f + 0.7f,
                radius / 2f + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Get round bitmap from file path
     *
     * @param path - file path
     * @return - output round bitmap
     */
    public Bitmap getRoundBitmapFromPath(String path) {
        File file = new File(path);

        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

        // Calculate inSampleSize
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions);

        // Decode bitmap with actual size
        bmOptions.inJustDecodeBounds = false;
        Bitmap smallBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        if (smallBitmap == null) return null;

        return ImageUtils.getInstance().getRoundBitmap(smallBitmap);
    }


    /**
     * Calculate the inSampleSize value for given bitmap options & image dimensions
     *
     * @param options - bitmap options
     * @return inSampleSize value
     * https://developer.android.com/topic/performance/graphics/load-bitmap.html#java
     */
    private int calculateInSampleSize(BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > 500 || width > 500) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= 500
                    && (halfWidth / inSampleSize) >= 500) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * convert a bitmap to gray scale and return it
     *
     * @param bmpOriginal original bitmap which is converted to a new
     *                    grayscale bitmap
     */
    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, bmpOriginal.getConfig());
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * Saves bitmap to external storage
     *
     * @param filename    - name of the file
     * @param finalBitmap - bitmap to save
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String saveImage(String filename, Bitmap finalBitmap) {

        if (finalBitmap == null)
            return null;

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File myDir = new File(root + pdfDirectory);
        String fileName = filename + ".png";

        File file = new File(myDir, fileName);
        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.v("saving", fileName);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myDir + "/" + fileName;
    }

    /**
     * Open a dialog to select some Images
     * @param frag the fragment that should receive the Images
     * @param requestCode the internal request code the fragment uses for image selection
     */
    public static void selectImages(Activity frag, int requestCode) {
        Matisse.from(frag)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, AUTHORITY_APP))
                .maxSelectable(1000)
                .imageEngine(new PicassoEngine())
                .forResult(requestCode);
    }

    /**
     * Checks of the bitmap is just all white pixels
     *
     * @param bitmap - input bitmap
     * @return - true, if bitmap is all white
     */
    private static boolean checkIfBitmapIsWhite(Bitmap bitmap) {
        if (bitmap == null)
            return true;
        Bitmap whiteBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(whiteBitmap);
        canvas.drawColor(Color.WHITE);
        return bitmap.sameAs(whiteBitmap);
    }


}
