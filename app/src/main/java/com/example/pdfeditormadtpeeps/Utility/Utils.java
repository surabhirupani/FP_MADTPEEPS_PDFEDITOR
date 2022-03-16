package com.example.pdfeditormadtpeeps.Utility;

import android.graphics.Color;

import com.example.pdfeditormadtpeeps.Model.FileData;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final String APP_NAME = "PDFEDITOR";

    //Login Utils
    public static final String loginControlKey = "isLogin";
    public static final String loginUserNameKey = "userName";
    public static final String loginUserPassword = "userPassword";
    public static final String DEFAULT_COMPRESSION = "DefaultCompression";
    public static final String SORTING_INDEX = "SORTING_INDEX";
    public static int copy_flag = 0;
    public static List<FileData> fileDataListSelected = new ArrayList<>();
    public static final String IMAGE_EDITOR_KEY = "first";
    public static final String DEFAULT_FONT_SIZE_TEXT = "DefaultFontSize";
    public static final int DEFAULT_FONT_SIZE = 11;
    public static final String PREVIEW_IMAGES = "preview_images";
    public static final String DATABASE_NAME = "PdfEditorDB.db";
    public static final String DEFAULT_FONT_FAMILY_TEXT = "DefaultFontFamily";
    public static final String DEFAULT_FONT_FAMILY = "TIMES_ROMAN";
    public static final String DEFAULT_FONT_COLOR_TEXT = "DefaultFontColor";
    public static final int DEFAULT_FONT_COLOR = -16777216;
    // key for text to pdf (TTP) page color
    public static final String DEFAULT_PAGE_COLOR_TTP = "DefaultPageColorTTP";
    // key for images to pdf (ITP) page color
    public static final String DEFAULT_PAGE_COLOR_ITP = "DefaultPageColorITP";
    public static final int DEFAULT_PAGE_COLOR = Color.WHITE;
    public static final String DEFAULT_THEME_TEXT = "DefaultTheme";
    public static final String DEFAULT_THEME = "White";
    public static final String DEFAULT_IMAGE_BORDER_TEXT = "Image_border_text";
    public static final String RESULT = "result";
    public static final String SAME_FILE = "SameFile";
    public static final String DEFAULT_PAGE_SIZE_TEXT = "DefaultPageSize";
    public static final String DEFAULT_PAGE_SIZE = "A4";
    public static final String CHOICE_REMOVE_IMAGE = "CHOICE_REMOVE_IMAGE";
    public static final int DEFAULT_QUALITY_VALUE = 30;
    public static final int DEFAULT_BORDER_WIDTH = 0;
    public static final String STORAGE_LOCATION = "storage_location";
    public static final String DEFAULT_IMAGE_SCALE_TYPE_TEXT = "image_scale_type";
    public static final String IMAGE_SCALE_TYPE_STRETCH = "stretch_image";
    public static final String IMAGE_SCALE_TYPE_ASPECT_RATIO = "maintain_aspect_ratio";
    public static final String PG_NUM_STYLE_PAGE_X_OF_N = "pg_num_style_page_x_of_n";
    public static final String PG_NUM_STYLE_X_OF_N = "pg_num_style_x_of_n";
}
