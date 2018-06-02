package com.example.mathi.smartexpense.network;

import android.app.ProgressDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by mathi on 31/05/2018.
 */

public class ImageProcess {

    private HttpURLConnection httpURLConnection ;
    private URL url;
    private OutputStream outputStream;
    private BufferedWriter bufferedWriter ;
    private int RC ;
    private BufferedReader bufferedReader ;
    private boolean check = true;
    private StringBuilder stringBuilder;

    public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            url = new URL(requestURL);

            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(20000);

            httpURLConnection.setConnectTimeout(20000);

            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDoInput(true);

            httpURLConnection.setDoOutput(true);

            outputStream = httpURLConnection.getOutputStream();

            bufferedWriter = new BufferedWriter(

                    new OutputStreamWriter(outputStream, "UTF-8"));

            bufferedWriter.write(bufferedWriterDataFN(PData));

            bufferedWriter.flush();

            bufferedWriter.close();

            outputStream.close();

            RC = httpURLConnection.getResponseCode();

            if (RC == HttpsURLConnection.HTTP_OK) {

                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                stringBuilder = new StringBuilder();

                String RC2;

                while ((RC2 = bufferedReader.readLine()) != null) {

                    stringBuilder.append(RC2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

        stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
            if (check)
                check = false;
            else
                stringBuilder.append("&");

            stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

            stringBuilder.append("=");

            stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
        }

        return stringBuilder.toString();
    }
}
