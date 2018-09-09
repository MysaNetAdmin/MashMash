package com.example.samy9.mashmash;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Acceuil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);
        FileOutputStream outputStream;

        // Creating file with default value
        try {
            outputStream = openFileOutput("currency_old.txt", 0);
            outputStream.write("0.66".getBytes());
            outputStream.close();
        } catch (Exception e) {
        }
    }

    private boolean checked = false;

    /** Called when the user taps the Send button */
    public void sendMessage(View view)
    {
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        double d = Double.parseDouble(message);
        currentCurrencyRates rates = new currentCurrencyRates();
        double taux;

        try {
            taux = rates.execute().get();}
        catch (Exception e) {
            taux = 0.0;
        }

        if (checked)
            d = (((d * 20) / 100) + d) * taux;
        else
            d = d * taux;
        TextView changingText = findViewById(R.id.textView);
        changingText.setText(d + "");
    }

    /** Called when the user check the CheckBox */
    public void onCheckboxClicked(View view)
    {
        if ((view.getId()) == R.id.checkBox)
            checked = !checked;
    }

    private class currentCurrencyRates extends AsyncTask<URL, Void, Double>
    {
        protected Double doInBackground(URL... url)
        {
            // Setting URL
            String url_str = "https://v3.exchangerate-api.com/bulk/54269c2a9d79d57f892d2e4a/CAD";
            Double rate = 0.0;

            // Making Request
            try {
                FileOutputStream outputStream;
                URL url2 = new URL(url_str);
                HttpURLConnection request = (HttpURLConnection) url2.openConnection();
                request.connect();

                // Convert to JSON
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject jsonobj = root.getAsJsonObject();
                JsonObject jsonobj2 = jsonobj.get("rates").getAsJsonObject();

                // Stocking new value on the file
                outputStream = openFileOutput("currency_old.txt", 0);
                outputStream.write(jsonobj2.get("EUR").getAsString().getBytes());
                outputStream.close();

                // Accessing object
                rate = jsonobj2.get("EUR").getAsDouble();
            } catch (Exception e) {
                FileInputStream inputStream;

                // Reading the file with the default value
                try {
                    inputStream = openFileInput("currency_old.txt");
                    inputStream.read();
                    inputStream.close();
                } catch (Exception e2) {
                    rate = 0.0;
                }
            }
            return rate;
        }
    }
}
