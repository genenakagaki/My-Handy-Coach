package com.genenakagaki.myhandycoach.data.model;

import android.database.Cursor;

import com.genenakagaki.myhandycoach.data.ExerciseContract;

import static com.genenakagaki.myhandycoach.data.ExerciseContract.*;

/**
 * Created by gene on 4/18/17.
 */

public class RegularExercise {

    public long id;
    public String name;
    public int reps;
    public int sets;
    public int setDuration;
    public int restDuration;

    public RegularExercise() {

    }

    public RegularExercise(long id, String name, int reps, int sets, int setDuration, int restDuration) {
        this.id = id;
        this.name = name;
        this.reps = reps;
        this.sets = sets;
        this.setDuration = setDuration;
        this.restDuration = restDuration;
    }
}
