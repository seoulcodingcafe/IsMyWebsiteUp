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

import android.annotation.SuppressLint;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class TrustManagerBuilder {

	public X509TrustManager buildSkeleton() {
		return new X509TrustManager() {
			@SuppressLint("TrustAllX509TrustManager")
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
			}

			@SuppressLint("TrustAllX509TrustManager")
			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

		};
	}
}
