package com.example.exmcollapsing;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout.LayoutParams;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.exmcollapsing.adapter.RecyclerAdapter;

public class ScrollFlagActivity extends AppCompatActivity {
	private CoordinatorLayout cl_main;
	private CollapsingToolbarLayout ctl_title;
	private RecyclerView rv_main;
	private String[] yearArray = {"鼠年", "牛年", "虎年", "兔年", "龙年", "蛇年",
			"马年", "羊年", "猴年", "鸡年", "狗年", "猪年"};

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll_flag);
		Toolbar tl_title = (Toolbar) findViewById(R.id.tl_title);
		tl_title.setBackgroundColor(Color.YELLOW);
		setSupportActionBar(tl_title);
		cl_main = (CoordinatorLayout) findViewById(R.id.cl_main);
		ctl_title = (CollapsingToolbarLayout) findViewById(R.id.ctl_title);
		ctl_title.setTitle("滚动标志");
		initFlagSpinner();
		
		rv_main = (RecyclerView) findViewById(R.id.rv_main);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayout.VERTICAL);
		rv_main.setLayoutManager(llm);
		RecyclerAdapter adapter = new RecyclerAdapter(this, yearArray);
		rv_main.setAdapter(adapter);
	}

	private String[] descArray={
			"scroll", 
			"scroll|enterAlways", 
			"scroll|exitUntilCollapsed", 
			"scroll|enterAlways|enterAlwaysCollapsed", 
			"scroll|snap"};
	private int[] flagArray={
			LayoutParams.SCROLL_FLAG_SCROLL, 
			LayoutParams.SCROLL_FLAG_SCROLL|LayoutParams.SCROLL_FLAG_ENTER_ALWAYS,
			LayoutParams.SCROLL_FLAG_SCROLL|LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED, 
			LayoutParams.SCROLL_FLAG_SCROLL|LayoutParams.SCROLL_FLAG_ENTER_ALWAYS|LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED, 
			LayoutParams.SCROLL_FLAG_SCROLL|LayoutParams.SCROLL_FLAG_SNAP	};
	private void initFlagSpinner() {
		ArrayAdapter<String> flagAdapter = new ArrayAdapter<String>(this,
				R.layout.item_select, descArray);
		Spinner sp_style = (Spinner) findViewById(R.id.sp_flag);
		sp_style.setPrompt("请选择滚动标志");
		sp_style.setAdapter(flagAdapter);
		sp_style.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				LayoutParams params = (LayoutParams) ctl_title.getLayoutParams();
				params.setScrollFlags(flagArray[arg2]);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		sp_style.setSelection(0);
	}

}
