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

package io.github.ismywebsiteup.ui.result;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.github.ismywebsiteup.R;
import io.github.ismywebsiteup.db.TaskViewModel;

public class ResultFragment extends Fragment {

	private ResultListAdapter mAdapter;
	private EditText mSearch;
	private TaskViewModel mTaskViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_result, container, false);
		RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
		mAdapter = new ResultListAdapter(getActivity());
		recyclerView.setAdapter(mAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
		mTaskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> mAdapter.setResults(tasks));
		mSearch = root.findViewById(R.id.search);
		mSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				search();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
		return root;
	}

	private void search() {
		if (mSearch.getText().length() < 1)
			mTaskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> mAdapter.setResults(tasks));
		else
			mTaskViewModel.search(mSearch.getText().toString()).observe(getViewLifecycleOwner(),
					tasks -> mAdapter.setResults(tasks));
	}
}
