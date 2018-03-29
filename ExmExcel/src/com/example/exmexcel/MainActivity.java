package com.example.exmexcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.exmexcel.adapter.InfoListAdapter;
import com.example.exmexcel.bean.PersonInfo;
import com.example.exmexcel.dialog.ConfirmDialogFragment;
import com.example.exmexcel.dialog.ConfirmDialogFragment.ConfirmCallbacks;
import com.example.exmexcel.dialog.FileSaveFragment;
import com.example.exmexcel.dialog.FileSaveFragment.FileSaveCallbacks;
import com.example.exmexcel.dialog.FileSelectFragment;
import com.example.exmexcel.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.exmexcel.dialog.InputDialogFragment;
import com.example.exmexcel.dialog.InputDialogFragment.InputCallbacks;
import com.example.exmexcel.util.ExcelUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends Activity implements OnClickListener,OnItemLongClickListener
	,ConfirmCallbacks,FileSelectCallbacks,FileSaveCallbacks,InputCallbacks{
	
    private static final String TAG = "MainActivity";
    private Button btn_open;
    private Button btn_save;
    private Button btn_add;
    private TextView tv_file;
    private ListView lv_person;
    private ArrayList<PersonInfo> mPersonList = new ArrayList<PersonInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn_open = (Button)findViewById(R.id.btn_open);
		btn_save = (Button)findViewById(R.id.btn_save);
		btn_add = (Button)findViewById(R.id.btn_add);
        btn_open.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        tv_file = (TextView)findViewById(R.id.tv_file);
        lv_person = (ListView)findViewById(R.id.lv_person);
        lv_person.setOnItemLongClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[]{"xls", "xlsx"}, null);
		} else if (v.getId() == R.id.btn_save) {
			FileSaveFragment.show(this, "xls");
		} else if (v.getId() == R.id.btn_add) {
			InputDialogFragment dialog = InputDialogFragment.newInstance("请输入人员信息");
			String fragTag = getResources().getString(R.string.app_name);
			dialog.show(getFragmentManager(), fragTag);
		}
	}

	@Override
	public void onInput(PersonInfo info) {
		mPersonList.add(info);
		InfoListAdapter adapter = new InfoListAdapter(this, mPersonList);
		lv_person.setAdapter(adapter);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		ConfirmDialogFragment fsf = ConfirmDialogFragment.newInstance(
				R.drawable.ic_launcher, "记录删除确认", "是否删除姓名为"+mPersonList.get(position).name+"的记录？");
		Map<String, Object> map_param = new HashMap<String, Object>();
		map_param.put("position", position);
		fsf.setParam(map_param);
		fsf.show(getFragmentManager(), "");
		return true;
	}

	@Override
	public void onConfirmSelect(Map<String, Object> map_param) {
		int position = (Integer) map_param.get("position");
		mPersonList.remove(position);
		InfoListAdapter adapter = new InfoListAdapter(this, mPersonList);
		lv_person.setAdapter(adapter);
	}

	@Override
	public boolean onCanSave(String absolutePath, String fileName) {
		if (mPersonList.size() > 0) {
			return true;
		} else {
			Toast.makeText(this, "请先添加人员信息记录", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	@Override
	public void onConfirmSave(String absolutePath, String fileName) {
		String fullPath = String.format("%s/%s", absolutePath, fileName);
		List<List<Object>> data_list = new ArrayList<List<Object>>();
		for (PersonInfo item : mPersonList) {
			ArrayList<Object> data_item = new ArrayList<Object>();
			data_item.add(item.name);
			data_item.add(PersonInfo.sexArray[item.sex]);
			data_item.add(item.age+"");
			data_item.add(item.job);
			data_list.add(data_item);
		}
		ExcelUtil.writeExcel(fullPath, data_list);
		tv_file.setText("已保存文件："+fullPath);
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName,
			Map<String, Object> map_param) {
		String fullPath = String.format("%s/%s", absolutePath, fileName);
		List<List<Object>> data_list = ExcelUtil.read(fullPath);
		mPersonList.clear();
		for (int i=0; i<data_list.size(); i++) {
			PersonInfo item = new PersonInfo();
			List<Object> data_item = data_list.get(i);
			for (int j=0; j<data_item.size(); j++) {
				Object obj = data_item.get(j);
				Log.d(TAG, "i="+i+",j="+j+",value="+(String)obj);
				if (j == 0) {
					item.name = (String)obj;
				} else if (j == 1) {
					String str = ((String)obj).trim();
					item.sex = (str.equals("男")?0:1);
				} else if (j == 2) {
					item.age = Integer.parseInt((String)obj);
				} else if (j == 3) {
					item.job = (String)obj;
				}
			}
			mPersonList.add(item);
		}
		InfoListAdapter adapter = new InfoListAdapter(this, mPersonList);
		lv_person.setAdapter(adapter);
		tv_file.setText("已打开文件："+fullPath);
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName,
			Map<String, Object> map_param) {
		return true;
	}

}
