package com.genenakagaki.myhandycoach.data.model;

import android.database.Cursor;

import com.genenakagaki.myhandycoach.data.ExerciseContract;

import static com.genenakagaki.myhandycoach.data.ExerciseContract.*;

/**
 * Created by gene on 4/18/17.
 */

public class ReactionExercise {

    public long id;
    public String name;
    public int reps;
    public int sets;
    public int choices;
    public int choiceInterval;
    public int restDuration;

    public ReactionExercise() {}

    public ReactionExercise(long id, String name, int reps, int sets, int choices, int choiceInterval, int restDuration) {
        this.id = id;
        this.name = name;
        this.reps = reps;
        this.sets = sets;
        this.choices = choices;
        this.choiceInterval = choiceInterval;
        this.restDuration = restDuration;
    }
}

