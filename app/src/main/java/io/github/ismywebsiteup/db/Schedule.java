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

package io.github.ismywebsiteup.db;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import io.github.ismywebsiteup.MainService;
import io.github.ismywebsiteup.R;
import com.pixplicity.easyprefs.library.Prefs;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

@Entity(tableName = "schedule_table")
public class Schedule {
	@PrimaryKey(autoGenerate = true)
	public int id;
	public int weeks = 0;
	public int days = 1;
	public int hours = 0;
	public int minutes = 0;
	public long lastRun = 0;
	public long nextRun = 0;
	@NonNull
	public String name = "";
	@NonNull
	public String URL = "";
	public boolean checkHyperlinks;
	public int checkHyperlinksDepth;
	public boolean checkIfMatchRegex;
	@NonNull
	public String checkRegex = "";
	public boolean dontCheckIfMatchRegex;
	@NonNull
	public String dontCheckRegex = "";
	public boolean checkHyperlinksSpecificDomains;
	@NonNull
	public String checkHyperlinksDomain = "";
	public boolean checkJavaScriptHyperlinks;
	public boolean checkHyperlinksIgnoreSymbol;
	public boolean checkHyperlinksSendReferer;
	@NonNull
	public String userAgent = "";
	@NonNull
	public String authorizationHeader = "";
	@NonNull
	public String acceptHeader = "";
	@NonNull
	public String acceptCharsetHeader = "";
	@NonNull
	public String acceptEncodingHeader = "";
	@NonNull
	public String acceptLanguageHeader = "";
	@NonNull
	public String customHeaders = "";
	@NonNull
	public String referer = "";
	public int timeout;
	public int retry;
	public int maxRetry;
	public int retryDelay = 10;
	public boolean sendDNT;
	public boolean checkBinaryResponse = false;
	public boolean ignoreSSLErrors;

	public static LiveData<List<Schedule>> all(Context context) {
		return Database.getDatabase(context).scheduleDao().all();
	}

	public static List<Schedule> allReady(Context context) {
		return Database.getDatabase(context).scheduleDao().allReady(System.currentTimeMillis());
	}

	public static Schedule get(int id, Context context) {
		return Database.getDatabase(context).scheduleDao().get(id);
	}

	public static Schedule nextInFuture(Context context) {
		return Database.getDatabase(context).scheduleDao().nextInFuture(System.currentTimeMillis());
	}

	public static LiveData<List<Schedule>> search(String searchWord, Context context) {
		return Database.getDatabase(context).scheduleDao().search(searchWord);
	}

	public String buildEveryString(Context context) {
		if (weeks < 1 && days < 1 && hours < 1 && minutes < 1) {
			return "";
		}
		String build = context.getString(R.string.every);
		if (weeks > 0) {
			build += " " + weeks + " " + context.getString(R.string.weeks);
		}
		if (days > 0) {
			build += " " + days + " " + context.getString(R.string.days);
		}
		if (hours > 0) {
			build += " " + hours + " " + context.getString(R.string.hours);
		}
		if (minutes > 0) {
			build += " " + minutes + " " + context.getString(R.string.minutes);
		}
		return build;
	}

	public String buildLastRunString(Context context) {
		if (lastRun < 1) {
			return "";
		}
		return context.getString(R.string.last_run) + " " + new PrettyTime().format(new Date(lastRun));
	}

	public void delete(Context context) {
		Database.getDatabase(context).scheduleDao().delete(this);
	}

	public void fromPrefs() {
		this.name = Prefs.getString("schedule.name", "");
		this.weeks = Integer.parseInt(Prefs.getString("scheduleweeks", "" + this.weeks));
		this.days = Integer.parseInt(Prefs.getString("scheduledays", "" + this.days));
		this.hours = Integer.parseInt(Prefs.getString("schedulehours", "" + this.hours));
		this.minutes = Integer.parseInt(Prefs.getString("scheduleminutes", "" + this.minutes));
		this.URL = Prefs.getString("schedule.taskurl", "");
		this.nextRun = lastRun + getEveryMs();
		this.checkHyperlinks = Prefs.getBoolean("schedule.taskcheckhyperlinks", false);
		this.checkHyperlinksDepth = Integer.parseInt(Prefs.getString("schedule.taskcheckhyperlinksmaxdepth", "" + 1));
		this.checkIfMatchRegex = Prefs.getBoolean("schedule.taskcheckhyperlinkifmatchregex", false);
		this.checkRegex = Prefs.getString("schedule.taskcheckhyperlinkregex", "");
		this.dontCheckIfMatchRegex = Prefs.getBoolean("schedule.taskdontcheckhyperlinkifmatchregex", false);
		this.dontCheckRegex = Prefs.getString("schedule.taskdontcheckhyperlinkregex", "");
		this.checkHyperlinksSpecificDomains = Prefs.getBoolean("schedule.taskcheckhyperlinksonlyforspecificdomains",
				false);
		this.checkHyperlinksIgnoreSymbol = Prefs.getBoolean("schedule.taskcheckhyperlinksignorepart", true);
		this.checkJavaScriptHyperlinks = Prefs.getBoolean("schedule.taskcheckjavascripthyperlinks", false);
		this.checkHyperlinksDomain = Prefs.getString("schedule.taskcheckhyperlinksdomains", "");
		this.checkHyperlinksSendReferer = Prefs.getBoolean("schedule.taskcheckhyperlinkssendreferer", false);
		this.userAgent = Prefs.getString("schedule.taskuseragent", "");
		this.referer = Prefs.getString("schedule.taskreferer", "");
		this.retry = Integer.parseInt(Prefs.getString("schedule.taskretry", "3"));
		this.retryDelay = Integer.parseInt(Prefs.getString("schedule.taskretrydelay", "10"));
		this.maxRetry = Integer.parseInt(Prefs.getString("schedule.taskretry", "3"));
		this.timeout = Integer.parseInt(Prefs.getString("schedule.tasktimeout", "10"));
		this.sendDNT = Prefs.getBoolean("schedule.tasksenddnt", false);
		this.authorizationHeader = Prefs.getString("schedule.taskauthorizationheader", "");
		this.acceptHeader = Prefs.getString("schedule.taskacceptheader", "");
		this.acceptCharsetHeader = Prefs.getString("schedule.taskacceptcharsetheader", "");
		this.acceptEncodingHeader = Prefs.getString("schedule.taskacceptencodingheader", "");
		this.acceptLanguageHeader = Prefs.getString("schedule.taskacceptlanguageheader", "");
		this.customHeaders = Prefs.getString("schedule.taskcustomheaders", "");
		this.checkBinaryResponse = Prefs.getBoolean("schedule.taskcheckbinaryresponses", false);
		this.ignoreSSLErrors = Prefs.getBoolean("schedule.taskignoresslerrors", false);
	}

	public long getEveryMs() {
		long weeks = this.weeks * 7 * 24 * 60 * 60 * 1000;
		long days = this.days * 24 * 60 * 60 * 1000;
		long hours = this.hours * 60 * 60 * 1000;
		long minutes = this.minutes * 60 * 1000;
		return weeks + days + hours + minutes;
	}

	public void insert(Context context) {
		Database.getDatabase(context).scheduleDao().insert(this);
	}

	public void queue(Context context) {
		updateLastAndNextRunTimes(context);
		Task task = asTask();
		task.insert(context);
	}

	public void run(Context context) {
		updateLastAndNextRunTimes(context);
		Task task = asTask();
		task.insert(context);
		ContextCompat.startForegroundService(context, new Intent(context, MainService.class));
	}

	public void toPrefs() {
		Prefs.putString("schedule.name", this.name);
		Prefs.putString("scheduleweeks", "" + this.weeks);
		Prefs.putString("scheduledays", "" + this.days);
		Prefs.putString("schedulehours", "" + this.hours);
		Prefs.putString("scheduleminutes", "" + this.minutes);
		Prefs.putString("schedule.taskurl", this.URL);
		Prefs.putBoolean("schedule.taskcheckhyperlinks", this.checkHyperlinks);
		Prefs.putString("schedule.taskcheckhyperlinksmaxdepth", "" + this.checkHyperlinksDepth);
		Prefs.putBoolean("schedule.taskcheckhyperlinkifmatchregex", this.checkIfMatchRegex);
		Prefs.putString("schedule.taskcheckhyperlinkregex", this.checkRegex);
		Prefs.putBoolean("schedule.taskdontcheckhyperlinkifmatchregex", this.dontCheckIfMatchRegex);
		Prefs.putString("schedule.taskdontcheckhyperlinkregex", this.dontCheckRegex);
		Prefs.putBoolean("schedule.taskcheckhyperlinksonlyforspecificdomains", this.checkHyperlinksSpecificDomains);
		Prefs.putString("schedule.taskcheckhyperlinksdomains", this.checkHyperlinksDomain);
		Prefs.putBoolean("schedule.taskcheckhyperlinksignorepart", this.checkHyperlinksIgnoreSymbol);
		Prefs.putBoolean("schedule.taskcheckjavascripthyperlinks", this.checkJavaScriptHyperlinks);
		Prefs.putBoolean("schedule.taskcheckhyperlinkssendreferer", this.checkHyperlinksSendReferer);
		Prefs.putString("schedule.taskuseragent", this.userAgent);
		Prefs.putString("schedule.taskreferer", this.referer);
		Prefs.putString("schedule.taskretry", "" + this.maxRetry);
		Prefs.putString("schedule.taskretrydelay", "" + this.retryDelay);
		Prefs.putString("schedule.tasktimeout", "" + this.timeout);
		Prefs.putBoolean("schedule.tasksenddnt", this.sendDNT);
		Prefs.putString("schedule.taskauthorizationheader", this.authorizationHeader);
		Prefs.putString("schedule.taskacceptheader", this.acceptHeader);
		Prefs.putString("schedule.taskacceptcharsetheader", this.acceptCharsetHeader);
		Prefs.putString("schedule.taskacceptencodingheader", this.acceptEncodingHeader);
		Prefs.putString("schedule.taskacceptlanguageheader", this.acceptLanguageHeader);
		Prefs.putString("schedule.taskcustomheaders", this.customHeaders);
		Prefs.putBoolean("schedule.taskcheckbinaryresponses", this.checkBinaryResponse);
		Prefs.putBoolean("schedule.taskignoresslerrors", this.ignoreSSLErrors);
	}

	public void update(Context context) {
		Database.getDatabase(context).scheduleDao().update(this);
	}

	public void updateLastAndNextRunTimes(Context context) {
		lastRun = System.currentTimeMillis();
		nextRun = lastRun + getEveryMs();
		update(context);
	}

	private Task asTask() {
		Task task = new Task();
		task.URL = this.URL;
		task.scheduleId = this.id;
		task.nameOfSchedule = this.name;
		task.checkHyperlinks = this.checkHyperlinks;
		task.checkHyperlinksDepth = this.checkHyperlinksDepth;
		task.checkIfMatchRegex = this.checkIfMatchRegex;
		task.checkRegex = this.checkRegex;
		task.dontCheckIfMatchRegex = this.dontCheckIfMatchRegex;
		task.dontCheckRegex = this.dontCheckRegex;
		task.checkHyperlinksSpecificDomains = this.checkHyperlinksSpecificDomains;
		task.checkHyperlinksDomain = this.checkHyperlinksDomain;
		task.checkHyperlinksSendReferer = this.checkHyperlinksSendReferer;
		task.checkJavaScriptHyperlinks = this.checkJavaScriptHyperlinks;
		task.checkHyperlinksIgnoreSymbol = this.checkHyperlinksIgnoreSymbol;
		task.userAgent = this.userAgent;
		task.referer = this.referer;
		task.retry = this.maxRetry;
		task.retryDelay = this.retryDelay;
		task.maxRetry = this.maxRetry;
		task.timeout = this.timeout;
		task.sendDNT = this.sendDNT;
		task.authorizationHeader = this.authorizationHeader;
		task.acceptHeader = this.acceptHeader;
		task.acceptCharsetHeader = this.acceptCharsetHeader;
		task.acceptEncodingHeader = this.acceptEncodingHeader;
		task.acceptLanguageHeader = this.acceptLanguageHeader;
		task.customHeaders = this.customHeaders;
		task.checkBinaryResponse = this.checkBinaryResponse;
		task.ignoreSSLErrors = this.ignoreSSLErrors;
		return task;
	}
}
