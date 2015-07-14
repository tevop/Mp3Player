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
	public static final int MESSAGE_EXIT = 2;
//	public static final int MESSAGE_LOOP = 3;
//	public static final int MESSAGE_UNLOOP = 4;
	public static final int MESSAGE_SKIP = 5;
	MediaPlayer mp;
//	private boolean looping;
	private boolean running;

	@Override
	public IBinder onBind(Intent intent) {
//		return new MyBinder();
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		createPlayer();
//		System.out.println("playService onCreate");
	}
	
	private void createPlayer() {
		if (mp != null) {
			mp.release();
		}
		mp = new MediaPlayer();
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
//				System.out.println("looping is: " + looping);
//				if (looping) {
//					start();
//				}
				stop();
				Intent intent = new Intent(Const.ACTION_SONG_FINISHED);
				sendBroadcast(intent);
			}
		});
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		System.out.println("playService onStartCommand");
//		if (intent == null) {
//			System.out.println("intent ====================== null");
//		}
		int state = intent.getIntExtra("state", MESSAGE_PLAY);
		if (state == MESSAGE_EXIT) {
			stopSelf();
		} else if (state == MESSAGE_START) {
			stop();
			String url = intent.getStringExtra("url");
//			String lyricsUrl = url.replaceAll("\\\\.mp3", "\\\\.lrc");
			prepareMP(url);
			if (mp != null) {
				start();
			} else {
				createPlayer();
			}
		} else if (state == MESSAGE_PLAY) {
			if (mp.isPlaying()) {
				pause();
			} else {
				start();
			}
		} /*else if (state == MESSAGE_LOOP || state == MESSAGE_UNLOOP) {
			looping = state == MESSAGE_LOOP ? true : false;
		} */else if (state == MESSAGE_SKIP) {
			int progress = intent.getIntExtra("value", 0);
//			System.out.println("value is: " + progress);
			if (mp.isPlaying()) {
//				System.out.println("mp seek to :" + (progress * mp.getDuration() / 100));
				mp.seekTo(progress * mp.getDuration() / 100);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void prepareMP(String url) {
		try {
			mp.reset();                 // sometime's can't reuse
			mp.setDataSource(url);
			mp.prepare();
		} catch (IllegalStateException e) {
			mp.release();
			mp = null;
			e.printStackTrace();
		} catch (IOException e) {
			mp.release();
			mp = null;
			e.printStackTrace();
		}
	}
	
	private void pause() {
		mp.pause();
		running = false;
	}
	
	private void start() {
		mp.start();
		sendSongTime(mp.getDuration());
		if (!running) {
			running = true;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(running) {
						if (mp == null) {
							return;
						}
						try {
						int currentPosition = mp.getCurrentPosition();
						int duration = mp.getDuration();
							Thread.sleep(Const.TIME_PEROID);
							Intent intent = new Intent(Const.ACTION_PROGRESS);
//							intent.putExtra("progress", currentPosition * 100 / duration);
							intent.putExtra("time", currentPosition);
							intent.putExtra("duration", duration);
							sendBroadcast(intent);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch(IllegalStateException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}
	
	private void sendSongTime(long time) {
		Intent intent = new Intent(Const.ACTION_SONG_TIME);
		intent.putExtra("duration", time);
		sendBroadcast(intent);
	}
	
	private void stop() {
		System.out.println("stopping");
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
