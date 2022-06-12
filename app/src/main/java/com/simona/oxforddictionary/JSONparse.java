package com.simona.oxforddictionary;

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

public class JSONparse {

    // https://developer.oxforddictionaries.com/documentation#!/Entries/get_entries_source_lang_word_id
    //https://developer.oxforddictionaries.com/documentation/making-requests-to-the-api
    private static URL createURL(String x) {
        URL myURL = null;
        try {
            myURL = new URL(x);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return myURL;
    }

    private static String makeHttpRequestAndReadFromStream(URL myUrl) {
        String answer = "";
        String appId = MainActivity.getApiIDmain();
        String APP_KEY = MainActivity.getApiKEYmain();
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.setRequestProperty("app_id", appId);
            httpURLConnection.setRequestProperty("app_key", APP_KEY);
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String currentLine = "";
            StringBuffer stringBuffer = new StringBuffer();
            while ((currentLine = bufferedReader.readLine()) != null) {
                stringBuffer.append(currentLine);
            }
            answer = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return answer;
    }

    private static Word parseJSON(String x) {
        Word word = null;
        String searchedWord = "";
        ArrayList<String> definitionArray = new ArrayList<>();
        ArrayList<String> synonymsArray = new ArrayList<>();
        ArrayList<String> examplesArray = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(x);
            searchedWord = root.getString("word");
            JSONArray results = root.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentResult = results.getJSONObject(i);
                JSONArray lexicalEntries = currentResult.getJSONArray("lexicalEntries");
                JSONArray exampleJsonArray;
                JSONArray synonymusJsonArray;

                for (int j = 0; j < lexicalEntries.length(); j++) {
                    JSONObject currentLexical = lexicalEntries.getJSONObject(j);
                    JSONArray entries = currentLexical.getJSONArray("entries");

                    for (int k = 0; k < entries.length(); k++) {
                        JSONObject currentEntry = entries.getJSONObject(k);
                        JSONArray senses = currentEntry.getJSONArray("senses");

                        for (int h = 0; h < senses.length(); h++) {
                            JSONObject currentSenses = senses.getJSONObject(h);
                            try {
                                JSONArray definition = currentSenses.getJSONArray("definitions");
                                String currentDefinition = definition.getString(0);
                                definitionArray.add(currentDefinition);
                            } catch (JSONException e) {
                                try {
                                    JSONArray subsenses = currentSenses.getJSONArray("subsenses");
                                    try {
                                        for (int q = 0; q < subsenses.length(); q++) {
                                            JSONObject subsenseCurrent = subsenses.getJSONObject(q);
                                            JSONArray definitiiJsonArray = subsenseCurrent.getJSONArray("definitions");
                                            String defDinSubsens = definitiiJsonArray.getString(0);
                                            definitionArray.add(defDinSubsens);
                                        }
                                    } catch (JSONException ex) {
                                    }
                                } catch (JSONException ex) {
                                }
//                                e.printStackTrace();
                            }
                            try {
                                // check whether there are examples in each element of SENSES
                                exampleJsonArray = currentSenses.getJSONArray("examples");
                                for (int f = 0; f < exampleJsonArray.length(); f++) {
                                    JSONObject obiectExempleCurent = exampleJsonArray.getJSONObject(f);
                                    String exempluCurent = obiectExempleCurent.getString("text");
                                    examplesArray.add(exempluCurent);
//                                    Log.i("resul=exempluCurr " + f, " = " + exempluCurent);
                                }
                            } catch (JSONException e) {
                                // is there a subsens in each ENTRIES
                                try {
                                    JSONArray subsenses = currentSenses.getJSONArray("subsenses");
                                    for (int q = 0; q < subsenses.length(); q++) {
                                        //Log.i("resul= forul cu g", "acum");
                                        JSONObject subsenseCurrent = subsenses.getJSONObject(q);
                                        // might not find EXAMPLES in each element of SUBSENSES
                                        try {
                                            exampleJsonArray = subsenseCurrent.getJSONArray("examples");
                                            for (int r = 0; r < exampleJsonArray.length(); r++) {
//                                                Log.i("resul=forul cu r", "acum");
                                                examplesArray.add(((JSONObject) exampleJsonArray.getJSONObject(r)).getString("text"));
                                            }
                                        } catch (JSONException ex) {
                                        }
                                    }
                                } catch (JSONException ex) {
                                }
                            }

                            try {
                                // check whether there are synonymus in each element of SENSES
                                synonymusJsonArray = currentSenses.getJSONArray("synonyms");
                                // Log.i("resul=SynoSUS=", sinonimeJsonArray.length()+"");
                                for (int s = 0; s < synonymusJsonArray.length(); s++) {
                                    JSONObject sinoCurent = synonymusJsonArray.getJSONObject(s);
                                    synonymsArray.add(sinoCurent.getString("text"));
                                }

                            } catch (JSONException e) {
                                // is there a subsens in each ENTRIES
                                try {
                                    JSONArray subsenses = currentSenses.getJSONArray("subsenses");
                                    for (int v = 0; v < subsenses.length(); v++) {
                                        JSONObject subsenseCurrent = subsenses.getJSONObject(v);
                                        // is there a synonymus in each SUBSENS
                                        try {
                                            synonymusJsonArray = subsenseCurrent.getJSONArray("synonyms");
                                            //Log.i("resul=SynoCATCH=", sinonimeJsonArray.length()+"");
                                            for (int w = 0; w < synonymusJsonArray.length(); w++) {
//                                                Log.i("resul=forul cu w", "acum");
                                                String sino = ((JSONObject) synonymusJsonArray.getJSONObject(w)).getString("text");
                                                synonymsArray.add(sino);
//                                                Log.i("resul=sinoCATCH=", sino);
                                            }
                                        } catch (JSONException ex) {
                                        }
                                    }
                                } catch (JSONException ex) {
                                }
                            }
                        }
                    }
                }

            }
            if (synonymsArray.size() == 0){
                synonymsArray.add("Didn't find any synonymus");
            }
            if (examplesArray.size() == 0){
                examplesArray.add("Didn't find any examples");
            }
            word = new Word(searchedWord, definitionArray, synonymsArray, examplesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }

    public static Word allTogether(String x){
        URL myUrl = createURL(x);
        String answer = makeHttpRequestAndReadFromStream(myUrl);
        Word word = parseJSON(answer);
        return word;
    }

}
