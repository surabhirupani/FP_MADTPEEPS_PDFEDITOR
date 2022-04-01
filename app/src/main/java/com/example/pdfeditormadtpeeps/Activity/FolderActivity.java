package com.example.pdfeditormadtpeeps.Activity;

import static com.example.pdfeditormadtpeeps.Utility.Constants.pdfExtension;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.example.pdfeditormadtpeeps.Interface.MergeFilesListener;
import com.example.pdfeditormadtpeeps.Interface.OnPDFCreatedInterface;
import com.example.pdfeditormadtpeeps.Interface.OnSelectionListner;
import com.example.pdfeditormadtpeeps.Model.FileData;
import com.example.pdfeditormadtpeeps.R;
import com.example.pdfeditormadtpeeps.Utility.Constants;
import com.example.pdfeditormadtpeeps.Utility.CreatePdf;
import com.example.pdfeditormadtpeeps.Utility.DialogUtils;
import com.example.pdfeditormadtpeeps.Utility.EqualSpacingItemDecoration;
import com.example.pdfeditormadtpeeps.Utility.ExcelToPDFAsync;
import com.example.pdfeditormadtpeeps.Utility.FileUtils;
import com.example.pdfeditormadtpeeps.Utility.ImageToPDFOptions;
import com.example.pdfeditormadtpeeps.Utility.ImageUtils;
import com.example.pdfeditormadtpeeps.Utility.MergePdf;
import com.example.pdfeditormadtpeeps.Utility.PageSizeUtils;
import com.example.pdfeditormadtpeeps.Utility.PathUtil;
import com.example.pdfeditormadtpeeps.Utility.PermissionsUtils;
import com.example.pdfeditormadtpeeps.Utility.RealPathUtil;
import com.example.pdfeditormadtpeeps.Utility.StringUtils;
import com.example.pdfeditormadtpeeps.adapter.RecentFileadapter;
import com.example.pdfeditormadtpeeps.database.DatabaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class FolderActivity extends AppCompatActivity implements MergeFilesListener, OnPDFCreatedInterface, OnSelectionListner {
    ImageView iv_back, iv_name, iv_date, iv_grid;
    LinearLayoutCompat ll_name, ll_date, ll_files, ll_new, ll_gallery, ll_nofile, ll_texttopdf, ll_exceltopdf;
    TextView tv_sort_by, tv_folder_name, tv_select, tv_done;
    String folder_name;
    List<FileData> fileDataArrayList;
    List<File> files;
    private int mFontSize = 11;
    private RecyclerView mRecyclerView;
    private RecentFileadapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public String btn_select = "0", display_type="row", open_bottom_dg="";
    public String sort_type="date";
    public static List<FileData> fileDataListSelected;
    LinearLayoutCompat ll_delete, ll_copy, ll_share, ll_merge;
    private DatabaseHelper mDatabaseHelper;
    ImageButton iv_delete;
    private ArrayList<String> mFilePaths;
    private FileUtils mFileUtils;
    private boolean mPasswordProtected = false;
    private MaterialDialog mMaterialDialog;
    private String mHomePath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/mypdf/", mPassword=null;
    private boolean mPermissionGranted = false;
    private boolean mIsButtonAlreadyClicked = false;
    private static final int REQUEST_PERMISSIONS_CODE = 124;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    public int permission_flag = 0;
    public static ArrayList<String> mImagesUri = new ArrayList<>();
    private static final ArrayList<String> mUnarrangedImagesUri = new ArrayList<>();
    private String mPath;
    private ImageToPDFOptions mPdfOptions;
    private int mMarginTop = 50;
    private int mMarginBottom = 38;
    private int mMarginLeft = 50;
    private int mMarginRight = 38;
    private String mPageNumStyle;
    private SharedPreferences mSharedPreferences;
    private PageSizeUtils mPageSizeUtils;
    private int mPageColor;
    LinearLayout design_bottom_sheet;
    private static String mTextPath;
    private Uri mExcelFileUri;
    private String mRealPath;
    private static final int INTENT_REQUEST_PICK_TEXT_FILE_CODE = 0;
    private Font.FontFamily mFontFamily = Font.FontFamily.valueOf("TIMES_ROMAN");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        folder_name = getIntent().getStringExtra("folder_name");
        mHomePath+=folder_name+"/";

        iv_back = findViewById(R.id.iv_back);
        iv_grid = findViewById(R.id.iv_grid);
        tv_sort_by = findViewById(R.id.tv_sort_by);
        tv_folder_name = findViewById(R.id.tv_folder_name);
        tv_select = findViewById(R.id.tv_select);
        tv_done = findViewById(R.id.tv_done);
        ll_nofile = findViewById(R.id.ll_nofile);
        FloatingActionButton fab = findViewById(R.id.fab);
        ll_copy = findViewById(R.id.ll_copy);
        ll_delete = findViewById(R.id.ll_delete);
        iv_delete = findViewById(R.id.iv_delete);
        ll_merge = findViewById(R.id.ll_merge);
        ll_share = findViewById(R.id.ll_share);
        design_bottom_sheet = (LinearLayout) findViewById(R.id.design_bottom_sheet);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPermissionGranted = PermissionsUtils.getInstance().checkRuntimePermissions(this,
                Constants.READ_WRITE_CAMERA_PERMISSIONS);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP,
                Constants.DEFAULT_PAGE_COLOR);

        if (!mPermissionGranted) {
            getRuntimePermissions();
        }


        fileDataArrayList = new ArrayList<>();
        mFilePaths = new ArrayList<>();
        mDatabaseHelper = new DatabaseHelper(this);
        mFileUtils = new FileUtils(this);

        tv_folder_name.setText(folder_name);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_files);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(12, EqualSpacingItemDecoration.VERTICAL));
        mRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(12, EqualSpacingItemDecoration.HORIZONTAL));
        getAllFiles();
        resetValuesf();
        if(Constants.copy_flag == 1) {
            tv_select.setVisibility(View.GONE);
            tv_done.setVisibility(View.VISIBLE);
            tv_done.setText("Paste");
        } else {
            tv_done.setVisibility(View.GONE);
            tv_done.setText("Done");
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateDialog();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_sort_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSortByDialog();
            }
        });

        tv_select.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                tv_select.setVisibility(View.GONE);
                tv_done.setVisibility(View.VISIBLE);
                btn_select = "1";
                open_bottom_dg = "open_bottom_bg";
                fab.setVisibility(View.GONE);
                List<FileData> fileDataListAll = mAdapter.getSelectedFiles();

                fileDataListSelected = new ArrayList<>();
                mFilePaths = new ArrayList<>();
                for (int i = 0; i < fileDataListAll.size(); i++) {
                    if (fileDataListAll.get(i).getSelected()) {
                        fileDataListSelected.add(fileDataListAll.get(i));
                        mFilePaths.add(String.valueOf(fileDataListAll.get(i).getFile_path()));
                    }
                }
                if (fileDataListSelected.size() > 0) {
                    View bottomSheetview = getLayoutInflater().inflate(R.layout.bottom_selection_layout, null);
                    design_bottom_sheet.setVisibility(View.VISIBLE);

                    ll_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog myQuittingDialogBox = new AlertDialog.Builder(FolderActivity.this)
                                    // set message, title, and icon
                                    .setMessage("Are you sure want to delete " + fileDataListSelected.size() + " files?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int whichButton) {
                                            //your deleting code
                                            for (int i = 0; i < fileDataListAll.size(); i++) {
                                                if (fileDataListAll.get(i).getSelected()) {
                                                    mDatabaseHelper.deleteRecord(fileDataArrayList.get(i).getName());
                                                    fileDataArrayList.get(i).getFile_path().delete();
                                                    fileDataArrayList.remove(i);
                                                }
                                            }

                                            d.dismiss();
                                            tv_done.setVisibility(View.GONE);
                                            tv_select.setVisibility(View.VISIBLE);
                                            fab.setVisibility(View.VISIBLE);
                                            btn_select = "0";
                                            design_bottom_sheet.setVisibility(View.GONE);
                                            setListInRecycleView();
//                                            mAdapter.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create();
                            myQuittingDialogBox.show();
                        }
                    });

                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ll_delete.performClick();
                        }
                    });

                    ll_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                            shareIntent.setType("*/*");
//                        Uri uri = Uri.fromFile(fileDataList.get(pos).getFile_path());
                            ArrayList<Uri> Uris = new ArrayList<>();
                            for (int i = 0; i < fileDataListSelected.size(); i++) {
                                Uri fileUri = FileProvider.getUriForFile(FolderActivity.this,
                                        getPackageName() + ".provider", fileDataListSelected.get(i).getFile_path());
                                Uris.add(fileUri);
                            }

                            shareIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, Uris);
                            startActivity(Intent.createChooser(shareIntent, "Share"));
                            design_bottom_sheet.setVisibility(View.GONE);
                        }
                    });

                    ll_merge.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onClick(View view) {
                            mergeFiles(view);
                            tv_done.setVisibility(View.GONE);
                            fab.setVisibility(View.VISIBLE);
                            btn_select = "0";
                            tv_select.setVisibility(View.VISIBLE);
                            design_bottom_sheet.setVisibility(View.GONE);
                        }
                    });

                    ll_copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            design_bottom_sheet.setVisibility(View.GONE);
                            if(Constants.copy_flag == 0) {
                                Constants.copy_flag = 1;
                                Constants.fileDataListSelected = fileDataListSelected;
                                finish();
                            }

                        }
                    });

                } else {
//                    Toast.makeText(FolderActivity.this, "Please select file", Toast.LENGTH_LONG).show();
                    setListInRecycleView();
                    design_bottom_sheet.setVisibility(View.GONE);
                }
            }
        });

        iv_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(display_type.equals("row")) {
                    iv_grid.setImageResource(R.drawable.group_16863);
                    display_type = "grid";
                    mRecyclerView.setLayoutManager(new GridLayoutManager(FolderActivity.this, 2));
                    setListInRecycleView();
                } else {
                    iv_grid.setImageResource(R.drawable.group_16853);
                    display_type = "row";
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(FolderActivity.this));
                    setListInRecycleView();
                }

            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if (Constants.copy_flag == 1) {
                    if(Constants.fileDataListSelected.size() > 0) {
                        for (int i = 0; i < Constants.fileDataListSelected.size(); i++) {
                            FileData fileData = Constants.fileDataListSelected.get(i);
                            copyFile(fileData.getFile_path().getParent()+"/", fileData.getName(), mHomePath);
                            fileDataArrayList.add(fileData);
                        }
                    }
                    Constants.copy_flag = 0;
                    fab.setVisibility(View.VISIBLE);
                    tv_done.setVisibility(View.GONE);
                    tv_select.setVisibility(View.VISIBLE);
                    Constants.fileDataListSelected = new ArrayList<>();
                    mAdapter.notifyDataSetChanged();
                } else {
                    tv_select.setVisibility(View.VISIBLE);
                    design_bottom_sheet.setVisibility(View.GONE);
                    tv_done.setVisibility(View.GONE);
                    btn_select = "0";
                    open_bottom_dg = "";
                    setListInRecycleView();
                }
            }
        });
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void getAllFiles() {
        try {
            files = getListFiles(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/mypdf/"+folder_name)));
            Log.d("LENGTH", String.valueOf(files.size()));
            if(files.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                tv_select.setVisibility(View.VISIBLE);
                ll_nofile.setVisibility(View.GONE);
//                sortListByDateName(sort_type);
                setListInRecycleView();
            }  else {
                mRecyclerView.setVisibility(View.GONE);
                ll_nofile.setVisibility(View.VISIBLE);
                tv_select.setVisibility(View.GONE);
            }
//            for (int i=0;i<files.size();i++){
//                Log.d("FILE_NAME", files.get(i).getName());
//                Log.d("FILE_PATH", files.get(i).getPath());
//            }
        } catch (Exception e) {

        }
    }

    private void setListInRecycleView() {
        sortListByDateName(sort_type);
        for (int i=0;i<fileDataArrayList.size();i++) {
            fileDataArrayList.get(i).setSelected(false);
        }
        mAdapter = new RecentFileadapter(this, fileDataArrayList, display_type, btn_select, this);

        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);
    }

    private void openSortByDialog() {
        View bottomSheetview = getLayoutInflater().inflate(R.layout.bottom_sortby_layout, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(FolderActivity.this);
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
                sort_type = "name";
                tv_sort_by.setText("Name");
                dialog.dismiss();
                sortListByDateName(sort_type);
                mAdapter.notifyDataSetChanged();
            }
        });

        ll_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort_type = "date";
                tv_sort_by.setText("Date");
                dialog.dismiss();
                sortListByDateName(sort_type);
                mAdapter.notifyDataSetChanged();
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

        final BottomSheetDialog dialog = new BottomSheetDialog(FolderActivity.this);
        dialog.setContentView(bottomSheetview);
        ll_files = bottomSheetview.findViewById(R.id.ll_files);
        ll_gallery = bottomSheetview.findViewById(R.id.ll_gallery);
        ll_new = bottomSheetview.findViewById(R.id.ll_new);
        ll_texttopdf = bottomSheetview.findViewById(R.id.ll_texttopdf);
        ll_exceltopdf = bottomSheetview.findViewById(R.id.ll_exceltopdf);

        ll_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permission_flag = 1;
                if (!mPermissionGranted) {
                    getRuntimePermissions();
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
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
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


        ll_texttopdf.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Environment.getRootDirectory() + "/");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(uri, "*/*");
                String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/msword", getString(R.string.text_type)};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(
                            Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                            INTENT_REQUEST_PICK_TEXT_FILE_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    StringUtils.getInstance().showSnackbar(FolderActivity.this, R.string.install_file_manager);
                }
                dialog.dismiss();
            }
        });

        ll_exceltopdf.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Environment.getRootDirectory() + "/");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(uri, "*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(
                            Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                            2208);
                } catch (android.content.ActivityNotFoundException ex) {
                    StringUtils.getInstance().showSnackbar(FolderActivity.this, R.string.install_file_manager);
                }
                dialog.dismiss();
            }
        });

        dialog
                .getWindow()
                .findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
        dialog.show();
    }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            FileData fileData = new FileData();
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
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
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(String sometext){
        FileData fileData = new FileData();
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
        String directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/mypdf/"+folder_name+"/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }

        Random generator = new Random();
        int n = 100000;
        n = generator.nextInt(n);
        String fname = "pdfeditor-" + n + ".pdf";
        String targetPdf = directory_path+fname;
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
            fileData.setName(fname);
            fileData.setDuration(formatLastModifiedDate(filePath.lastModified()));
            fileData.setFile_type("f");
            fileData.setFile_path(filePath);
            fileDataArrayList.add(fileData);
            sortListByDateName(sort_type);
            mRecyclerView.setVisibility(View.VISIBLE);
            tv_select.setVisibility(View.VISIBLE);
            ll_nofile.setVisibility(View.GONE);
            mAdapter = new RecentFileadapter(this, fileDataArrayList, display_type, btn_select, this);

            // set the adapter object to the Recyclerview
            mRecyclerView.setAdapter(mAdapter);
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void mergeFiles(final View view) {
        String[] pdfpaths = mFilePaths.toArray(new String[0]);
        new MaterialDialog.Builder(FolderActivity.this)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.getInstance().isEmpty(input)) {
                        StringUtils.getInstance().showSnackbar(this, R.string.snackbar_name_not_blank);
                    } else {
                        if (!mFileUtils.isFileExist(input + getString(R.string.pdf_ext))) {
                            new MergePdf(input.toString(), mHomePath, mPasswordProtected,
                                    mPassword, this, "PDF Editor").execute(pdfpaths);
                        } else {
                            MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(this);
                            builder.onPositive((dialog12, which) -> new MergePdf(input.toString(),
                                    mHomePath, mPasswordProtected, mPassword,
                                    this, "PDF Editor").execute(pdfpaths))
                                    .onNegative((dialog1, which) -> mergeFiles(view)).show();
                        }
                    }
                })
                .show();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void resetValues(boolean isPDFMerged, String path) {
        if (isPDFMerged) {
//            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.pdf_merged)
//                    .setAction(R.string.snackbar_viewAction,
//                            v -> mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
            StringUtils.getInstance().showSnackbar(this, R.string.pdf_merged);
            FileData fileData = new FileData();
            File file = new File(path);
            fileData.setName(file.getName());
            fileData.setDuration(formatLastModifiedDate(file.lastModified()));
            fileData.setFile_type("f");
            fileData.setFile_path(file);
            fileDataArrayList.add(fileData);
            setListInRecycleView();
            mExcelFileUri = null;
            mDatabaseHelper.deleteRecord(file.getName());
            mDatabaseHelper.insertRecord(String.valueOf(path),
                    fileData.getDuration(), fileData.getFile_type(), fileData.getName());
        } else
            StringUtils.getInstance().showSnackbar(this, R.string.file_access_error);

        mFilePaths.clear();
        mPasswordProtected = false;
    }

    @Override
    public void mergeStarted() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        PermissionsUtils.getInstance().handleRequestPermissionsResult(this, grantResults,
                requestCode, REQUEST_PERMISSIONS_CODE, () -> {
                    mPermissionGranted = true;
                    if(permission_flag == 0) {
//                        getAllFiles();
                    } else {
                        selectImages();
                    }
                });
    }

    private void  getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                Constants.READ_WRITE_CAMERA_PERMISSIONS,
                REQUEST_PERMISSIONS_CODE);
    }

    /**
     * Opens Matisse activity to select Images
     */
    private void selectImages() {
        ImageUtils.selectImages(FolderActivity.this, INTENT_REQUEST_GET_IMAGES);
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
                    String fname = "pdfeditor-" + n;
//                    save(false, fname);
                    mFileUtils.openSaveDialog(fname, ext, filename -> save(false, filename));
//                    mFileUtils.openSaveDialog(preFillName, ext, filename -> save(isGrayScale, filename));
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
                    try {
                        String path = PathUtil.getPath(this, uri);
                        Log.d("PDF", path);
                        Log.d("PDF", path);

                        String file_name = new File(path).getName();
                        File dest_file = new File(mHomePath + file_name);
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
                            sortListByDateName(sort_type);
                            mAdapter = new RecentFileadapter(this, fileDataArrayList, display_type, btn_select, this::onSelected);

                            // set the adapter object to the Recyclerview
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(this, OpenPdfActivity.class);
                        intent.putExtra("pdf_name", dest_file.getPath());
                        startActivity(intent);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "No file selected", Toast.LENGTH_LONG).show();
                }

                break;

            case INTENT_REQUEST_PICK_TEXT_FILE_CODE:
                mTextPath = RealPathUtil.getInstance().getRealPath(this, data.getData());
                Toast.makeText(getApplicationContext(), mTextPath, Toast.LENGTH_LONG).show();
                if(mTextPath!=null) {
                    if (!mPermissionGranted) {
                        getRuntimePermissions();
                    } else{
                        openPdfNameDialog_();

                    }
                }
                break;

            case 2208:
                mExcelFileUri = data.getData();
                mRealPath = RealPathUtil.getInstance().getRealPath(this, mExcelFileUri);
                processUri();
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void processUri() {
        String fileName = mFileUtils.getFileName(mExcelFileUri);
        if (fileName != null && !fileName.endsWith(Constants.excelExtension) &&
                !fileName.endsWith(Constants.excelWorkbookExtension)) {
            StringUtils.getInstance().showSnackbar(this, R.string.extension_not_supported);
            return;
        }

        openExcelToPdf_();
        Log.d("EXCEL_FILE", fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void openExcelToPdf_() {
        new MaterialDialog.Builder(FolderActivity.this)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.getInstance().isEmpty(input)) {
                        StringUtils.getInstance().showSnackbar(FolderActivity.this, R.string.snackbar_name_not_blank);
                    } else {
                        final String inputName = input.toString();
                        if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                            convertToPdf(inputName);
                        } else {
                            MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(FolderActivity.this);
                            builder.onPositive((dialog12, which) -> convertToPdf(inputName))
                                    .onNegative((dialog1, which) -> openExcelToPdf_())
                                    .show();
                        }
                    }
                })
                .show();
    }

    private void convertToPdf(String mFilename) {
        String mStorePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/mypdf/"+folder_name+"/";
        String mPath1 = mStorePath + mFilename + pdfExtension;
        mPath = mPath1;
        new ExcelToPDFAsync(mRealPath, mPath1, FolderActivity.this, mPasswordProtected, mPassword).execute();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void openPdfNameDialog_() {
        new MaterialDialog.Builder(FolderActivity.this)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.getInstance().isEmpty(input)) {
                        StringUtils.getInstance().showSnackbar(FolderActivity.this, R.string.snackbar_name_not_blank);
                    } else {
                        final String inputName = input.toString();
                        if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                            addText(inputName, mFontSize, mFontFamily);
                        } else {
                            MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(FolderActivity.this);
                            builder.onPositive((dialog12, which) -> addText(inputName, mFontSize, mFontFamily))
                                    .onNegative((dialog1, which) -> openPdfNameDialog_())
                                    .show();
                        }
                    }
                })
                .show();
    }

    /**
     * This method is used to add append the text to an existing PDF file and
     * make a final new pdf with the appended text to the old pdf content.
     *
     * @param fileName - the name of the new pdf that is to be created.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addText(String fileName, int fontSize, Font.FontFamily fontFamily) {
        String mStorePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/mypdf/"+folder_name+"/";
        String mPath1 = mStorePath + fileName + pdfExtension;
        mPath = mPath1;
        try {
            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(mTextPath));
            String line;
            Log.d("path_doc", mTextPath);
            while ((line = br.readLine()) != null) {
                text.append(line);
                Log.d("line", line);
                text.append('\n');
            }
            br.close();

            OutputStream fos = new FileOutputStream(new File(mPath));

//            PdfReader pdfReader = new PdfReader(mPath);

            Document document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
            document.open();
            PdfContentByte cb = pdfWriter.getDirectContent();
//            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
//                PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);
//
//                document.newPage();
//                cb.addTemplate(page, 0, 0);
//            }
            document.setPageSize(PageSize.A4);
            document.newPage();
            document.add(new Paragraph(new Paragraph(text.toString(),
                    FontFactory.getFont(fontFamily.name(), fontSize))));
            document.close();

            FileData fileData = new FileData();
            fileData.setName(fileName+pdfExtension);
            fileData.setDuration(formatLastModifiedDate(new File(mPath).lastModified()));
            fileData.setFile_type("f");
            fileData.setFile_path(new File(mPath));
            fileDataArrayList.add(fileData);
//            setSeectionPagerAdapter();
            Intent intent = new Intent(FolderActivity.this, OpenPdfActivity.class);
            intent.putExtra("pdf_name", mPath);
            startActivity(intent);


           setListInRecycleView();

//            StringUtils.getInstance().getSnackbarwithAction(FolderActivity.this, R.string.snackbar_pdfCreated)
//                    .setAction(R.string.snackbar_viewAction,
//                            v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF))
//                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Toast.makeText(getApplicationContext(), "New PDF Created", Toast.LENGTH_LONG).show();
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
        Log.d("PDF_NAME", filename);
        mPdfOptions.setOutFileName(filename);
        mPath = mHomePath+filename+pdfExtension;

        new CreatePdf(mPdfOptions, mHomePath, this).execute();

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
        Log.d("EXCEL+PATH", path);
//        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        Toast.makeText(getApplicationContext(), "PDF Created!", Toast.LENGTH_LONG).show();
//        StringUtils.getInstance().getSnackbarwithAction(this, R.string.snackbar_pdfCreated)
//                .setAction(R.string.snackbar_viewAction,
//                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
//        mPath = path;
        Log.d("IMAGEPATH", mPath);
        FileData fileData = new FileData();
        File file = new File(mPath);
        fileData.setName(file.getName());
        fileData.setDuration(formatLastModifiedDate(file.lastModified()));
        fileData.setFile_type("f");
        fileData.setFile_path(file);
        fileDataArrayList.add(fileData);
        if(fileDataArrayList.size() == 1) {
            mRecyclerView.setVisibility(View.VISIBLE);
            tv_select.setVisibility(View.VISIBLE);
            ll_nofile.setVisibility(View.GONE);
            setListInRecycleView();
        } else {
            sortListByDateName(sort_type);
            mAdapter.notifyDataSetChanged();
        }
        mDatabaseHelper.deleteRecord(file.getName());
        mDatabaseHelper.insertRecord(String.valueOf(path),
                fileData.getDuration(), fileData.getFile_type(), fileData.getName());
        resetValuesf();
    }

    private void resetValuesf() {
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

    @Override
    public void onSelected() {
        tv_select.performClick();
    }
}