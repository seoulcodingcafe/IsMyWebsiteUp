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
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.pixplicity.easyprefs.library.Prefs;

public class CheckNetwork {

	private Context mContext;

	public CheckNetwork(Context context) {
		mContext = context;
	}

	public boolean isConfigWifiOnly() {
		return Prefs.getBoolean("wifionly", false);
	}

	public boolean isConnected() {
		if (Prefs.getBoolean("forcestart", false))
			return true;
		if (isConfigWifiOnly())
			return isConnectedToWifi();
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null)
			return false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
			if (nc == null)
				return false;
			if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
				return true;
			if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
				return true;
			if (nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
				return true;
		}
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null)
			return false;
		int type = ni.getType();
		return type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE
				|| type == ConnectivityManager.TYPE_ETHERNET || type == ConnectivityManager.TYPE_WIMAX;
	}

	public boolean isConnectedToWifi() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null)
			return false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
			if (nc == null)
				return false;
			if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
				return true;
			if (nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
				return true;
		}
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null)
			return false;
		int type = ni.getType();
		return type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_ETHERNET;
	}
}
