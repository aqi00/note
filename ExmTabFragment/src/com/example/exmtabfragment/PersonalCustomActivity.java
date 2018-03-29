package com.example.exmtabfragment;

import com.example.exmtabfragment.adapter.TabCustomAdapter;
import com.example.exmtabfragment.util.TabUtil;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class PersonalCustomActivity extends Activity {
	private GridView gv_custom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_custom);
		gv_custom = (GridView) findViewById(R.id.gv_custom);
		TabCustomAdapter adapter = new TabCustomAdapter(this, TabUtil.readTabInfo(this));
		gv_custom.setAdapter(adapter);
	}
	
}
