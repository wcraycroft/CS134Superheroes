package edu.miracostacollege.cs134.cs134superheroes.model;

import java.util.Objects;

/**
 * Represents a CS 134 Superhero for the purposes of the Superhero quiz. Stores the Superhero's name,
 * Superpower, One Thing and the file name for its image.
 *
 * @author William Craycroft
 * @version 1.0
 */
public class Superhero {

    private String mName;
    private String mSuperpower;
    private String mOneThing;
    private String mFileName;

    /**
     * Instantiates a new <code>Superhero</code> given its name, superpower, one thing and file name.
     * @param name The first name and last initial of the <code>Superhero</code>
     * @param superpower The Superpower of the <code>Superhero</code>
     * @param oneThing The "one thing" you should know about the <code>Superhero</code>
     * @param fileName The file name which is formatted as first initial + last name
     */
    public Superhero(String name, String superpower, String oneThing, String fileName) {
        mName = name;
        mSuperpower = superpower;
        mOneThing = oneThing;
        mFileName = fileName;
    }

    /**
     * Gets the name of the <code>Superhero</code>.
     * @return The name of the <code>Superhero</code>
     */
    public String getName() {
        return mName;
    }

    /**
     * Gets the Superpower of the <code>Superhero</code>.
     * @return The Superpower of the <code>Superhero</code>
     */
    public String getSuperpower() {
        return mSuperpower;
    }

    /**
     * Gets the "one thing" about the <code>Superhero</code>.
     * @return The "one thing" about the <code>Superhero</code>
     */
    public String getOneThing() {
        return mOneThing;
    }

    /**
     * Gets the file name of the image associated with the <code>Superhero</code>
     * @return The file name of the <code>Superhero</code> image.
     */
    public String getFileName() {
        return mFileName;
    }

    /**
     * Gets String containing either the name, superpower or "one thing" of the <code>Superhero</code>
     * based on the input information type (name, superpower or oneThing).
     * @return The appropriate <code>Superhero</code> information.
     */
    public String getInfo(String infoType) {
        switch (infoType) {
            case "name":
                return mName;
            case "superpower":
                return mSuperpower;
            case "oneThing":
                return mOneThing;
        }
        return "Error: invalid info type sent to Model";
    }

    /**
     * Compares two Superheroes for equality based on name, superpower, one thing and file name.
     * @param o The other Superhero.
     * @return True if the Superheroes are the same, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Superhero superhero = (Superhero) o;
        return Objects.equals(mName, superhero.mName) &&
                Objects.equals(mSuperpower, superhero.mSuperpower) &&
                Objects.equals(mOneThing, superhero.mOneThing) &&
                Objects.equals(mFileName, superhero.mFileName);
    }

    /**
     * Generates an integer based hash code to uniquely represent this <code>Superhero</code>.
     * @return An integer based hash code to represent this <code>Superhero</code>.
     */
    @Override
    public int hashCode() {
        return Objects.hash(mName, mSuperpower, mOneThing, mFileName);
    }

    /**
     * Generates a text based representation of this <code>Superhero</code>.
     * @return A text based representation of this <code>Superhero</code>.
     */
    @Override
    public String toString() {
        return "Superhero{" +
                "mName='" + mName + '\'' +
                ", mSuperpower='" + mSuperpower + '\'' +
                ", mOneThing='" + mOneThing + '\'' +
                ", mFileName='" + mFileName + '\'' +
                '}';
    }
}
