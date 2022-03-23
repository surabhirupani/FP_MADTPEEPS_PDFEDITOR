package com.example.pdfeditormadtpeeps.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.pdfeditormadtpeeps.R;
import com.example.pdfeditormadtpeeps.Utility.Constants;
import com.example.pdfeditormadtpeeps.Utility.DialogUtils;
import com.example.pdfeditormadtpeeps.adapter.RearrangePdfAdapter;
import com.example.pdfeditormadtpeeps.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RearrangePdfPages extends AppCompatActivity implements RearrangePdfAdapter.OnClickListener {
    ImageView iv_back;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    public static ArrayList<Bitmap> mImages;
    private RearrangePdfAdapter mRearrangeImagesAdapter;
    private SharedPreferences mSharedPreferences;
    private ArrayList<Integer> mSequence, mInitialSequence;
    TextView tv_file_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rearrange_pdf_pages);

        iv_back = findViewById(R.id.iv_back);
        tv_file_name = findViewById(R.id.tv_file_name);
        tv_file_name.setText(getIntent().getStringExtra("pdf_name"));
        ButterKnife.bind(this);

        mSequence = new ArrayList<>();
        mInitialSequence = new ArrayList<>();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if (mImages == null || mImages.size() < 1) {
            finish();
        } else
            initRecyclerView(mImages);
    }

    private void initRecyclerView(ArrayList<Bitmap> images) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        mRearrangeImagesAdapter = new RearrangePdfAdapter(this, images, this);
        recyclerView.setAdapter(mRearrangeImagesAdapter);
        mSequence = new ArrayList<>();
        for ( int i = 0; i < images.size(); i++) {
            mSequence.add(i + 1);
        }
        mInitialSequence.addAll(mSequence);
    }

    /**
     * Swaps values at given positions
     * @param pos1 - first value
     * @param pos2 - second value
     */
    private void swap(int pos1, int pos2) {
        if (pos1 >= mSequence.size())
            return;
        int val = mSequence.get(pos1);
        mSequence.set(pos1, mSequence.get(pos2));
        mSequence.set(pos2, val);
    }

    @Override
    public void onUpClick(int position) {
        mImages.add(position - 1, mImages.remove(position));
        mRearrangeImagesAdapter.positionChanged(mImages);
        swap(position, position - 1);
    }

    @Override
    public void onDownClick(int position) {
        mImages.add(position + 1, mImages.remove(position));
        mRearrangeImagesAdapter.positionChanged(mImages);
        swap(position, position + 1);
    }

    @Override
    public void onRemoveClick(int position) {
        if (mSharedPreferences.getBoolean(Constants.CHOICE_REMOVE_IMAGE, false)) {
            mImages.remove(position);
            mRearrangeImagesAdapter.positionChanged(mImages);
            mSequence.remove(position);
        } else {
            MaterialDialog.Builder builder = DialogUtils.getInstance().createWarningDialog(this,
                    R.string.remove_page_message);
            builder.checkBoxPrompt(getString(R.string.dont_show_again), false, null)
                    .onPositive((dialog, which) -> {
                        if (dialog.isPromptCheckBoxChecked()) {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putBoolean(Constants.CHOICE_REMOVE_IMAGE, true);
                            editor.apply();
                        }
                        mImages.remove(position);
                        mRearrangeImagesAdapter.positionChanged(mImages);
                        mSequence.remove(position);
                    })
                    .show();
        }
    }

    private void passUris() {
        Intent returnIntent = new Intent();
        StringBuilder result = new StringBuilder();
        for ( int x : mSequence)
            result.append(x).append(",");
        returnIntent.putExtra(Constants.RESULT, result.toString());
        boolean sameFile = mInitialSequence.equals(mSequence);
        returnIntent.putExtra(Constants.SAME_FILE, sameFile);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        passUris();
        super.onBackPressed();
    }


    public static Intent getStartIntent(Context context) {
        return new Intent(context, RearrangePdfPages.class);
    }
}