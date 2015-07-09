package person.tevop.mp3player.adapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import person.tevop.mp3player.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectFileAdapter extends BaseAdapter{
	
	private List<File> list;
	private String dir;
	private Context context;
	public SelectFileAdapter(Context context, String dir) {
		this.dir = dir;
		this.context = context;
		File dirFile = new File(dir);
		File[] files = dirFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()
						|| file.getName().toLowerCase().endsWith(".mp3")) {
					return true;
				}
				return false;
			}
		});
		list = new ArrayList<File>();
		if (files == null || files.length == 0)
		{
			return;
		}
		for (File file : files) {
			list.add(file);
		}
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			// iv = mInflater.inflate(R.layout.icon_item, null);
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_select_file, null);
			holder = new ViewHolder();
			holder.image = (ImageView)convertView.findViewById(R.id.img);
			holder.text = (TextView)convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
//			iv = (View) convertView;
			holder = (ViewHolder)convertView.getTag();
		}
		File file = list.get(position);
		holder.image.setImageResource(file.isDirectory() ? R.drawable.img_dir: R.drawable.img_file);
		holder.text.setText(file.getName());
		return convertView;
	}
	
	private class ViewHolder {
		ImageView image;
		TextView text;
	}

}
