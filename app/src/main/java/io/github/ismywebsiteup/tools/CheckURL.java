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

import io.github.ismywebsiteup.R;
import com.jaredrummler.cyanea.Cyanea;
import com.pixplicity.easyprefs.library.Prefs;

import java.net.URI;
import java.net.URISyntaxException;

import id.ionbit.ionalert.IonAlert;

public class CheckURL {

	Context mContext;

	public CheckURL(Context context) {
		mContext = context;
	}

	public boolean checkURL(String prefKey) {
		String URL = Prefs.getString(prefKey, "");
		boolean errorInURL = false;
		if (URL.length() == 0) {
			errorInURL = true;
		}
		if (!errorInURL && !URL.contains("://")) {
			URL = "http://" + URL;
			Prefs.putString(prefKey, URL);
		}
		if (!errorInURL) {
			try {
				new URI(URL);
			} catch (URISyntaxException u) {
				errorInURL = true;
			}
		}
		if (errorInURL) {
			IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
			new IonAlert(mContext, IonAlert.ERROR_TYPE).setTitleText(mContext.getString(R.string.error))
					.setContentText(
							new ConvertStringHTML().convertBackslashN(mContext.getString(R.string.check_the_url)))
					.show();
		}
		return !errorInURL;
	}
}
