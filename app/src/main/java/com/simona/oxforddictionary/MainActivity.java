package com.simona.oxforddictionary;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText inputED;
    Button searchBtn;
    TextView nameTV, definitionTV, synonymusTV, exampleTV;
    ImageButton spearkerButton;
    TextToSpeech tts;
    String searchedWordString = "";
    View.OnClickListener clickBtnListener;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        speak();

    }


    private void initViews() {
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#135D98")));
        searchBtn = findViewById(R.id.searchButton);
        inputED = findViewById(R.id.editText);
        definitionTV = findViewById(R.id.textViewDefinitieID);
        exampleTV = findViewById(R.id.textViewExemID);
        nameTV = findViewById(R.id.numeID);
        synonymusTV = findViewById(R.id.textViewSinoID);
        spearkerButton = findViewById(R.id.speakerButton);
        spearkerButton.setEnabled(false);

        clickBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedID = view.getId();
                switch (clickedID) {
                    case R.id.searchButton:
                        if (inputED.getText().toString().trim().equals(null)){
                            return;
                        }
                        getResultsFromAPIbyJSON();
                        break;
                    case R.id.speakerButton:
                        tts.setSpeechRate(1.0f);
                        tts.speak(searchedWordString, TextToSpeech.QUEUE_FLUSH, null);
                        break;
                }
            }
        };

        searchBtn.setOnClickListener(clickBtnListener);
        spearkerButton.setOnClickListener(clickBtnListener);

        handler = new Handler(Looper.getMainLooper());
    }

    private void speak() {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int rezultat = tts.setLanguage(Locale.ENGLISH);
                    if (rezultat == TextToSpeech.LANG_MISSING_DATA || rezultat == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("AICI ", "don't know the pronunciation");
                    } else {
                        spearkerButton.setEnabled(true);
                        Log.i("AICI ", " ACTIVARE BUTON");
                    }
                }
            }
        });
    }

    private void getResultsFromAPIbyJSON() {
        Word[] wordObjectReturnedByJSON = {null};
        Thread jsonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = urlConstruct();
                wordObjectReturnedByJSON[0] = JSONparse.allTogether(url);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(wordObjectReturnedByJSON[0]);
                    }
                });

            }
        });
        jsonThread.start();
    }

    private String urlConstruct() {
        searchedWordString = inputED.getText().toString().toLowerCase();
        String a = "https://od-api.oxforddictionaries.com:443/api/v2/entries/en-us/" + searchedWordString + "?&strictMatch=false";
        return a;
    }

    private void updateUI(Word o) {
        inputED.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputED.getWindowToken(), 0);

        try {
            nameTV.setText("SEARCHED WORD: " + o.getWord());

            definitionTV.setText("DEFINITIONS: " + "\n");
            for (int i = 0; i < o.getDefinition().size(); i++) {
                definitionTV.append(o.getDefinition().get(i) + "\n ");
            }

            synonymusTV.setText("SYNONYMS: " + "\n");
            for (int i = 0; i < o.getSynonims().size(); i++) {
                synonymusTV.append(o.getSynonims().get(i) + "\n ");
            }

            exampleTV.setText("EXAMPLES: " + "\n");
            for (int i = 0; i < o.getExamples().size(); i++) {
                exampleTV.append(o.getExamples().get(i) + "\n ");
            }

        } catch (NullPointerException e) {
            String x = inputED.getText().toString().trim().toLowerCase();
            nameTV.setText("Cuvantul " + x + " nu exista in dictionar." + "\n" + "Try again !");
            definitionTV.setText("");
            synonymusTV.setText("");
            exampleTV.setText("");
            inputED.setText("");
            inputED.requestFocus();

        }
    }


}