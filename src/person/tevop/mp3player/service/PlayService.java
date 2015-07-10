package person.tevop.mp3player.service;

import java.io.IOException;

import person.tevop.mp3player.Const;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;

public class PlayService extends Service {
	public static final int MESSAGE_START = 0;
	public static final int MESSAGE_PLAY = 1;
	public static final int MESSAGE_EXIT = 2;  //no use
	public static final int MESSAGE_LOOP = 3;
	public static final int MESSAGE_UNLOOP = 4;
	public static final int MESSAGE_SKIP = 5;
	MediaPlayer mp;
	private boolean looping;
	private boolean running;

	@Override
	public IBinder onBind(Intent intent) {
//		return new MyBinder();
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		System.out.println("playService onCreate");
		mp = new MediaPlayer();
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
//				System.out.println("looping is: " + looping);
//				if (looping) {
//					start();
//				}
				Intent intent = new Intent(Const.ACTION_SONG_FINISHED);
				sendBroadcast(intent);
			}
		});
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("playService onStartCommand");
		if (intent == null) {
			System.out.println("intent ====================== null");
		}
		int state = intent.getIntExtra("state", MESSAGE_PLAY);
		if (state == MESSAGE_EXIT) {
			stopSelf();
		} else if (state == MESSAGE_START) {
			String url = intent.getStringExtra("url");
//			String lyricsUrl = url.replaceAll("\\\\.mp3", "\\\\.lrc");
			try {
				mp.reset();
				mp.setDataSource(url);
				mp.prepare();
				start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (state == MESSAGE_PLAY) {
			if (mp.isPlaying()) {
				pause();
			} else {
				start();
			}
		} else if (state == MESSAGE_LOOP || state == MESSAGE_UNLOOP) {
			looping = state == MESSAGE_LOOP ? true : false;
		} else if (state == MESSAGE_SKIP) {
			int progress = intent.getIntExtra("value", 0);
//			System.out.println("value is: " + progress);
			if (mp.isPlaying()) {
//				System.out.println("mp seek to :" + (progress * mp.getDuration() / 100));
				mp.seekTo(progress * mp.getDuration() / 100);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void pause() {
		mp.pause();
		running = false;
	}
	
	private void start() {
		mp.start();
		if (!running) {
			running = true;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(running) {
						if (mp == null) {
							return;
						}
						int currentPosition = mp.getCurrentPosition();
						int duration = mp.getDuration();
						try {
							Thread.sleep(Const.TIME_PEROID);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent = new Intent(Const.ACTION_PROGRESS);
						intent.putExtra("progress", currentPosition * 100 / duration);
						intent.putExtra("time", currentPosition);
						sendBroadcast(intent);
					}
				}
			}).start();
		}
	}
	
	private void stop() {
		running = false;
	}

	@Override
	public void onDestroy() {
//		System.out.println("playService onDestroy");
		if (mp != null) {
			mp.release();
			mp = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	class MyBinder extends Binder {
		
		
	}
	
	
}
