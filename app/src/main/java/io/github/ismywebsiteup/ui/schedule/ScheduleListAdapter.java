//    The GNU General Public License does not permit incorporating this program
//    into proprietary programs.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

package io.github.ismywebsiteup.ui.schedule;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.github.ismywebsiteup.R;
import io.github.ismywebsiteup.ScheduleActivity;
import io.github.ismywebsiteup.db.Schedule;

import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ScheduleViewHolder> {

	private final LayoutInflater mInflater;
	private List<Schedule> mSchedules;

	ScheduleListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemCount() {
		if (mSchedules != null)
			return mSchedules.size();
		else
			return 0;
	}

	@Override
	public void onBindViewHolder(@NonNull ScheduleListAdapter.ScheduleViewHolder holder, int position) {
		if (mSchedules != null) {
			holder.mSchedule = mSchedules.get(position);
			holder.updateContent();
		}
	}

	@Override
	public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = mInflater.inflate(R.layout.recycler_schedule, parent, false);

		return new ScheduleViewHolder(itemView);
	}

	void setSchedules(List<Schedule> schedules) {
		mSchedules = schedules;
		notifyDataSetChanged();
	}

	static class ScheduleViewHolder extends RecyclerView.ViewHolder {
		private final TextView mTextViewBold;
		private final TextView mTextViewNorm;
		private final View mBackGround;
		Schedule mSchedule;

		private ScheduleViewHolder(View itemView) {
			super(itemView);
			mTextViewBold = itemView.findViewById(R.id.textViewBold);
			mTextViewNorm = itemView.findViewById(R.id.textViewNorm);
			mBackGround = itemView.findViewById(R.id.backGround);
		}

		private void openSchedule(Context context) {
			ScheduleActivity.setmSchedule(mSchedule);
			mSchedule.toPrefs();
			context.startActivity(new Intent(context, ScheduleActivity.class));
		}

		void updateContent() {
			mTextViewBold.setText(mSchedule.name);
			mTextViewNorm.setText(mSchedule.buildEveryString(itemView.getContext()) + "\n"
					+ mSchedule.buildLastRunString(itemView.getContext()));

			mTextViewBold.setOnClickListener(v -> openSchedule(v.getContext()));
			mTextViewNorm.setOnClickListener(v -> openSchedule(v.getContext()));
			mBackGround.setOnClickListener(v -> openSchedule(v.getContext()));
		}
	}
}
