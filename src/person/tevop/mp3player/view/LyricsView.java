package person.tevop.mp3player.view;

import java.util.List;

import person.tevop.mp3player.bean.LyricsBean;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class LyricsView extends TextView{
	
	private Paint currentPaint;
	private Paint normalPaint;
	private Paint clearPaint;
	private List<LyricsBean> list;
	private int currentIndex;
	private boolean cleared;
//	private boolean playing;

	public LyricsView(Context context) {
		super(context);
		init();
	}
	
	public LyricsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		currentPaint = new Paint();
		currentPaint.setColor(Color.BLUE);
		currentPaint.setTextAlign(Paint.Align.CENTER);
		currentPaint.setTextSize(42);
		normalPaint = new Paint();
		normalPaint.setColor(Color.BLACK);
		normalPaint.setTextAlign(Paint.Align.CENTER);
		normalPaint.setTextSize(42);
		clearPaint = new Paint();
		clearPaint.setColor(Color.WHITE);
	}
	
	public void setList(List<LyricsBean> list) {
		this.list = list;
	}
	
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
//	private void startLyricsThread() {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while(playing) {
//						int size = list == null ? 0 : list.size();
//						for (int i = 0; i < size; i++) {
//							try {
//								Thread.sleep(list.get(i).getTime());
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							currentIndex = i;
//							LyricsView.this.invalidate();
//						}
//				}
//				
//			}
//		}).start();
//	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		System.out.println("ondraw............." + list);
		if (list == null) {
			if (cleared) {
				return;
			}
			canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), clearPaint);
			cleared = true;
			return;
		}
		int width = getWidth();
		int height = getHeight();
		float currentX = width * 1.0f/2;
		float marginY = 40.0f;
		float currentY;
		if (currentIndex < 0) {
			currentY = 0;
			for (int i = 0; i < list.size(); i ++) {
				canvas.drawText(list.get(i).getText(), currentX, currentY + (i * marginY), normalPaint);
			}
			return;
		}
		currentY = height * 1.0f/2;
		canvas.drawText(list.get(currentIndex).getText(), currentX, currentY, currentPaint);
		for (int i = currentIndex - 1; i >= 0; i--) {
			canvas.drawText(list.get(i).getText(), currentX, currentY - (currentIndex - i) * marginY, normalPaint);
			if (currentY - (currentIndex - i - 1) * marginY < 0) {
				break;
			}
		}
		for (int i = currentIndex + 1; i < list.size(); i ++) {
			canvas.drawText(list.get(i).getText(), currentX, currentY + (i - currentIndex) * marginY, normalPaint);
			if (currentY + (i - currentIndex + 1) * marginY > height) {
				break;
			}
		}
		cleared = false;
	}
}