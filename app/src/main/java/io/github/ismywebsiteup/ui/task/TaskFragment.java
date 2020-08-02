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

package io.github.ismywebsiteup.ui.task;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import io.github.ismywebsiteup.MainActivity;
import io.github.ismywebsiteup.MainService;
import io.github.ismywebsiteup.R;
import io.github.ismywebsiteup.ScheduleActivity;
import io.github.ismywebsiteup.db.Task;
import io.github.ismywebsiteup.tools.CheckJSON;
import io.github.ismywebsiteup.tools.CheckURL;
import io.github.ismywebsiteup.ui.Alert;
import io.github.ismywebsiteup.ui.NumberPref;
import com.pixplicity.easyprefs.library.Prefs;

public class TaskFragment extends PreferenceFragmentCompat {
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.task, rootKey);
		findPreference("commandrun").setOnPreferenceClickListener(preference -> {
			if (new CheckURL(getActivity()).checkURL("taskurl")) {
				if (!new CheckJSON().check(Prefs.getString("taskcustomheaders", ""), true)) {
					new Alert(getActivity()).checkCustomHeaders();
					return false;
				}
				runTask();
			}
			return false;
		});
		findPreference("commandschedule").setOnPreferenceClickListener(preference -> {
			if (new CheckURL(getActivity()).checkURL("taskurl")) {
				if (!new CheckJSON().check(Prefs.getString("taskcustomheaders", ""), true)) {
					new Alert(getActivity()).checkCustomHeaders();
					return false;
				}
				scheduleTask();
			}
			return false;
		});
		findPreference("taskignoresslerrors").setOnPreferenceChangeListener((preference, newValue) -> {
			if ((boolean) newValue)
				new Alert(getActivity()).ignoreSSLWarning();
			return true;
		});
		changeIntoNumberPref();
	}

	private void changeIntoNumberPref() {
		new NumberPref().change(findPreference("taskcheckhyperlinksmaxdepth"));
		new NumberPref().change(findPreference("tasktimeout"));
		new NumberPref().change(findPreference("taskretry"));
		new NumberPref().change(findPreference("taskretrydelay"));
	}

	private void runTask() {
		Task task = new Task();
		task.URL = Prefs.getString("taskurl", "");
		task.checkHyperlinks = Prefs.getBoolean("taskcheckhyperlinks", false);
		task.checkHyperlinksDepth = Integer.parseInt(Prefs.getString("taskcheckhyperlinksmaxdepth", "1"));
		task.checkIfMatchRegex = Prefs.getBoolean("taskcheckhyperlinkifmatchregex", false);
		task.checkRegex = Prefs.getString("taskcheckhyperlinkregex", "");
		task.dontCheckIfMatchRegex = Prefs.getBoolean("taskdontcheckhyperlinkifmatchregex", false);
		task.dontCheckRegex = Prefs.getString("taskdontcheckhyperlinkregex", "");
		task.checkHyperlinksSpecificDomains = Prefs.getBoolean("taskcheckhyperlinksonlyforspecificdomains", false);
		task.checkHyperlinksDomain = Prefs.getString("taskcheckhyperlinksdomains", "");
		task.checkHyperlinksSendReferer = Prefs.getBoolean("taskcheckhyperlinkssendreferer", false);
		task.checkHyperlinksIgnoreSymbol = Prefs.getBoolean("taskcheckhyperlinksignorepart", true);
		task.checkJavaScriptHyperlinks = Prefs.getBoolean("taskcheckjavascripthyperlinks", false);
		task.userAgent = Prefs.getString("taskuseragent", "");
		task.referer = Prefs.getString("taskreferer", "");
		task.retry = Integer.parseInt(Prefs.getString("taskretry", "3"));
		task.retryDelay = Integer.parseInt(Prefs.getString("taskretrydelay", "10"));
		task.maxRetry = Integer.parseInt(Prefs.getString("taskretry", "3"));
		task.timeout = Integer.parseInt(Prefs.getString("tasktimeout", "10"));
		task.sendDNT = Prefs.getBoolean("tasksenddnt", false);
		task.authorizationHeader = Prefs.getString("taskauthorizationheader", "");
		task.acceptHeader = Prefs.getString("taskacceptheader", "");
		task.acceptCharsetHeader = Prefs.getString("taskacceptcharsetheader", "");
		task.acceptEncodingHeader = Prefs.getString("taskacceptencodingheader", "");
		task.acceptLanguageHeader = Prefs.getString("taskacceptlanguageheader", "");
		task.customHeaders = Prefs.getString("taskcustomheaders", "");
		task.checkBinaryResponse = Prefs.getBoolean("taskcheckbinaryresponses", false);
		task.ignoreSSLErrors = Prefs.getBoolean("taskignoresslerrors", false);
		task.insert(getContext());
		ContextCompat.startForegroundService(getActivity(), new Intent(getContext(), MainService.class));
		((MainActivity) getActivity()).goToResult();
	}

	private void scheduleTask() {
		for (int preferenceIdx = 0; preferenceIdx < getPreferenceScreen().getPreferenceCount(); preferenceIdx++) {
			PreferenceCategory prefCat = (PreferenceCategory) getPreferenceScreen().getPreference(preferenceIdx);
			for (int subPrefIdx = 0; subPrefIdx < prefCat.getPreferenceCount(); subPrefIdx++) {
				Preference preference = prefCat.getPreference(subPrefIdx);
				String k = preference.getKey();
				if (k != null && k.startsWith("task")) {
					try {
						Prefs.putString("schedule." + k, Prefs.getString(preference.getKey(), null));
					} catch (ClassCastException c) {
						Prefs.putBoolean("schedule." + k, Prefs.getBoolean(preference.getKey(), false));

					}
				}
			}

		}
		Prefs.putString("schedule.name", "");
		ScheduleActivity.removemSchedule();
		getActivity().startActivity(new Intent(getActivity(), ScheduleActivity.class));
	}
}
