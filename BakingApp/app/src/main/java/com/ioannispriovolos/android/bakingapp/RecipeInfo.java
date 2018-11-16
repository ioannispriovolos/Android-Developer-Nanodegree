package com.ioannispriovolos.android.bakingapp;

import java.io.Serializable;
import java.util.ArrayList;

// http://www.vogella.com/tutorials/JavaSerialization/article.html
public class RecipeInfo implements Serializable {

    private String image;
    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;

    public String getCakeImage(){
        return this.image;
    }
    public String getCakeName(){
        return this.name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return this.ingredients;
    }
    public ArrayList<Step> getSteps() {
        return this.steps;
    }

    public void setIngredients(ArrayList <Ingredient> cakeIngredients){
        this.ingredients = cakeIngredients;

    }

    @Override
    public String toString() {
        return this.name;
    }
}

