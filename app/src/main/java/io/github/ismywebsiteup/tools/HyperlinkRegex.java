package io.github.ismywebsiteup.tools;

import io.github.ismywebsiteup.db.Task;

public class HyperlinkRegex {
	private Task mTask;

	public HyperlinkRegex(Task task) {
		mTask = task;
	}

	public boolean canCheckHyperlink(String hyperlink) {
		boolean checkMatch;
		if (!mTask.checkIfMatchRegex)
			checkMatch = true;
		else
			checkMatch = hyperlink.matches(mTask.checkRegex);
		boolean dontCheckMatch;
		if (!mTask.dontCheckIfMatchRegex)
			dontCheckMatch = false;
		else
			dontCheckMatch = hyperlink.matches(mTask.dontCheckRegex);
		if (!checkMatch || dontCheckMatch) {
			if (mTask.ignoredByRegex.length() > 0)
				mTask.ignoredByRegex += "\n";
			mTask.ignoredByRegex += hyperlink;
		}

		return checkMatch && !dontCheckMatch;
	}
}
