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

import io.github.ismywebsiteup.db.Task;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Cache;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CheckJavaScriptHyperlinks {
	public int size = 0;
	private Task mTask;
	private String startURL;
	private WebClient mWebClient;
	private HtmlPage mPage;
	private Set<String> sources = new HashSet<>();

	public CheckJavaScriptHyperlinks(Task task) {
		mTask = task;
	}

	public String getSource() throws IOException, JSONException {
		mWebClient = makeWebClient();
		WebRequest wr = makeWebRequest();
		mPage = mWebClient.getPage(wr);
		mWebClient.waitForBackgroundJavaScript(1000);
		sources.add(mPage.asXml());
		size += mPage.asXml().length() / 1024;
		startURL = getCurrentURL();
		List<Object> objects = mPage.getByXPath("*");
		for (Object object : objects) {
			if (object instanceof HtmlElement) {
				checkElementLinks((HtmlElement) object);
			}
		}
		StringBuilder combineSource = new StringBuilder();
		for (String source : sources) {
			combineSource.append(source);
		}
		mWebClient.close();
		return combineSource.toString();
	}

	private String getCurrentURL() {
		return mWebClient.getCurrentWindow().getEnclosedPage().getUrl().toString();
	}

	private void checkElementLinks(HtmlElement el) {
		List<Object> objects = el.getByXPath("*");
		for (Object object : objects) {
			if (object instanceof HtmlElement) {
				checkElementLinks((HtmlElement) object);
			}
		}
		if (el.getOnClickAttribute().length() > 0 && !el.hasAttribute("href")) {
			try {
				el.click();
				mWebClient.waitForBackgroundJavaScript(1000);
				sources.add(mPage.asXml());
				if (!getCurrentURL().equals(startURL)) {
					sources.add("<a href=" + getCurrentURL() + "></a>");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private WebClient makeWebClient() {
		WebClient wc = new WebClient(BrowserVersion.BEST_SUPPORTED);
		wc.getOptions().setRedirectEnabled(true);
		wc.getOptions().setTimeout(mTask.timeout);
		wc.getOptions().setDownloadImages(false);
		wc.setJavaScriptTimeout(60000);
		if (mTask.ignoreSSLErrors) {
			wc.getOptions().setUseInsecureSSL(true);
		}
		Cache c = new Cache();
		c.clear();
		wc.setCache(c);
		return wc;
	}

	private WebRequest makeWebRequest() throws MalformedURLException, JSONException {
		WebRequest wr = new WebRequest(new URL(mTask.URL));
		Map<String, String> headers = new HashMap<>();
		if (mTask.authorizationHeader.length() > 0) {
			headers.put("Authorization", mTask.authorizationHeader);
		}
		if (mTask.acceptHeader.length() > 0) {
			headers.put("Accept", mTask.acceptHeader);
		}
		if (mTask.acceptCharsetHeader.length() > 0) {
			headers.put("Accept-Charset", mTask.acceptCharsetHeader);
		}
		if (mTask.acceptEncodingHeader.length() > 0) {
			headers.put("Accept-Encoding", mTask.acceptEncodingHeader);
		}
		if (mTask.acceptLanguageHeader.length() > 0) {
			headers.put("Accept-Language", mTask.acceptLanguageHeader);
		}
		headers.put("User-Agent", mTask.userAgent);
		if (mTask.referer.length() > 0) {
			headers.put("Referer", mTask.referer);
		}
		if (mTask.sendDNT) {
			headers.put("DNT", "1");
		}
		if (mTask.customHeaders.length() > 0) {
			JSONObject custom = new JSONObject(mTask.customHeaders);
			Iterator<String> keys = custom.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				headers.put(key, custom.getString(key));
			}
		}
		wr.setAdditionalHeaders(headers);

		if (Prefs.getBoolean("useproxy", false)) {
			String address = Prefs.getString("proxyaddress", "");
			int port = Integer.parseInt(Prefs.getString("proxyport", "0"));
			wr.setProxyHost(address);
			wr.setProxyPort(port);
			wr.setSocksProxy(Prefs.getString("proxytype", "HTTP").equals("SOCKS"));
		}

		return wr;
	}

}
