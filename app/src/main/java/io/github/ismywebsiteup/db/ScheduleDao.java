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
public interface ScheduleDao {
	@Query("SELECT * from schedule_table ORDER BY id DESC")
	LiveData<List<Schedule>> all();

	@Query("SELECT * from schedule_table WHERE nextRun <= :currentTimeMillis")
	List<Schedule> allReady(long currentTimeMillis);

	@Delete
	void delete(Schedule schedule);

	@Query("SELECT * from schedule_table WHERE id = :id LIMIT 1")
	Schedule get(int id);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Schedule schedule);

	@Query("SELECT * from schedule_table WHERE nextRun > :currentTimeMillis ORDER BY nextRun ASC LIMIT 1")
	Schedule nextInFuture(long currentTimeMillis);

	@Query("SELECT * from schedule_table WHERE url LIKE '%' || :searchWord || '%' "
			+ "OR name LIKE '%' || :searchWord || '%' ORDER BY id DESC")
	LiveData<List<Schedule>> search(String searchWord);

	@Update
	void update(Schedule schedule);
}
