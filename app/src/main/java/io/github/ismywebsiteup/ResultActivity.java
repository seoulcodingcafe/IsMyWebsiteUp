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

package io.github.ismywebsiteup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.ismywebsiteup.R;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;

import id.ionbit.ionalert.IonAlert;

public class ResultActivity extends CyaneaAppCompatActivity {

	private ImageView mImageView;
	private TextView mTextView;

	private void openIntent() {
		Intent i = getIntent();
		setTitle(i.getStringExtra("title"));
		mTextView.setText(i.getStringExtra("content"));
		switch (i.getIntExtra("type", IonAlert.NORMAL_TYPE)) {
		case (IonAlert.ERROR_TYPE):
			mImageView.setBackgroundResource(R.drawable.fail);
			break;
		case (IonAlert.SUCCESS_TYPE):
			mImageView.setBackgroundResource(R.drawable.success);
			break;
		case (IonAlert.NORMAL_TYPE):
			mImageView.setBackgroundResource(R.drawable.wait);
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		mImageView = findViewById(R.id.imageView);
		mTextView = findViewById(R.id.content);
	}

	@Override
	protected void onResume() {
		super.onResume();
		openIntent();
	}
}
