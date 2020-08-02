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

import com.pixplicity.easyprefs.library.Prefs;

import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.OkHttpClient;

public class ProxyBuilder {

	public OkHttpClient.Builder applyProxyIntoBuilder(OkHttpClient.Builder builder) {
		if (!Prefs.getBoolean("useproxy", false))
			return builder;
		Proxy.Type type = Proxy.Type.HTTP;
		if (Prefs.getString("proxytype", "HTTP").equals("SOCKS"))
			type = Proxy.Type.SOCKS;
		String address = Prefs.getString("proxyaddress", "");
		int port = Integer.parseInt(Prefs.getString("proxyport", "0"));
		builder.proxy(new Proxy(type, new InetSocketAddress(address, port)));
		return builder;
	}
}
