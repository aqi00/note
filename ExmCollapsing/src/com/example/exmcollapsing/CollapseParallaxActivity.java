package com.example.exmcollapsing;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.example.exmcollapsing.adapter.RecyclerAdapter;

public class CollapseParallaxActivity extends AppCompatActivity {
	private CoordinatorLayout cl_main;
	private CollapsingToolbarLayout ctl_title;
	private RecyclerView rv_main;
	private String[] yearArray = {"鼠年", "牛年", "虎年", "兔年", "龙年", "蛇年",
			"马年", "羊年", "猴年", "鸡年", "狗年", "猪年"};

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collapse_parallax);
		Toolbar tl_title = (Toolbar) findViewById(R.id.tl_title);
		tl_title.setBackgroundColor(Color.RED);
		setSupportActionBar(tl_title);
		cl_main = (CoordinatorLayout) findViewById(R.id.cl_main);
		ctl_title = (CollapsingToolbarLayout) findViewById(R.id.ctl_title);
		ctl_title.setTitle("Hello World");
		
		rv_main = (RecyclerView) findViewById(R.id.rv_main);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayout.VERTICAL);
		rv_main.setLayoutManager(llm);
		RecyclerAdapter adapter = new RecyclerAdapter(this, yearArray);
		rv_main.setAdapter(adapter);
	}
    
}
