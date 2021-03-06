package com.ayros.historycleaner.cleaning.items.firefox;

import java.util.ArrayList;
import java.util.List;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.DBHelper;
import com.ayros.historycleaner.helpers.Logger;
import com.ayros.historycleaner.helpers.RootHelper;

public class _FirefoxNightly_History extends CleanItem
{
	public _FirefoxNightly_History(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "History";
	}

	@Override
	public String getPackageName()
	{
		return "org.mozilla.fennec";
	}

	public static String getFirefoxNightlyDataPath()
	{
		String profiles = RootHelper.getFileContents("/data/data/org.mozilla.fennec/files/mozilla/profiles.ini");
		if (profiles == null)
		{
			Logger.debug("Could not read the FireFox Nightly profiles.ini - app hasn't been opened yet");
			return "";
		}

		String[] lines = profiles.split("\n");
		for (String line : lines)
		{
			if (line.contains("Path="))
			{
				String folder = "/data/data/org.mozilla.fennec/files/mozilla/" + line.replace("Path=", "");
				if (RootHelper.fileOrFolderExists(folder))
				{
					return folder;
				}
				else
				{
					Logger.error("Found FireFox Nightly data path, but it doesn't seem to exist: " + folder);
					return null;
				}
			}
		}

		Logger.error("Could not find path variable in FireFox Nightly profiles.ini");
		return null;
	}
	
	@Override
	public List<String []> getSavedData()
	{
		String path = getFirefoxNightlyDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox Nightly data path to view history");
			return null;
		}
		else if (path.length() == 0)
		{
			return new ArrayList<String []>();
		}

		return DBHelper.queryDatabase
		(
			Globals.getContext(),
			path + "/browser.db",
			new String[] { "Title", "URL" },
			"history",
			new String[] { "title", "url" },
			null
		);
	}
	
	@Override
	public boolean clean()
	{
		String path = getFirefoxNightlyDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox Nightly data path to clear history");
			return false;
		}
		else if (path.length() == 0)
		{
			return true;
		}

		return DBHelper.updateDatabase
		(
			Globals.getContext(),
			path + "/browser.db",
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='history';",
				"DELETE FROM history;",
			}
		);
	}
}