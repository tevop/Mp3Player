package person.tevop.mp3player;

import java.io.File;

import person.tevop.mp3player.adapter.SelectFileAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class SelectFileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_file);
		init(getIntent().getStringExtra("url"));
	}
	
	private void init(String dir) {
		GridView gridView = (GridView)findViewById(R.id.grid);
		SelectFileAdapter adapter = new SelectFileAdapter(this, dir);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				File file = (File)parent.getItemAtPosition(position);
				Intent intent = null;
				if (file.isFile()) {
					intent = new Intent();
					intent.putExtra("url", file.getPath());
				setResult(Const.RESULT_SUCCESS, intent);
				finish();
				return;
				}
				intent = new Intent(SelectFileActivity.this, SelectFileActivity.class);
				intent.putExtra("url", file.getPath());
				Tools.performStartActivityForResult(SelectFileActivity.this, intent, Const.REQUEST_OPEN_FILE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String url = null;
		if (requestCode == Const.REQUEST_OPEN_FILE) {
			setResult(resultCode, data);
			if (resultCode == Const.RESULT_SUCCESS) {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	

	@Override
	public void onBackPressed() {
		setResult(Const.RESULT_CANCEL, null);
		super.onBackPressed();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	

}
