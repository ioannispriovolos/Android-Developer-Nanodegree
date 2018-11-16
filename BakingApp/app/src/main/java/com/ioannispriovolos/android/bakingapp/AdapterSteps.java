package com.ioannispriovolos.android.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterSteps extends RecyclerView.Adapter<AdapterSteps.StepsViewHolder> {


    private ArrayList<Step> mStepsDataList;
    private final StepAdapterOnClickHandler mClickHandler;

    public AdapterSteps(StepAdapterOnClickHandler clickHandler){

        mClickHandler = clickHandler;
    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.step_item, parent, false);

        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {

        String stepDescription = mStepsDataList.get(position).getShortDescription();
        holder.mStepDescription.setText(stepDescription);
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.step_description) TextView mStepDescription;

        public  StepsViewHolder(View view){

            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            mClickHandler.onClickStep(mStepsDataList, position);
        }
    }

    @Override
    public int getItemCount() {

        if (mStepsDataList == null) return 0;
        return mStepsDataList.size();
    }

    public void setStepsDataList(ArrayList<Step> stepsModels){

        mStepsDataList = stepsModels;
        notifyDataSetChanged();
    }

    public interface StepAdapterOnClickHandler{

        void onClickStep(ArrayList<Step> stepsModelArrayList, int stepPosition);
    }
}
