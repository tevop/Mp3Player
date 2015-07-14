package person.tevop.mp3player.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SongInfo {
//	private int progress;
	private String url;
	private long time;
	private long cTime;
	private static final String KEY_URL = "url";
//	private static final String KEY_PROGRESS = "progress";
	private static final String KEY_TIME = "time";
	private static final String KEY_CURRENT_TIME= "current_time";
	
	private Context context;
	public SongInfo(Context context) {
		this.context = context;
	}
	
	public void saveData() {
		SharedPreferences sharedPreferences = context.getSharedPreferences("songInfo", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();//获取编辑器
//		editor.putInt(KEY_PROGRESS, progress);
		editor.putString(KEY_URL, url);
		editor.putLong(KEY_CURRENT_TIME, cTime);
		editor.putLong(KEY_TIME, time);
		editor.commit();//提交修改
	}
	
	public void loadData() {
		SharedPreferences sharedPreferences = context.getSharedPreferences("songInfo", Context.MODE_PRIVATE);
//		setProgress(sharedPreferences.getInt(KEY_PROGRESS, 0));
		setUrl(sharedPreferences.getString(KEY_URL, null));
		setTime(sharedPreferences.getLong(KEY_TIME, 1));
		setCurrentTime(sharedPreferences.getLong(KEY_CURRENT_TIME, 0));
	}
	
//	public int getProgress() {
//		return progress;
//	}
//	
//	public void setProgress(int progress) {
//		this.progress = progress;
//	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	public long getCurrentTime() {
		return cTime;
	}
	
	public void setCurrentTime(long currentTime) {
		this.cTime = currentTime;
	}
	
}
