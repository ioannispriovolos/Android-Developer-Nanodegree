package com.ioannispriovolos.android.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterRecipe extends RecyclerView.Adapter<AdapterRecipe.CakeAdapterViewHolder>{

    private List<RecipeInfo> mCakeModelList;
    private Context mContext;
    private final CakeAdapterOnClickHandler mClickHandler;

    public AdapterRecipe(Context context, CakeAdapterOnClickHandler clickHandler){

        mClickHandler = clickHandler;
        mContext = context;
    }

    public class CakeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView (R.id.cake_image_main) ImageView cakeImage;
        @BindView(R.id.tv_cake_name_main) TextView cakeName;

        CakeAdapterViewHolder(View view){

            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            mClickHandler.onClick(mCakeModelList.get(position));
        }
    }

    @Override
    public CakeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recipe_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new CakeAdapterViewHolder(view) ;

    }

    public void onBindViewHolder(CakeAdapterViewHolder holder, int position) {

        String currentCakeName = mCakeModelList.get(position).getCakeName();
        String currentCakeImage = mCakeModelList.get(position).getCakeImage();
        int recipePicture;

        switch (currentCakeName){
            case "Nutella Pie":
                recipePicture = R.drawable.nutella_pie;
                break;
            case "Yellow Cake":
                recipePicture = R.drawable.yellow_cake;
                break;
            case "Brownies":
                recipePicture = R.drawable.brownies;
                break;
            case "Cheesecake":
                recipePicture = R.drawable.cheesecake;
                break;
            default:
                recipePicture = R.drawable.place_holder;
        }
        holder.cakeName.setText(currentCakeName);


        if (!currentCakeImage.equals("")){
            Picasso.with(mContext).load(currentCakeImage).placeholder(R.drawable.place_holder).error(R.drawable.user_placeholder_error).into(holder.cakeImage);
        }
        else {
            Picasso.with(mContext).load(recipePicture).placeholder(R.drawable.place_holder).error(R.drawable.user_placeholder_error).into(holder.cakeImage);
        }
    }

    @Override
    public int getItemCount() {

        if(mCakeModelList == null) return 0;
        return mCakeModelList.size();
    }
    public void setCakeList(List<RecipeInfo> cakeList){

        mCakeModelList = cakeList;
        notifyDataSetChanged();
    }

    public interface CakeAdapterOnClickHandler{

        void onClick(RecipeInfo currentCake);
    }


}

