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

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import io.github.ismywebsiteup.db.Task;
import io.github.ismywebsiteup.tools.CPUWakeLock;
import io.github.ismywebsiteup.tools.CheckNetwork;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;

public class MainService extends Service {
	ArrayList<Task> tasks = new ArrayList<Task>();
	Notification mNotification;

	private boolean waitForNetwork;
	private CPUWakeLock mCPUWakeLock;

	public MainService() {
		mNotification = new Notification(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNotification.showProgress(false, 0, 1);
		startForeground(1, mNotification.getmNotificationProgress());
		mCPUWakeLock = new CPUWakeLock(this);
		mCPUWakeLock.acquireIfIsEnabled();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		runTask();
		return super.onStartCommand(intent, flags, startId);
	}

	private void closeMyself() {
		mNotification.hideProgress();
		mCPUWakeLock.releaseIfIsHeld();
		stopSelf();
	}

	private void runTask() {
		ArrayList<Task> finished = new ArrayList<>();
		for (Task task : tasks) {
			if (task.complete || !task.running) {
				finished.add(task);
			}
		}
		for (Task task : finished) {
			tasks.remove(task);
		}

		Task next = Task.next(this);
		Task futureNext = Task.futureNext(this);

		if ((next != null || futureNext != null) && !new CheckNetwork(this).isConnected()) {
			waitForNetwork = true;
			scheduleMyself(System.currentTimeMillis() + 5000);
			mNotification.showProgress(waitForNetwork, 0, 1);
			return;
		} else
			waitForNetwork = false;

		int tasksMax = Integer.parseInt(Prefs.getString("multiconnections", "3"));
		while (tasks.size() < tasksMax && next != null) {
			next.retry -= 1;
			next.running = true;
			tasks.add(next);
			next.update(this);
			next.run(this);
			next = Task.next(this);
		}
		if (futureNext != null)
			scheduleMyself(futureNext.retryTime);
		mNotification.showProgress(waitForNetwork, tasksMax - tasks.size(), tasksMax);
		if (tasks.size() == 0 && futureNext == null) {
			closeMyself();
		}
	}

	private void scheduleMyself(long time) {
		final Handler handler = new Handler();
		handler.postDelayed(this::runTask, Math.max(0L, time - System.currentTimeMillis()));
	}

}
