package edu.miracostacollege.cs134.cs134superheroes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.miracostacollege.cs134.cs134superheroes.model.Superhero;
import edu.miracostacollege.cs134.cs134superheroes.model.JSONLoader;

/**
 * This controller class handles the behavior of the Quiz Activity, which encompasses the entire
 * life-cycle of the Superhero Quiz. Gets Superhero data from the JSONLoader and creates a randomized
 * quiz. Tracks user input (guesses) as well as the end and restarting of the quiz.
 *
 * @author William Craycroft
 * @version 1.0
 */
public class QuizActivity extends AppCompatActivity {

    public static final String TAG = "CS 134 Superheroes";

    private static final String TYPE_KEY = "pref_type";     // Pref key for quiz type
    private static final int SUPERHEROES_IN_QUIZ = 10;      // The number of quiz questions

    private String mQuizType;                       // the quiz type (name, superpower or oneThing)
    private Button[] mButtons = new Button[4];      // array of all 4 button objects
    private List<Superhero> mAllSuperheroesList;    // all the Superheroes loaded from JSON
    private List<Superhero> mQuizSuperheroesList;   // Superheroes in current quiz
    private Superhero mCorrectSuperhero;            // correct Superhero for the current question
    private int mTotalGuesses;                      // number of total guesses made
    private int mCorrectGuesses;                    // number of correct guesses
    private SecureRandom rng;                       // used to randomize the quiz
    private Handler handler;                        // used to delay loading next Superhero

    private TextView mQuestionNumberTextView;       // shows current question #
    private ImageView mSuperheroImageView;          // displays the image of a Superhero
    private TextView mAnswerTextView;               // displays correct answer or "Incorrect guess"
    private TextView mGuessTypeTextView;            // displays what information the user is guessing

    // Preference listener object
    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                    Log.i(TAG, "key: " + key);

                    mQuizType = sharedPreferences.getString(TYPE_KEY,
                            getString(R.string.default_quiz_type));
                    resetQuiz();

                    Toast.makeText(QuizActivity.this, R.string.restarting_quiz,
                            Toast.LENGTH_SHORT).show();
                }
            };

    /**
     * Inflates the activity_quiz layout, links views, and instantiates any resources needed when
     * the application is first launched.
     *
     * @param savedInstanceState - Bundle of data saved from previous state (unused)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Instantiate list and helper objects
        mQuizSuperheroesList = new ArrayList<>(SUPERHEROES_IN_QUIZ);
        rng = new SecureRandom();
        handler = new Handler();

        // Store quiz type value from saved preferences (name, superheroes or oneThing)
        try {
            mQuizType = PreferenceManager.getDefaultSharedPreferences(this).getString(TYPE_KEY,
                    getString(R.string.default_quiz_type));
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
        // Log quiz type for debugging purposes
        Log.i(TAG, "Quiz type set to " + mQuizType);

        // Get references to GUI components (TextViews and ImageView)
        mQuestionNumberTextView = findViewById(R.id.questionNumberTextView);
        mSuperheroImageView = findViewById(R.id.superheroImageView);
        mAnswerTextView = findViewById(R.id.answerTextView);
        mGuessTypeTextView = findViewById(R.id.guessTypeTextView);

        // Link all 4 buttons to the mButtons array
        mButtons[0] = findViewById(R.id.button);
        mButtons[1] = findViewById(R.id.button2);
        mButtons[2] = findViewById(R.id.button3);
        mButtons[3] = findViewById(R.id.button4);

        // Set mQuestionNumberTextView's text to the appropriate strings.xml resource
        mQuestionNumberTextView.setText(getString(R.string.question, 1, SUPERHEROES_IN_QUIZ));

        // Load all the Superheroes from the JSON file using the JSONLoader
        try {
            mAllSuperheroesList = JSONLoader.loadJSONFromAsset(this);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        // Attach preference listener
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);

        // Call the method resetQuiz() to start the quiz.
        resetQuiz();
    }

    /**
     * Generates a new set of randomized Superheroes and starts a new quiz.
     */
    public void resetQuiz() {

        // Reset the number of correct guesses made
        mCorrectGuesses = 0;
        // Reset the total number of guesses the user made
        mTotalGuesses = 0;
        // Clear list of quiz Superheroes (for prior games played)
        mQuizSuperheroesList.clear();

        // Randomly add SUPERHEROES_IN_QUIZ (10) superheroes from the mAllSuperheroesList into the mQuizSuperheroesList
        int size = mAllSuperheroesList.size();
        int randomPosition;
        Superhero randomSuperhero;
        while (mQuizSuperheroesList.size() <= SUPERHEROES_IN_QUIZ) {

            randomPosition = rng.nextInt(size);
            randomSuperhero = mAllSuperheroesList.get(randomPosition);

            // Ensure no duplicate superheroes (e.g. don't add a superhero if it's already in mQuizSuperheroesList)
            // Check for duplicates (!contains) before adding to quiz
            if (!mQuizSuperheroesList.contains(randomSuperhero)) {
                mQuizSuperheroesList.add(randomSuperhero);
            }
        }

        // Set the text of guessTypeTextView based on quiz type
        switch(mQuizType) {
            case ("name"):
                mGuessTypeTextView.setText(R.string.guess_name);
                break;
            case("superpower"):
                mGuessTypeTextView.setText(R.string.guess_superpower);
                break;
            case("oneThing"):
                mGuessTypeTextView.setText(R.string.guess_one_thing);
                break;
        }

        // Start the quiz by calling loadNextSuperhero
        loadNextSuperhero();
    }

    /**
     * Method initiates the process of loading the next Superhero for the quiz, showing
     * the Superhero's image and then 4 buttons, one of which contains the correct answer.
     */
    private void loadNextSuperhero() {
        // Initialize the mCorrectSuperhero by removing the item at position 0 in the mQuizSuperheroesList
        mCorrectSuperhero = mQuizSuperheroesList.get(0);
        mQuizSuperheroesList.remove(0);
        // Clear the mAnswerTextView so that it doesn't show text from the previous question
        mAnswerTextView.setText("");
        // Display current question number in the mQuestionNumberTextView
        mQuestionNumberTextView.setText(getString(R.string.question, mCorrectGuesses + 1, SUPERHEROES_IN_QUIZ));

        // Use AssetManager to load next image from assets folder
        AssetManager am = getAssets();

        // Set the image drawable to the correct Superhero image.
        try {
            InputStream stream = am.open(mCorrectSuperhero.getFileName());
            Drawable image = Drawable.createFromStream(stream, mCorrectSuperhero.getName());
            mSuperheroImageView.setImageDrawable(image);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        // Shuffle the order of all the superheroes (use Collections.shuffle)
        do {
            Collections.shuffle(mAllSuperheroesList);
        }
        // If first entry is correct superhero, reshuffle
        while (mAllSuperheroesList.subList(0, mButtons.length).contains(mCorrectSuperhero));

        // Loop through 4 buttons to be enabled, enable them and set them to the first 4 superheroes
        for (int i = 0; i < mButtons.length; i++) {
            mButtons[i].setEnabled(true);
            // Display info based on quiz type using custom getter in model
            mButtons[i].setText(mAllSuperheroesList.get(i).getInfo(mQuizType));

        }

        // After the loop, randomly replace one of the active buttons with the correct Superhero
        mButtons[rng.nextInt(mButtons.length)].setText(mCorrectSuperhero.getInfo(mQuizType));

    }

    /**
     * Handles the click event of one of the 4 buttons indicating the guess of a Superhero's name,
     * superpower, or one thing to match the image displayed.  If the guess is correct, the correct
     * answer is show in green and a success sounds if played, followed by a slight delay of 2 seconds,
     * then the next Superhero image will be loaded.  Otherwise, the word "Incorrect Guess" will be
     * shown in RED, an failure sound will be played and the button will be disabled.
     *
     * @param v - The view calling this method (should always be a Button object)
     */
    public void makeGuess(View v) {

        // Instantiate MediaPlayer for success/failure sound files
        final MediaPlayer successMP = MediaPlayer.create(this, R.raw.success);
        final MediaPlayer failedMP = MediaPlayer.create(this, R.raw.failed);

        mTotalGuesses++;
        // Downcast the View v into a Button (since it's one of the 4 buttons)
        Button clickedButton = (Button) v;

        // Get the guessed information from the text of the button
        String guessedInfo = clickedButton.getText().toString();

        // If the guess matches the correct superhero info...
        if (guessedInfo.equalsIgnoreCase(mCorrectSuperhero.getInfo(mQuizType))) {
            // Increment the number of correct guesses
            mCorrectGuesses++;

            // Play correct guess sound
            successMP.start();
            // If quiz is not over...
            if (mCorrectGuesses < SUPERHEROES_IN_QUIZ) {
                // Change answer text to correct answer and set text color to green
                mAnswerTextView.setText(mCorrectSuperhero.getInfo(mQuizType));
                mAnswerTextView.setTextColor(getResources().getColor(R.color.correct_answer));
                // Disable all 4 buttons (can't keep guessing once it's correct)
                for (int i = 0; i < mButtons.length; i++) {
                    mButtons[i].setEnabled(false);
                }
                // Pause before going to next Superhero
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextSuperhero();
                    }
                }, 2000);
            }
            // If quiz is over...
            else {
                // Create AlertDialog with text and button to reset quiz
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                double percentage = (double) mCorrectGuesses / mTotalGuesses * 100.0;
                builder.setMessage(getString(R.string.results, mTotalGuesses, percentage));
                builder.setPositiveButton(getString(R.string.reset_quiz), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetQuiz();
                    }
                });
                // Disable the cancel operation (can't cancel dialog)
                builder.setCancelable(false);
                builder.create();
                builder.show();
            }
        }

        // If answer is incorrect...
        else {
            // Display "Incorrect Guess!" in red
            mAnswerTextView.setText(getString(R.string.incorrect_answer));
            mAnswerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer));
            // Disable just the incorrect button.
            clickedButton.setEnabled(false);
            // Play incorrect guess sound
            failedMP.start();
        }

    }

    /**
     * Inflates an Options Menu which displays a settings icon on the Quiz Activity title bar.
     *
     * @param menu - The options Menu object
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles the click event for the Options Menu. Creates an empty intent and sends the user to
     * the SettingsActivity.
     *
     * @param item - the selected MenuItem
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        return super.onOptionsItemSelected(item);
    }

}
