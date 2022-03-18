package com.example.pdfeditormadtpeeps.Activity;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.pdfeditormadtpeeps.Interface.OnPDFCreatedInterface;
import com.example.pdfeditormadtpeeps.Interface.OnSelectionListner;
import com.example.pdfeditormadtpeeps.Model.FileData;
import com.example.pdfeditormadtpeeps.R;
import com.example.pdfeditormadtpeeps.Utility.Constants;
import com.example.pdfeditormadtpeeps.Utility.CreatePdf;
import com.example.pdfeditormadtpeeps.Utility.EqualSpacingItemDecoration;
import com.example.pdfeditormadtpeeps.Utility.FileUtils;
import com.example.pdfeditormadtpeeps.Utility.ImageToPDFOptions;
import com.example.pdfeditormadtpeeps.Utility.ImageUtils;
import com.example.pdfeditormadtpeeps.Utility.PageSizeUtils;
import com.example.pdfeditormadtpeeps.Utility.PathUtil;
import com.example.pdfeditormadtpeeps.Utility.StringUtils;
import com.example.pdfeditormadtpeeps.adapter.RecentFileadapter;
import com.example.pdfeditormadtpeeps.database.DatabaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnPDFCreatedInterface, OnSelectionListner {
    ImageView iv_grid, iv_select, iv_name, iv_date;
    String list_type = "row";
    public int permission_flag = 0;
    LinearLayoutCompat ll_select, ll_name, ll_date, ll_files, ll_new, ll_gallery;
    TextView tv_done, tv_select, tv_sort_by, tv_create;
    private boolean mIsButtonAlreadyClicked = false;
    private static final int REQUEST_PERMISSIONS_CODE = 124;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    List<File> files;
    List<FileData> fileDataArrayList;
    EditText et_folder_name;
    public String sort_type="date";
    private DatabaseHelper mDatabaseHelper;
    public static ArrayList<String> mImagesUri = new ArrayList<>();
    private static final ArrayList<String> mUnarrangedImagesUri = new ArrayList<>();
    private String mPath;
    private FileUtils mFileUtils;
    private ImageToPDFOptions mPdfOptions;
    private String mHomePath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/mypdf/";
    private int mMarginTop = 50;
    private int mMarginBottom = 38;
    private int mMarginLeft = 50;
    private int mMarginRight = 38;
    private String mPageNumStyle;
    private SharedPreferences mSharedPreferences;
    private PageSizeUtils mPageSizeUtils;
    private int mPageColor;
    EditText et_search;
    int first_time = 0;
    private RecyclerView mRecyclerView;
    private RecentFileadapter mAdapter;
    public MainActivity mActivity;
    public String btn_select = "0", display_type="row", open_bottom_dg="";
    public static List<FileData> fileDataListSelected;
    LinearLayoutCompat ll_delete, ll_copy, ll_share, ll_merge;
    ImageButton iv_delete;
    private ArrayList<String> mFilePaths;
    private boolean mPasswordProtected = false;
    private MaterialDialog mMaterialDialog;
    LinearLayout design_bottom_sheet;
   
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        mFileUtils = new FileUtils(this);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP,
                Constants.DEFAULT_PAGE_COLOR);

        fileDataArrayList = new ArrayList<>();

        iv_grid = findViewById(R.id.iv_grid);
        iv_select = findViewById(R.id.iv_select);
        tv_select = findViewById(R.id.tv_select);
        ll_select = findViewById(R.id.ll_select);
        tv_done = findViewById(R.id.tv_done);
        tv_sort_by = findViewById(R.id.tv_sort_by);
        et_search = findViewById(R.id.et_search);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_files);
        design_bottom_sheet = (LinearLayout) findViewById(R.id.design_bottom_sheet);
        ll_copy = findViewById(R.id.ll_copy);
        ll_delete = findViewById(R.id.ll_delete);
        iv_delete = findViewById(R.id.iv_delete);
        ll_merge = findViewById(R.id.ll_merge);
        ll_share = findViewById(R.id.ll_share);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(12, EqualSpacingItemDecoration.VERTICAL));
        mRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(12, EqualSpacingItemDecoration.HORIZONTAL));
//        Toast.makeText(getActivity(), mParam2, Toast.LENGTH_LONG).show();
        // create an Object for Adapter
        sortListByDateName(sort_type);


        FloatingActionButton fab = findViewById(R.id.fab);
        resetValues();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                openCreateDialog();
            }
        });


        if (!checkPermission()) {
            requestPermission();
        } else{
            getAllFiles();

        }

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                viewModelSearch.sendData(s.toString());
                filter(s.toString());
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                ll_select.setVisibility(View.VISIBLE);
                tv_done.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
//                if (tabs.getSelectedTabPosition() == 0) {
//                    mAboutDataListener.onDataReceived("0", "");
//                } else {
//                    mAboutDataDListener.onDataReceived("0", "");
//                }

            }
        });

        tv_sort_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSortByDialog();
            }
        });

        ll_select.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                ll_select.setVisibility(View.GONE);
                tv_done.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
//                if (tabs.getSelectedTabPosition() == 0) {
//                    mAboutDataListener.onDataReceived("1", "open_bottom_dg");
//                } else {
//                    mAboutDataDListener.onDataReceived("1", "open_bottom_dg");
//                }

            }
        });


        iv_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(list_type.equals("row")) {
                    iv_grid.setImageResource(R.drawable.group_16863);
                    list_type = "grid";
                } else {
                    iv_grid.setImageResource(R.drawable.group_16853);
                    list_type = "row";
                }
//                myViewModel.sendData(list_type);
            }
        });
    }

    private void setList() {
        for (int i=0;i<fileDataArrayList.size();i++) {
            fileDataArrayList.get(i).setSelected(false);
        }
        mAdapter = new RecentFileadapter(this, fileDataArrayList, display_type, btn_select, this);
        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, CAMERA, WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<FileData> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (int i=0;i<fileDataArrayList.size();i++) {
            //if the existing elements contains the search input
            FileData lawyerContactBean = fileDataArrayList.get(i);
            if (lawyerContactBean.getName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(lawyerContactBean);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        mAdapter.filterList(filterdNames);
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getAllFiles() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code here
            first_time = 1;
            createPdf("");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        try {
            files = getListFiles(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/mypdf")));
            Log.d("LENGTH", String.valueOf(files.size()));
            for (int i=0;i<files.size();i++){
                Log.d("FILE_NAME", files.get(i).getName());
                Log.d("FILE_PATH", files.get(i).getPath());
            }
            setList();

        } catch (Exception e) {
            Log.d("EXCEPTION", e.toString());
        }
    }


    private void openSortByDialog() {
        View bottomSheetview = getLayoutInflater().inflate(R.layout.bottom_sortby_layout, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
        dialog.setContentView(bottomSheetview);
        ll_name = bottomSheetview.findViewById(R.id.ll_name);
        ll_date = bottomSheetview.findViewById(R.id.ll_date);
        iv_name = bottomSheetview.findViewById(R.id.iv_name);
        iv_date = bottomSheetview.findViewById(R.id.iv_date);

        if(sort_type.equals("date")) {
            iv_date.setImageResource(R.drawable.group_16909);
            iv_name.setImageResource(R.drawable.ellipse_670);
        } else {
            iv_name.setImageResource(R.drawable.group_16909);
            iv_date.setImageResource(R.drawable.ellipse_670);
        }

        ll_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_sort_by.setText("Name");
                dialog.dismiss();
//                viewModelSort.sendData("name");
                sort_type = "name";
            }
        });

        ll_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_sort_by.setText("Date");
                dialog.dismiss();
//                viewModelSort.sendData("date");
                sort_type = "date";
            }
        });

        dialog
                .getWindow()
                .findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
        dialog.show();
    }

    private void openCreateDialog() {
        View bottomSheetview = getLayoutInflater().inflate(R.layout.bottom_create_layout, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
        dialog.setContentView(bottomSheetview);
        ll_files = bottomSheetview.findViewById(R.id.ll_files);
        ll_gallery = bottomSheetview.findViewById(R.id.ll_gallery);
        ll_new = bottomSheetview.findViewById(R.id.ll_new);

        ll_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permission_flag = 1;
                if (!checkPermission()) {
                    requestPermission();
                    return;
                }
                if (!mIsButtonAlreadyClicked) {
                    selectImages();
                    mIsButtonAlreadyClicked = true;
                    dialog.dismiss();
                }
            }
        });

        ll_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                startActivityForResult(Intent.createChooser(intent,"Select File"), 10001);
                dialog.dismiss();
            }
        });

        ll_new.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                createPdf("");
                dialog.dismiss();
            }
        });

        dialog
                .getWindow()
                .findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
        dialog.show();
    }

    @Override
    public void onPDFCreationStarted() {
        Toast.makeText(getApplicationContext(), "Creating PDF", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onPDFCreated(boolean success, String path) {
        if (!success) {
            StringUtils.getInstance().showSnackbar(this, R.string.snackbar_folder_not_created);
            return;
        }
//        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        Toast.makeText(getApplicationContext(), "PDF Created!", Toast.LENGTH_LONG).show();
//        StringUtils.getInstance().getSnackbarwithAction(this, R.string.snackbar_pdfCreated)
//                .setAction(R.string.snackbar_viewAction,
//                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mPath = path;
        Log.d("IMAGEPATH", mPath);
        resetValues();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onSelected() {

    }

    public interface OnAboutDataReceivedListener {
        void onDataReceived(String btn_select, String open_bottom_dg);
    }

    public interface OnAboutDataReceivedDListener {
        void onDataReceived(String btn_select, String open_bottom_dg);
    }

    private void resetValues() {
        mPdfOptions = new ImageToPDFOptions();
        mPdfOptions.setBorderWidth(mSharedPreferences.getInt(Constants.DEFAULT_IMAGE_BORDER_TEXT,
                Constants.DEFAULT_BORDER_WIDTH));
        mPdfOptions.setQualityString(
                Integer.toString(mSharedPreferences.getInt(Constants.DEFAULT_COMPRESSION,
                        Constants.DEFAULT_QUALITY_VALUE)));
        mPdfOptions.setPageSize(mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                Constants.DEFAULT_PAGE_SIZE));
        mPdfOptions.setPasswordProtected(false);
        mPdfOptions.setWatermarkAdded(false);
        mImagesUri.clear();
//        showEnhancementOptions();
        ImageUtils.getInstance().mImageScaleType = mSharedPreferences.getString(Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT,
                Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setMargins(0, 0, 0, 0);
        mPageNumStyle = mSharedPreferences.getString (Constants.PREF_PAGE_STYLE, null);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP,
                Constants.DEFAULT_PAGE_COLOR);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted1 = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && cameraAccepted1) {
                        if (permission_flag == 0) {
                            getAllFiles();
                        } else {
                            selectImages();
                        }
                    }
                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, CAMERA, WRITE_EXTERNAL_STORAGE},
                                                            REQUEST_PERMISSIONS_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }


    }

    private void sortListByDateName(String sort_type) {
        if(sort_type.equals("date")) {
            Collections.sort(fileDataArrayList, new Comparator<FileData>() {
                DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                @Override
                public int compare(FileData lhs, FileData rhs) {
                    try {
                        return f.parse(lhs.getDuration()).compareTo(f.parse(rhs.getDuration()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
            Collections.reverse(fileDataArrayList);
        } else {
            Collections.sort(fileDataArrayList, new Comparator<FileData>() {
                @Override
                public int compare(FileData lhs,FileData rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }
        Collections.sort(fileDataArrayList, new Comparator<FileData>() {
            @Override
            public int compare(FileData lhs,FileData rhs) {
                return lhs.getFile_type().compareTo(rhs.getFile_type());
            }
        });
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    /**
     * Opens Matisse activity to select Images
     */
    private void selectImages() {
        ImageUtils.selectImages(MainActivity.this, INTENT_REQUEST_GET_IMAGES);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIsButtonAlreadyClicked = false;
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:
                mImagesUri.clear();
                mUnarrangedImagesUri.clear();
                mImagesUri.addAll(Matisse.obtainPathResult(data));
                mUnarrangedImagesUri.addAll(mImagesUri);
                if (mImagesUri.size() > 0) {
                    Log.d("IMAGE", mImagesUri.get(0)+"\n"+mImagesUri.size());
                    String preFillName = mFileUtils.getLastFileName(mImagesUri);
                    String ext = getString(R.string.pdf_ext);
//                    mFileUtils.openSaveDialog(preFillName, ext, filename -> save(false, filename));
                    Random generator = new Random();
                    int n = 100000;
                    n = generator.nextInt(n);
                    String fname = "pdfhero-" + n;
//                    save(false, fname);

                    mFileUtils.openSaveDialog(fname, ext, filename -> save(false, filename));
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                HashMap<Integer, Uri> croppedImageUris =
                        (HashMap) data.getSerializableExtra(CropImage.CROP_IMAGE_EXTRA_RESULT);

                for (int i = 0; i < mImagesUri.size(); i++) {
                    if (croppedImageUris.get(i) != null) {
                        mImagesUri.set(i, croppedImageUris.get(i).getPath());
                    }
                }
                break;

            case 10001:
                if(data.getData()!=null) {
                    Uri uri = data.getData();
//                    String uriString = uri.toString();
//                    File myFile = new File(uriString);
//                    String displayName = null;
//                    if (uriString.startsWith("content://")) {
//                        Cursor cursor = null;
//                        try {
//                            cursor = getContentResolver().query(uri, null, null, null, null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                            }
//                        } finally {
//                            cursor.close();
//                        }
//                    } else if (uriString.startsWith("file://")) {
//                        displayName = myFile.getName();
//                    }
                    try {
                        String path = PathUtil.getPath(this, uri);
                        Log.d("PDF", path);

                        String file_name = new File(path).getName();
                        File dest_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/mypdf/" + file_name);
                        try {
                            copy(new File(path), dest_file);
                            mDatabaseHelper.deleteRecord(file_name);
                            mDatabaseHelper.insertRecord(dest_file.getPath(),
                                    formatLastModifiedDate(dest_file.lastModified()), "f", file_name);
                            FileData fileData = new FileData();
                            fileData.setName(file_name);
                            fileData.setDuration(formatLastModifiedDate(dest_file.lastModified()));
                            fileData.setFile_type("f");
                            fileData.setFile_path(dest_file);
                            fileDataArrayList.add(fileData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        Intent intent = new Intent(this, OpenPdfActivity.class);
//                        intent.putExtra("pdf_name", dest_file.getPath());
//                        startActivity(intent);


//                        mImagesUri.clear();
//                        mUnarrangedImagesUri.clear();
//                        mImagesUri.add(path);
//                        mUnarrangedImagesUri.addAll(mImagesUri);
//                        if (mImagesUri.size() > 0) {
//                            Log.d("IMAGE", mImagesUri.get(0)+"\n"+mImagesUri.size());
//                            String preFillName = mFileUtils.getLastFileName(mImagesUri);
//                            String ext = getString(R.string.pdf_ext);
//                            Random generator = new Random();
//                            int n = 100000;
//                            n = generator.nextInt(n);
//                            String fname = "pdfhero-" + n;
//                            save(false, fname);
//
////                    mFileUtils.openSaveDialog(preFillName, ext, filename -> save(isGrayScale, filename));
//                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }



                } else {
                    Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
                }

//                if (data.getClipData() != null) {
//                    ClipData mClipData = data.getClipData();
//                    for (int i = 0; i < mClipData.getItemCount(); i++) {
//                        ClipData.Item item = mClipData.getItemAt(i);
//                        Uri uri = item.getUri();
//                        // display your images
//                        String file_url = getPathFromUri(this, uri);
//                        if (file_url.contains(".jpg") || file_url.contains(".png") || file_url.contains(".jpeg")) {
//                            mImagesUri.add(file_url);
//                        }
//                    }
//                } else if (data.getData() != null) {
//                    Uri uri = data.getData();
//                    // display your image
//                    String file_url = getPathFromUri(this, uri);
//                    if (file_url.contains(".jpg") || file_url.contains(".png") || file_url.contains(".jpeg")) {
//                        mImagesUri.add(file_url);
//                    }
//                }
//                if (mImagesUri.size() > 0) {
//                    Log.d("IMAGE", mImagesUri.get(0)+"\n"+mImagesUri.size());
//                    Random generator = new Random();
//                    int n = 10000;
//                    n = generator.nextInt(n);
//                    String fname = "pdfhero-" + n;
//                    save(false, fname);
//                }
                break;

        }
    }

    public void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    private void save(boolean isGrayScale, String filename) {
        mPdfOptions.setImagesUri(mImagesUri);
        mPdfOptions.setPageSize(mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                Constants.DEFAULT_PAGE_SIZE));
        mPdfOptions.setImageScaleType(ImageUtils.getInstance().mImageScaleType);
        mPdfOptions.setPageNumStyle(mPageNumStyle);
//        mPdfOptions.setMasterPwd(mSharedPreferences.getString(MASTER_PWD_STRING, appName));
        mPdfOptions.setPageColor(mPageColor);
        mPdfOptions.setOutFileName(filename);
        new CreatePdf(mPdfOptions, mHomePath, this).execute();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }

        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            FileData fileData = new FileData();
            if (file.isDirectory()) {
                fileData.setName(file.getName());
                fileData.setDuration(formatLastModifiedDate(file.lastModified()));
                fileData.setFile_type("d");
                fileData.setFile_path(file);
                fileDataArrayList.add(fileData);
//                inFiles.addAll(getListFiles(file));
            } else {
                if(file.getName().endsWith(".pdf")){
                    fileData.setName(file.getName());
                    fileData.setDuration(formatLastModifiedDate(file.lastModified()));
                    fileData.setFile_type("f");
                    fileData.setFile_path(file);
                    fileDataArrayList.add(fileData);
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    private String formatLastModifiedDate(long modifyDate) {
        Date lastModified = new Date(modifyDate);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDateString = formatter.format(lastModified);
        return formattedDateString;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(String sometext){
        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
//        Canvas canvas = page.getCanvas();
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        canvas.drawCircle(50, 50, 30, paint);
//        paint.setColor(Color.BLACK);
//        canvas.drawText(sometext, 80, 50, paint);
        //canvas.drawt
        // finish the page
        document.finishPage(page);
// draw text on the graphics object of the page

        // Create Page 2
//        pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 2).create();
//        page = document.startPage(pageInfo);
//        canvas = page.getCanvas();
//        paint = new Paint();
//        paint.setColor(Color.BLUE);
//        canvas.drawCircle(100, 100, 100, paint);
//        document.finishPage(page);

        // write the document content

        String directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/mypdf/";
//        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//
//        File folder = new File(root.getAbsolutePath());
//        Log.e("Tag", folder.getAbsolutePath());
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//        String directory_path = folder.getAbsolutePath() + "/mypdf/";

        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }

        Random generator = new Random();
        int n = 100000;
        n = generator.nextInt(n);
        String fname = "pdfhero-" + n + ".pdf";
        String targetPdf = directory_path+fname;
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            mDatabaseHelper.deleteRecord(fname);
            mDatabaseHelper.insertRecord(String.valueOf(filePath),
                    formatLastModifiedDate(file.lastModified()), "f", fname);
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();

        if(first_time == 0) {
            FileData fileData = new FileData();
            fileData.setName(fname);
            fileData.setDuration(formatLastModifiedDate(filePath.lastModified()));
            fileData.setFile_type("f");
            fileData.setFile_path(filePath);
            fileDataArrayList.add(fileData);
//            setSeectionPagerAdapter();
//            Intent intent = new Intent(MainActivity.this, OpenPdfActivity.class);
//            intent.putExtra("pdf_name", filePath.toString());
//            startActivity(intent);

        }
        first_time = 0;
//        finish();
//        startActivity(getIntent());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestart() {
        super.onRestart();
        fileDataArrayList = new ArrayList<>();
        getAllFiles();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void refreshPage() {
        Log.d("RESUME", "resume");
        fileDataArrayList = new ArrayList<>();
        getAllFiles();
    }
}