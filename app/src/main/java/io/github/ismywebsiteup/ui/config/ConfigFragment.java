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

package io.github.ismywebsiteup.ui.config;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import io.github.ismywebsiteup.R;
import io.github.ismywebsiteup.db.Task;
import io.github.ismywebsiteup.tools.BatteryConfig;
import io.github.ismywebsiteup.tools.ConvertStringHTML;
import io.github.ismywebsiteup.ui.Alert;
import io.github.ismywebsiteup.ui.NumberPref;
import com.jaredrummler.cyanea.Cyanea;
import com.jaredrummler.cyanea.prefs.CyaneaSettingsActivity;

import id.ionbit.ionalert.IonAlert;

public class ConfigFragment extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.config, rootKey);
		findPreference("commandthemeconfiguration").setOnPreferenceClickListener(preference -> {
			getActivity().startActivity(new Intent(getActivity(), CyaneaSettingsActivity.class));
			return false;
		});
		findPreference("commandcleanfinishedresultsnow").setOnPreferenceClickListener(preference -> {
			IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
			new IonAlert(getActivity(), IonAlert.WARNING_TYPE).setTitleText(getString(R.string.confirm_delete))
					.setContentText(new ConvertStringHTML().convertBackslashN(getString(R.string.are_you_sure)))
					.setConfirmText(getString(R.string.yes)).setCancelText(getString(R.string.no))
					.setConfirmClickListener(ionAlert -> {
						ionAlert.dismissWithAnimation();
						cleanFinishedResults();
					}).show();
			return false;
		});
		findPreference("commanddisablebatteryoptimizations").setOnPreferenceClickListener(preference -> {
			new BatteryConfig(getActivity()).ignoreBatteryOptimizations();
			return false;
		});
		changeIntoNumberPref();
	}

	private void changeIntoNumberPref() {
		new NumberPref().change(findPreference("multiconnections"));
		new NumberPref().change(findPreference("buffersize"));
		new NumberPref().change(findPreference("autocleansuccessresultsafter"));
		new NumberPref().change(findPreference("autocleanfailresultsafter"));
		new NumberPref().change(findPreference("autocleanoldcount"));
		new NumberPref().change(findPreference("proxyport"));
		new NumberPref().change(findPreference("dbrefreshrate"));
	}

	private void cleanFinishedResults() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		IonAlert ionAlert = new IonAlert(getActivity(), IonAlert.PROGRESS_TYPE);
		ionAlert.setTitleText(getString(R.string.deleting));
		ionAlert.setCancelable(false);
		ionAlert.setSpinKit("FadingCircle");
		ionAlert.setSpinColor("#" + Integer.toHexString(Cyanea.getInstance().getAccent()));
		ionAlert.show();
		Task.deleteAll(getContext());
		ionAlert.dismissWithAnimation();
		new Alert(getActivity()).deleted();
	}

}
