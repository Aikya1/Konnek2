package com.aikya.konnek2.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.aikya.konnek2.utils.listeners.AppTranslatorUtil;

import java.util.ArrayList;

/**
 * Created by Lenovo on 23-10-2017.
 */

public class AppSpeechToTextConvertor implements AppIConvertor {      // by suresh for Voice to Text Offline Conversion


    private ArrayList data;
    SpeechRecognizer sr;
    Intent intent;

    public AppSpeechToTextConvertor(AppConversionCallaback appConversionCallaback) {
        this.appConversionCallaback = appConversionCallaback;
    }

    private AppConversionCallaback appConversionCallaback;


    @Override
    public AppIConvertor initialize(String message, String SelectedLanguage, Activity appContext) {

//        SelectedLanguage = "zh_HK";
        //Prepeare Intent

//        SelectedLanguage = "gu_IN";
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, SelectedLanguage);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, SelectedLanguage);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, SelectedLanguage);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, SelectedLanguage);
       /* intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                message);*/

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                appContext.getPackageName());

        //Add listeners
        CustomRecognitionListener listener = new CustomRecognitionListener();
        sr = SpeechRecognizer.createSpeechRecognizer(appContext);
        sr.setRecognitionListener(listener);
        sr.startListening(intent);

        return this;
    }


    class CustomRecognitionListener implements RecognitionListener {
        private static final String TAG = "RecognitionListener";


        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error) {
            appConversionCallaback.onConversionErrorOccured(AppTranslatorUtil.getErrorText(error));
        }

        public void onResults(Bundle results) {
            ArrayList<String> result2 = results.getStringArrayList("results_recognition");
            appConversionCallaback.onConversionSuccess(result2.get(0));
        }

        public void onPartialResults(Bundle partialResults) {

        }

        public void onEvent(int eventType, Bundle params) {

        }


    }


}
