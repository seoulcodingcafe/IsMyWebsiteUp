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

package io.github.ismywebsiteup.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.View;

import io.github.ismywebsiteup.LicenseActivity;
import io.github.ismywebsiteup.R;
import com.jaredrummler.cyanea.Cyanea;
import com.skydoves.needs.Needs;
import com.skydoves.needs.NeedsAnimation;
import com.skydoves.needs.NeedsItem;
import com.skydoves.needs.NeedsItemTheme;
import com.skydoves.needs.NeedsTheme;
import com.skydoves.needs.TextForm;

import id.ionbit.ionalert.IonAlert;

public class About {

	private Context mContext;
	private Needs mNeeds;

	public About(Context context) {
		mContext = context;
	}

	public void about() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.NORMAL_TYPE).setTitleText(mContext.getString(R.string.app_name))
				.setContentText(mContext.getString(R.string.this_program_is_free_software)).show();
	}

	public void license() {
		mContext.startActivity(new Intent(mContext, LicenseActivity.class));
	}

	public void permission(View v) {
		int textColor;
		if (Cyanea.getInstance().isDark())
			textColor = Color.WHITE;
		else
			textColor = Color.BLACK;

		mNeeds = new Needs.Builder(mContext).setTitle(mContext.getString(R.string.permissions_in_this_app))
				.addNeedsItem(new NeedsItem(null, mContext.getString(R.string.internet), " ",
						mContext.getString(R.string.internet_d)))
				.addNeedsItem(new NeedsItem(null, mContext.getString(R.string.access_network_state), " ",
						mContext.getString(R.string.access_network_state_d)))
				.addNeedsItem(new NeedsItem(null, mContext.getString(R.string.receive_boot_completed), " ",
						mContext.getString(R.string.receive_boot_completed_d)))
				.addNeedsItem(new NeedsItem(null, mContext.getString(R.string.wake_lock),
						mContext.getString(R.string.optional), mContext.getString(R.string.wake_lock_d)))
				.addNeedsItem(new NeedsItem(null, mContext.getString(R.string.request_ignore_battery_optimizations),
						mContext.getString(R.string.optional),
						mContext.getString(R.string.request_ignore_battery_optimizations_d)))
				.setDescription(" ").setConfirm(mContext.getString(R.string.close))
				.setNeedsAnimation(NeedsAnimation.ELASTIC).setBackgroundColor(Cyanea.getInstance().getBackgroundColor())
				.setConfirmBackgroundColor(Cyanea.getInstance().getNavigationBar())
				.setNeedsTheme(
						new NeedsTheme.Builder(mContext).setBackgroundColor(Cyanea.getInstance().getBackgroundColor())
								.setTitleTextForm(new TextForm.Builder().setTextColor(textColor).build())
								.setDescriptionTextForm(new TextForm.Builder().setTextColor(textColor).build())
								.setConfirmTextForm(new TextForm.Builder()
										.setTextColor(Cyanea.getInstance().getMenuIconColor()).build())
								.build())
				.setNeedsItemTheme(new NeedsItemTheme.Builder(mContext)
						.setBackgroundColor(Cyanea.getInstance().getBackgroundColor())
						.setRequireTextForm(new TextForm.Builder().setTextColor(textColor).build())
						.setTitleTextForm(new TextForm.Builder().setTextColor(textColor).build())
						.descriptionTextForm(new TextForm.Builder().setTextColor(textColor).build()).build())
				.setBackgroundAlpha(0.3f).build();
		mNeeds.setOnConfirmListener(() -> mNeeds.dismiss());
		mNeeds.show(v);
	}

	public void version() throws PackageManager.NameNotFoundException {
		PackageInfo p = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.NORMAL_TYPE).setTitleText(mContext.getString(R.string.version))
				.setContentText(p.versionName).show();
	}

}
