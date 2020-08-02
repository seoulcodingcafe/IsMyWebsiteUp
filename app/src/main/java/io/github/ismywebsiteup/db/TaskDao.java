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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
	@Query("SELECT * from task_table ORDER BY id DESC")
	LiveData<List<Task>> all();

	@Query("DELETE from task_table WHERE retry<0 AND running=0 AND createdAt < :createdAtLimit")
	void cleanFail(Long createdAtLimit);

	@Query("DELETE from task_table WHERE running=0 AND (complete=1 OR retry<0) AND id NOT IN"
			+ " (SELECT id from task_table WHERE running=0 AND (complete=1 OR retry<0) ORDER BY id DESC LIMIT :limit)")
	int cleanGlobal(int limit);

	@Query("DELETE from task_table WHERE complete=1 AND running=0 AND createdAt < :createdAtLimit")
	void cleanSuccess(Long createdAtLimit);

	@Delete
	void delete(Task task);

	@Query("DELETE from task_table WHERE running=0 AND (complete=1 OR retry<0)")
	void deleteAll();

	@Query("SELECT * from task_table WHERE complete=0 AND running=0 AND retry>-1 AND retryTime > STRFTIME('%s', 'now') * 1000 ORDER BY retryTime, id ASC LIMIT 1")
	Task futureNext();

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Task task);

	@Query("SELECT * from task_table WHERE complete=0 AND running=0 AND retry>-1 AND retryTime <= STRFTIME('%s', 'now') * 1000 ORDER BY retryTime, id ASC LIMIT 1")
	Task next();

	@Query("UPDATE task_table SET running=0")
	void onStart();

	@Query("SELECT * from task_table WHERE url LIKE '%' || :searchWord || '%' "
			+ "OR nameOfSchedule LIKE '%' || :searchWord || '%' ORDER BY id DESC")
	LiveData<List<Task>> search(String searchWord);

	@Update
	void update(Task task);
}
