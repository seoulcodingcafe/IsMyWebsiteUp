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

package io.github.ismywebsiteup.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import io.github.ismywebsiteup.ui.Alert;

import static android.content.Context.POWER_SERVICE;

public class BatteryConfig {
	private Context mContext;

	public BatteryConfig(Context context) {
		mContext = context;
	}

	@SuppressLint("BatteryLife")
	public void ignoreBatteryOptimizations() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PowerManager pm = (PowerManager) mContext.getSystemService(POWER_SERVICE);
			if (!pm.isIgnoringBatteryOptimizations(mContext.getPackageName())) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setData(Uri.parse("package:" + mContext.getPackageName()));
				mContext.startActivity(intent);
			} else {
				new Alert(mContext).alreadyDisabled();
			}
		} else {
			new Alert(mContext).notAvailable();
		}
	}

	public boolean isIgnoringBatteryOptimizations() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PowerManager pm = (PowerManager) mContext.getSystemService(POWER_SERVICE);
			return pm.isIgnoringBatteryOptimizations(mContext.getPackageName());
		} else
			return true;
	}
}
