package com.example.ehdus.testscan;

import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

class Recipe extends FilterableObject {
    public static final String NAME = "title", RATING = "likes", PIC = "image", ID = "id", SOURCE = "sourceUrl", ADD_FLAG = "ADD_FLAG";
    private int mRating, mID;
    private Uri mSourceUrl;

    // parses input JSON object to return values we care about
    Recipe(FilterAdapter a, String input) {
        super(a);
        try {
            JSONObject entry = new JSONObject(input);
            mName = entry.getString(NAME);
            mRating = entry.getInt(RATING);
            mID = entry.getInt(ID);
            new recipeImport().execute(mID);

            new ImageGetter().execute(new JSONArray().put(entry.getString(PIC)));
        } catch (JSONException e) {
            setError("Image import failed");
        }
    }

    Recipe(FilterAdapter a, String error, int err) {
        super(a);
        setError(error);
    }

    private void setError(String error) {
        mName = "Error: " + error;
        mRating = 0;
        new ImageGetter().execute(new JSONArray().put("https://pbs.twimg.com/profile_images/520273796549189632/d1et-xaU_400x400.png"));
    }

    public int getRating() {
        return mRating;
    }

    private int getID() { return mID;}

    public Uri getSourceUrl() { return  mSourceUrl; }

    @Override
    public String getFilterable() {
        return mName;
    }


    private class recipeImport extends AsyncTask<Integer, String, Uri> {

        @Override
        protected Uri doInBackground(Integer... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            Uri result;

            try {
                    URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/" + params[0] + "/information");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("X-Mashape-Key", MainActivity.KEY);
                    connection.connect(); //connects to server and returns data as input stream

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder buffer = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String finalJson = buffer.toString();

                    if (finalJson == null) {
                        //TODO: need to fix smarter error handling
                        result = Uri.parse("www.google.com");
                    } else {
                        JSONObject object = new JSONObject(finalJson);
                        result = Uri.parse(object.getString(SOURCE));
                    }

                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    reader.close();
                }

            } catch (Exception e) {
                //TODO: need to fix smarter error handling
                result = null;
                return result;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Uri result) {
            super.onPostExecute(result);
            mSourceUrl = result;
        }
    }

    public void setFields(String name, int rating) {
        this.mName = name;
        this.mRating = rating;
    }

    @Override
    public String write() {

        JSONObject output = new JSONObject();
        try {
            output.put(NAME, mName);
            output.put(RATING, mRating);
            output.put(ID, mID);
            output.put(SOURCE, mSourceUrl);
            output.put(PIC, mPictureUrl);
        } catch (JSONException e) {
            // TODO: smarter exceptions
        }

        return output.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Recipe) {
            return mID == ((Recipe) o).getID();
        }
        return false;
    }

}
