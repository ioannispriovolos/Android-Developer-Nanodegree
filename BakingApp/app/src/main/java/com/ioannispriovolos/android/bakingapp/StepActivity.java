package com.ioannispriovolos.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class StepActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        if (savedInstanceState == null){

            Intent intentThatStartThisActivity = getIntent();
            Bundle bundle = intentThatStartThisActivity.getBundleExtra(Intent.EXTRA_TEXT);
            ArrayList<Step> stepsModelArrayList = (ArrayList<Step>) bundle.getSerializable("stepsList");
            int stepNumber = bundle.getInt("stepNumber");
            FragmentStepDetails fragmentStepDetails = new FragmentStepDetails();
            fragmentStepDetails.setStepsListAndPosition(stepsModelArrayList, stepNumber);
            getSupportFragmentManager().beginTransaction().add(R.id.step_detail_container, fragmentStepDetails).commit();
        }
    }
}