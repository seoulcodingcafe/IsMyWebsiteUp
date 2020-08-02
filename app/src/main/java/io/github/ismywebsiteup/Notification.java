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

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import io.github.ismywebsiteup.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Notification {
	private Context mContext;
	private android.app.Notification mNotificationProgress;

	public Notification(Context context) {
		mContext = context;
	}

	public Notification createConnectionProblemChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
			if (notificationManager.getNotificationChannel("CONNECTION") != null)
				return this;
			CharSequence name = mContext.getString(R.string.connection_problem);
			String description = mContext.getString(R.string.when_there_is_a_connection_problem);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel("CONNECTION", name, importance);
			channel.setDescription(description);
			notificationManager.createNotificationChannel(channel);
		}
		return this;
	}

	public Notification createProgressChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
			if (notificationManager.getNotificationChannel("PROGRESS") != null)
				return this;
			CharSequence name = mContext.getString(R.string.progress);
			String description = mContext.getString(R.string.progress_when_we_are_running_in_background);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel("PROGRESS", name, importance);
			channel.setDescription(description);
			channel.enableLights(false);
			channel.enableVibration(false);
			channel.setSound(null, null);
			notificationManager.createNotificationChannel(channel);
		}
		return this;
	}

	public Notification createWebsiteDownChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
			if (notificationManager.getNotificationChannel("WEBSITE.DOWN") != null)
				return this;
			CharSequence name = mContext.getString(R.string.website_down);
			String description = mContext.getString(R.string.when_your_website_is_down);
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel channel = new NotificationChannel("WEBSITE.DOWN", name, importance);
			channel.setDescription(description);
			notificationManager.createNotificationChannel(channel);
		}
		return this;
	}

	public android.app.Notification getmNotificationProgress() {
		return mNotificationProgress;
	}

	public void hideConnectionProblem() {
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(3);
	}

	public void hideProgress() {
		if (mNotificationProgress != null) {
			NotificationManager notificationManager = (NotificationManager) mContext
					.getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(1);
			mNotificationProgress = null;
		}
	}

	public void showConnectionProblem() {
		android.app.Notification notification = new NotificationCompat.Builder(mContext, "CONNECTION")
				.setAutoCancel(true).setOngoing(false).setOnlyAlertOnce(true)
				.setPriority(android.app.Notification.PRIORITY_DEFAULT)
				.setContentTitle(mContext.getString(R.string.no_connection_for_scheduled_task))
				.setContentText(mContext.getString(R.string.scheduled_task_cannot_start_we_will_retry_again_soon))
				.setStyle(new androidx.core.app.NotificationCompat.BigTextStyle()
						.bigText(mContext.getString(R.string.scheduled_task_cannot_start_we_will_retry_again_soon)))
				.setSmallIcon(R.drawable.offline).build();

		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(3, notification);
	}

	public void showProgress(boolean waitingConnection, int min, int max) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "PROGRESS").setAutoCancel(false)
				.setOngoing(true).setOnlyAlertOnce(true).setSound(null)
				.setPriority(android.app.Notification.PRIORITY_LOW).setProgress(min, max, waitingConnection);
		if (waitingConnection) {
			builder.setContentTitle(mContext.getString(R.string.waiting_on_connection));
			builder.setSmallIcon(R.drawable.waiting);
		} else {
			builder.setContentTitle(mContext.getString(R.string.running));
			builder.setSmallIcon(R.drawable.running);
		}

		if (mNotificationProgress != null) {
			mNotificationProgress = builder.build();
			NotificationManager notificationManager = (NotificationManager) mContext
					.getSystemService(NOTIFICATION_SERVICE);
			notificationManager.notify(1, mNotificationProgress);
			return;
		}
		mNotificationProgress = builder.build();
	}

	public void showWebsiteDown() {
		android.app.Notification notification = new NotificationCompat.Builder(mContext, "WEBSITE.DOWN")
				.setAutoCancel(true).setOngoing(false).setOnlyAlertOnce(true)
				.setPriority(android.app.Notification.PRIORITY_HIGH)
				.setContentTitle(mContext.getString(R.string.website_seems_down)).setSmallIcon(R.drawable.down).build();

		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(2, notification);
	}

	public void showWifiProblem() {
		android.app.Notification notification = new NotificationCompat.Builder(mContext, "CONNECTION")
				.setAutoCancel(true).setOngoing(false).setOnlyAlertOnce(true)
				.setPriority(android.app.Notification.PRIORITY_DEFAULT)
				.setContentTitle(mContext.getString(R.string.no_wifi_for_scheduled_task))
				.setContentText(mContext.getString(R.string.scheduled_task_cannot_start_we_will_retry_again_soon))
				.setStyle(new androidx.core.app.NotificationCompat.BigTextStyle()
						.bigText(mContext.getString(R.string.scheduled_task_cannot_start_we_will_retry_again_soon)))
				.setSmallIcon(R.drawable.offline).build();

		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(3, notification);
	}
}
