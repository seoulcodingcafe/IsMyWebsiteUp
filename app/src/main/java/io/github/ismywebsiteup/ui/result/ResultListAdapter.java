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

package io.github.ismywebsiteup.ui.result;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.github.ismywebsiteup.R;
import io.github.ismywebsiteup.ResultActivity;
import io.github.ismywebsiteup.db.Task;
import io.github.ismywebsiteup.tools.ConvertStringHTML;
import com.jaredrummler.cyanea.Cyanea;

import java.util.List;

import id.ionbit.ionalert.IonAlert;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ResultViewHolder> {

	private final LayoutInflater mInflater;
	private List<Task> mResults;

	ResultListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemCount() {
		if (mResults != null)
			return mResults.size();
		else
			return 0;
	}

	@Override
	public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
		if (mResults != null) {
			holder.mTask = mResults.get(position);
			holder.updateContent();
		}
	}

	@Override
	public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = mInflater.inflate(R.layout.recycler_task, parent, false);

		return new ResultViewHolder(itemView);
	}

	void setResults(List<Task> results) {
		mResults = results;
		notifyDataSetChanged();
	}

	static class ResultViewHolder extends RecyclerView.ViewHolder {
		private final TextView mTextView;
		private final ImageView mImageView;
		private final View mBackGround;
		Task mTask;

		private ResultViewHolder(View itemView) {
			super(itemView);
			mTextView = itemView.findViewById(R.id.content);
			mImageView = itemView.findViewById(R.id.imageView);
			mBackGround = itemView.findViewById(R.id.backGround);
		}

		private int getBackGroundFail() {
			if (Cyanea.getInstance().isDark()) {
				return 0xFF770000;
			}
			return 0xFFFF6969;
		}

		private int getBackGroundRunning() {
			if (Cyanea.getInstance().isDark()) {
				return 0xFF000077;
			}
			return 0xFFAAAAFF;
		}

		private int getBackGroundSuccess() {
			if (Cyanea.getInstance().isDark()) {
				return 0xFF007700;
			}
			return 0xFF69FF69;
		}

		private int getBackGroundWait() {
			if (Cyanea.getInstance().isDark()) {
				return 0xFF666600;
			}
			return 0xFFFFFF69;
		}

		private void showResult(View v) {
			String[] taskString = mTask.buildResultString(true, v.getContext()).split("\n");
			String contentText = "";
			for (int i = 1; i < taskString.length; i++) {
				if (!taskString[i].startsWith(v.getContext().getString(R.string.bullet))
						&& !(taskString[i].equals(v.getContext().getString(R.string.ignored_domains))
								|| taskString[i].equals(v.getContext().getString(R.string.ignored_by_regex))))
					contentText += taskString[i] + "\n\n";
				else
					contentText += taskString[i] + "\n";
			}
			if (mTask.ignoredByRegex.length() > 0 || mTask.ignoredDomains.length() > 0)
				showResultWithIgnored(taskString, contentText, v);
			else
				showResultNoIgnored(taskString, contentText, v);
		}

		private void showResultNoIgnored(String[] taskString, String contentText, View v) {
			IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
			if (mTask.retry < 0 && !mTask.running)
				new IonAlert(v.getContext(), IonAlert.ERROR_TYPE).setTitleText(taskString[0])
						.setContentText(new ConvertStringHTML().convertBackslashN(contentText)).show();
			else if (mTask.complete)
				new IonAlert(v.getContext(), IonAlert.SUCCESS_TYPE).setTitleText(taskString[0])
						.setContentText(new ConvertStringHTML().convertBackslashN(contentText)).show();
			else
				new IonAlert(v.getContext(), IonAlert.NORMAL_TYPE).setTitleText(taskString[0])
						.setContentText(new ConvertStringHTML().convertBackslashN(contentText)).show();
		}

		private void showResultWithIgnored(String[] taskString, String contentText, View v) {
			Intent i = new Intent(v.getContext(), ResultActivity.class);
			i.putExtra("title", taskString[0]);
			i.putExtra("content", contentText);

			if (mTask.retry < 0 && !mTask.running)
				i.putExtra("type", IonAlert.ERROR_TYPE);
			else if (mTask.complete)
				i.putExtra("type", IonAlert.SUCCESS_TYPE);
			else
				i.putExtra("type", IonAlert.NORMAL_TYPE);
			v.getContext().startActivity(i);
		}

		void updateContent() {
			mTextView.setText(mTask.buildResultString(false, mTextView.getContext()));
			if (mTask.running) {
				mBackGround.setBackgroundColor(getBackGroundRunning());
				mImageView.setBackgroundResource(R.drawable.progress);
			} else {
				if (mTask.retry < 0) {
					mBackGround.setBackgroundColor(getBackGroundFail());
					mImageView.setBackgroundResource(R.drawable.fail);
				} else {
					if (mTask.complete) {
						mBackGround.setBackgroundColor(getBackGroundSuccess());
						mImageView.setBackgroundResource(R.drawable.success);
					} else {
						mBackGround.setBackgroundColor(getBackGroundWait());
						mImageView.setBackgroundResource(R.drawable.wait);
					}
				}
			}
			mTextView.setOnClickListener(this::showResult);
			mBackGround.setOnClickListener(this::showResult);
			mImageView.setOnClickListener(this::showResult);
		}
	}
}
