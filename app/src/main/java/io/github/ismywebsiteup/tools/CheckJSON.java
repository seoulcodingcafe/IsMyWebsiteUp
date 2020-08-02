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

import org.json.JSONException;
import org.json.JSONObject;

public class CheckJSON {
	public boolean check(String json, boolean canEmpty) {
		if (json.length() == 0 && canEmpty)
			return true;
		try {
			new JSONObject(json);
		} catch (JSONException j) {
			return false;
		}
		return true;
	}
}
