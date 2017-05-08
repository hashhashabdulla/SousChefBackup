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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hashh on 4/2/2017.
 */

public class SearchIngredients extends Activity {
    static String result = "";

    //    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    ListView listView;
    Button addButton;
    AutoCompleteTextView ingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_ingredients);

        arrayList = new ArrayList<String>();
        Log.d("WORLD", "HEYYYYY!!!");

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute();

        addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 1;
                listView = (ListView)findViewById(R.id.ingListView);
                //arrayAdapter = new ArrayAdapter<String>(this, R.layout.inglist, R.id.ingItem, arrayList);

                for(int i=0; i<arrayList.size(); i++){
                    if(result.equals(arrayList.get(i))){
                        flag = 0;
                        break;
                    }
                }
                if(flag==1) {
                    //addItem(arrayAdapter);
                    arrayList.add(result);

                }
                else
                    Toast.makeText(getApplicationContext(),"Ingredient already added", Toast.LENGTH_LONG).show();
                CustomAdapter customAdapter = new CustomAdapter(arrayList, getApplicationContext());

                listView.setAdapter(customAdapter);
                Log.d("RESULT","List: "+arrayList);

                ingName.setText("");

            }
        });
    }

    public void searchButton(View view){
        Intent intent = new Intent(this, RecipeList.class);
        intent.putStringArrayListExtra("recipe_list", arrayList);
        startActivity(intent);
    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {

        Context ctx;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... params) {
            String ing_url = "http://10.0.2.2/ingredients/selectIngredients.php";

            try {
                URL url = new URL(ing_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


                //String line = "";
                String response = bufferedReader.readLine();
                //while((line = bufferedReader.readLine())!=null)
                  //  response += line;


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return response;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("HELLO2", "Ingredients:"+result);
            List<String> ingredientList = new ArrayList<String>();

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("ingredients");
                for(int i=0; i<jsonArray.length(); i++){
                    ingredientList.add(jsonArray.getString(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //String[] ingredients = result.split(",");

            ingName = (AutoCompleteTextView)findViewById(R.id.ingName);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.select_dialog_item, ingredientList);
            ingName.setThreshold(1);
            ingName.setAdapter(adapter);

            ingName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SearchIngredients.result = (String)parent.getItemAtPosition(position);
                }
            });
            Log.d("RESULT2", "Item: "+ SearchIngredients.result);
        }


    }
}
