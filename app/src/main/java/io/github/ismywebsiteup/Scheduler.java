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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import io.github.ismywebsiteup.db.Schedule;
import io.github.ismywebsiteup.db.Task;
import io.github.ismywebsiteup.tools.CPUWakeLock;
import io.github.ismywebsiteup.tools.CheckNetwork;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

public class Scheduler extends Service {
	private boolean running = false;

	public Scheduler() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!running) {
			running = true; // prevent several onStartCommands
			arrangeSchedule();
			running = false;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void arrangeSchedule() {
		CPUWakeLock cpuWakeLock = new CPUWakeLock(this);
		cpuWakeLock.acquireIfIsEnabled();
		Task.clean(this);
		List<Schedule> ready = Schedule.allReady(this);
		if (ready.size() == 0) {
			cpuWakeLock.releaseIfIsHeld();
			return;
		}
		if (!new CheckNetwork(this).isConnected()) {
			scheduleSelf(System.currentTimeMillis() + 60000);
			if (new CheckNetwork(this).isConfigWifiOnly()) {
				new Notification(this).showWifiProblem();
			} else {
				new Notification(this).showConnectionProblem();
			}
			cpuWakeLock.releaseIfIsHeld();
			return;
		}
		new Notification(this).hideConnectionProblem();
		for (Schedule schedule : ready)
			schedule.queue(this);
		if (ready.size() > 0) {
			ContextCompat.startForegroundService(this, new Intent(this, MainService.class));
		}
		Schedule nextInFuture = Schedule.nextInFuture(this);
		if (nextInFuture != null) {
			long nextRunTime = nextInFuture.nextRun;
			if (nextRunTime < System.currentTimeMillis())
				nextRunTime += Math.round(Math
						.ceil((System.currentTimeMillis() - nextInFuture.nextRun) / (1.0 * nextInFuture.getEveryMs())))
						* nextInFuture.getEveryMs();
			scheduleSelf(nextRunTime);
		}
		cpuWakeLock.releaseIfIsHeld();
	}

	private void scheduleSelf(long time) {
		Intent intent = new Intent(this, Scheduler.class);
		PendingIntent pIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager aManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		aManager.cancel(pIntent);
		boolean exact = !Prefs.getBoolean("allowinexactscheduletimes", false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && exact)
			aManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pIntent);
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && exact)
			aManager.setExact(AlarmManager.RTC_WAKEUP, time, pIntent);
		else
			aManager.set(AlarmManager.RTC_WAKEUP, time, pIntent);
	}
}
