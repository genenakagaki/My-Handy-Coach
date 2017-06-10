package com.genenakagaki.myhandycoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.genenakagaki.myhandycoach.fragment.AbstractExerciseSettingsFragment;
import com.genenakagaki.myhandycoach.fragment.ReactionExerciseSettingsFragment;
import com.genenakagaki.myhandycoach.fragment.RegularExerciseSettingsFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tablayout) TabLayout tabLayout;
    @BindView(R.id.start_exercise_button) Button startExerciseButton;

    private Unbinder unbinder;

    private static int sCurrentViewPagerItem = -1;

    private ExercisePagerAdapter mExercisePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mExercisePagerAdapter = new ExercisePagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(mExercisePagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                sCurrentViewPagerItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        tabLayout.setupWithViewPager(viewPager);

        if (sCurrentViewPagerItem == -1) {
            sCurrentViewPagerItem = 0;
        } else {
            viewPager.setCurrentItem(sCurrentViewPagerItem);
        }

        startExerciseButton.setOnClickListener(this);

        // TODO: remove
        ButterKnife.setDebug(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == startExerciseButton.getId()) {
            int index = viewPager.getCurrentItem();
            AbstractExerciseSettingsFragment fragment =
                    (AbstractExerciseSettingsFragment) mExercisePagerAdapter.getFragment(index);

            boolean isValid = fragment.validateInputs();
            if (isValid) {
                fragment.saveExerciseSettings();

                Intent intent = new Intent(this, ExerciseActivity.class);
                if (index == 0) {
                    intent.putExtra(ExerciseActivity.EXTRA_EXERCISE_TYPE, ExerciseType.REGULAR);
                } else {
                    intent.putExtra(ExerciseActivity.EXTRA_EXERCISE_TYPE, ExerciseType.REACTION);
                }
                startActivity(intent);
            }
        }
    }

    public class ExercisePagerAdapter extends FragmentPagerAdapter {

        private Map<Integer, Fragment> pageReferenceMap = new HashMap<>();

        public ExercisePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Timber.d("Creating fragment with regular exercise.  Position: " + position);
                Fragment fragment = new RegularExerciseSettingsFragment();
                pageReferenceMap.put(position, fragment);
                return fragment;
            } else {
                Timber.d("Creating fragment with regular exercise.  Position: " + position);
                Fragment fragment = new ReactionExerciseSettingsFragment();
                pageReferenceMap.put(position, fragment);
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.regular_exercise);
                case 1:
                    return getString(R.string.reaction_exercise);
            }
            return null;
        }

        public Fragment getFragment(int position) {
            return pageReferenceMap.get(position);
        }
    }
}
