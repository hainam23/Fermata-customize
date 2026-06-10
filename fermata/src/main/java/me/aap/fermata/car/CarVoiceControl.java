package me.aap.fermata.car;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Handles voice recognition from car steering wheel buttons
 * and integrates with YouTube search functionality
 */
public class CarVoiceControl implements RecognitionListener {
    
    private final Context context;
    private final SpeechRecognizer speechRecognizer;
    private final CarVoiceCallback callback;
    private boolean isListening = false;
    
    public interface CarVoiceCallback {
        void onVoiceRecognized(String text);
        void onError(String errorMessage);
        void onListeningStarted();
        void onListeningStopped();
    }
    
    public CarVoiceControl(@NonNull Context context, @NonNull CarVoiceCallback callback) {
        this.context = context;
        this.callback = callback;
        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        this.speechRecognizer.setRecognitionListener(this);
    }
    
    /**
     * Start listening for voice input from car steering wheel
     */
    public void startVoiceRecognition() {
        if (isListening) {
            return;
        }
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tìm kiếm video YouTube...");
        
        isListening = true;
        callback.onListeningStarted();
        speechRecognizer.startListening(intent);
    }
    
    /**
     * Stop voice recognition
     */
    public void stopVoiceRecognition() {
        if (isListening) {
            speechRecognizer.stopListening();
            isListening = false;
        }
    }
    
    @Override
    public void onReadyForSpeech(Bundle params) {
        // Ready to accept voice input
    }
    
    @Override
    public void onBeginningOfSpeech() {
        // User has started speaking
    }
    
    @Override
    public void onRmsChanged(float rmsdB) {
        // Sound level changed
    }
    
    @Override
    public void onBufferReceived(byte[] buffer) {
        // Audio buffer received
    }
    
    @Override
    public void onEndOfSpeech() {
        // User stopped speaking
    }
    
    @Override
    public void onError(int error) {
        String errorMessage = getErrorMessage(error);
        callback.onError(errorMessage);
        isListening = false;
        callback.onListeningStopped();
    }
    
    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(
            SpeechRecognizer.RESULTS_RECOGNITION);
        
        if (matches != null && !matches.isEmpty()) {
            String recognizedText = matches.get(0);
            callback.onVoiceRecognized(recognizedText);
        }
        
        isListening = false;
        callback.onListeningStopped();
    }
    
    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> partial = partialResults.getStringArrayList(
            SpeechRecognizer.RESULTS_RECOGNITION);
        if (partial != null && !partial.isEmpty()) {
            callback.onVoiceRecognized(partial.get(0));
        }
    }
    
    @Override
    public void onEvent(int eventType, Bundle params) {
        // Additional events
    }
    
    /**
     * Convert error code to human-readable message
     */
    private String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Lỗi âm thanh";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Lỗi khách hàng";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Quyền truy cập âm thanh bị từ chối";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Lỗi kết nối mạng";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "Không nhận dạng được giọng nói";
            case SpeechRecognizer.ERROR_SERVER:
                return "Lỗi máy chủ";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "Hết thời gian chờ";
            default:
                return "Lỗi không xác định";
        }
    }
    
    /**
     * Release resources
     */
    public void release() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
    
    public boolean isListening() {
        return isListening;
    }
}
