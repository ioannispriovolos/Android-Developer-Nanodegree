package capstone.ioannispriovolos.udacity.teleprompter;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

// https://developer.android.com/reference/android/support/v7/preference/PreferenceFragmentCompat
public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {}

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        addPreferencesFromResource(R.xml.pref_settings);

    }
}
