package com.example.exmword;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.btn_chm).setOnClickListener(this);
		findViewById(R.id.btn_umd).setOnClickListener(this);
		findViewById(R.id.btn_epub).setOnClickListener(this);
		findViewById(R.id.btn_word_text).setOnClickListener(this);
		findViewById(R.id.btn_word_html).setOnClickListener(this);
		findViewById(R.id.btn_ppt_text).setOnClickListener(this);
		findViewById(R.id.btn_ppt_html).setOnClickListener(this);
		findViewById(R.id.btn_pdf_self).setOnClickListener(this);
		findViewById(R.id.btn_pdf_stack).setOnClickListener(this);
		findViewById(R.id.btn_pdf_slider).setOnClickListener(this);
		findViewById(R.id.btn_pdf_turn).setOnClickListener(this);
		findViewById(R.id.btn_pdf_opengl).setOnClickListener(this);
		findViewById(R.id.btn_vudroid_pdf).setOnClickListener(this);
		findViewById(R.id.btn_vudroid_djvu).setOnClickListener(this);
		findViewById(R.id.btn_pdf_list).setOnClickListener(this);
		findViewById(R.id.btn_pdf_pager).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_chm) {
			Intent intent = new Intent(this, ChmActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_umd) {
			Intent intent = new Intent(this, UmdActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_epub) {
			Intent intent = new Intent(this, EpubActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_word_text) {
			Intent intent = new Intent(this, WordTextActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_word_html) {
			Intent intent = new Intent(this, WordHtmlActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_ppt_text) {
			Intent intent = new Intent(this, PptTextActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_ppt_html) {
			Intent intent = new Intent(this, PptHtmlActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_pdf_self) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Intent intent = new Intent(this, PdfSelfActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(this, "该功能需要Android5.0以上支持", 
						Toast.LENGTH_SHORT).show();
			}
		} else if (v.getId() == R.id.btn_pdf_stack) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Intent intent = new Intent(this, PdfStackActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(this, "该功能需要Android5.0以上支持", 
						Toast.LENGTH_SHORT).show();
			}
		} else if (v.getId() == R.id.btn_pdf_slider) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Intent intent = new Intent(this, PdfSliderActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(this, "该功能需要Android5.0以上支持", 
						Toast.LENGTH_SHORT).show();
			}
		} else if (v.getId() == R.id.btn_pdf_turn) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Intent intent = new Intent(this, PdfTurnActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(this, "该功能需要Android5.0以上支持", 
						Toast.LENGTH_SHORT).show();
			}
		} else if (v.getId() == R.id.btn_pdf_opengl) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Intent intent = new Intent(this, PdfOpenglActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(this, "该功能需要Android5.0以上支持", 
						Toast.LENGTH_SHORT).show();
			}
		} else if (v.getId() == R.id.btn_vudroid_pdf) {
			Intent intent = new Intent(this, VudroidPdfActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_vudroid_djvu) {
			Intent intent = new Intent(this, VudroidDjvuActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_pdf_list) {
			Intent intent = new Intent(this, PdfListActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_pdf_pager) {
			Intent intent = new Intent(this, PdfPagerActivity.class);
			startActivity(intent);
		}
	}

}
