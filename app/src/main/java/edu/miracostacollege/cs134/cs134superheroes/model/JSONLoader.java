package edu.miracostacollege.cs134.cs134superheroes.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.miracostacollege.cs134.cs134superheroes.QuizActivity;

/**
 * This class loads Superhero data from a formatted JSON (JavaScript Object Notation) file.
 * Populates data model (Superhero) with data.
 *
 * @author William Craycroft
 * @version 1.0
 */
public class JSONLoader {

    /**
     * Loads JSON data from a file in the assets directory.
     *
     * @param context The activity from which the data is loaded.
     * @throws IOException If there is an error reading from the JSON file.
     */
    public static List<Superhero> loadJSONFromAsset(Context context) throws IOException {
        List<Superhero> allSuperheroesList = new ArrayList<>();
        String json = null;
        InputStream is = context.getAssets().open("cs134superheroes.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allCountriesJSON = jsonRootObject.getJSONArray("CS134Superheroes");

            // Loop through all the superheroes in the JSON data, create a Superhero object for each
            JSONObject countryJSON;
            String name, superpower, oneThing, fileName;
            for (int i = 0; i < allCountriesJSON.length(); i++) {
                // Grab JSON object
                countryJSON = allCountriesJSON.getJSONObject(i);
                // Get Superhero information from JSON object
                name = countryJSON.getString("Name");
                superpower = countryJSON.getString("Superpower");
                oneThing = countryJSON.getString("OneThing");
                fileName = countryJSON.getString("FileName");
                // Create new Superhero object and add it to list
                allSuperheroesList.add(new Superhero(name, superpower, oneThing, fileName));
            }


        } catch (JSONException e) {
            Log.e(QuizActivity.TAG, e.getMessage());
        }

        return allSuperheroesList;
    }
}
