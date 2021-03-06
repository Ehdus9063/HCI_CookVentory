package com.example.ehdus.testscan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RecipeViewFragment extends FilterFragment implements SwipeRefreshLayout.OnRefreshListener, IngredientViewFragment.FragPass {

    private static boolean DEV;
    private int mode; // this will be used to determine where to draw recipes from
    private Set<String> query = new HashSet<>();

    // INIT: busy spinner, recipe list import and display
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        // TODO: touch listener to get recipe online

        a = new RecipeAdapter(this.getContext());
        rv.setAdapter(a);

        // swipe to refresh implementation
        if (mode == 0) {
            swipe.setEnabled(true);
            swipe.setOnRefreshListener(this);
            // automatically refresh on load
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            });
        }
        return rootView;
    }

    // Swipe listener implementation; loads recipes on swipe
    @Override
    public void onRefresh() {
        // INIT: fetch recipes conforming to query
        //  recipeImport task populates the list on completion
        if (DEV) {
            a.clear();
            a.add(new Recipe(a, "Recipe API turned off", 0));
            swipe.setRefreshing(false);
        } else {
            new recipeImport().execute(query);
        }
    }


    // Sets mode; 0 for Top Picks and 1 for Favorites
    public void setMode(int inMode) {
        mode = inMode;
    }

    void setDev(boolean dev) {
        DEV = dev;
    }

    @Override
    public void queryListener(Set<String> query) {
        if (query.equals(this.query))
            return;
        this.query = query;
        onRefresh();
    }

    @Override
    public boolean isFavorite(Recipe checkFav){
        IngredientViewFragment.FragPass fragPass;
        if(getActivity() instanceof IngredientViewFragment.FragPass) {
            fragPass = (IngredientViewFragment.FragPass) getActivity();
            return fragPass.isFavorite(checkFav);
        }
        return false;
    }

    @Override
    public void toFavorites(String faveRecipe){
        return;
    }

    @Override
    public void updateRecipe() {
        a.notifyDataSetChanged();
    }

    // INIT: gets list of recipes from Yummly
    //  on completion, stops spinner and populates list
    private class recipeImport extends AsyncTask<Set<String>, String, ArrayList<Recipe>> {

        // Makes query to get recipe list
        @Override
        protected ArrayList<Recipe> doInBackground(Set<String>... params) {
            HttpURLConnection connection;
            BufferedReader reader;
            ArrayList<Recipe> recipeList = new ArrayList<>();

            try {
                StringBuilder urlBuilder = new StringBuilder("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?");
                urlBuilder.append("fillIngredients=false&"); // don't request information about what's used and what isn't
                urlBuilder.append("limitLicense=false&"); // show recipes without regard to attribution license
                urlBuilder.append("number=5&"); // how many recipes to return
                urlBuilder.append("ranking=2&"); // minimize missing ingredients first
                urlBuilder.append("ingredients=");
                Set<String> query = params[0];
                for (String s : query)
                    urlBuilder.append(s + "%2C");
                urlBuilder.delete(urlBuilder.length() - 3, urlBuilder.length());
                URL url = new URL(urlBuilder.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("X-Mashape-Key", MainActivity.KEY);
                connection.connect(); //connects to server and returns data as input stream

                InputStream stream = connection.getInputStream();

                // Parse input
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONArray parentArray = new JSONArray(finalJson);

                // Create list of recipes (recipe handles JSON parsing)
                for (int i = 0; i < parentArray.length(); i++)
                    recipeList.add(new Recipe(a, parentArray.getJSONObject(i).toString()));

                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }

            } catch (IOException e) {
                recipeList.add(new Recipe(a, "API connection exception occurred", 0));
            } catch (JSONException e) {
                recipeList.add(new Recipe(a, "JSON read exception occurred", 0));
            }

            return recipeList;

        }

        // publishes results to the RecyclerView after search ends
        @Override
        protected void onPostExecute(ArrayList<Recipe> result) {
            super.onPostExecute(result);
            a.clear();
            for (Recipe r : result) {
                a.add(r);
            }
            swipe.setRefreshing(false);
        }
    }


}