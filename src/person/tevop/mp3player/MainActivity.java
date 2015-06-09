package person.tevop.mp3player;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import person.tevop.mp3player.bean.LyricsBean;
import person.tevop.mp3player.service.PlayService;
import person.tevop.mp3player.view.LyricsView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {

	private SeekBar mSeekBar;
	private boolean started;
	private boolean playing;
	private ImageButton mButtonPlay;
	private BroadcastReceiver receiver;
	private LyricsView lyricsView;
	private String url/* = "mnt/sdcard/Download/Breeze.mp3" */;
	private Handler handler;
	private List<LyricsBean> lyricsList;
	private List<String> songList;
	private int currentSongIndex;
	private int currentPosition = -1;
	private String currentDirPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				System.out.println("playing is: " + playing);
				if (fromUser && playing) {
					Intent intent = new Intent(MainActivity.this,
							PlayService.class);
					intent.putExtra("state", PlayService.MESSAGE_SKIP);
					intent.putExtra("value", progress);
					startService(intent);
				}
			}
		});
		mButtonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		mButtonPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (url == null) {
					openFile();
					return;
				}
				play();
				changeState(playing = !playing);
			}
		});
		findViewById(R.id.buttonExit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopService(new Intent(MainActivity.this, PlayService.class));
				finish();
			}
		});
		findViewById(R.id.buttonOpenFile).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						openFile();
					}
				});
		findViewById(R.id.buttonNext).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				skipToNext();
			}

		});
		findViewById(R.id.buttonPre).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				skipToPre();
			}

		});
		lyricsView = (LyricsView) findViewById(R.id.textLyrics);
		initReceiver();
		initHandler();
	}

	private void initReceiver() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Const.ACTION_PROGRESS)) {
					refreshProgress(intent.getIntExtra("progress", 0));
					if (lyricsList != null) {
						prepareLyrics(intent.getIntExtra("time", 0));
					}
					// showLyrics(intent.getLongExtra("time", 0));
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.ACTION_PROGRESS);
		registerReceiver(receiver, filter);
	}

	private void prepareLyrics(long time) {
		int len = lyricsList.size();
		int index = -1;
		long timeFlow = 0;
		long timeInList;
		for (int i = 0; i < len; i++) {
			timeInList = lyricsList.get(i).getTime();
			if (lyricsList.get(i).getTime() > time) {
				timeFlow = timeInList - time;
				index = i;
				break;
			}
		}
		if (index == currentPosition) {
			return;
		}
		currentPosition = index;
		System.out.println("time is:" + time + ", index is: " + index
				+ ", l time is: " + lyricsList.get(index).getTime());
		lyricsView.setCurrentIndex(currentPosition);
		final long finalFlow = timeFlow;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(finalFlow);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (playing && handler != null) {
					handler.sendEmptyMessage(Const.MESSAGE_REFRESH_LYRICS);
				}
			}

		}).start();
		// lyricsView.postInvalidate();
	}

	private void showDefaultLyricsList() {
		lyricsView.setCurrentIndex(-1);
		lyricsView.postInvalidate();
	}

	private void refreshLyrics() {
		lyricsView.setCurrentIndex(currentPosition);
		lyricsView.postInvalidate();
	}

	private void initHandler() {
		handler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case Const.MESSAGE_REFRESH_LYRICS:
					if (lyricsList != null) {
						refreshLyrics();
					}
					// System.out.println("seekBar is set to: " + progress);
					return true;
				case Const.MESSAGE_REFRESH_PROGRESS:
					if (mSeekBar != null) {
						int progress = msg.arg1;
						mSeekBar.setProgress(progress);
					}
					return true;
				}
				return false;
			}
		});
	}

	private void refreshProgress(int progress) {
		Message msg = Message.obtain(handler);
		msg.what = Const.MESSAGE_REFRESH_PROGRESS;
		msg.arg1 = progress;
		msg.sendToTarget();
	}

	private void openFile() {
		Intent intent = new Intent(getApplicationContext(),
				SelectFileActivity.class);
		intent.putExtra("url", Environment.getExternalStorageDirectory()
				.getPath()/* + "/Download" */);
		Tools.performStartActivityForResult(this, intent,
				Const.REQUEST_OPEN_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Const.REQUEST_OPEN_FILE) {
			if (resultCode == Const.RESULT_CANCEL || data == null) {
				return;
			}
			changeSong(data.getStringExtra("url"));
			startPlay();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void skipToNext() {
		if (songList == null) {
			return;
		}
		if (currentSongIndex == songList.size() - 1) {
			currentSongIndex = 0;
		} else {
			currentSongIndex++;
		}
		changeSong(currentSongIndex);
		startPlay();
	}

	private void skipToPre() {
		if (songList == null) {
			return;
		}
		if (currentSongIndex == 0) {
			currentSongIndex = songList.size() - 1;
		} else {
			currentSongIndex--;
		}
		changeSong(currentSongIndex);
		startPlay();
	}

	private void changeSong(int index) {
		url = songList.get(index);
		readLyricsFile();
	}

	private void changeSong(String url) {
		this.url = url;
		String parent = url.substring(0, url.lastIndexOf(File.separatorChar));
		if (!parent.equals(currentDirPath)) {
			currentDirPath = parent;
			File parentDir = new File(parent);
			if (parentDir.exists()) {
				File[] files = parentDir.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						if (file.getName().toLowerCase().endsWith(".mp3")) {
							return true;
						}
						return false;
					}

				});
				if (songList == null) {
					songList = new ArrayList<String>();
				}
				songList.clear();
				if (files != null && files.length > 0) {
					for (File subFile : files) {
						songList.add(subFile.getPath());
					}
				}
				Collections.sort(songList);
				currentSongIndex = getSongIndex(url, songList);
			}
		}
		readLyricsFile();
	}

	private void readLyricsFile() {
		String lyricsUrl = url.replaceAll("\\.mp3", "\\.lrc");
		File file = new File(lyricsUrl);
		if (!file.exists()) {
			lyricsList = null;
			lyricsView.setList(null);
			lyricsView.postInvalidate();
			return;
		}
		lyricsList = Tools.readLyrics(file);
		lyricsView.setList(lyricsList);
		showDefaultLyricsList();
	}

	private int getSongIndex(String url, List<String> songList) {
		for (int i = 0; i < songList.size(); i++) {
			if (url.equals(songList.get(i))) {
				return i;
			}
		}
		return -1;
	}

	// private void

	private void startPlay() {
		Intent intent = new Intent(this, PlayService.class);
		// stopService(intent);
		// if (!started) {
		intent.putExtra("state", PlayService.MESSAGE_START);
		intent.putExtra("url", url);
		started = true;
		playing = true;
		// } else {
		//
		// intent.putExtra("state", PlayService.MESSAGE_PLAY);
		// }
		startService(intent);
		intent.putExtra("state", PlayService.MESSAGE_LOOP);
		startService(intent);
	}

	private void play() {
		if (url == null) {
			openFile();
			return;
		}
		Intent intent = new Intent(this, PlayService.class);
		intent.putExtra("state", PlayService.MESSAGE_PLAY);
		startService(intent);
		intent.putExtra("state", PlayService.MESSAGE_LOOP);
		startService(intent);
	}

	private void changeState(boolean playing) {
		if (playing) {
			mButtonPlay.setImageResource(R.drawable.pause);
			return;
		}
		mButtonPlay.setImageResource(R.drawable.play);
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
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		// Intent intent = new Intent(this, PlayService.class);
		// intent.putExtra("state", PlayService.MESSAGE_FINISH);
		// startService(intent);
		// finish();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		mSeekBar = null;
		mButtonPlay = null;
		if (handler != null) {
			handler = null;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		if (lyricsList != null) {
			lyricsList.clear();
			lyricsList = null;
		}
		if (songList != null) {
			songList.clear();
			songList = null;
		}
		if (mButtonPlay != null) {
			mButtonPlay = null;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		if (lyricsView != null) {
			lyricsView = null;
		}
		super.onDestroy();
	}

}
