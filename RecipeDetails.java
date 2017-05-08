package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by hashh on 17-04-2017.
 */

public class RecipeDetails extends Activity {

    String recipeName;
    String getrecipe_url = "http://10.0.2.2/ingredients/getDetails2.php";
    String addtofavorites_url = "http://10.0.2.2/ingredients/addtofavorites.php";
    String removefavorites_url = "http://10.0.2.2/ingredients/removeFavorites.php";

    TextView recipeTitle, recipeDescription, cookingTime;
    LinearLayout linearLayout;
    Button addToFavoritesButton, removeFavoritesButton, rateButton;
    RatingBar avgRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_details);

        final Intent intent = getIntent();
        recipeName = intent.getExtras().getString("recipeName");

        recipeTitle = (TextView)findViewById(R.id.recipeTitle);
        recipeTitle.setText(recipeName);

        addToFavoritesButton = (Button)findViewById(R.id.addToFavoritesButton);
        removeFavoritesButton = (Button)findViewById(R.id.removeFavoritesButton);
        rateButton = (Button)findViewById(R.id.rateButton);

        avgRatingBar = (RatingBar)findViewById(R.id.avgRatingBar);

        new BTask(this).execute();
        new GetRatingBGTask().execute();

        addToFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddToFavoritesTask().execute();
            }
        });

        removeFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemoveFavoritesTask().execute();
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(RecipeDetails.this, RateReview.class);
                intent1.putExtra("recipeName", recipeName);
                startActivity(intent1);
            }
        });
    }

    class GetRatingBGTask extends AsyncTask<Void, Void, String>{

        String getRatingURL = "http://10.0.2.2/ingredients/getRating.php";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(getRatingURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String data = URLEncoder.encode("recipename", "utf-8")+"="+URLEncoder.encode(recipeName, "utf-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = bufferedReader.readLine();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            float avgRating = Float.parseFloat(s);
            avgRatingBar.setRating(avgRating);
        }
    }

    class BTask extends AsyncTask<Void, Void, String>{

        Context context;

        BTask(Context context){
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(getrecipe_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("rec_name", "UTF-8")+"="+URLEncoder.encode(recipeName, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = bufferedReader.readLine();
                bufferedReader.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            linearLayout = (LinearLayout)findViewById(R.id.stepsLayout);
            try {
                JSONObject jsonObject = new JSONObject(result);
                cookingTime = (TextView)findViewById(R.id.cookingtime);
                recipeDescription = (TextView)findViewById(R.id.description);

                cookingTime.setText(jsonObject.getString("cookingtime"));
                recipeDescription.setText(jsonObject.getString("description"));

                JSONArray jsonArray = jsonObject.getJSONArray("steps");

                for(int i=0; i<jsonArray.length(); i++){
                    TextView step = new TextView(RecipeDetails.this);
                    step.setText(String.valueOf(i+1)+". "+jsonArray.getString(i));
                    step.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    //step.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, La));
                    linearLayout.addView(step);
                }

                String imageURL = jsonObject.getString("img_url");
                new GetImageTask((ImageView)findViewById(R.id.recipeImage2)).execute(imageURL);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class GetImageTask extends AsyncTask<String, Void, Bitmap>{

        ImageView imageView;

        GetImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(params[0]).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

    private class AddToFavoritesTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(addtofavorites_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String data = URLEncoder.encode("username", "utf-8")+"="+URLEncoder.encode(MainActivity.Username, "utf-8")+"&"+
                              URLEncoder.encode("rec_name", "utf-8")+"="+URLEncoder.encode(recipeName, "utf-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = bufferedReader.readLine();
                httpURLConnection.disconnect();

                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("Success")) {
                HomePage.arrayAdapter.add(recipeName);
                Toast.makeText(RecipeDetails.this, recipeName + " added to favorites", Toast.LENGTH_LONG).show();
            }
            else if(s.equals("Failure"))
                Toast.makeText(RecipeDetails.this, recipeName+" is already in your favorites", Toast.LENGTH_LONG).show();
        }
    }

    private class RemoveFavoritesTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(removefavorites_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String data = URLEncoder.encode("username", "utf-8")+"="+URLEncoder.encode(MainActivity.Username, "utf-8")+"&"+
                        URLEncoder.encode("rec_name", "utf-8")+"="+URLEncoder.encode(recipeName, "utf-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = bufferedReader.readLine();
                httpURLConnection.disconnect();

                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("Success")){
                HomePage.arrayAdapter.remove(recipeName);
                Toast.makeText(RecipeDetails.this, recipeName+" removed from favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
