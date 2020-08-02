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

public class TaskViewModel extends AndroidViewModel {

	private LiveData<List<Task>> mAllTasks;
	private Application mApplication;

	public TaskViewModel(Application application) {
		super(application);
		mApplication = application;
		mAllTasks = Task.all(mApplication);
	}

	public LiveData<List<Task>> getAllTasks() {
		return mAllTasks;
	}

	public LiveData<List<Task>> search(String searchWord) {
		return Task.search(searchWord, mApplication);
	}
}
