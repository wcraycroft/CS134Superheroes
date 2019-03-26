package edu.miracostacollege.cs134.cs134superheroes;

import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This controller class inflates the Settings Activity and links it to the preferences layout.
 * Allows the user to select the type of Superhero information the quiz will ask for.
 *
 * @author William Craycroft
 * @version 1.0
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Inflates the inner SettingsActivityFragment class using FragmentManager.
     *
     * @param savedInstanceState - Bundle of data saved from previous state (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Display the fragment as the main content
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsActivityFragment()).commit();
    }

    /**
     * This inner class overrides the onCreate in PreferenceFragment in order to manually load
     * the preferences XML.
     */
    public static class SettingsActivityFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
