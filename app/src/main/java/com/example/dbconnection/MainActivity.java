package com.example.dbconnection;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADD = "address";

    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String, String>>();
        String urrl ="http://10.0.2.2/PHP_insert.php";
        getData("http://10.0.2.2/PHP_connection.php"); //수정 필요
        Button joinbtn = (Button) findViewById(R.id.join);
        EditText age = (EditText) findViewById(R.id.age);
        EditText name = (EditText) findViewById(R.id.name);
        EditText adress = (EditText) findViewById(R.id.adress);

        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData(urrl,age.getText().toString(), name.getText().toString(),adress.getText().toString());
            }
        });
    }


    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String name = c.getString(TAG_NAME);
                String address = c.getString(TAG_ADD);

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_ID, id);
                persons.put(TAG_NAME, name);
                persons.put(TAG_ADD, address);

                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, personList, R.layout.list_item,
                    new String[]{TAG_ID, TAG_NAME, TAG_ADD},
                    new int[]{R.id.id, R.id.name, R.id.address}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void insertData(String urrl, String age, String name, String address){
        class GetDataJSON extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Log.d("Tag : ", s); // php에서 가져온 값을 최종 출력함
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(String... params) {
                try{
                    String ageText = (String) params[0];
                    String nameText = (String) params[1];
                    String addressText = (String) params[2];

                    String data = URLEncoder.encode("age","UTF-8") + "=" + URLEncoder.encode(ageText,"UTF-8");
                    data += "&" + URLEncoder.encode("name","UTF-8") + "=" + URLEncoder.encode(nameText, "UTF-8")
                    +"&" + URLEncoder.encode("adress","UTF-8")+"="+URLEncoder.encode(addressText,"UTF-8");
                    String data1 = "age="+ageText+"&"+"name="+nameText+"&"+"adress="+addressText;
                    URL url = new URL(urrl);
                    System.out.println(url);
                    System.out.println(data);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestProperty("Content-Type","application/x-www.-form-urlencoded");
//                    conn.setRequestMethod("POST");
//                    conn.setConnectTimeout(5000);
//                    conn.setDoOutput(true);
//                    conn.setDoInput(true);
//                    OutputStream outputStream = conn.getOutputStream();
//                    outputStream.write(data1.getBytes("UTF-8"));
//                    outputStream.flush();
//                    outputStream.close();
//                    conn.disconnect();
//                    return "1";
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
                    outputStreamWriter.write(data);
                    outputStreamWriter.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    Log.d("tag : ", sb.toString()); // php에서 결과값을 리턴
                    return sb.toString();

                }catch (Exception e){
                    return new String("Exception: "+e.getMessage());
                }
            }

        }
        GetDataJSON tast = new GetDataJSON();
        tast.execute(age,name,address);

    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }


            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}
