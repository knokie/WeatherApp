package com.zebra.globalsw.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private TextView tvLocation, tvTemperature, tvHumidity, tvWindSpeed, tvCloudiness;
    private Button btnRefresh;
    private ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = (TextView) findViewById(R.id.location);
        tvTemperature = (TextView) findViewById(R.id.temperature);
        tvHumidity = (TextView) findViewById(R.id.humidity);
        tvWindSpeed = (TextView) findViewById(R.id.wind_speed);
        tvCloudiness = (TextView) findViewById(R.id.cloudiness);
        btnRefresh = (Button) findViewById(R.id.button_refresh);
        ivIcon = (ImageView) findViewById(R.id.icon);

        btnRefresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                new WeatherDataRetrival().execute();
            }



        });
    }

    private class WeatherDataRetrival extends AsyncTask<Void, Void, String>{

        private static final String WEATHER_SOURCE = "http://api.openweathermap.org/data/2.5/weather?APPID=82445b6c96b99bc3ffb78a4c0e17fca5&mode=json&id=1735161";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            NetworkInfo networkInfo = ((ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                //Network is connected
            }
            else
            {
                Toast.makeText(MainActivity.this, "No network connection", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null)
            {
                final JSONObject weatherJSON;
                try {
                    weatherJSON = new JSONObject(result);
                    tvLocation.setText(weatherJSON.getString("name") + "," + weatherJSON.getJSONObject("sys").getString("country"));
                    tvWindSpeed.setText(String.valueOf(weatherJSON.getJSONObject("wind").getDouble("speed")) + "mps");
                    tvCloudiness.setText(String.valueOf(weatherJSON.getJSONObject("clouds").getInt("all")) + "%");

                    final JSONObject mainJSON = weatherJSON.getJSONObject("main");

                    tvTemperature.setText(String.valueOf(mainJSON.getDouble("temp") - 273.15));
                    tvHumidity.setText(String.valueOf(mainJSON.getInt("humidity")) + "%");

                    final JSONArray weatherJSONArray = weatherJSON.getJSONArray("weather");

                    if (weatherJSONArray.length() > 0)
                    {
                        int code = weatherJSONArray.getJSONObject(0).getInt("id");
                        ivIcon.setImageResource(getIcon(code));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }

        private int getIcon(int code)
        {
            switch(code / 100) {
                case 2:
                    return (int) R.drawable.ic_thunderstorm_large;

                case 3:
                    return (int) R.drawable.ic_drizzle_large;

                case 5:
                    return (int) R.drawable.ic_rain_large;

                case 6:
                    return (int) R.drawable.ic_snow_large;


                case 8:
                    switch (code % 100) {
                        case 0:
                            return (int) R.drawable.ic_day_clear_large;

                        case 1:
                            return (int) R.drawable.ic_day_few_clouds_large;

                        case 2:
                            return (int) R.drawable.ic_scattered_clouds_large;

                        case 3:
                        case 4:
                            return (int) R.drawable.ic_broken_clouds_large;
                    }
                    break;

                case 7:
                    if (code < 781)
                        return (int) R.drawable.ic_fog_large;
                    else
                        return (int) R.drawable.ic_tornado_large;

                case 9:
                    if (code == 900)
                        return (int) R.drawable.ic_tornado_large;
                    else if (code == 905)
                        return (int) R.drawable.ic_windy_large;
                    else
                        return (int) R.drawable.ic_hail_large;

            }
            return 0;
        }


        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(WEATHER_SOURCE);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    if(bufferedReader != null) {
                        String readline;
                        StringBuffer strBuffer = new StringBuffer();
                        while ((readline = bufferedReader.readLine()) != null)
                        {
                            strBuffer.append(readline);
                        }
                        return strBuffer.toString();
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}

