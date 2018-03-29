package com.example.exmexcel.dialog;

import com.example.exmexcel.R;
import com.example.exmexcel.bean.PersonInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class InputDialogFragment extends DialogFragment {
	private static final String TAG = "InputDialogFragment";
	
	private LinearLayout root;
	private String mMessage;
	private InputCallbacks mCallbacks;
	private TextView tv_message;
	private EditText et_name;
	private Spinner sp_sex;
	private EditText et_age;
	private EditText et_job;
	
	public static InputDialogFragment newInstance(String message) {
		InputDialogFragment frag = new InputDialogFragment();
		Bundle args = new Bundle();
		args.putString("message", message);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMessage = getArguments().getString("message");
		mCallbacks = (InputCallbacks) activity;
	}

	public interface InputCallbacks {
		public void onInput(PersonInfo info);
	}
	
	private LinearLayout newLinearLayout(int orientation) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F);
		LinearLayout llayout = new LinearLayout(getActivity());
		llayout.setOrientation(orientation);
		llayout.setLayoutParams(params);
		return llayout;
	}
	
	private TextView newTextView(String label) {
		LinearLayout.LayoutParams item_params = new LinearLayout.LayoutParams(
				0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		TextView tv_new = new TextView(getActivity());
		tv_new.setText(label);
		tv_new.setLayoutParams(item_params);
		return tv_new;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		root = newLinearLayout(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams item_params = new LinearLayout.LayoutParams(
				0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);

		tv_message = new TextView(getActivity());
		tv_message.setText(mMessage);
		root.addView(tv_message);

		LinearLayout ll_name = newLinearLayout(LinearLayout.HORIZONTAL);
		ll_name.addView(newTextView("姓名"));
		et_name = new EditText(getActivity());
		et_name.setLayoutParams(item_params);
		ll_name.addView(et_name);
		root.addView(ll_name);

		LinearLayout ll_sex = newLinearLayout(LinearLayout.HORIZONTAL);
		ll_sex.addView(newTextView("性别"));
		sp_sex = new Spinner(getActivity(), Spinner.MODE_DIALOG);
		sp_sex.setLayoutParams(item_params);
		ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.spinner_item, PersonInfo.sexArray);
		sexAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		sp_sex.setPrompt("请选择性别");
		sp_sex.setAdapter(sexAdapter);
		sp_sex.setOnItemSelectedListener(new SexSelectedListener());
		sp_sex.setSelection(0);
		ll_sex.addView(sp_sex);
		root.addView(ll_sex);

		LinearLayout ll_age = newLinearLayout(LinearLayout.HORIZONTAL);
		ll_age.addView(newTextView("年龄"));
		et_age = new EditText(getActivity());
		et_age.setLayoutParams(item_params);
		ll_age.addView(et_age);
		root.addView(ll_age);

		LinearLayout ll_job = newLinearLayout(LinearLayout.HORIZONTAL);
		ll_job.addView(newTextView("职业"));
		et_job = new EditText(getActivity());
		et_job.setLayoutParams(item_params);
		ll_job.addView(et_job);
		root.addView(ll_job);
		
		Builder popupBuilder = new AlertDialog.Builder(getActivity());
		popupBuilder.setView(root);

		popupBuilder.setPositiveButton("确  定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						PersonInfo info = new PersonInfo();
						info.name = et_name.getText().toString();
						info.sex = mSex;
						info.age = Integer.parseInt(et_age.getText().toString());
						info.job = et_job.getText().toString();
						mCallbacks.onInput(info);
					}
				});

		return popupBuilder.create();
	}

	private int mSex;
	private class SexSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			mSex = arg2;
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

}
