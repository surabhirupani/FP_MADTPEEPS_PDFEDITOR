package com.example.pdfeditormadtpeeps.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pdfeditormadtpeeps.Interface.ExtractImagesListener;
import com.example.pdfeditormadtpeeps.Interface.OnPDFCreatedInterface;
import com.example.pdfeditormadtpeeps.Interface.OnPdfReorderedInterface;
import com.example.pdfeditormadtpeeps.Model.FileData;

import com.example.pdfeditormadtpeeps.R;
import com.example.pdfeditormadtpeeps.Utility.Constants;
import com.example.pdfeditormadtpeeps.Utility.CreatePdf;
import com.example.pdfeditormadtpeeps.Utility.CustomViewPager;
import com.example.pdfeditormadtpeeps.Utility.EqualSpacingItemDecoration;
import com.example.pdfeditormadtpeeps.Utility.FileInfoUtils;
import com.example.pdfeditormadtpeeps.Utility.FileUtils;
import com.example.pdfeditormadtpeeps.Utility.ImageToPDFOptions;
import com.example.pdfeditormadtpeeps.Utility.ImageUtils;
import com.example.pdfeditormadtpeeps.Utility.PDFUtils;
import com.example.pdfeditormadtpeeps.Utility.PageSizeUtils;

import com.example.pdfeditormadtpeeps.Utility.PdfToImages;
import com.example.pdfeditormadtpeeps.Utility.PermissionsUtils;
import com.example.pdfeditormadtpeeps.Utility.PrintDocumentAdapterHelper;
import com.example.pdfeditormadtpeeps.Utility.StringUtils;

import com.example.pdfeditormadtpeeps.adapter.EditImageAdapter;
import com.example.pdfeditormadtpeeps.adapter.ImagePdfAdapter;
import com.example.pdfeditormadtpeeps.database.DatabaseHelper;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.ViewType;
import top.defaults.colorpicker.ColorPickerPopup;

public class OpenPdfActivity extends AppCompatActivity implements OnPdfReorderedInterface, View.OnClickListener, ExtractImagesListener, OnPhotoEditorListener, OnPDFCreatedInterface, OnPageChangeListener {
    ImageView iv_back, iv_share, iv_more, iv_layout;
    int layout_flag = 0;
    LinearLayoutCompat ll_layout1, ll_insert, ll_annotation, ll_color, ll_opacity, ll_brush, ll_mode,
        ll_text, ll_background, ll_font, ll_fontsize, ll_alignment, ll_reorder, ll_addpage, ll_addImage;
    Button btn_highlight, btn_draw, btn_text;
    TextView tv_opacity, tv_brush, tv_file_name;
    AppCompatSeekBar sb_opacity, sb_brush;
    FloatingActionButton iv_edit, iv_close;
    File file_path;
    File to = null;
    File from = null;
    PDFView pdfView;
    private DatabaseHelper mDatabaseHelper;
    private PDFUtils mPDFUtils;
    private FileUtils mFileUtils;
    private String mPath;
    String timeStamp;
    private PrintManager printManager;
    private static final int INTENT_REQUEST_REARRANGE_PDF = 11;

    private boolean mPermissionGranted = false;
    private boolean mIsButtonAlreadyClicked = false;
    private static final int REQUEST_PERMISSIONS_CODE = 124;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    public int permission_flag = 0;
    public static ArrayList<String> mImagesUri = new ArrayList<>();
    private static final ArrayList<String> mUnarrangedImagesUri = new ArrayList<>();
    private ImageToPDFOptions mPdfOptions;
    ImageView im_Eraser, tv_done;

    ImageButton imgColorpicker,imgTextColorpicker,imgBackgroundColorpicker;
    Button btn_pink,btn_blue,btn_orange,btn_rama,btn_purple;
    Button btn_txtPink,btn_txtBlue,btn_txtOrange,btn_txtRama,btn_txtPurple;
    Button btn_bgPink,btn_bgBlue,btn_bgOrange,btn_bgRama,btn_bgPurple;
    TextView tvFont ;
    Spinner spSize;
    String[] size = { "10","12","14","16","18","20","22","24","26","28","30"};
    RadioGroup rg_mode,rg_align;
    RadioButton rb_mode1,rb_mode2,rb_mode3,rb_align_left,rb_align_center,rb_align_right;
    private String[] mInputPassword;
    private ArrayList<String> mOutputFilePaths;
    RecyclerView rv_images;
    int pdf_imag_flag = 0;
    int edit_imag_flag = 0;
    int annotate_flag = 0;
    int eraser_flag = 0;
    private ProgressDialog dialog;
    CustomViewPager viewPager;
    EditImageAdapter editImageAdapter;
    int txtColor = 0xF7D6D4D4;
    int drawColor = 0x000000;
    int bgColor;
    float txtFontsize = 16f;
    String strAlignment = "Center";
    String btnSelected = "Draw";
    private String mPageNumStyle;
    private SharedPreferences mSharedPreferences;
    private PageSizeUtils mPageSizeUtils;
    private int mPageColor;
    ImageView iv_prev, iv_next;
    RelativeLayout rl_viewpager;
    private int progress_brush = 10;
    public int save_flag = 0;
    private int pageNumber=0;
    private int progress_opacity = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_pdf);
        //add 30/11/2020 by surabhi
        Constants.copy_flag = 0;

        rl_viewpager = findViewById(R.id.rl_viewpager);
        iv_prev = findViewById(R.id.iv_prev);
        iv_next = findViewById(R.id.iv_next);
        iv_back = findViewById(R.id.iv_back);
        iv_share = findViewById(R.id.iv_share);
        iv_more = findViewById(R.id.iv_more);
        iv_edit = findViewById(R.id.iv_edit);
        iv_layout = findViewById(R.id.iv_layout);
        iv_close = findViewById(R.id.iv_close);
        ll_layout1 = findViewById(R.id.ll_layout1);
        ll_insert = findViewById(R.id.ll_insert);
        ll_annotation = findViewById(R.id.ll_annotation);
        rv_images = findViewById(R.id.rv_images);
        pdfView = findViewById(R.id.pdfView);
        tv_file_name = findViewById(R.id.tv_file_name);
        tv_done = findViewById(R.id.tv_done);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(0);
        printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        mDatabaseHelper = new DatabaseHelper(this);
        mFileUtils = new FileUtils(this);
        mPDFUtils = new PDFUtils(this);
        mPermissionGranted = PermissionsUtils.getInstance().checkRuntimePermissions(this,
                Constants.READ_WRITE_CAMERA_PERMISSIONS);
        rv_images.setHasFixedSize(true);
        dialog = new ProgressDialog(this);
        mOutputFilePaths = new ArrayList<>();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP,
                Constants.DEFAULT_PAGE_COLOR);

        // use a linear layout manager
        rv_images.setLayoutManager(new GridLayoutManager(OpenPdfActivity.this, 2));

        rv_images.addItemDecoration(new EqualSpacingItemDecoration(15, EqualSpacingItemDecoration.VERTICAL));
        rv_images.addItemDecoration(new EqualSpacingItemDecoration(15, EqualSpacingItemDecoration.HORIZONTAL));

        if (!mPermissionGranted) {
            getRuntimePermissions();
        }


        file_path =  new File(getIntent().getStringExtra("pdf_name"));
        mPath = file_path.getPath();
        tv_file_name.setText(file_path.getName());
        pdfView.setBackgroundColor(Color.LTGRAY);
        pdfView.fromFile(file_path)
                .defaultPage(0)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        return false;
                    }
                })
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
//                        Toast.makeText(OpenPdfActivity.this, displayedPage+"", Toast.LENGTH_LONG).show();
                    }
                })
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .load();


        resetValuesf();

        viewPager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageNumber = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_prev.setVisibility(View.VISIBLE);
                if(viewPager.getCurrentItem()+1==mOutputFilePaths.size()-1) {
                    iv_next.setVisibility(View.GONE);
                } else {
                    iv_next.setVisibility(View.VISIBLE);
                }
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);
            }
        });

        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem()-1==0) {
                    iv_prev.setVisibility(View.GONE);
                } else {
                    iv_prev.setVisibility(View.VISIBLE);
                }
                iv_next.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1, true);
            }
        });

        iv_layout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(annotate_flag == 1) {
                    editImageAdapter.saveImage();
                    annotate_flag = 0;
                    saveImagestoPDF();
                }
                annotate_flag = 0;

                if(layout_flag == 0) {
                    iv_layout.setImageResource(R.drawable.single);
                    layout_flag = 1;
                    rv_images.setVisibility(View.VISIBLE);
                    pdfView.setVisibility(View.GONE);
                    rl_viewpager.setVisibility(View.GONE);
                    tv_done.setVisibility(View.GONE);
                    if(mOutputFilePaths.size() > 0) {
                        pdf_imag_flag = 1;
                        ImagePdfAdapter mAdapter = new ImagePdfAdapter(OpenPdfActivity.this, mOutputFilePaths);
                        // set the adapter object to the Recyclerview
                        rv_images.setAdapter(mAdapter);
                        pdfView.setVisibility(View.GONE);
                        rv_images.setVisibility(View.VISIBLE);
                        rl_viewpager.setVisibility(View.GONE);
                        tv_done.setVisibility(View.GONE);
                    }
                    if(pdf_imag_flag == 0) {
                        pdf_imag_flag = 1;
                        new PdfToImages(OpenPdfActivity.this, mInputPassword, mPath, Uri.fromFile(file_path), OpenPdfActivity.this)
                                .execute();
                        pdfView.setVisibility(View.VISIBLE);
                        rv_images.setVisibility(View.GONE);
                        rl_viewpager.setVisibility(View.GONE);
                        tv_done.setVisibility(View.GONE);
                    }
                } else {
                    iv_layout.setImageResource(R.drawable.group_16975);
                    layout_flag = 0;
                    rv_images.setVisibility(View.GONE);
                    pdfView.setVisibility(View.VISIBLE);
                    rl_viewpager.setVisibility(View.GONE);
                    tv_done.setVisibility(View.GONE);
                }
            }
        });

        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_edit.setVisibility(View.GONE);
                iv_layout.setVisibility(View.GONE);
                iv_close.setVisibility(View.VISIBLE);
                ll_annotation.setVisibility(View.VISIBLE);
                ll_insert.setVisibility(View.VISIBLE);
                ll_layout1.setVisibility(View.VISIBLE);
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_edit.setVisibility(View.VISIBLE);
                iv_layout.setVisibility(View.VISIBLE);
                iv_close.setVisibility(View.GONE);
                ll_annotation.setVisibility(View.GONE);
                ll_insert.setVisibility(View.GONE);
                ll_layout1.setVisibility(View.GONE);
            }
        });

        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMoreDialog();
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareDialog();
            }
        });

        ll_insert.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(annotate_flag == 1) {
                    editImageAdapter.saveImage();
                    annotate_flag = 0;
                    saveImagestoPDF();
                }
                annotate_flag = 0;
                openInsertDialog();
            }
        });

        ll_layout1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(annotate_flag == 1) {
                    editImageAdapter.saveImage();
                    annotate_flag = 0;
                    saveImagestoPDF();
                }
                annotate_flag = 0;
                openReoderDialog();
            }
        });

        ll_annotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAnnotateDialog();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                annotate_flag = 0;
                tv_done.setVisibility(View.GONE);
                editImageAdapter.saveImage();
                saveImagestoPDF();
            }
        });
    }

    private void saveImagestoPDF() {
        if(mOutputFilePaths.size() > 0) {
            save_flag = 1;
            mPdfOptions.setImagesUri(mOutputFilePaths);
            mPdfOptions.setPageSize(mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT,
                    Constants.DEFAULT_PAGE_SIZE));
            mPdfOptions.setImageScaleType(ImageUtils.getInstance().mImageScaleType);
            mPdfOptions.setPageNumStyle(mPageNumStyle);
//        mPdfOptions.setMasterPwd(mSharedPreferences.getString(MASTER_PWD_STRING, appName));
            mPdfOptions.setPageColor(mPageColor);
            String file_name = tv_file_name.getText().toString();
            file_name = file_name.substring(0, file_name.length() - 4);
            String parent_path = file_path.getParent();
            timeStamp = new SimpleDateFormat("ddmmyyyyhhmmss").format(new
                    Date());
            Log.d("PDF_NAME", file_name);
            mPdfOptions.setOutFileName(timeStamp);
            to = new File(parent_path,file_path.getName());
            from = new File(parent_path,timeStamp+".pdf");
            new CreatePdf(mPdfOptions, parent_path+"/", OpenPdfActivity.this).execute();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openAnnotateDialog() {
        edit_imag_flag = 1;
        annotate_flag = 1;
        tv_done.setVisibility(View.VISIBLE);
        rl_viewpager.setVisibility(View.VISIBLE);
        rv_images.setVisibility(View.GONE);
        pdfView.setVisibility(View.GONE);
        viewPager.setCurrentItem(pageNumber);
        if(mOutputFilePaths.size() == 0) {
            new PdfToImages(OpenPdfActivity.this, mInputPassword, mPath, Uri.fromFile(file_path), OpenPdfActivity.this)
                    .execute();
        }

        View bottomSheetview = getLayoutInflater().inflate(R.layout.bottom_annotate_layout, null);
        BottomSheetDialog dialog = new BottomSheetDialog(OpenPdfActivity.this);
        dialog.setContentView(bottomSheetview);
        btn_highlight = dialog.findViewById(R.id.btn_highlight);
        btn_draw = dialog.findViewById(R.id.btn_draw);
        btn_text = dialog.findViewById(R.id.btn_text);
        tv_opacity = dialog.findViewById(R.id.tv_opacity);
        sb_opacity = dialog.findViewById(R.id.sb_opacity);
        sb_brush = dialog.findViewById(R.id.sb_brush);
        tv_brush = dialog.findViewById(R.id.tv_brush);
        ll_brush = dialog.findViewById(R.id.ll_brush);
        ll_color = dialog.findViewById(R.id.ll_color);
        ll_opacity = dialog.findViewById(R.id.ll_opacity);
        ll_mode = dialog.findViewById(R.id.ll_mode);
        ll_text = dialog.findViewById(R.id.ll_text);
        ll_background = dialog.findViewById(R.id.ll_background);
        ll_font = dialog.findViewById(R.id.ll_font);
        ll_fontsize = dialog.findViewById(R.id.ll_fontsize);
        ll_alignment = dialog.findViewById(R.id.ll_alignment);
        im_Eraser = dialog.findViewById(R.id.im_Eraser);

        if (btnSelected.equals("Highlight"))
        {
            im_Eraser.setVisibility(View.GONE);
            sb_opacity.setProgress(progress_opacity);
            btn_highlight.setBackgroundResource(R.drawable.group_16981);
            btn_draw.setBackgroundResource(R.drawable.button_bg);
            btn_text.setBackgroundResource(R.drawable.button_bg);
            ll_color.setVisibility(View.VISIBLE);
            ll_mode.setVisibility(View.VISIBLE);
            ll_opacity.setVisibility(View.VISIBLE);
            ll_brush.setVisibility(View.GONE);
            ll_text.setVisibility(View.GONE);
            ll_background.setVisibility(View.GONE);
            ll_font.setVisibility(View.GONE);
            ll_fontsize.setVisibility(View.GONE);
            ll_alignment.setVisibility(View.GONE);
        }
        else if (btnSelected.equals("Draw"))
        {
            im_Eraser.setVisibility(View.VISIBLE);
            sb_brush.setProgress(progress_brush);
            tv_brush.setText(progress_brush+"%");
            sb_opacity.setProgress(progress_opacity);
            tv_opacity.setText(progress_opacity+"%");
            btn_draw.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#226A63")));
            btn_text.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3C444E")));

            btn_highlight.setBackgroundResource(R.drawable.button_bg);
            btn_draw.setBackgroundResource(R.drawable.group_16981);
            btn_text.setBackgroundResource(R.drawable.button_bg);
            ll_color.setVisibility(View.VISIBLE);
            ll_mode.setVisibility(View.GONE);
            ll_opacity.setVisibility(View.VISIBLE);
            ll_brush.setVisibility(View.VISIBLE);
            ll_text.setVisibility(View.GONE);
            ll_background.setVisibility(View.GONE);
            ll_font.setVisibility(View.GONE);
            ll_fontsize.setVisibility(View.GONE);
            ll_alignment.setVisibility(View.GONE);
        }
        else if (btnSelected.equals("Text"))
        {
            bgColor = 0;
            btn_highlight.setBackgroundResource(R.drawable.button_bg);
            btn_draw.setBackgroundResource(R.drawable.button_bg);
            btn_text.setBackgroundResource(R.drawable.group_16981);
            btn_text.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#226A63")));
            btn_draw.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3C444E")));

            ll_color.setVisibility(View.GONE);
            ll_mode.setVisibility(View.GONE);
            ll_opacity.setVisibility(View.GONE);
            ll_brush.setVisibility(View.GONE);
            ll_text.setVisibility(View.VISIBLE);
            ll_background.setVisibility(View.VISIBLE);
            ll_font.setVisibility(View.VISIBLE);
            ll_fontsize.setVisibility(View.VISIBLE);
            ll_alignment.setVisibility(View.VISIBLE);
        }


        imgColorpicker = dialog.findViewById(R.id.imgColorpicker);
        imgTextColorpicker = dialog.findViewById(R.id.imgTextColorpicker);
        imgBackgroundColorpicker = dialog.findViewById(R.id.imgBackgroundColorpicker);

        btn_pink=dialog.findViewById(R.id.btn_pink);
        btn_blue=dialog.findViewById(R.id.btn_blue);
        btn_orange=dialog.findViewById(R.id.btn_orange);
        btn_rama=dialog.findViewById(R.id.btn_rama);
        btn_purple= dialog.findViewById(R.id.btn_purple);

        btn_txtPink = dialog.findViewById(R.id.btn_txtPink);
        btn_txtBlue = dialog.findViewById(R.id.btn_txtBlue);
        btn_txtOrange= dialog.findViewById(R.id.btn_txtOrange);
        btn_txtRama = dialog.findViewById(R.id.btn_txtRama);
        btn_txtPurple = dialog.findViewById(R.id.btn_txtPurple);

        btn_bgPink = dialog.findViewById(R.id.btn_bgPink);
        btn_bgBlue = dialog.findViewById(R.id.btn_bgBlue);
        btn_bgOrange= dialog.findViewById(R.id.btn_bgOrange);
        btn_bgRama = dialog.findViewById(R.id.btn_bgRama);
        btn_bgPurple = dialog.findViewById(R.id.btn_bgPurple);

        tvFont = dialog.findViewById(R.id.tvFont);
        spSize = dialog.findViewById(R.id.spSize);
        rg_mode = dialog.findViewById(R.id.rg_mode);
        rb_mode1 = dialog.findViewById(R.id.rb_mode1);
        rb_mode2 = dialog.findViewById(R.id.rb_mode2);
        rb_mode3 = dialog.findViewById(R.id.rb_mode3);

        rg_align = dialog.findViewById(R.id.rg_align);
        rb_align_left = dialog.findViewById(R.id.rb_align_left);
        rb_align_center = dialog.findViewById(R.id.rb_align_center);
        rb_align_right = dialog.findViewById(R.id.rb_align_right);

        // Highlights Color click
        btn_pink.setOnClickListener(this);
        btn_blue.setOnClickListener(this);
        btn_orange.setOnClickListener(this);
        btn_rama.setOnClickListener(this);
        btn_purple.setOnClickListener(this);
        // Text Color click
        btn_highlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                im_Eraser.setVisibility(View.GONE);
                btnSelected ="Highlight";
                Toast.makeText(getApplicationContext(),"This feature is coming in next version!!",Toast.LENGTH_LONG).show();
                sb_opacity.setProgress(progress_opacity);
                btn_highlight.setBackgroundResource(R.drawable.group_16981);
                btn_draw.setBackgroundResource(R.drawable.button_bg);
                btn_text.setBackgroundResource(R.drawable.button_bg);
                ll_color.setVisibility(View.VISIBLE);
                ll_mode.setVisibility(View.VISIBLE);
                ll_opacity.setVisibility(View.VISIBLE);
                ll_brush.setVisibility(View.GONE);
                ll_text.setVisibility(View.GONE);
                ll_background.setVisibility(View.GONE);
                ll_font.setVisibility(View.GONE);
                ll_fontsize.setVisibility(View.GONE);
                ll_alignment.setVisibility(View.GONE);
            }
        });
        btn_draw.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                im_Eraser.setVisibility(View.VISIBLE);
                btnSelected = "Draw";
               // Toast.makeText(getApplicationContext(),btnSelected,Toast.LENGTH_LONG).show();
                sb_brush.setProgress(progress_brush);
                tv_brush.setText(progress_brush+"%");
                sb_opacity.setProgress(progress_opacity);
                tv_opacity.setText(progress_opacity+"%");
                btn_highlight.setBackgroundResource(R.drawable.button_bg);
                btn_draw.setBackgroundResource(R.drawable.group_16981);
                btn_text.setBackgroundResource(R.drawable.button_bg);
                btn_draw.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#226A63")));
                btn_text.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3C444E")));
                ll_color.setVisibility(View.VISIBLE);
                ll_mode.setVisibility(View.GONE);
                ll_opacity.setVisibility(View.VISIBLE);
                ll_brush.setVisibility(View.VISIBLE);
                ll_text.setVisibility(View.GONE);
                ll_background.setVisibility(View.GONE);
                ll_font.setVisibility(View.GONE);
                ll_fontsize.setVisibility(View.GONE);
                ll_alignment.setVisibility(View.GONE);


            }
        });
        btn_text.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                btnSelected = "Text";

               // Toast.makeText(getApplicationContext(),btnSelected,Toast.LENGTH_LONG).show();
                btn_text.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#226A63")));
                btn_draw.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3C444E")));
                btn_highlight.setBackgroundResource(R.drawable.button_bg);
                btn_draw.setBackgroundResource(R.drawable.button_bg);
                btn_text.setBackgroundResource(R.drawable.group_16981);
                ll_color.setVisibility(View.GONE);
                ll_mode.setVisibility(View.GONE);
                ll_opacity.setVisibility(View.GONE);
                ll_brush.setVisibility(View.GONE);
                ll_text.setVisibility(View.VISIBLE);
                ll_background.setVisibility(View.VISIBLE);
                ll_font.setVisibility(View.VISIBLE);
                ll_fontsize.setVisibility(View.VISIBLE);
                ll_alignment.setVisibility(View.VISIBLE);
            }
        });

        im_Eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "eraser", Toast.LENGTH_LONG).show();
                eraser_flag = 1;
                editImageAdapter.onEraser();
                dialog.dismiss();
            }
        });


        btn_txtPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_txtPink.setSelected(false);
                btn_txtPink.setPressed(false);
                btn_txtBlue.setSelected(false);
                btn_txtBlue.setPressed(false);
                btn_txtOrange.setSelected(false);
                btn_txtOrange.setPressed(false);
                btn_txtRama.setSelected(false);
                btn_txtRama.setPressed(false);
                btn_txtPurple.setSelected(false);
                btn_txtPurple.setPressed(false);

                // change state
                btn_txtPink.setSelected(true);
                btn_txtPink.setPressed(false);

              //  int myColor = 0xF32269;
                 txtColor = Color.parseColor("#F32269");



            }
        });

        btn_txtBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_txtPink.setSelected(false);
                btn_txtPink.setPressed(false);
                btn_txtBlue.setSelected(false);
                btn_txtBlue.setPressed(false);
                btn_txtOrange.setSelected(false);
                btn_txtOrange.setPressed(false);
                btn_txtRama.setSelected(false);
                btn_txtRama.setPressed(false);
                btn_txtPurple.setSelected(false);
                btn_txtPurple.setPressed(false);
                // change state
                btn_txtBlue.setSelected(true);
                btn_txtBlue.setPressed(false);

                txtColor = Color.parseColor("#1937DD");
               // txtColor = Integer.parseInt(color);


            }
        });

        btn_txtOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_txtPink.setSelected(false);
                btn_txtPink.setPressed(false);
                btn_txtBlue.setSelected(false);
                btn_txtBlue.setPressed(false);
                btn_txtOrange.setSelected(false);
                btn_txtOrange.setPressed(false);
                btn_txtRama.setSelected(false);
                btn_txtRama.setPressed(false);
                btn_txtPurple.setSelected(false);
                btn_txtPurple.setPressed(false);

                // change state
                btn_txtOrange.setSelected(true);
                btn_txtOrange.setPressed(false);

                txtColor = Color.parseColor("#EA4917");
               // txtColor = Integer.parseInt(color);



            }
        });
        btn_txtRama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_txtPink.setSelected(false);
                btn_txtPink.setPressed(false);
                btn_txtBlue.setSelected(false);
                btn_txtBlue.setPressed(false);
                btn_txtOrange.setSelected(false);
                btn_txtOrange.setPressed(false);
                btn_txtRama.setSelected(false);
                btn_txtRama.setPressed(false);
                btn_txtPurple.setSelected(false);
                btn_txtPurple.setPressed(false);

                // change state
                btn_txtRama.setSelected(true);
                btn_txtRama.setPressed(false);

                txtColor = Color.parseColor("#47CABE");



            }
        });

        btn_txtPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_txtPink.setSelected(false);
                btn_txtPink.setPressed(false);
                btn_txtBlue.setSelected(false);
                btn_txtBlue.setPressed(false);
                btn_txtOrange.setSelected(false);
                btn_txtOrange.setPressed(false);
                btn_txtRama.setSelected(false);
                btn_txtRama.setPressed(false);
                btn_txtPurple.setSelected(false);
                btn_txtPurple.setPressed(false);
                // change state
                btn_txtPurple.setSelected(true);
                btn_txtPurple.setPressed(false);

                txtColor = Color.parseColor("#CC29E8");
            }
        });

        btn_bgPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_bgPink.setSelected(false);
                btn_bgPink.setPressed(false);
                btn_bgBlue.setSelected(false);
                btn_bgBlue.setPressed(false);
                btn_bgOrange.setSelected(false);
                btn_bgOrange.setPressed(false);
                btn_bgRama.setSelected(false);
                btn_bgRama.setPressed(false);
                btn_bgPurple.setSelected(false);
                btn_bgPurple.setPressed(false);

                // change state
                btn_bgPink.setSelected(true);
                btn_bgPink.setPressed(false);
                bgColor = Color.parseColor("#F32269");
//                Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();

            }
        });
        btn_bgBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_bgPink.setSelected(false);
                btn_bgPink.setPressed(false);
                btn_bgBlue.setSelected(false);
                btn_bgBlue.setPressed(false);
                btn_bgOrange.setSelected(false);
                btn_bgOrange.setPressed(false);
                btn_bgRama.setSelected(false);
                btn_bgRama.setPressed(false);
                btn_bgPurple.setSelected(false);
                btn_bgPurple.setPressed(false);

                // change state
                btn_bgBlue.setSelected(true);
                btn_bgBlue.setPressed(false);
                bgColor = Color.parseColor("#1937DD");
//                Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();

            }
        });
        btn_bgOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_bgPink.setSelected(false);
                btn_bgPink.setPressed(false);
                btn_bgBlue.setSelected(false);
                btn_bgBlue.setPressed(false);
                btn_bgOrange.setSelected(false);
                btn_bgOrange.setPressed(false);
                btn_bgRama.setSelected(false);
                btn_bgRama.setPressed(false);
                btn_bgPurple.setSelected(false);
                btn_bgPurple.setPressed(false);

                // change state
                btn_bgOrange.setSelected(true);
                btn_bgOrange.setPressed(false);
                bgColor = Color.parseColor("#EA4917");
//                Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();

            }
        });
        btn_bgRama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_bgPink.setSelected(false);
                btn_bgPink.setPressed(false);
                btn_bgBlue.setSelected(false);
                btn_bgBlue.setPressed(false);
                btn_bgOrange.setSelected(false);
                btn_bgOrange.setPressed(false);
                btn_bgRama.setSelected(false);
                btn_bgRama.setPressed(false);
                btn_bgPurple.setSelected(false);
                btn_bgPurple.setPressed(false);

                // change state
                btn_bgRama.setSelected(true);
                btn_bgRama.setPressed(false);
                bgColor = Color.parseColor("#47CABE");
                //Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();

            }
        });
        btn_bgPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_bgPink.setSelected(false);
                btn_bgPink.setPressed(false);
                btn_bgBlue.setSelected(false);
                btn_bgBlue.setPressed(false);
                btn_bgOrange.setSelected(false);
                btn_bgOrange.setPressed(false);
                btn_bgRama.setSelected(false);
                btn_bgRama.setPressed(false);
                btn_bgPurple.setSelected(false);
                btn_bgPurple.setPressed(false);

                // change state
                btn_bgPurple.setSelected(true);
                btn_bgPurple.setPressed(false);
                bgColor = Color.parseColor("#CC29E8");
//                String color = String.format("#%06X", (0xCC29E8));
//              Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();
            }
        });



        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                int myColor = -3808902;
                if (btnSelected.equals("Text")){
                    editImageAdapter.onTextChange(txtColor, bgColor, txtFontsize, tvFont.getText().toString(),strAlignment);
                }
                else if (btnSelected.equals("Draw")){
                    if(eraser_flag == 1) {
                        editImageAdapter.onEraser();
                        eraser_flag = 0;
                    } else {
                        editImageAdapter.onColorChanged(drawColor);
                        editImageAdapter.onBrushSizeChanged(progress_brush);
                    }
                }


            }
        });



        rg_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_mode1:
                        //Toast.makeText(OpenPdfActivity.this,"hello",Toast.LENGTH_LONG).show();
                        // do operations specific to this selection
                        break;
                    case R.id.rb_mode2:
                        //Toast.makeText(OpenPdfActivity.this,"hello2",Toast.LENGTH_LONG).show();
                        // do operations specific to this selection
                        break;
                    case R.id.rb_mode3:
                        //Toast.makeText(OpenPdfActivity.this,"hello3",Toast.LENGTH_LONG).show();
                        // do operations specific to this selection
                        break;
                }
            }
        });

// for the alignment
        rg_align.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_align_left:
                        strAlignment = "Left";
                        Toast.makeText(OpenPdfActivity.this,strAlignment,Toast.LENGTH_LONG).show();
                        // do operations specific to this selection
                        break;
                    case R.id.rb_align_center:
                        strAlignment = "Center";
                        //Toast.makeText(OpenPdfActivity.this,strAlignment,Toast.LENGTH_LONG).show();

                        break;
                    case R.id.rb_align_right:
                        strAlignment = "Right";
                       // Toast.makeText(OpenPdfActivity.this,strAlignment,Toast.LENGTH_LONG).show();

                        break;
                }
            }
        });

// for the size
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_item,size);
        aa.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spSize.setAdapter(aa);
        spSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Choose Football players from lis")){
                }else {
                     //txtFontsize = position;
                    String item = parent.getItemAtPosition(position).toString();
                    txtFontsize = Float.parseFloat(item);
//                    Toast.makeText(parent.getContext(),"Selected: " +txtFontsize, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        imgColorpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraser_flag = 0;
                new ColorPickerPopup.Builder(OpenPdfActivity.this)
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(false)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onColorPicked(int color) {
                                //v.setBackgroundColor(color);
                                drawColor = color;
                                editImageAdapter.onColorChanged(color);

                                btn_pink.setSelected(false);
                                btn_pink.setPressed(false);
                                btn_blue.setSelected(false);
                                btn_blue.setPressed(false);
                                btn_orange.setSelected(false);
                                btn_orange.setPressed(false);
                                btn_rama.setSelected(false);
                                btn_rama.setPressed(false);
                                btn_purple.setSelected(false);
                                btn_purple.setPressed(false);

                                // change state
                                btn_purple.setSelected(true);
                                btn_purple.setPressed(false);
                                btn_purple.setBackgroundTintList(ColorStateList.valueOf(color));
                                Log.d("Colorpicker", String.format("#%06X", ( color)));
                                //Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();
                            }

                            public void onColor(char color, boolean fromUser) {
                            }
                        });


            }
        });

        imgTextColorpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(OpenPdfActivity.this)
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(false)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onColorPicked(int color) {
                                //v.setBackgroundColor(color);
                                btn_txtPink.setSelected(false);
                                btn_txtPink.setPressed(false);
                                btn_txtBlue.setSelected(false);
                                btn_txtBlue.setPressed(false);
                                btn_txtOrange.setSelected(false);
                                btn_txtOrange.setPressed(false);
                                btn_txtRama.setSelected(false);
                                btn_txtRama.setPressed(false);
                                btn_txtPurple.setSelected(false);
                                btn_txtPurple.setPressed(false);
                                // change state
//                                btn_txtPurple.setBackground(ContextCompat.getDrawable(OpenPdfActivity.this, R.drawable.btn_bg_selector));
                                btn_txtPurple.setBackgroundTintList(ColorStateList.valueOf(color));
//                                btn_txtPurple.setBackgroundColor(color);
                                btn_txtPurple.setSelected(true);
                                btn_txtPurple.setPressed(false);

                                txtColor = color;
                                Log.d("TextColorPicker", String.format("#%06X", ( color)));
                                //Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();
                            }

                            public void onColor(char color, boolean fromUser) {
                            }
                        });

            }
        });
        imgBackgroundColorpicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(OpenPdfActivity.this)
                        .initialColor(Color.RED) // Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(false)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onColorPicked(int color) {
                                btn_bgPink.setSelected(false);
                                btn_bgPink.setPressed(false);
                                btn_bgBlue.setSelected(false);
                                btn_bgBlue.setPressed(false);
                                btn_bgOrange.setSelected(false);
                                btn_bgOrange.setPressed(false);
                                btn_bgRama.setSelected(false);
                                btn_bgRama.setPressed(false);
                                btn_bgPurple.setSelected(false);
                                btn_bgPurple.setPressed(false);

                                // change state
                                btn_bgPurple.setSelected(true);
                                btn_bgPurple.setPressed(false);
                                btn_bgPurple.setBackgroundTintList(ColorStateList.valueOf(color));
                                //v.setBackgroundColor(color);
                                bgColor = color;
                                Log.d("BackgroundColorPicker", String.format("#%06X", ( color)));
                                //Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();
                            }

                            public void onColor(char color, boolean fromUser) {
                            }
                        });
            }
        });
        tvFont.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(OpenPdfActivity.this);

                dialog.setContentView(R.layout.dialog_font);
                dialog.setTitle("Android Custom Dialog Box");

                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                TextView tvFont1 = (TextView)dialog.findViewById(R.id.tvFont1);
                TextView tvFont2 = (TextView)dialog.findViewById(R.id.tvFont2);
                TextView tvFont3 = (TextView)dialog.findViewById(R.id.tvFont3);
                TextView tvFont4 = (TextView)dialog.findViewById(R.id.tvFont4);
                TextView tvFont5 = (TextView)dialog.findViewById(R.id.tvFont5);
                TextView tvFont6 = (TextView)dialog.findViewById(R.id.tvFont6);
                TextView tvFont7 = (TextView)dialog.findViewById(R.id.tvFont7);
                TextView tvFont8 = (TextView)dialog.findViewById(R.id.tvFont8);
                TextView tvFont9 = (TextView)dialog.findViewById(R.id.tvFont9);
                TextView tvFont10 = (TextView)dialog.findViewById(R.id.tvFont10);
                TextView tvFont11 = (TextView)dialog.findViewById(R.id.tvFont11);
                TextView tvFont12 = (TextView)dialog.findViewById(R.id.tvFont12);
                TextView tvFont13 = (TextView)dialog.findViewById(R.id.tvFont13);
                TextView tvFont14 = (TextView)dialog.findViewById(R.id.tvFont14);
                TextView tvFont15 = (TextView)dialog.findViewById(R.id.tvFont15);
                TextView tvFont16 = (TextView)dialog.findViewById(R.id.tvFont16);
                TextView tvFont17 = (TextView)dialog.findViewById(R.id.tvFont17);
                TextView tvFont18 = (TextView)dialog.findViewById(R.id.tvFont18);
                TextView tvFont19 = (TextView)dialog.findViewById(R.id.tvFont19);
                TextView tvFont20 = (TextView)dialog.findViewById(R.id.tvFont20);
                TextView tvFont21 = (TextView)dialog.findViewById(R.id.tvFont21);
                TextView tvFont22 = (TextView)dialog.findViewById(R.id.tvFont22);
                TextView tvFont23 = (TextView)dialog.findViewById(R.id.tvFont23);
                TextView tvFont24 = (TextView)dialog.findViewById(R.id.tvFont24);
                TextView tvFont25 = (TextView)dialog.findViewById(R.id.tvFont25);
                TextView tvFont26 = (TextView)dialog.findViewById(R.id.tvFont26);
                TextView tvFont27 = (TextView)dialog.findViewById(R.id.tvFont27);
                TextView tvFont28 = (TextView)dialog.findViewById(R.id.tvFont28);
                TextView tvFont29 = (TextView)dialog.findViewById(R.id.tvFont29);
                TextView tvFont30 = (TextView)dialog.findViewById(R.id.tvFont30);

                ImageView iv_close = (ImageView)dialog.findViewById(R.id.iv_close);

                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final Typeface face1 = ResourcesCompat.getFont(getApplicationContext(), R.font.aladinregular);
                tvFont1.setTypeface(face1);

                tvFont1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face1);
                        tvFont.setText(tvFont1.getText().toString());

                        //Toast.makeText(getApplicationContext(),font,Toast.LENGTH_LONG).show();
                    }
                });
                final Typeface face2 = ResourcesCompat.getFont(getApplicationContext(), R.font.poppinsregular);
                tvFont2.setTypeface(face2);

                tvFont2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face2);
                        tvFont.setText(tvFont2.getText().toString());
                    }
                });
                Typeface face3 = ResourcesCompat.getFont(getApplicationContext(), R.font.satisfy);
                tvFont3.setTypeface(face3);
                tvFont3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face3);
                        tvFont.setText(tvFont3.getText().toString());
                    }
                });

                Typeface face4 = ResourcesCompat.getFont(getApplicationContext(), R.font.synemono);
                tvFont4.setTypeface(face4);
                tvFont4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face4);
                        tvFont.setText(tvFont4.getText().toString());
                    }
                });

                Typeface face5 = ResourcesCompat.getFont(getApplicationContext(), R.font.electrolize);
                tvFont5.setTypeface(face5);
                tvFont5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face5);
                        tvFont.setText(tvFont5.getText().toString());
                    }
                });

                Typeface face6 = ResourcesCompat.getFont(getApplicationContext(), R.font.emilyscandy);
                tvFont6.setTypeface(face6);
                tvFont6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face6);
                        tvFont.setText(tvFont6.getText().toString());
                    }
                });

                Typeface face7 = ResourcesCompat.getFont(getApplicationContext(), R.font.arapey);
                tvFont7.setTypeface(face7);
                tvFont7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face7);
                        tvFont.setText(tvFont7.getText().toString());
                    }
                });

                Typeface face8 = ResourcesCompat.getFont(getApplicationContext(), R.font.shortstack);
                tvFont8.setTypeface(face8);
                tvFont8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face8);
                        tvFont.setText(tvFont8.getText().toString());
                    }
                });


                Typeface face9 = ResourcesCompat.getFont(getApplicationContext(), R.font.aclonica);
                tvFont9.setTypeface(face9);
                tvFont9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face9);
                        tvFont.setText(tvFont9.getText().toString());
                    }
                });

                Typeface face10 = ResourcesCompat.getFont(getApplicationContext(), R.font.almendra);
                tvFont10.setTypeface(face10);
                tvFont10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face10);
                        tvFont.setText(tvFont10.getText().toString());
                    }
                });

                Typeface face11 = ResourcesCompat.getFont(getApplicationContext(), R.font.bungeeshade);
                tvFont11.setTypeface(face11);
                tvFont11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face11);
                        tvFont.setText(tvFont11.getText().toString());
                    }
                });

                Typeface face12 = ResourcesCompat.getFont(getApplicationContext(), R.font.chonburi);
                tvFont12.setTypeface(face12);
                tvFont12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face12);
                        tvFont.setText(tvFont12.getText().toString());
                    }
                });

                Typeface face13 = ResourcesCompat.getFont(getApplicationContext(), R.font.doppioone);
                tvFont13.setTypeface(face13);
                tvFont13.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face13);
                        tvFont.setText(tvFont13.getText().toString());
                    }
                });

                Typeface face14 = ResourcesCompat.getFont(getApplicationContext(), R.font.passeroone);
                tvFont14.setTypeface(face14);
                tvFont14.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face14);
                        tvFont.setText(tvFont14.getText().toString());
                    }
                });

                Typeface face15 = ResourcesCompat.getFont(getApplicationContext(), R.font.croissantone);
                tvFont15.setTypeface(face15);
                tvFont15.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face15);
                        tvFont.setText(tvFont15.getText().toString());
                    }
                });

                Typeface face16 = ResourcesCompat.getFont(getApplicationContext(), R.font.poly);
                tvFont16.setTypeface(face16);
                tvFont16.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face16);
                        tvFont.setText(tvFont16.getText().toString());
                    }
                });

                Typeface face17 = ResourcesCompat.getFont(getApplicationContext(), R.font.averialibre);
                tvFont17.setTypeface(face17);
                tvFont17.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face17);
                        tvFont.setText(tvFont17.getText().toString());
                    }
                });

                Typeface face18 = ResourcesCompat.getFont(getApplicationContext(), R.font.philosopher);
                tvFont18.setTypeface(face18);
                tvFont18.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face18);
                        tvFont.setText(tvFont18.getText().toString());
                    }
                });

                Typeface face19 = ResourcesCompat.getFont(getApplicationContext(), R.font.shanti);
                tvFont19.setTypeface(face19);
                tvFont19.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face19);
                        tvFont.setText(tvFont19.getText().toString());
                    }
                });

                Typeface face20 = ResourcesCompat.getFont(getApplicationContext(), R.font.faustina);
                tvFont20.setTypeface(face20);
                tvFont20.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face20);
                        tvFont.setText(tvFont20.getText().toString());
                    }
                });

                Typeface face21 = ResourcesCompat.getFont(getApplicationContext(), R.font.gudea);
                tvFont21.setTypeface(face21);
                tvFont21.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face21);
                        tvFont.setText(tvFont21.getText().toString());
                    }
                });

                Typeface face22 = ResourcesCompat.getFont(getApplicationContext(), R.font.publicsans);
                tvFont22.setTypeface(face22);
                tvFont22.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face22);
                        tvFont.setText(tvFont22.getText().toString());
                    }
                });

                Typeface face23 = ResourcesCompat.getFont(getApplicationContext(), R.font.amaranth);
                tvFont23.setTypeface(face23);
                tvFont23.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face23);
                        tvFont.setText(tvFont23.getText().toString());
                    }
                });

                Typeface face24 = ResourcesCompat.getFont(getApplicationContext(), R.font.itim);
                tvFont24.setTypeface(face24);
                tvFont24.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face24);
                        tvFont.setText(tvFont24.getText().toString());
                    }
                });

                Typeface face25 = ResourcesCompat.getFont(getApplicationContext(), R.font.sansita);
                tvFont25.setTypeface(face25);
                tvFont25.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face25);
                        tvFont.setText(tvFont25.getText().toString());
                    }
                });

                Typeface face26 = ResourcesCompat.getFont(getApplicationContext(), R.font.scada);
                tvFont26.setTypeface(face26);
                tvFont26.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face26);
                        tvFont.setText(tvFont26.getText().toString());
                    }
                });

                Typeface face27 = ResourcesCompat.getFont(getApplicationContext(), R.font.b612);
                tvFont27.setTypeface(face27);
                tvFont27.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face27);
                        tvFont.setText(tvFont27.getText().toString());
                    }
                });

                Typeface face28 = ResourcesCompat.getFont(getApplicationContext(), R.font.charm);
                tvFont28.setTypeface(face28);
                tvFont28.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face28);
                        tvFont.setText(tvFont28.getText().toString());
                    }
                });

                Typeface face29 = ResourcesCompat.getFont(getApplicationContext(), R.font.mirza);
                tvFont29.setTypeface(face29);
                tvFont29.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face29);
                        tvFont.setText(tvFont29.getText().toString());
                    }
                });

                Typeface face30 = ResourcesCompat.getFont(getApplicationContext(), R.font.esteban);
                tvFont30.setTypeface(face30);
                tvFont30.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        tvFont.setTypeface(face30);
                        tvFont.setText(tvFont30.getText().toString());
                    }
                });

                dialog.dismiss();
                dialog.show();
            }
        });




        sb_opacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // You can have your own calculation for progress
                progress_opacity = progress;
                tv_opacity.setText(progress +"%");
                editImageAdapter.onOpacityChanged(progress_opacity);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_brush.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // You can have your own calculation for progress
                progress_brush = progress;
                tv_brush.setText(progress +"%");
                editImageAdapter.onBrushSizeChanged(progress_brush);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialog
                .getWindow()
                .findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
        dialog.show();
    }



    private void openReoderDialog() {
        rl_viewpager.setVisibility(View.GONE);
        tv_done.setVisibility(View.GONE);
        rv_images.setVisibility(View.GONE);
        pdfView.setVisibility(View.VISIBLE);
        View bottomSheetview = getLayoutInflater().inflate(R.layout.bottom_reorder_layout, null);

        BottomSheetDialog dialog = new BottomSheetDialog(OpenPdfActivity.this);
        dialog.setContentView(bottomSheetview);

        ll_addpage = dialog.findViewById(R.id.ll_addpage);
        ll_reorder = dialog.findViewById(R.id.ll_reorder);

        ll_addpage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                PdfReader reader = null;
                String timeStamp = null;
                File to = null;
                File from = null;
                try {
                    reader = new PdfReader(file_path.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PdfStamper stamper = null;
                try {
                    timeStamp = new SimpleDateFormat("ddmmyyyyhhmmss").format(new
                            Date());
                    String dir = file_path.getParent();
                    stamper = new PdfStamper(reader, new FileOutputStream(file_path.getParent()+"/"+timeStamp+".pdf"));
                    to = new File(dir,file_path.getName());
                    from = new File(dir,timeStamp+".pdf");
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stamper.insertPage(reader.getNumberOfPages() + 1,
                        reader.getPageSizeWithRotation(1));
                try {
                    stamper.close();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                reader.close();
                pdfView.fromFile(new File(file_path.getParent() + "/" + timeStamp + ".pdf"))
                        .defaultPage(pdfView.getPageCount())
                        .enableAnnotationRendering(true)
                        .scrollHandle(new DefaultScrollHandle(OpenPdfActivity.this))
                        .spacing(10) // in dp
                        .onPageChange(OpenPdfActivity.this)
                         .load();
                file_path.delete();
                if(from.exists())
                    from.renameTo(to);


                file_path = to;
                mPath = file_path.getPath();
                dialog.dismiss();
            }
        });

        ll_reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mPDFUtils.reorderPdfPages(Uri.fromFile(file_path), file_path.toString(), OpenPdfActivity.this);
            }
        });

        dialog
                .getWindow()
                .findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
        dialog.show();
    }

    private void openInsertDialog() {
        rl_viewpager.setVisibility(View.GONE);
        tv_done.setVisibility(View.GONE);
        rv_images.setVisibility(View.GONE);
        pdfView.setVisibility(View.VISIBLE);
        View bottomSheetview = getLayoutInflater().inflate(R.layout.bottom_insert_layout, null);

        BottomSheetDialog dialog = new BottomSheetDialog(OpenPdfActivity.this);
        dialog.setContentView(bottomSheetview);

        ll_addImage = dialog.findViewById(R.id.ll_addImage);
        ll_addImage.setOnClickListener(new View.OnClickListener() {
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
        dialog
                .getWindow()
                .findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
        dialog.show();
    }

    private void openShareDialog() {
        View bottomSheetview = getLayoutInflater().inflate(R.layout.bottom_share_layout, null);

        BottomSheetDialog dialog = new BottomSheetDialog(OpenPdfActivity.this);
        dialog.setContentView(bottomSheetview);
        TextView tv_name = dialog.findViewById(R.id.tv_name);
        TextView tv_date = dialog.findViewById(R.id.tv_date);
        TextView tv_page_size = dialog.findViewById(R.id.tv_page_size);
        ImageButton ll_copy = dialog.findViewById(R.id.ll_copy);
        ImageButton ll_print = dialog.findViewById(R.id.ll_print);
        ImageButton ll_markup = dialog.findViewById(R.id.ll_markup);
        dialog
                .getWindow()
                .findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        tv_name.setText(file_path.getName());
        tv_date.setText(formatLastModifiedDate(file_path.lastModified()));
        tv_page_size.setText(pdfView.getPageCount()+" Page | "+ FileInfoUtils.getFormattedSize(file_path));

        ll_print.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                final PrintDocumentAdapter mPrintDocumentAdapter = new PrintDocumentAdapterHelper(OpenPdfActivity.this, file_path.getName(),file_path.getPath());

                PrintManager printManager = (PrintManager) OpenPdfActivity.this
                        .getSystemService(Context.PRINT_SERVICE);
                String jobName = OpenPdfActivity.this.getString(R.string.app_name) + " Document";
                if (printManager != null) {
                    printManager.print(jobName, mPrintDocumentAdapter, null);
                }
            }
        });

        ll_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add 30/11/2020 by surabhi
                dialog.dismiss();
                if(Constants.copy_flag == 0) {
                    Constants.copy_flag = 1;
                    List<FileData> fileDataListSelected = new ArrayList<>();
                    FileData fileData = new FileData();
                    fileData.setDuration(formatLastModifiedDate(file_path.lastModified()));
                    fileData.setSelected(false);
                    fileData.setFile_type("f");
                    fileData.setName(file_path.getName());
                    fileData.setFile_path(file_path);
                    fileDataListSelected.add(fileData);
                    Constants.fileDataListSelected = fileDataListSelected;
                    finish();
                }
            }
        });

        ll_markup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openAnnotateDialog();
            }
        });

        dialog.show();
    }

    private void openMoreDialog() {
        View bottomSheetview = getLayoutInflater().inflate(R.layout.bottom_more_layout, null);

        BottomSheetDialog dialog = new BottomSheetDialog(OpenPdfActivity.this);
        dialog.setContentView(bottomSheetview);
        TextView tv_name = dialog.findViewById(R.id.tv_name);
        TextView tv_date = dialog.findViewById(R.id.tv_date);
        TextView tv_page_size = dialog.findViewById(R.id.tv_page_size);
        ImageButton ll_share = dialog.findViewById(R.id.ll_share);
        ImageButton ll_rename = dialog.findViewById(R.id.ll_rename);
        ImageButton ll_edit = dialog.findViewById(R.id.ll_edit);
        ImageButton ll_print = dialog.findViewById(R.id.ll_print);
        ImageButton ll_duplicate = dialog.findViewById(R.id.ll_duplicate);
        ImageButton ll_delete = dialog.findViewById(R.id.ll_delete);
        dialog
                .getWindow()
                .findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        tv_name.setText(file_path.getName());
        tv_date.setText(formatLastModifiedDate(file_path.lastModified()));
        tv_page_size.setText(pdfView.getPageCount()+" Page | "+FileInfoUtils.getFormattedSize(file_path));

        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dialog.dismiss();
            }
        });

        ll_duplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String file_name = file_path.getName();
                file_name = file_name.substring(0, file_name.length() - 4);

                String dir = file_path.getParent();
                Random generator = new Random();
                int n = 100;
                n = generator.nextInt(n);
                file_name = file_name+"_copy"+n+".pdf";
                File dest_file = new File(dir+"/"+file_name);
                try {
                    copy(file_path, dest_file);
                    //Toast.makeText(OpenPdfActivity.this, "File created Successfully", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ll_print.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                final PrintDocumentAdapter mPrintDocumentAdapter = new PrintDocumentAdapterHelper(OpenPdfActivity.this, file_path.getName(),file_path.getPath());

                PrintManager printManager = (PrintManager) OpenPdfActivity.this
                        .getSystemService(Context.PRINT_SERVICE);
                String jobName = OpenPdfActivity.this.getString(R.string.app_name) + " Document";
                if (printManager != null) {
                    printManager.print(jobName, mPrintDocumentAdapter, null);
                }
            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                        Toast.makeText(OpenPdfActivity.this, "Share", Toast.LENGTH_LONG).show();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
//                        Uri uri = Uri.fromFile(file_path.getFile_path());

                Uri fileUri = FileProvider.getUriForFile(OpenPdfActivity.this,
                        OpenPdfActivity.this.getPackageName() + ".provider", file_path);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                OpenPdfActivity.this.startActivity(Intent.createChooser(shareIntent, "Share"));
                dialog.dismiss();
            }
        });

        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(OpenPdfActivity.this)
                        // set message, title, and icon
                        .setMessage("Are you sure want to delete?")

                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface d, int whichButton) {
                                //your deleting code
                                mDatabaseHelper.deleteRecord(file_path.getName());
                                file_path.delete();
                                d.dismiss();
                                dialog.dismiss();
                                finish();
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

        ll_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(OpenPdfActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dlog_rename_layout);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
                EditText et_file_name = (EditText) dialog.findViewById(R.id.et_file_name);
                TextView tv_rename = (TextView) dialog.findViewById(R.id.tv_rename);

                String file_name = file_path.getName();
                file_name = file_name.substring(0, file_name.length() - 4);

                et_file_name.setText(file_name);
                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                String dir = file_path.getParent();

                tv_rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fname_new = et_file_name.getText().toString();
                        if(fname_new.contains(".pdf")) {
                            fname_new = fname_new.replace(".pdf", "");
                        } else if(fname_new.contains(".")) {
                            fname_new = fname_new.replace(".", "");
                        }

                        if(!fname_new.isEmpty()){
                            File from = new File(dir,file_path.getName());
                            File to = new File(dir,fname_new+".pdf");
                            if(from.exists())
                                from.renameTo(to);
                            mDatabaseHelper.updateRecord(fname_new+".pdf", to.toString(), from.toString());

                            tv_name.setText(fname_new+".pdf");
                            tv_file_name.setText(fname_new+".pdf");
                            file_path = to;
                            dialog.dismiss();
                        } else {
                            et_file_name.setError("Please enter file name");
                        }
                    }
                });

                dialog.show();
            }
        });
        dialog.show();
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //Toast.makeText(getApplicationContext(),String.valueOf(annotate_flag),Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(OpenPdfActivity.this, MainActivity.class);
//        startActivity(intent);
        if(annotate_flag == 1) {
            AlertDialog ad = new AlertDialog.Builder(OpenPdfActivity.this)
                    // set message, title, and icon
                    .setMessage("Are you sure want to continue without saving?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int whichButton) {
                            //your deleting code
                            deleteImgesBack();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            editImageAdapter.saveImage();
                            saveImagestoPDF();
                            //deleteImgesBack();
                        }
                    })
                    .create();
            ad.show();
        } else {
            deleteImgesBack();
        }

    }

    private void deleteImgesBack() {
        if (mOutputFilePaths != null) {
            if (mOutputFilePaths.size() > 0) {
                for (int i = 0; i < mOutputFilePaths.size(); i++) {
                    new File(mOutputFilePaths.get(i)).delete();
                }
            }
        }
        finish();
    }

     private String formatLastModifiedDate(long modifyDate) {
        Date lastModified = new Date(modifyDate);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDateString = formatter.format(lastModified);
        return formattedDateString;
    }

    @Override
    public void onPdfReorderStarted() {

    }

    @Override
    public void onPdfReorderCompleted(List<Bitmap> bitmaps) {
        com.example.pdfeditormadtpeeps.Activity.RearrangePdfPages.mImages = new ArrayList<>(bitmaps);
        bitmaps.clear(); //releasing memory
        Intent intent = new Intent(this, com.example.pdfeditormadtpeeps.Activity.RearrangePdfPages.class);
        intent.putExtra("pdf_name", file_path.getName());
        startActivityForResult(intent,
                INTENT_REQUEST_REARRANGE_PDF);
    }

    @Override
    public void onPdfReorderFailed() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        super.onActivityResult(requestCode, resultCode, data);
        mIsButtonAlreadyClicked = false;
        if (data == null || resultCode != RESULT_OK)
            return;
        if (requestCode == INTENT_REQUEST_REARRANGE_PDF) {
            String pages = data.getStringExtra(Constants.RESULT);
            boolean sameFile = data.getBooleanExtra("SameFile", false);
            if (mPath == null)
                return;
            String outputPath;

            outputPath = setPath(pages);
//            Log.d("PATH", mPath+"\n"+outputPath);

            if (!sameFile) {
                if (mPDFUtils.reorderRemovePDF(mPath, outputPath, pages)) {
                    file_path =  new File(outputPath);
                    mPath = file_path.getPath();
                    tv_file_name.setText(file_path.getName());
                    pdfView.fromFile(file_path)
                            .defaultPage(pdfView.getPageCount())
                            .enableAnnotationRendering(true)
                            .scrollHandle(new DefaultScrollHandle(OpenPdfActivity.this))
                            .spacing(10) // in dp
                            .onPageChange(OpenPdfActivity.this)
                            .load();
                }
            } else {
                StringUtils.getInstance().showSnackbar(this, R.string.file_order);
            }
            mPath = outputPath;
            Log.d("REORDER", mPath);
        }

        else if (requestCode == INTENT_REQUEST_GET_IMAGES) {
            mImagesUri.clear();
            mUnarrangedImagesUri.clear();
            mImagesUri.addAll(Matisse.obtainPathResult(data));
            mUnarrangedImagesUri.addAll(mImagesUri);
            if (mImagesUri.size() > 0) {
            Log.d("PATHIMAGES", mPath+"\n"+file_path+"\n");
                String timeStamp = new SimpleDateFormat("ddmmyyyyhhmmss").format(new
                        Date());
                String dir = file_path.getParent();
                File to = new File(dir,file_path.getName());
                File from = new File(dir,timeStamp+".pdf");
                String outputPath = from.getPath();
                mPDFUtils.addImagesToPdf(mPath, outputPath, mImagesUri);
//                pdfView.fromFile(new File(file_path.getParent() + "/" + timeStamp + ".pdf"))
//                        .defaultPage(0)
//                        .enableAnnotationRendering(true)
//                        .scrollHandle(new DefaultScrollHandle(OpenPdfActivity.this))
//                        .spacing(10) // in dp
//                        .load();
                file_path.delete();
                if(from.exists())
                    from.renameTo(to);

                file_path = to;
                finish();
                Intent intent = new Intent(OpenPdfActivity.this, OpenPdfActivity.class);
                intent.putExtra("pdf_name", file_path.toString());
                startActivity(intent);
                finish();
                mImagesUri.clear();
            }
            else {
                StringUtils.getInstance().showSnackbar(OpenPdfActivity.this, R.string.no_images_selected);
            }
        }

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
        ImageUtils.selectImages(OpenPdfActivity.this, INTENT_REQUEST_GET_IMAGES);
    }


    private String setPath(String pages) {
        //add 30/11/2020 by surabhi
        String outputPath;
//        outputPath = mPath;
        outputPath = mPath.replace(getString(R.string.pdf_ext),
                "_edited" + getString(R.string.pdf_ext));
//        if (pages.length() > 50) {
//
//        } else {
//            outputPath = mPath.replace(getString(R.string.pdf_ext),
//                    "_edited" + pages + getString(R.string.pdf_ext));
//
//        }
        return outputPath;
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @Override
    public void onClick(View v) {
        eraser_flag = 0;
        Button button = (Button) v;

        // clear state
        btn_pink.setSelected(false);
        btn_pink.setPressed(false);
        btn_blue.setSelected(false);
        btn_blue.setPressed(false);
        btn_orange.setSelected(false);
        btn_orange.setPressed(false);
        btn_rama.setSelected(false);
        btn_rama.setPressed(false);
        btn_purple.setSelected(false);
        btn_purple.setPressed(false);

        // change state
        button.setSelected(true);
        button.setPressed(false);

        if (v.getId() == R.id.btn_pink){
            drawColor = 0xF32269;
            editImageAdapter.onColorChanged(drawColor);


        }
        else if (v.getId() == R.id.btn_blue){
            drawColor = 0x1937DD;
            editImageAdapter.onColorChanged(drawColor);

        }
        else if (v.getId() == R.id.btn_orange){
            drawColor = 0xEA4917;
            editImageAdapter.onColorChanged(drawColor);

        }
        else if (v.getId() == R.id.btn_rama){
            drawColor = 0x47CABE;
            editImageAdapter.onColorChanged(drawColor);

        }
        else if (v.getId() == R.id.btn_purple){
            drawColor = 0xCC29E8;
            editImageAdapter.onColorChanged(drawColor);

            // Toast.makeText(getApplicationContext(),color,Toast.LENGTH_LONG).show();
        }
    }




    @Override
    public void resetView() {

    }

    @Override
    public void extractionStarted() {
        dialog.setMessage("Please wait...");
        dialog.show();
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
        from = null;
        to = null;
    }

    @Override
    public void updateView(int imageCount, ArrayList<String> outputFilePaths) {
        dialog.dismiss();

        mOutputFilePaths = outputFilePaths;
        if(pdf_imag_flag == 1) {
            pdfView.setVisibility(View.GONE);
            rv_images.setVisibility(View.VISIBLE);
            rl_viewpager.setVisibility(View.GONE);
            tv_done.setVisibility(View.GONE);
            if (mOutputFilePaths.size() > 0) {
                ImagePdfAdapter mAdapter = new ImagePdfAdapter(this, mOutputFilePaths);
                rv_images.setAdapter(mAdapter);

                 editImageAdapter = new EditImageAdapter(this, mOutputFilePaths);
                 viewPager.setAdapter(editImageAdapter);
            }
        } else if(edit_imag_flag == 1) {
            pdfView.setVisibility(View.GONE);
            rv_images.setVisibility(View.GONE);
            rl_viewpager.setVisibility(View.VISIBLE);
            tv_done.setVisibility(View.VISIBLE);

            if (mOutputFilePaths.size() > 0) {
                editImageAdapter = new EditImageAdapter(this, mOutputFilePaths);
                viewPager.setAdapter(editImageAdapter);
            }
        }

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Toast.makeText(getApplicationContext(), pageNumber+"", Toast.LENGTH_LONG).show();
//                pageNumber = position;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });





        viewPager.setCurrentItem(pageNumber);
        if(viewPager.getCurrentItem() == 0) {
            iv_next.setVisibility(View.VISIBLE);
            iv_prev.setVisibility(View.GONE);
        } else if(viewPager.getCurrentItem() == mOutputFilePaths.size()-1) {
            iv_next.setVisibility(View.GONE);
            iv_prev.setVisibility(View.VISIBLE);
        } else {
            iv_next.setVisibility(View.VISIBLE);
            iv_prev.setVisibility(View.VISIBLE);
        }
        if(mOutputFilePaths.size() == 1) {
            iv_next.setVisibility(View.GONE);
            iv_prev.setVisibility(View.GONE);
        }

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


    @Override
    public void onPDFCreationStarted() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onPDFCreated(boolean success, String path) {
        if (!success) {
            StringUtils.getInstance().showSnackbar(this, R.string.snackbar_folder_not_created);
            return;
        }
//        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
//        Toast.makeText(getApplicationContext(), "PDF Saved!", Toast.LENGTH_LONG).show();
//        StringUtils.getInstance().getSnackbarwithAction(this, R.string.snackbar_pdfCreated)
//                .setAction(R.string.snackbar_viewAction,
//                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        file_path.delete();
        if(from.exists())
            from.renameTo(to);


        file_path = to;
        mPath = file_path.getPath();
        pdfView.fromFile(file_path)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageChange(this)
                .load();
        rl_viewpager.setVisibility(View.GONE);
        pdfView.setVisibility(View.VISIBLE);
        Log.d("IMAGEPATH", mPath);
        resetValuesf();
        if(annotate_flag == 1) {
            deleteImgesBack();
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        Log.d("PAGE", pageNumber+"");
    }
}