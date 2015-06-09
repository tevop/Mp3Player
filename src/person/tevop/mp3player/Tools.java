package person.tevop.mp3player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import person.tevop.mp3player.bean.LyricsBean;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Tools {
	public static void performStartActivity(Context context, Intent intent) {
		context.startActivity(intent);
	}
	
	public static void performStartActivityForResult(Activity activity, Intent intent, int requestCode) {
		activity.startActivityForResult(intent, requestCode);
	}
	
	public static List<LyricsBean> readLyrics(File file) {
//		file = new File("d:\\Breeze.lrc");
		List<LyricsBean> list = new ArrayList<LyricsBean>();
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			br = new BufferedReader(isr);
			String line = null;
			String[] strings = null;
			String stringTime = null;
			String text = null;
			long currentTime = 0;
			while((line = br.readLine()) != null) {
//				System.out.println("line is: " + line);
				strings = line.split("]");
//				System.out.println("string's length is: " + strings.length);
				if (strings.length < 2) {
					list.add(new LyricsBean(currentTime++, strings[0]));
					continue;
				}
				stringTime = strings[0].replace("[", "");
				long time = convertTime(stringTime);
//				System.out.println("time is: " + time);
				list.add(new LyricsBean(time, strings[1]));
			}
//			showList(list);
			Collections.sort(list, new Comparator<LyricsBean>(){
				@Override
				public int compare(LyricsBean lhs, LyricsBean rhs) {
					return (int)(lhs.getTime() - rhs.getTime());
				}
			});
			return list;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isr = null;
			} if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
	}
	
	private static long convertTime(String stringTime) {
		long time = 0;
		String splits[] = stringTime.split(":");
		int length = splits.length;
		if (length == 2) {
//			System.out.println("splits is: " + splits[0] + ", " + splits[1]);
			time = (long)((Integer.parseInt(splits[0]) * 60 + Double.parseDouble(splits[1])) * 1000);
		} else if (length == 3) {
			time = (long)((Integer.parseInt(splits[0]) * 60 * 60
					+ Integer.parseInt(splits[1]) * 60 +
					Double.parseDouble(splits[1])) * 1000);
		} else {
			System.out.println("invalid syntax");
			return -1;
		}
		return time;
	}
}
