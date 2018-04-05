package com.example.ehdus.testscan;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RecipeViewFragment extends Fragment {

    private int mode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        RecyclerView rv = rootView.findViewById(R.id.recipe_list);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        rv.setAdapter(new RecipeAdapter(this.getContext(), recipeImport()));

        return rootView;
    }

    public void setMode(int inMode) {
        mode = inMode;
    }

    //TODO: Recipe import
    private ArrayList<Recipe> recipeImport() {
        ArrayList<Recipe> recipes = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            recipes.add(new Recipe("Recipe " + i, "This is recipe #" + i + "\nDescriptions can be multiline up to 2 lines\nBut not more", R.drawable.temp));
        }
        return recipes;
    }
}