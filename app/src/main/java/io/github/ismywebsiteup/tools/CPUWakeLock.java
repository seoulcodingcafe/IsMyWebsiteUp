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

import android.content.Context;
import android.os.PowerManager;

import com.pixplicity.easyprefs.library.Prefs;

public class CPUWakeLock {
	private Context mContext;
	private PowerManager mPowerManager;
	private PowerManager.WakeLock mWakeLock;

	public CPUWakeLock(Context context) {
		mContext = context;
		mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				context.getPackageName() + System.currentTimeMillis());
	}

	public void acquireIfIsEnabled() {
		if (!Prefs.getBoolean("usewakelockwheninbackground", true))
			return;
		mWakeLock.acquire();
	}

	public void releaseIfIsHeld() {
		if (!mWakeLock.isHeld())
			return;
		mWakeLock.release();
	}

}
