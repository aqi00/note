package com.example.exmexcel.dialog;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ConfirmDialogFragment extends DialogFragment {
	private static final String TAG = "ConfirmDialogFragment";
	
	private Map<String, Object> mMapParam;
	private ConfirmCallbacks mCallbacks;
	private LinearLayout mRoot;
	private int mIconId;
	private String mTitle;
	private String mMessage;
	
	public static ConfirmDialogFragment newInstance(int icon_id, String title, String message) {
		ConfirmDialogFragment frag = new ConfirmDialogFragment();
		Bundle args = new Bundle();
		args.putInt("icon_id", icon_id);
		args.putString("title", title);
		args.putString("message", message);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof ConfirmCallbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		mCallbacks = (ConfirmCallbacks) activity;
		mIconId = getArguments().getInt("icon_id");
		mTitle = getArguments().getString("title");
		mMessage = getArguments().getString("message");
	}

	public void setParam(Map<String, Object> mapParam) {
		mMapParam = mapParam;
	}

	public interface ConfirmCallbacks {
		public void onConfirmSelect(Map<String, Object> map_param);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LinearLayout.LayoutParams rootLayout = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F);
		mRoot = new LinearLayout(getActivity());
		mRoot.setOrientation(LinearLayout.VERTICAL);
		mRoot.setLayoutParams(rootLayout);

		Builder popupBuilder = new AlertDialog.Builder(getActivity());
		popupBuilder.setView(mRoot);
		if (mIconId > 0) {
			popupBuilder.setIcon(mIconId);
		}
		popupBuilder.setTitle(mTitle);
		popupBuilder.setMessage(mMessage);

		popupBuilder.setPositiveButton("确  定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mCallbacks.onConfirmSelect(mMapParam);
					}
				});

		popupBuilder.setNegativeButton("取  消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

		return popupBuilder.create();
	}

}
