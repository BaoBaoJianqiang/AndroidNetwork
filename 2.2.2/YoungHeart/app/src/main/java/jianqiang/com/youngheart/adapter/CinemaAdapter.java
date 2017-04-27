package jianqiang.com.youngheart.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jianqiang.com.youngheart.R;
import jianqiang.com.youngheart.base.AppBaseActivity;
import jianqiang.com.youngheart.entity.CinemaBean;

public class CinemaAdapter extends BaseAdapter {
	private final ArrayList<CinemaBean> cinemaList;
	private final AppBaseActivity context;

	public CinemaAdapter(ArrayList<CinemaBean> cinemaList,
			AppBaseActivity context) {
		this.cinemaList = cinemaList;
		this.context = context;
	}

	public int getCount() {
		return cinemaList.size();
	}

	public CinemaBean getItem(final int position) {
		return cinemaList.get(position);
	}

	public long getItemId(final int position) {

		return position;
	}

	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = context.getLayoutInflater().inflate(
					R.layout.item_cinemalist, null);
			holder.tvCinemaName = (TextView) convertView
					.findViewById(R.id.tvCinemaName);
			holder.tvCinemaId = (TextView) convertView
					.findViewById(R.id.tvCinemaId);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		CinemaBean cinema = cinemaList.get(position);
		holder.tvCinemaName.setText(cinema.getCinemaName());
		holder.tvCinemaId.setText(cinema.getCinemaId());
		return convertView;
	}

	class Holder {
		TextView tvCinemaName;
		TextView tvCinemaId;
	}
}
