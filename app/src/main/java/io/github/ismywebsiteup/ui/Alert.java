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

import io.github.ismywebsiteup.R;
import io.github.ismywebsiteup.tools.ConvertStringHTML;
import com.jaredrummler.cyanea.Cyanea;

import id.ionbit.ionalert.IonAlert;

public class Alert {

	private Context mContext;

	public Alert(Context context) {
		mContext = context;
	}

	public void alreadyDisabled() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.SUCCESS_TYPE)
				.setTitleText(mContext.getString(R.string.this_is_already_disabled)).show();
	}

	public void checkCustomHeaders() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.ERROR_TYPE).setTitleText(mContext.getString(R.string.error)).setContentText(
				new ConvertStringHTML().convertBackslashN(mContext.getString(R.string.check_the_custom_headers)))
				.show();
	}

	public void deleted() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.SUCCESS_TYPE).setTitleText(mContext.getString(R.string.deleted)).show();
	}

	public void ignoreSSLWarning() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.WARNING_TYPE).setTitleText(mContext.getString(R.string.warning))
				.setContentText(
						new ConvertStringHTML().convertBackslashN(mContext.getString(R.string.use_at_your_own_risk)))
				.show();
	}

	public IonAlert loading() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		return new IonAlert(mContext, IonAlert.PROGRESS_TYPE).setTitleText(mContext.getString(R.string.loading))
				.setSpinKit("FadingCircle")
				.setSpinColor("#" + Integer.toHexString(Cyanea.getInstance().getPrimaryDark()));
	}

	public void notAvailable() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(mContext, IonAlert.SUCCESS_TYPE)
				.setTitleText(mContext.getString(R.string.this_is_not_available_in_your_version_of_android)).show();
	}
}
