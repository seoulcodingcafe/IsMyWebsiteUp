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

import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceFragmentCompat;

import io.github.ismywebsiteup.R;

import io.github.ismywebsiteup.db.Schedule;
import io.github.ismywebsiteup.tools.BatteryConfig;
import io.github.ismywebsiteup.tools.CheckJSON;
import io.github.ismywebsiteup.tools.CheckURL;
import io.github.ismywebsiteup.tools.ConvertStringHTML;
import io.github.ismywebsiteup.ui.Alert;
import io.github.ismywebsiteup.ui.NumberPref;
import com.jaredrummler.cyanea.Cyanea;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;
import com.pixplicity.easyprefs.library.Prefs;

import id.ionbit.ionalert.IonAlert;

public class ScheduleActivity extends CyaneaAppCompatActivity {

	private static Schedule mSchedule;

	public static void removemSchedule() {
		mSchedule = null;
	}

	public static void setmSchedule(Schedule schedule) {
		mSchedule = schedule;
	}

	private void afterDelete() {
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(this, IonAlert.SUCCESS_TYPE).setTitleText(getString(R.string.deleted))
				.setConfirmClickListener(ionAlert -> finish()).setCancelClickListener(ionAlert -> finish()).show();
	}

	private void askScheduleRunInBackground() {
		if (new BatteryConfig(this).isIgnoringBatteryOptimizations()) {
			finish();
			return;
		}
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(this, IonAlert.INPUT_TYPE)
				.setTitleText(getString(R.string.should_schedules_also_run_when_the_device_standby))
				.setContentText(getString(R.string.we_will_need_your_permission))
				.setConfirmText(getString(R.string.yes)).setCancelText(getString(R.string.no))
				.setConfirmClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					new BatteryConfig(this).ignoreBatteryOptimizations();
				}).setCancelClickListener(ionAlert -> finish()).show();

	}

	private boolean checkScheduleSaved() {
		if (mSchedule == null) {
			IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
			new IonAlert(this, IonAlert.ERROR_TYPE).setTitleText(getString(R.string.schedule_not_yet_saved)).show();
			return false;
		}
		return true;
	}

	protected void cancel() {
		finish();
	}

	protected void copySchedule() {
		if (!checkScheduleSaved())
			return;
		mSchedule = null;
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(this, IonAlert.SUCCESS_TYPE)
				.setTitleText(getString(R.string.youre_editing_a_copied_schedule_now_it_is_not_yet_saved)).show();
		this.startService(new Intent(this, Scheduler.class));
	}

	protected void deleteSchedule() {
		if (!checkScheduleSaved())
			return;
		final Schedule schedule = mSchedule;
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(this, IonAlert.WARNING_TYPE).setTitleText(getString(R.string.confirm_delete))
				.setContentText(new ConvertStringHTML().convertBackslashN(getString(R.string.are_you_sure)))
				.setConfirmText(getString(R.string.yes)).setCancelText(getString(R.string.no))
				.setConfirmClickListener(ionAlert -> {
					schedule.delete(getApplicationContext());
					afterDelete();
				}).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_activity);
		getSupportFragmentManager().beginTransaction().replace(R.id.settings, new ScheduleFragment()).commit();
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	protected void runnow() {
		if (!checkScheduleSaved())
			return;
		mSchedule.run(this);
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(this, IonAlert.SUCCESS_TYPE).setTitleText(getString(R.string.it_is_running_now)).show();
		this.startService(new Intent(this, Scheduler.class));
	}

	protected void saveSchedule() {
		if (Prefs.getString("schedule.name", "").length() == 0) {
			IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
			new IonAlert(this, IonAlert.ERROR_TYPE).setTitleText(getString(R.string.error))
					.setContentText(new ConvertStringHTML().convertBackslashN(getString(R.string.check_the_name)))
					.show();
			return;
		}
		if (!new CheckURL(this).checkURL("schedule.taskurl")) {
			return;
		}
		if (!new CheckJSON().check(Prefs.getString("schedule.taskcustomheaders", ""), true)) {
			new Alert(this).checkCustomHeaders();
			return;
		}
		if (Integer.parseInt(Prefs.getString("scheduleweeks", "0")) < 1
				&& Integer.parseInt(Prefs.getString("scheduledays", "1")) < 1
				&& Integer.parseInt(Prefs.getString("schedulehours", "0")) < 1
				&& Integer.parseInt(Prefs.getString("scheduleminutes", "0")) < 1) {
			new IonAlert(this, IonAlert.ERROR_TYPE).setTitleText(getString(R.string.error))
					.setContentText(new ConvertStringHTML().convertBackslashN(getString(R.string.check_the_times)))
					.show();
			return;
		}
		if (mSchedule == null) {
			mSchedule = new Schedule();
			mSchedule.fromPrefs();
			mSchedule.insert(this);
		} else {
			mSchedule.fromPrefs();
			mSchedule.update(this);
		}
		IonAlert.DARK_STYLE = Cyanea.getInstance().isDark();
		new IonAlert(this, IonAlert.SUCCESS_TYPE).setTitleText(getString(R.string.saved))
				.setConfirmClickListener(ionAlert -> {
					ionAlert.dismissWithAnimation();
					askScheduleRunInBackground();
				}).setCancelClickListener(ionAlert -> finish()).show();

		this.startService(new Intent(this, Scheduler.class));
	}

	public static class ScheduleFragment extends PreferenceFragmentCompat {
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			setPreferencesFromResource(R.xml.schedule, rootKey);

			findPreference("commandschedulesave").setOnPreferenceClickListener(preference -> {
				((ScheduleActivity) getActivity()).saveSchedule();
				return false;
			});
			findPreference("commandschedulecopy").setOnPreferenceClickListener(preference -> {
				((ScheduleActivity) getActivity()).copySchedule();
				return false;
			});
			findPreference("commandscheduledelete").setOnPreferenceClickListener(preference -> {
				((ScheduleActivity) getActivity()).deleteSchedule();
				return false;
			});
			findPreference("commandschedulecancel").setOnPreferenceClickListener(preference -> {
				((ScheduleActivity) getActivity()).cancel();
				return false;
			});
			findPreference("commandschedulerunnow").setOnPreferenceClickListener(preference -> {
				((ScheduleActivity) getActivity()).runnow();
				return false;
			});
			findPreference("schedule.taskignoresslerrors").setOnPreferenceChangeListener((preference, newValue) -> {
				if ((boolean) newValue)
					new Alert(getActivity()).ignoreSSLWarning();
				return true;
			});
			changeIntoNumberPref();
		}

		private void changeIntoNumberPref() {
			new NumberPref().change(findPreference("scheduleweeks"));
			new NumberPref().change(findPreference("scheduledays"));
			new NumberPref().change(findPreference("schedulehours"));
			new NumberPref().change(findPreference("scheduleminutes"));
			new NumberPref().change(findPreference("schedule.taskcheckhyperlinksmaxdepth"));
			new NumberPref().change(findPreference("schedule.tasktimeout"));
			new NumberPref().change(findPreference("schedule.taskretry"));
			new NumberPref().change(findPreference("schedule.taskretrydelay"));
		}
	}
}