package com.example.pdfeditormadtpeeps.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdfeditormadtpeeps.Activity.FolderActivity;
import com.example.pdfeditormadtpeeps.Activity.MainActivity;
import com.example.pdfeditormadtpeeps.Activity.OpenPdfActivity;
import com.example.pdfeditormadtpeeps.Interface.OnSelectionListner;
import com.example.pdfeditormadtpeeps.Model.FileData;
import com.example.pdfeditormadtpeeps.R;
import com.example.pdfeditormadtpeeps.Utility.FileInfoUtils;
import com.example.pdfeditormadtpeeps.Utility.PDFEncryptionUtility;
import com.example.pdfeditormadtpeeps.Utility.PDFUtils;
import com.example.pdfeditormadtpeeps.Utility.PrintDocumentAdapterHelper;
import com.example.pdfeditormadtpeeps.Utility.StringUtils;
import com.example.pdfeditormadtpeeps.Utility.ZipManager;
import com.example.pdfeditormadtpeeps.database.DatabaseHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RecentFileadapter extends
        RecyclerView.Adapter<RecentFileadapter.ViewHolder> {

    private final PDFUtils mPDFUtils;
    public List<FileData> fileDataList;
    String display_type = "row";
    String btn_select="0";
    Activity context;
    private final ArrayList<Integer> mSelectedFiles;
    private final DatabaseHelper mDatabaseHelper;
    private ArrayList<FileData> arrayList;
    OnSelectionListner onSelectionListner;

    public RecentFileadapter(Activity context, List<FileData> fileDataList, String display_type, String btn_select, OnSelectionListner onSelectionListner) {
        this.fileDataList = fileDataList;
        this.display_type = display_type;
        this.btn_select = btn_select;
        this.context = context;
        arrayList = new ArrayList<>();
        this.onSelectionListner = onSelectionListner;
        this.arrayList.addAll(fileDataList);
        mSelectedFiles = new ArrayList<>();
        mDatabaseHelper = new DatabaseHelper(context);
        mPDFUtils = new PDFUtils(context);
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View itemLayoutView;

        if(display_type.equals("row")) {
            itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_file_layout, null);
        } else {
            itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_file_grid_layout, null);
        }

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        final int pos = position;
        viewHolder.tv_name.setText(fileDataList.get(position).getName());
        viewHolder.tv_duration.setText(fileDataList.get(position).getDuration());
        Log.d("BTN", btn_select);

        if(fileDataList.get(position).getFile_type().equals("d")){
            viewHolder.iv_type.setImageResource(R.drawable.group_16874);
            viewHolder.iv_more.setVisibility(View.VISIBLE);
            viewHolder.cb_select.setVisibility(View.GONE);
        } else {
            viewHolder.iv_type.setImageResource(R.drawable.pdf);
            if(btn_select.equals("1")) {
                viewHolder.cb_select.setVisibility(View.VISIBLE);
                viewHolder.iv_more.setVisibility(View.GONE);
            } else {
                viewHolder.cb_select.setVisibility(View.GONE);
                viewHolder.iv_more.setVisibility(View.VISIBLE);
            }
        }

        viewHolder.iv_more.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                View bottomSheetview = context.getLayoutInflater().inflate(R.layout.bottom_more_layout, null);

                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.setContentView(bottomSheetview);
                TextView tv_name = dialog.findViewById(R.id.tv_name);
                TextView tv_date = dialog.findViewById(R.id.tv_date);
                TextView tv_pwd = dialog.findViewById(R.id.tv_pwd);
                TextView tv_page_size = dialog.findViewById(R.id.tv_page_size);
                ImageView iv_type = dialog.findViewById(R.id.iv_type);
                ImageButton ll_share = dialog.findViewById(R.id.ll_share);
                ImageButton ll_rename = dialog.findViewById(R.id.ll_rename);
                ImageButton ll_edit = dialog.findViewById(R.id.ll_edit);
                ImageButton ll_print = dialog.findViewById(R.id.ll_print);
                ImageButton ll_duplicate = dialog.findViewById(R.id.ll_duplicate);
                ImageButton ll_delete = dialog.findViewById(R.id.ll_delete);
                LinearLayoutCompat ll_pwd = dialog.findViewById(R.id.ll_pwd);
                ImageButton ib_pwd = dialog.findViewById(R.id.ib_pwd);
                TextView tv_edit = dialog.findViewById(R.id.tv_edit);
                TextView tv_print = dialog.findViewById(R.id.tv_print);
                dialog.getWindow()
                        .findViewById(R.id.design_bottom_sheet)
                        .setBackgroundResource(android.R.color.transparent);
                tv_name.setText(fileDataList.get(pos).getName());
                tv_date.setText(fileDataList.get(pos).getDuration());
                if(fileDataList.get(position).getFile_type().equals("d")){
                    iv_type.setImageResource(R.drawable.group_16874);
                    tv_edit.setText("Zip");
                    tv_print.setText("Mail");
                    ll_pwd.setVisibility(View.GONE);
                } else {
                    iv_type.setImageResource(R.drawable.pdf);
                    ll_pwd.setVisibility(View.VISIBLE);
                }
                PDFEncryptionUtility pdfEncryptionUtility = new PDFEncryptionUtility(context);

                String mPath = fileDataList.get(pos).getFile_path().toString();
                if (!mPDFUtils.isPDFEncrypted(mPath)) {
                    ib_pwd.setImageResource(R.drawable.ic_baseline_lock_24);
                } else {
                    ib_pwd.setImageResource(R.drawable.ic_baseline_lock_open_24);
                }

                try {
                    ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(fileDataList.get(pos).getFile_path(), ParcelFileDescriptor.MODE_READ_ONLY);
                    PdfRenderer pdfRenderer = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                        tv_page_size.setText(pdfRenderer.getPageCount()+" Page | "+ FileInfoUtils.getFormattedSize(fileDataList.get(pos).getFile_path()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tv_page_size.setText(FileInfoUtils.getFormattedSize(fileDataList.get(pos).getFile_path()));
                }

                ib_pwd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mPDFUtils.isPDFEncrypted(mPath)) {
                            pdfEncryptionUtility.setPassword(mPath, null);
                        } else {
                            pdfEncryptionUtility.removePassword(mPath, null);
                        }
                        notifyDataSetChanged();
                    }
                });


                ll_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(tv_edit.getText().toString().equals("Zip")){
                            //ZipManager zipManager = new ZipManager();
                            List<File> files;
                            files = getListFiles(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/mypdf/"+fileDataList.get(pos).getName())));
                            String[] s = new String[files.size()];
                            for (int i=0;i<files.size();i++){
                                Log.d("FILE_NAME", files.get(i).getName());
                                s[i] = files.get(i).getPath();
                            }
                            ZipManager zipManager = new ZipManager();
                            zipManager.zip(s, fileDataList.get(pos).getFile_path().toString() +".zip");
                            dialog.dismiss();
                            Toast.makeText(context, "Zip created at "+fileDataList.get(pos).getFile_path().toString() +".zip", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Intent intent = new Intent(context, OpenPdfActivity.class);
                            intent.putExtra("pdf_name", fileDataList.get(pos).getFile_path().toString());
                            context.startActivity(intent);
                            dialog.dismiss();
                        }
                    }
                });

                ll_duplicate.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view) {
                        String file_name = fileDataList.get(pos).getName();
                        if(fileDataList.get(position).getFile_type().equals("d")){
                            String dir = fileDataList.get(pos).getFile_path().getParent();
                            Random generator = new Random();
                            int n = 100;
                            n = generator.nextInt(n);
                            file_name = file_name+"_copy"+n;
                            File dest_file = new File(dir+"/"+file_name);
                            copyFileOrDirectory(fileDataList.get(pos).getFile_path().toString(), dest_file.toString());
                            FileData fileData = new FileData();
                            fileData.setName(file_name);
                            fileData.setFile_type("d");
                            fileData.setFile_path(dest_file);
                            fileData.setDuration(formatLastModifiedDate(dest_file.lastModified()));
                            fileDataList.add(fileData);
                            Toast.makeText(context, "Folder created Successfully", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                        else {
                            file_name = file_name.substring(0, file_name.length() - 4);
                            String dir = fileDataList.get(pos).getFile_path().getParent();
                            Random generator = new Random();
                            int n = 100;
                            n = generator.nextInt(n);
                            file_name = file_name + "_copy" + n + ".pdf";
                            File dest_file = new File(dir + "/" + file_name);
                            try {
                                copy(fileDataList.get(pos).getFile_path(), dest_file);
                                FileData fileData = new FileData();
                                fileData.setName(file_name);
                                fileData.setFile_type("f");
                                fileData.setFile_path(dest_file);
                                fileData.setDuration(formatLastModifiedDate(dest_file.lastModified()));
                                fileDataList.add(fileData);
                                Toast.makeText(context, "File created Successfully", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        notifyDataSetChanged();
                        ((MainActivity)context).refreshPage();
                    }
                });

                ll_print.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view) {
                        if (tv_print.getText().toString().equals("Mail")){
                            //Toast.makeText(context,"Mail",Toast.LENGTH_LONG).show();
                            List<File> files;
                            files = getListFiles(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/mypdf/"+fileDataList.get(pos).getName())));
                            String[] s = new String[files.size()];
                            for (int i=0;i<files.size();i++){
                                Log.d("FILE_NAME", files.get(i).getName());
                                s[i] = files.get(i).getPath();
                            }
                            ZipManager zipManager = new ZipManager();
                            zipManager.zip(s, fileDataList.get(pos).getFile_path().toString() +".zip");

                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setPackage("com.google.android.gm");
                            i.setType("application/zip");
                            Uri a = FileProvider.getUriForFile(context,
                                    context.getPackageName() + ".provider", new File(fileDataList.get(pos).getFile_path().toString()+".zip"));

//                            Uri a = Uri.fromFile ( new File(fileDataList.get(pos).getFile_path().toString() +".zip"));
                            i.putExtra(Intent.EXTRA_STREAM, a);
                            try {
                               context.startActivity(Intent.createChooser(i, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }
                           // Intent intent = new Intent(Intent.EXTRA_EMAIL);
                           // intent.setType("application/zip");
                           // Uri a = Uri.fromFile ( new File(fileDataList.get(pos).getFile_path().toString() +".zip"));
                           // intent.putExtra(Intent.EXTRA_STREAM, a);
                           // context.startActivity(Intent.createChooser(intent, "Send email..."));
                        }
                        else {
                            final PrintDocumentAdapter mPrintDocumentAdapter = new PrintDocumentAdapterHelper(context, fileDataList.get(pos).getName(),fileDataList.get(pos).getFile_path().getPath());
                            PrintManager printManager = (PrintManager) context
                                    .getSystemService(Context.PRINT_SERVICE);
                            String jobName = context.getString(R.string.app_name) + " Document";
                            if (printManager != null) {
                                printManager.print(jobName, mPrintDocumentAdapter, null);
                            }
                        }
                    }
                });
                ll_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(fileDataList.get(position).getFile_type().equals("d")){
                            List<File> files;
                            files = getListFiles(new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/mypdf/"+fileDataList.get(pos).getName())));
                            String[] s = new String[files.size()];
                            for (int i=0;i<files.size();i++){
                                Log.d("FILE_NAME", files.get(i).getName());
                                s[i] = files.get(i).getPath();
                            }
                            ZipManager zipManager = new ZipManager();
                            zipManager.zip(s, fileDataList.get(pos).getFile_path().toString() +".zip");

                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("application/zip");
//                            Uri outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, files);
                            Uri a = FileProvider.getUriForFile(context,
                                    context.getPackageName() + ".provider", new File(fileDataList.get(pos).getFile_path().toString()+".zip"));
                            i.putExtra(Intent.EXTRA_STREAM, a);
                            try {
                                context.startActivity(Intent.createChooser(i, "Send file..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                       else {
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("application/pdf");
                            Uri fileUri = FileProvider.getUriForFile(context,
                                    context.getPackageName() + ".provider", fileDataList.get(pos).getFile_path());
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                            context.startActivity(Intent.createChooser(shareIntent, "Share"));
                            dialog.dismiss();
                        }
                    }
                });

                ll_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(context)
                                // set message, title, and icon
                                .setMessage("Are you sure want to delete?")

                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    public void onClick(DialogInterface d, int whichButton) {
                                        //your deleting code
                                        mDatabaseHelper.deleteRecord(fileDataList.get(pos).getName());
                                        if(fileDataList.get(position).getFile_type().equals("d")){
                                            deleteFiles(fileDataList.get(pos).getFile_path().toString());
                                        } else {
                                            fileDataList.get(pos).getFile_path().delete();
                                        }
                                        fileDataList.remove(pos);
                                        notifyDataSetChanged();
                                        ((MainActivity)context).refreshPage();
                                        d.dismiss();
                                        dialog.dismiss();
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
                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.dlog_rename_layout);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
                        EditText et_file_name = (EditText) dialog.findViewById(R.id.et_file_name);
                        TextView tv_rename = (TextView) dialog.findViewById(R.id.tv_rename);


                        String  file_name = fileDataList.get(pos).getName();
                        if(fileDataList.get(position).getFile_type().equals("d")){
                            et_file_name.setText(file_name);
                        }
                        else {
                            file_name = file_name.substring(0, file_name.length() - 4);
                            et_file_name.setText(file_name);
                        }


                        iv_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        String dir = fileDataList.get(pos).getFile_path().getParent();

                        tv_rename.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onClick(View view) {
                                String fname_new = et_file_name.getText().toString();


                                if (fileDataList.get(position).getFile_type().equals("d")) {

                                    if (!fname_new.isEmpty()) {
                                        File from = new File(dir, fileDataList.get(pos).getName());
                                        File to = new File(dir, fname_new);
                                        if (from.exists())
                                            from.renameTo(to);

                                        tv_name.setText(fname_new);
                                        fileDataList.get(pos).setName(fname_new);
                                        fileDataList.get(pos).setFile_path(to);
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                    } else {
                                        et_file_name.setError("Please enter file name");
                                    }
                                } else {
                                    if (fname_new.contains(".pdf")) {
                                        fname_new = fname_new.replace(".pdf", "");
                                    } else if (fname_new.contains(".")) {
                                        fname_new = fname_new.replace(".", "");
                                    }

                                    if (!fname_new.isEmpty()) {
                                        File from = new File(dir, fileDataList.get(pos).getName());
                                        File to = new File(dir, fname_new + ".pdf");
                                        if (from.exists())
                                            from.renameTo(to);
                                        Log.d("RENAME_PATH", fname_new+"\n"+to+from);
                                        mDatabaseHelper.updateRecord(fname_new+".pdf", to.toString(), from.toString());

                                        tv_name.setText(fname_new + ".pdf");
                                        fileDataList.get(pos).setName(fname_new + ".pdf");
                                        fileDataList.get(pos).setFile_path(to);
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                    } else {
                                        et_file_name.setError("Please enter file name");
                                    }
                                    ((MainActivity)context).refreshPage();
                                }
                            }
                        });
                        dialog.show();
                    }
                });
                dialog.show();
            }
        });

        viewHolder.cb_select.setTag(position);
        viewHolder.itemView.setTag(viewHolder.cb_select);
        viewHolder.cb_select.setChecked(fileDataList.get(pos).getSelected());

        viewHolder.cb_select.setOnClickListener(new View.OnClickListener() {                                       //Add array selected image in array list
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) viewHolder.cb_select.getTag();
                if (fileDataList.get(pos).getSelected()) {
                    fileDataList.get(pos).setSelected(false);
                } else {
                    fileDataList.get(pos).setSelected(true);
                }
                onSelectionListner.onSelected();
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(fileDataList.get(position).getFile_type().equals("d")){
                    Intent intent = new Intent(context, FolderActivity.class);
                    intent.putExtra("folder_name", fileDataList.get(pos).getName());
                    context.startActivity(intent);
                } else {
                    if(btn_select.equals("1")) {
                        CheckBox cb = (CheckBox) v.getTag();
                        if (fileDataList.get(pos).getSelected()) {
                            fileDataList.get(pos).setSelected(false);
                            cb.setChecked(false);
                        } else {
                            fileDataList.get(pos).setSelected(true);
                            cb.setChecked(true);
                        }
                    } else {
                        try {
                            mDatabaseHelper.deleteRecord(fileDataList.get(pos).getName());
                            mDatabaseHelper.insertRecord(String.valueOf(fileDataList.get(pos).getFile_path()),
                            fileDataList.get(pos).getDuration(), fileDataList.get(pos).getFile_type(), fileDataList.get(pos).getName());
                            Intent intent = new Intent(context, OpenPdfActivity.class);
                            intent.putExtra("pdf_name", fileDataList.get(pos).getFile_path().toString());
                            context.startActivity(intent);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        });
    }
    List<FileData> fileDataArrayList = new ArrayList<>();
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

    public static void deleteFiles(String path) {

        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) { }
        }
    }


    private static final int BUFFER = 2048;

    public void zip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
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

    private String formatLastModifiedDate(long modifyDate) {
        Date lastModified = new Date(modifyDate);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDateString = formatter.format(lastModified);
        return formattedDateString;
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return fileDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name, tv_duration;
        public ImageView iv_more, iv_type;
        public CheckBox cb_select;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemLayoutView.setLayoutParams(lp);
            tv_name = (TextView) itemLayoutView
                    .findViewById(R.id.tv_name);
            iv_more = (ImageView) itemLayoutView.findViewById(R.id.iv_more);
            tv_duration = (TextView) itemLayoutView.findViewById(R.id.tv_duration);
            iv_type = (ImageView) itemLayoutView.findViewById(R.id.iv_type);
            cb_select = (CheckBox) itemLayoutView.findViewById(R.id.cb_select);
        }
    }


    // filter name in Search Bar
    public void filter(String characterText) {
        characterText = characterText.toLowerCase(Locale.getDefault());
        fileDataList.clear();
        if (characterText.length() == 0) {
            fileDataList.addAll(arrayList);
        } else {
            fileDataList.clear();
            for (FileData fileData: arrayList) {
                if (fileData.getName().toLowerCase(Locale.getDefault()).contains(characterText)) {
                    fileDataList.add(fileData);
                }
            }
        }
        notifyDataSetChanged();
    }

    // filter name in Search Bar
    public void filterList(ArrayList<FileData> filterdNames) {
        this.fileDataList = filterdNames;
        notifyDataSetChanged();
    }

    // method to access in activity after updating selection
    public List<FileData> getSelectedFiles() {
        return fileDataList;
    }
}
