package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hashh on 15-04-2017.
 */

public class RecipeList extends Activity {

    ArrayList<String> arrayList;
    String recing_url = "http://10.0.2.2/ingredients/getRecipeIng.php";
    String rec_url = "http://10.0.2.2/ingredients/getRecipe.php";
    String JSON_STRING, JSON_STRING1,jsonstr, jsonstr1, currIng, recIng;
    JSONObject jsonObject, jsonObject1;
    JSONArray jsonArray, jsonArray1, ingArray;
    TextView textView;
    ListView recipeListView;
    ArrayList<String> recipeList;
    ArrayList<Integer> rec_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);

        textView = (TextView)findViewById(R.id.textView2);

        arrayList = new ArrayList<String>();
        arrayList = getIntent().getStringArrayListExtra("recipe_list");

        BGTask bgTask = new BGTask(this);
        bgTask.execute();

        Log.d("INGREDIENT", "Ingredients: "+arrayList);
    }

    class BGTask extends AsyncTask<Void, Void, String> {

        Context ctx;

        BGTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //To get ingredients of all recipes
                URL url = new URL(recing_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSON_STRING=bufferedReader.readLine())!=null){
                    stringBuilder.append(JSON_STRING+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            jsonstr = s;
            rec_ids = new ArrayList<Integer>();
            try {
                jsonArray = new JSONArray(jsonstr);
                for (int i = 1; i <= jsonArray.length(); i++) {//i keeps track of number of recipes
                    jsonObject = jsonArray.getJSONObject(i - 1);
                    ingArray = jsonObject.getJSONArray("ingredients");
                    int j = 1, count = 0;
                    for (; j <= arrayList.size(); j++) {
                        currIng = arrayList.get(j - 1);
                        int k = 1;
                        for (; k <= ingArray.length() && k <= arrayList.size(); k++) {
                            recIng = ingArray.getString(k - 1);
                            if (currIng.equals(recIng)) {
                                count++;
                                break;
                            }
                        }
                    }
                    if (count == ingArray.length())
                        rec_ids.add(i);
                }
                Log.d("Recipes", "Recipes: "+rec_ids);
                BGTask2 bgTask2 = new BGTask2(ctx);
                bgTask2.execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class BGTask2 extends AsyncTask<Void, Void, String> {

        Context ctx;

        BGTask2(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(rec_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSON_STRING1=bufferedReader.readLine())!=null){
                    stringBuilder.append(JSON_STRING1+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            jsonstr1 = s;
            recipeList = new ArrayList<String>();
            int count = 0;
            try {
                jsonArray1 = new JSONArray(jsonstr1);
                for(int i=1; i<=jsonArray1.length()&&count!=rec_ids.size(); i++){
                    if(i==rec_ids.get(count)) {
                        jsonObject1 = jsonArray1.getJSONObject(i - 1);
                        recipeList.add(jsonObject1.getString("rec_name"));

                        count++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            recipeListView = (ListView)findViewById(R.id.recipeListView);
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ctx, R.layout.reclist, R.id.recipeName, recipeList);
            recipeListView.setAdapter(arrayAdapter);

            recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ctx, RecipeDetails.class);
                    intent.putExtra("recipeName", recipeList.get(position));
                    startActivity(intent);
                }
            });

        }
    }

}
