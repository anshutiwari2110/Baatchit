package com.anshutiwari.baatchit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import viewModel.OnBoardAdapter;


public class OnBoardActivity extends AppCompatActivity {

    private ViewPager slideViewPager;
    private OnBoardAdapter onBoardAdapter;
    private LinearLayout mLlDots;
    private TextView[] mDots;
    private Button mBtnBack;
    private Button mBtnNext;
    private TextView mBtnSkip;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        slideViewPager = findViewById(R.id.view_pager_onboard);
        mLlDots = findViewById(R.id.ll_dots);
        mBtnBack = findViewById(R.id.btn_slide_back);
        mBtnNext = findViewById(R.id.btn_slide_next);
        mBtnSkip = findViewById(R.id.tv_skip_onboard);


        onBoardAdapter = new OnBoardAdapter(this);
        slideViewPager.setAdapter(onBoardAdapter);
        addDotsIndicator(0);
        slideViewPager.addOnPageChangeListener(viewListener);

        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnBoardActivity.this,LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage == mDots.length - 1) {
                    startActivity(new Intent(OnBoardActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    slideViewPager.setCurrentItem(mCurrentPage + 1);
                }
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        mLlDots.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            mLlDots.addView(mDots[i]);
        }
        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.blue));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;

            if (position == 0) {
                mBtnBack.setEnabled(false);
                mBtnNext.setEnabled(true);
                mBtnBack.setVisibility(View.INVISIBLE);

                mBtnBack.setText("");
                mBtnNext.setText("Next");

            } else if (position == mDots.length - 1) {
                mBtnBack.setEnabled(true);
                mBtnNext.setEnabled(true);
                mBtnNext.setVisibility(View.VISIBLE);

                mBtnBack.setText("Back");
                mBtnNext.setText("Finish");

            } else {
                mBtnBack.setEnabled(true);
                mBtnNext.setEnabled(true);
                mBtnBack.setVisibility(View.VISIBLE);

                mBtnBack.setText("Back");
                mBtnNext.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}