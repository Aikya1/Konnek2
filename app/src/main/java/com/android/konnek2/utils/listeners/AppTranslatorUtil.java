package com.android.konnek2.utils.listeners;

import android.app.Activity;
import android.content.Intent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

/**
 * Created by Lenovo on 23-10-2017.
 */

public class AppTranslatorUtil {




    public static final String FILED_TO_INITIALIZE_STT_ENGINE = "Sorry! Your device doesn\'t support speech input";
    public static final String FAILED_TO_INITILIZE_TTS_ENGINE = "Filed to initilize TTS engine";

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    public static boolean findString(ArrayList<String> listOfPossibleMatches, String stringToMatch) {

        if (null != listOfPossibleMatches) {

            for (String transaltion : listOfPossibleMatches) {

                if (transaltion.contains(stringToMatch)) {

                    return true;
                }
            }
        }
        return false;
    }

    public static void share(String messageToShare, Activity activity) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, messageToShare);
        activity.startActivity(Intent.createChooser(shareIntent, "Share using"));
    }
}
