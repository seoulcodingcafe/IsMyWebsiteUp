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

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {

	private LiveData<List<Schedule>> mAllSchedules;
	private Application mApplication;

	public ScheduleViewModel(Application application) {
		super(application);
		mApplication = application;
		mAllSchedules = Schedule.all(mApplication);
	}

	public LiveData<List<Schedule>> getAllSchedules() {
		return mAllSchedules;
	}

	public LiveData<List<Schedule>> search(String searchWord) {
		return Schedule.search(searchWord, mApplication);
	}
}
