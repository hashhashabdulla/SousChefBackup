package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by hashh on 23-04-2017.
 */

public class HomePage extends Activity {

    TextView fullname, addFavoritesText;
    ListView recipeList;
    Button searchIngButton, logoutButton;
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        final Intent intent = getIntent();

        String username = intent.getExtras().getString("username");
        fullname = (TextView)findViewById(R.id.fullname);
        addFavoritesText = (TextView)findViewById(R.id.addFavoritesText);
        searchIngButton = (Button)findViewById(R.id.searchIngButton);
        logoutButton = (Button)findViewById(R.id.logoutButton);

        new HomePageBGTask().execute(username);

        searchIngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavoritesText.setText("");
                startActivity(new Intent(HomePage.this, SearchIngredients.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class HomePageBGTask extends AsyncTask<String, Void, String>{

        String getUserDetails = "http://10.0.2.2/ingredients/favorites.php";

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(getUserDetails);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String data = URLEncoder.encode("username", "utf-8")+"="+URLEncoder.encode(params[0], "utf-8");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            final ArrayList<String> arrayList = new ArrayList<String>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String firstname = jsonObject.getString("firstname");
                String lastname = jsonObject.getString("lastname");
                fullname.setText(firstname+" "+lastname);

                JSONArray jsonArray = jsonObject.getJSONArray("favorites");
                if(jsonArray.length()==0)
                    addFavoritesText.setText("You have not added any recipes to favorites.");

                for(int i=0; i<jsonArray.length(); i++){
                    arrayList.add(i, jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            recipeList = (ListView)findViewById(R.id.recipeList);
            arrayAdapter = new ArrayAdapter(HomePage.this, R.layout.reclist, R.id.recipeName, arrayList);
            recipeList.setAdapter(arrayAdapter);

            recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(HomePage.this, RecipeDetails.class);
                    intent.putExtra("recipeName", arrayList.get(position));
                    startActivity(intent);
                }
            });
        }
    }
}
