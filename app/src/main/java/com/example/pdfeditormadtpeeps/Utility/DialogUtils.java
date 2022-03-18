package com.example.pdfeditormadtpeeps.Utility;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.pdfeditormadtpeeps.R;


public class DialogUtils {

    private DialogUtils() {
    }

    private static class SingletonHolder {
        static final DialogUtils INSTANCE = new DialogUtils();
    }

    public static DialogUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Creates a material dialog with `Warning` title
     * @param activity - activity instance
     * @param content - content resource id
     * @return - material dialog builder
     */
    public MaterialDialog.Builder createWarningDialog(Activity activity,
                                                      int content) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.warning)
                .content(content)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates a material dialog with `warning title` and overwrite message as content
     * @param activity - activity instance
     * @return - material dialog builder
     */
    public MaterialDialog.Builder createOverwriteDialog(Activity activity) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.warning)
                .content(R.string.overwrite_message)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates a material dialog with given title & content
     * @param activity - activity instance
     * @param title - dialog title resource id
     * @param content - content resource id
     * @return - material dialog builder
     */
    public MaterialDialog.Builder createCustomDialog(Activity activity,
                                                     int title, int content) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }

    /**
     * Creates a material dialog with given title
     * @param activity - activity instance
     * @param title - dialog title resource id
     * @return - material dialog builder
     */
    public MaterialDialog.Builder createCustomDialogWithoutContent(Activity activity,
                                                                   int title) {
        return new MaterialDialog.Builder(activity)
                .title(title)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel);
    }



    public void showFilesInfoDialog(Activity activity, int dialogId) {
        int stringId = R.string.viewfiles_rotatepages;
        switch (dialogId) {
            case Constants.ROTATE_PAGES:
                stringId = R.string.viewfiles_rotatepages;
                break;
            case Constants.REMOVE_PASSWORD:
                stringId = R.string.viewfiles_removepassword;
                break;
            case Constants.ADD_PASSWORD:
                stringId = R.string.viewfiles_addpassword;
                break;
            case Constants.ADD_WATERMARK:
                stringId = R.string.viewfiles_addwatermark;
                break;
        }
        new MaterialDialog.Builder(activity)
                .title(R.string.app_name)
                .content(stringId)
                .positiveText(android.R.string.ok)
                .build()
                .show();
    }

}