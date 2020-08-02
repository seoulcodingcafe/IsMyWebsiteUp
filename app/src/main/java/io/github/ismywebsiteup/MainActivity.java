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

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import io.github.ismywebsiteup.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity;
import com.pixplicity.easyprefs.library.Prefs;

public class MainActivity extends CyaneaAppCompatActivity {

	private BottomNavigationView mNavView;

	public void goToResult() {
		mNavView.setSelectedItemId(R.id.navigation_result);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0)
			Prefs.putBoolean("welcomecomplete", true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mNavView = findViewById(R.id.nav_view);
		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_task,
				R.id.navigation_result, R.id.navigation_schedule, R.id.navigation_configuration, R.id.navigation_about)
						.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(mNavView, navController);

		if (!Prefs.getBoolean("welcomecomplete", false)) {
			Intent welcome = new Intent(this, WelcomeActivity.class);
			startActivityForResult(welcome, 0);
		}
	}

}
