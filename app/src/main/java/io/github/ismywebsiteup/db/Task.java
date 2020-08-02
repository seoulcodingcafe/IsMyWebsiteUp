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
import io.github.ismywebsiteup.Notification;
import io.github.ismywebsiteup.R;
import io.github.ismywebsiteup.tools.CheckJavaScriptHyperlinks;
import io.github.ismywebsiteup.tools.CheckNetwork;
import io.github.ismywebsiteup.tools.HyperlinkRegex;
import io.github.ismywebsiteup.tools.ProxyBuilder;
import io.github.ismywebsiteup.tools.TrustManagerBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//import okhttp3.internal.annotations.EverythingIsNonNull;

@Entity(tableName = "task_table")
public class Task {
	@PrimaryKey(autoGenerate = true)
	public int id;
	@NonNull
	public String nameOfSchedule = "";
	public int scheduleId = -1;
	@NonNull
	public String URL;
	public boolean checkHyperlinks;
	public int checkHyperlinksDepth;
	public boolean checkIfMatchRegex;
	@NonNull
	public String checkRegex = "";
	public boolean dontCheckIfMatchRegex;
	@NonNull
	public String dontCheckRegex = "";
	@NonNull
	public String ignoredByRegex = "";
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
	public boolean sendDNT;
	public boolean running = false;
	public boolean complete = false;
	public int retryDelay = 10;
	public long retryTime;
	@NonNull
	public String code = "-";
	public long received = 0;
	public long createdAt = System.currentTimeMillis();
	@NonNull
	public String ignoredDomains = "";
	public boolean isBinary = false;
	@NonNull
	public String parentURLs = "";
	public boolean checkBinaryResponse = false;
	public boolean ignoreSSLErrors;

	public static LiveData<List<Task>> all(Context context) {
		clean(context);
		return Database.getDatabase(context).taskDao().all();
	}

	public static void clean(Context context) {
		int globalLimit = Integer.parseInt(Prefs.getString("autocleanoldcount", "50"));
		Database.getDatabase(context).taskDao().cleanGlobal(globalLimit);
		int successDays = Integer.parseInt(Prefs.getString("autocleansuccessresultsafter", "1"));
		int failDays = Integer.parseInt(Prefs.getString("autocleanfailresultsafter", "7"));
		long successLimit = System.currentTimeMillis() - successDays * 24 * 60 * 60 * 1000;
		long failLimit = System.currentTimeMillis() - failDays * 24 * 60 * 60 * 1000;
		Database.getDatabase(context).taskDao().cleanSuccess(successLimit);
		Database.getDatabase(context).taskDao().cleanFail(failLimit);
	}

	public static void deleteAll(Context context) {
		Database.getDatabase(context).taskDao().deleteAll();
	}

	public static Task futureNext(Context context) {
		return Database.getDatabase(context).taskDao().futureNext();
	}

	public static Task next(Context context) {
		return Database.getDatabase(context).taskDao().next();
	}

	public static LiveData<List<Task>> search(String searchWord, Context context) {
		return Database.getDatabase(context).taskDao().search(searchWord);
	}

	public String buildResultString(boolean verbose, Context context) {
		String resultString = "";
		if (nameOfSchedule.length() > 0)
			resultString += nameOfSchedule + ", ";
		resultString += new PrettyTime().format(new Date(createdAt)) + "\n";
		if (parentURLs.length() > 0)
			resultString += context.getString(R.string.from_hyperlink) + " ";
		resultString += URL + "\n";
		resultString += context.getString(R.string.code) + ": " + code + "\n";

		if (retry >= 0 && !running && !complete) {
			if (retryTime > System.currentTimeMillis()) {
				resultString += context.getString(R.string.waiting_for_retry) + " " + (maxRetry - retry) + " / "
						+ maxRetry;
			} else
				resultString += context.getString(R.string.queueing);
		} else {
			if (!isBinary) {
				resultString += context.getString(R.string.received) + ": " + received + context.getString(R.string.KB);
			} else {
				resultString += context.getString(R.string.ignore_binary);
			}
		}
		if (ignoredByRegex.length() > 0) {
			if (!verbose) {
				resultString += "\n" + context.getString(R.string.ignored_by_regex) + ": "
						+ ignoredByRegex.split("\n").length;
			} else {
				resultString += "\n" + context.getString(R.string.ignored_by_regex);
				for (String ignored : ignoredByRegex.split("\n"))
					resultString += "\n" + context.getString(R.string.bullet) + " " + ignored;
			}
		}
		if (ignoredDomains.length() > 0) {
			if (!verbose) {
				resultString += "\n" + context.getString(R.string.ignored_domains) + ": "
						+ ignoredDomains.split(",").length;
			} else {
				resultString += "\n" + context.getString(R.string.ignored_domains);
				for (String ignored : ignoredDomains.split(","))
					resultString += "\n" + context.getString(R.string.bullet) + " " + ignored;
			}
		}
		return resultString;
	}

	public void delete(Context context) {
		Database.getDatabase(context).taskDao().delete(this);
	}

	public void insert(Context context) {
		Database.getDatabase(context).taskDao().insert(this);
	}

	public void run(final Context context) {
		CheckNetwork check = new CheckNetwork(context);
		if (check.isConfigWifiOnly() && !check.isConnectedToWifi()) {
			whenRequestFailed(context);
			return;
		}
		final OkHttpClient.Builder initialClientBuilder = new OkHttpClient.Builder()
				.connectTimeout(this.timeout, TimeUnit.SECONDS).readTimeout(this.timeout, TimeUnit.SECONDS)
				.writeTimeout(this.timeout, TimeUnit.SECONDS);
		final OkHttpClient.Builder[] clientBuilder = { null };
		Thread proxyThread = new Thread(
				() -> clientBuilder[0] = new ProxyBuilder().applyProxyIntoBuilder(initialClientBuilder));
		proxyThread.start();
		try {
			proxyThread.join();
		} catch (InterruptedException i) {
			whenRequestFailed(context);
			return;
		}
		runAfterProxyConfig(clientBuilder[0], context);
	}

	public void update(Context context) {
		Database.getDatabase(context).taskDao().update(this);
	}

	private boolean canCheckChildDomain(String domain) {
		for (String hyperlinkDomain : checkHyperlinksDomain.split(",")) {
			if (domain.toLowerCase().trim().equals(hyperlinkDomain.toLowerCase().trim())) {
				return true;
			}
		}
		if (ignoredDomains.length() > 0) {
			ignoredDomains += ",";
		}
		ignoredDomains += domain;
		return false;
	}

	private void createChildTask(String URL, Context context) {
		Task child = new Task();
		child.URL = URL;
		child.checkHyperlinksDepth = this.checkHyperlinksDepth - 1;
		child.checkHyperlinks = this.checkHyperlinks;
		child.checkIfMatchRegex = this.checkIfMatchRegex;
		child.checkRegex = this.checkRegex;
		child.dontCheckIfMatchRegex = this.dontCheckIfMatchRegex;
		child.dontCheckRegex = this.dontCheckRegex;
		child.checkHyperlinksSpecificDomains = this.checkHyperlinksSpecificDomains;
		child.checkHyperlinksDomain = this.checkHyperlinksDomain;
		child.userAgent = this.userAgent;
		child.timeout = this.timeout;
		child.sendDNT = this.sendDNT;
		child.retry = this.maxRetry;
		child.retryDelay = this.retryDelay;
		child.maxRetry = this.maxRetry;
		child.checkHyperlinksSendReferer = this.checkHyperlinksSendReferer;
		child.checkJavaScriptHyperlinks = this.checkJavaScriptHyperlinks;
		child.checkHyperlinksIgnoreSymbol = this.checkHyperlinksIgnoreSymbol;
		if (checkHyperlinksSendReferer) {
			child.referer = this.URL;
		} else {
			child.referer = this.referer;
		}
		if (parentURLs.length() == 0) {
			child.parentURLs = removeParameterOfURL(this.URL);
		} else {
			child.parentURLs = this.parentURLs + "," + removeParameterOfURL(this.URL);
		}
		child.authorizationHeader = this.authorizationHeader;
		child.acceptHeader = this.acceptHeader;
		child.acceptCharsetHeader = this.acceptCharsetHeader;
		child.acceptEncodingHeader = this.acceptEncodingHeader;
		child.acceptLanguageHeader = this.acceptLanguageHeader;
		child.customHeaders = this.customHeaders;
		child.checkBinaryResponse = this.checkBinaryResponse;
		child.ignoreSSLErrors = this.ignoreSSLErrors;
		child.nameOfSchedule = this.nameOfSchedule;
		child.insert(context);
	}

	private boolean isURLBackToParent(String url) {
		if (removeEndSlashes(removeParameterOfURL(url.toLowerCase().trim()))
				.equals(removeEndSlashes(removeParameterOfURL(URL.trim().toLowerCase()))))
			return true;
		for (String parentURL : parentURLs.split(",")) {
			if (removeEndSlashes(removeParameterOfURL(parentURL.toLowerCase().trim()))
					.equals(removeEndSlashes(removeParameterOfURL(url.toLowerCase().trim())))) {
				return true;
			}
		}
		return false;
	}

	private String removeEndSlashes(String string) {
		String remove = string;
		while (remove.endsWith("/") && remove.length() > 1)
			remove = remove.substring(0, remove.length() - 1);
		return remove;
	}

	private String removeParameterOfURL(String string) {
		if (!checkHyperlinksIgnoreSymbol)
			return string;
		String remove = string;
		if (remove.contains("?"))
			remove = remove.substring(0, remove.indexOf("?"));
		if (remove.contains("#"))
			remove = remove.substring(0, remove.indexOf("#"));
		return remove;
	}

	private void runAfterProxyConfig(OkHttpClient.Builder clientBuilder, final Context context) {
		if (this.ignoreSSLErrors) {
			try {
				final TrustManager[] trustAllCerts = new TrustManager[] { new TrustManagerBuilder().buildSkeleton() };
				final SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
				final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
				clientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
						.hostnameVerifier((hostname, session) -> true);
			} catch (NoSuchAlgorithmException | KeyManagementException nk) {
				nk.printStackTrace();
			}
		}

		OkHttpClient client = clientBuilder.build();
		Request.Builder requestBuilder = new Request.Builder()
				.cacheControl(new CacheControl.Builder().noCache().noStore().build());
		try {
			requestBuilder.url(this.URL);
		} catch (IllegalArgumentException i) {
			delete(context);
			return;
		}
		if (this.authorizationHeader.length() > 0) {
			requestBuilder.addHeader("Authorization", this.authorizationHeader);
		}
		if (this.acceptHeader.length() > 0) {
			requestBuilder.addHeader("Accept", this.acceptHeader);
		}
		if (this.acceptCharsetHeader.length() > 0) {
			requestBuilder.addHeader("Accept-Charset", this.acceptCharsetHeader);
		}
		if (this.acceptEncodingHeader.length() > 0) {
			requestBuilder.addHeader("Accept-Encoding", this.acceptEncodingHeader);
		}
		if (this.acceptLanguageHeader.length() > 0) {
			requestBuilder.addHeader("Accept-Language", this.acceptLanguageHeader);
		}
		if (this.userAgent.length() > 0) {
			requestBuilder.addHeader("User-Agent", this.userAgent);
		}
		if (this.referer.length() > 0) {
			requestBuilder.addHeader("Referer", this.referer);
		}
		if (this.sendDNT) {
			requestBuilder.addHeader("DNT", "1");
		}
		if (this.customHeaders.length() > 0) {
			try {
				JSONObject custom = new JSONObject(this.customHeaders);
				Iterator<String> keys = custom.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					requestBuilder.addHeader(key, custom.getString(key));
				}
			} catch (JSONException j) {
				whenRequestFailed(context);
				return;
			}
		}

		Request request = requestBuilder.build();
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			//@EverythingIsNonNull
			public void onFailure(Call call, IOException e) {
				whenRequestFailed(context);
			}

			//@EverythingIsNonNull
			public void onResponse(Call call, Response response) throws IOException {
				whenRequestStart(response, context);
			}
		});
	}

	private boolean shouldResponseStringBeRead(Response response) {
		String contentType = "text";
		try {
			contentType = response.body().contentType().type();
		} catch (NullPointerException n) {
			n.printStackTrace();
		}
		return contentType.trim().toLowerCase().equals("text");
	}

	public void whenRequestFailed(Context context) {
		this.retryTime = System.currentTimeMillis() + this.retryDelay * 1000;
		this.running = false;
		update(context);
		if (this.retry < 0) {
			(new Notification(context)).showWebsiteDown();
		}
		ContextCompat.startForegroundService(context, new Intent(context, MainService.class));
	}

	private void whenRequestStart(Response response, Context context) {
		this.code = "" + response.code();
		update(context);
		if (!response.isSuccessful()) {
			whenRequestFailed(context);
			return;
		}
		long size = 0;
		try {
			if (shouldResponseStringBeRead(response)) {
				if (checkHyperlinks && !checkJavaScriptHyperlinks) {
					String stringOfResponse = response.body().string();
					size = stringOfResponse.length();
					checkStringOfResponse(stringOfResponse, context);
				} else if (checkHyperlinks && checkJavaScriptHyperlinks) {
					response.body().close();
					CheckJavaScriptHyperlinks cjs = new CheckJavaScriptHyperlinks(this);
					size = cjs.size;
					checkStringOfResponse(cjs.getSource(), context);
				} else {
					response.body().close();
				}
			} else {
				if (!this.checkBinaryResponse) {
					isBinary = true;
					response.body().close();
				} else {
					int bufferSize = Integer.parseInt(Prefs.getString("buffersize", "128"));
					BufferedInputStream input = new BufferedInputStream(response.body().byteStream());
					byte[] data = new byte[bufferSize * 1024];
					long length = 0;
					if (this.received > 0) {
						input.skip(this.received * 1024);
						size = this.received * 1024;
					}
					long lastDB = 0;
					long DBRate = Long.parseLong(Prefs.getString("dbrefreshrate", "1000"));

					while ((length = input.read(data)) != -1) {
						size += length;
						if (lastDB + DBRate <= System.currentTimeMillis()) {
							lastDB = System.currentTimeMillis();
							this.received = size / 1024;
							update(context);
						}
					}
					response.body().close();
				}
				update(context);
			}
		} catch (IOException | JSONException ij) {
			whenRequestFailed(context);
			return;
		}
		this.received = size / 1024;
		whenRequestSuccess(context);
	}

	private void whenRequestSuccess(Context context) {
		this.running = false;
		this.complete = true;
		update(context);
		ContextCompat.startForegroundService(context, new Intent(context, MainService.class));
	}

	private void checkStringOfResponse(String stringOfResponse, Context context) {
		Document document = Jsoup.parse(stringOfResponse, this.URL);
		if (checkHyperlinks && checkHyperlinksDepth > 0) {
			Set<String> childrenHyperlinks = new HashSet<>();
			Elements links = document.select("[href]");
			for (Element link : links) {
				childrenHyperlinks.add(link.attr("abs:href"));
			}
			links = document.select("[src]");
			for (Element link : links) {
				childrenHyperlinks.add(link.attr("abs:src"));
			}
			HyperlinkRegex checkRegex = new HyperlinkRegex(this);
			Set<String> filterChildrenHyperlinks = new HashSet<>();
			for (String hyperlink : childrenHyperlinks) {
				try {
					if (checkRegex.canCheckHyperlink(hyperlink)
							&& (!checkHyperlinksSpecificDomains || canCheckChildDomain(new URI(hyperlink).getHost()))
							&& !isURLBackToParent(hyperlink)) {
						if (parentURLs.length() != 0) parentURLs +=  ",";
						parentURLs += removeParameterOfURL(hyperlink);
						filterChildrenHyperlinks.add(hyperlink);
					}
				} catch (URISyntaxException u) {
					u.printStackTrace();
					//skip URL if invalid
				}
			}
			for (String filterHyperlink : filterChildrenHyperlinks) {
				createChildTask(filterHyperlink, context);
				update(context);
			}
		}
	}

}
