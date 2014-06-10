package nz.co.gsf.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import nz.co.gsf.garagesale.GarageSale;
import nz.co.gsf.preferences.DefaultPrefs;
import nz.co.gsf.R;

public class GSPreferenceActivity extends PreferenceActivity {
	public static final String KEY_PREF_MAX_DISPLAY_DISTANCE = "pref_maxDisplayDistance";
	public static final String KEY_PREF_MAX_HIGHLIGHT_DISTANCE = "pref_maxHighlightDistance";
	public static final String KEY_PREF_NUM_GARAGESALES_TO_SHOW = "pref_numGarageSalesToShow";
	public static final String KEY_PREF_API_SERVER_URL = "pref_apiServer_url";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new MyPreferenceFragment())
				.commit();
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}

		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key.equals(KEY_PREF_MAX_DISPLAY_DISTANCE)) {
				int value = sharedPreferences.getInt(key,
						DefaultPrefs.MAX_DISPLAY_DISTANCE);
				setMinDisplaySummary(value);
			} else if (key.equals(KEY_PREF_MAX_HIGHLIGHT_DISTANCE)) {
				int value = sharedPreferences.getInt(key,
						DefaultPrefs.MAX_HIGHLIGHT_DISTANCE);
				setMinHighlightSummary(value);
			} else if (key.equals(KEY_PREF_NUM_GARAGESALES_TO_SHOW)) {
				int value = sharedPreferences.getInt(key,
						DefaultPrefs.NUM_GARAGESALES_TO_DISPLAY);
				setNumGarageSaleSummary(value);
			} else if (key.equals(KEY_PREF_API_SERVER_URL)) {
				String value = sharedPreferences.getString(key,
						DefaultPrefs.API_SERVER_URL);
				setApiServerUrl(value);
			}
		}

		private void setMinDisplaySummary(int value) {
			Preference pref = findPreference(KEY_PREF_MAX_DISPLAY_DISTANCE);
			pref.setSummary(String.format(
					getString(R.string.pref_maxDisplayDistance_summ),
					GarageSale.distanceFormat.format((float) value)));
		}

		private void setMinHighlightSummary(int value) {
			Preference pref = findPreference(KEY_PREF_MAX_HIGHLIGHT_DISTANCE);
			pref.setSummary(String.format(
					getString(R.string.pref_maxHighlightDistance_summ),
					GarageSale.distanceFormat.format((float) value)));
		}

		private void setNumGarageSaleSummary(int value) {
			Preference pref = findPreference(KEY_PREF_NUM_GARAGESALES_TO_SHOW);
			pref.setSummary(String.format(
					getString(R.string.pref_maxNumGaragesalesToShow_summ), value));
		}
		
		private void setApiServerUrl(String value) {
			Preference pref = findPreference(KEY_PREF_API_SERVER_URL);
			pref.setSummary(String.format(
					getString(R.string.pref_apiServer_url_summ), value));
		}

		@Override
		public void onResume() {
			super.onResume();
			SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
			prefs.registerOnSharedPreferenceChangeListener(this);
			setMinDisplaySummary(prefs.getInt(KEY_PREF_MAX_DISPLAY_DISTANCE,
					DefaultPrefs.MAX_DISPLAY_DISTANCE));
			setMinHighlightSummary(prefs.getInt(
					KEY_PREF_MAX_HIGHLIGHT_DISTANCE,
					DefaultPrefs.MAX_HIGHLIGHT_DISTANCE));
			setNumGarageSaleSummary(prefs.getInt(KEY_PREF_NUM_GARAGESALES_TO_SHOW,
					DefaultPrefs.NUM_GARAGESALES_TO_DISPLAY));
		}
		
		@Override
		public void onPause() {
			super.onPause();
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
