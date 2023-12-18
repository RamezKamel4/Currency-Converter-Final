package com.example.currencyconverter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.currencyconverter.FloatRatesXmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    // Widgets
    TextView tv_to, tv_from, tv_result;
    EditText edt_amount, edtCurrencyFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        // Assume you have a button to trigger the API request
        Button btnGetRates = findViewById(R.id.btnGetRates);
        btnGetRates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the currency filter text
                String currencyFilter = edtCurrencyFilter.getText().toString().toUpperCase();

                // Execute AsyncTask to fetch and parse rates
                new GetCurrencyRatesTask().execute(currencyFilter);
            }
        });
    }

    // Initialize all widgets
    public void init() {
        tv_to = findViewById(R.id.tv_to);
        tv_from = findViewById(R.id.tv_from);
        tv_result = findViewById(R.id.tvresult);
        edt_amount = findViewById(R.id.edt_amount);
        edtCurrencyFilter = findViewById(R.id.edt_currency_filter);
    }

    // AsyncTask to fetch and parse rates in the background
    private class GetCurrencyRatesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String currencyFilter = params[0];
            String apiEndpoint = "http://www.floatrates.com/daily/usd.xml";  // Replace with your API endpoint

            try {
                URL url = new URL(apiEndpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();

                // Use FloatRatesXmlParser to parse the XML response
                String result = FloatRatesXmlParser.getCurrencyRatesBaseUsd(stream);

                // Filter the result based on the currency filter
                if (!currencyFilter.isEmpty()) {
                    result = filterResultByCurrency(result, currencyFilter);
                }

                stream.close();
                connection.disconnect();

                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: Unable to fetch currency rates.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Display the result in the TextView
            tv_result.setText(result);
        }
    }

    // Filter the result based on the currency
    private String filterResultByCurrency(String result, String currencyFilter) {
        StringBuilder filteredResult = new StringBuilder();
        String[] lines = result.split("\n");
        for (String line : lines) {
            if (line.contains(currencyFilter)) {
                filteredResult.append(line).append("\n");
            }
        }
        return filteredResult.toString();
    }


}
