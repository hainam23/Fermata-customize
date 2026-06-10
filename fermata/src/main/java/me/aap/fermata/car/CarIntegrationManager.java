package me.aap.fermata.car;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Main manager for car-related features
 * Coordinates voice control, YouTube integration, and steering wheel listener
 */
public class CarIntegrationManager implements CarVoiceControl.CarVoiceCallback,
                                              CarSteeringWheelListener.SteeringWheelCallback {
    
    private final Context context;
    private CarVoiceControl voiceControl;
    private YouTubeSearchIntegration youtubeIntegration;
    private CarSteeringWheelListener steeringWheelListener;
    private CarIntegrationCallback callback;
    
    public interface CarIntegrationCallback {
        void onVoiceRecognized(String text);
        void onYouTubeSearchStarted(String query);
        void onYouTubeAppOpened();
        void onError(String errorMessage);
        void onVoiceListeningStarted();
        void onVoiceListeningStopped();
    }
    
    public CarIntegrationManager(@NonNull Context context) {
        this.context = context;
    }
    
    /**
     * Initialize all car integration components
     */
    public void initialize(@Nullable CarIntegrationCallback callback) {
        this.callback = callback;
        
        // Initialize voice control
        voiceControl = new CarVoiceControl(context, this);
        
        // Initialize YouTube integration
        youtubeIntegration = new YouTubeSearchIntegration(context);
        
        // Initialize steering wheel listener
        steeringWheelListener = new CarSteeringWheelListener(
            context, voiceControl, youtubeIntegration, this);
        steeringWheelListener.register();
    }
    
    /**
     * Start listening for voice input
     */
    public void startListening() {
        if (voiceControl != null) {
            voiceControl.startVoiceRecognition();
        }
    }
    
    /**
     * Stop listening for voice input
     */
    public void stopListening() {
        if (voiceControl != null) {
            voiceControl.stopVoiceRecognition();
        }
    }
    
    /**
     * Search YouTube with voice query
     */
    public void searchYouTube(String query) {
        if (youtubeIntegration != null) {
            youtubeIntegration.searchYouTube(query);
        }
    }
    
    /**
     * Open YouTube app
     */
    public void openYouTubeApp() {
        if (youtubeIntegration != null) {
            youtubeIntegration.openYouTubeApp();
        }
    }
    
    /**
     * Play specific YouTube video
     */
    public void playYouTubeVideo(String videoId) {
        if (youtubeIntegration != null) {
            youtubeIntegration.playYouTubeVideo(videoId);
        }
    }
    
    /**
     * Release all resources
     */
    public void release() {
        if (steeringWheelListener != null) {
            steeringWheelListener.unregister();
        }
        
        if (voiceControl != null) {
            voiceControl.release();
        }
    }
    
    // CarVoiceControl.CarVoiceCallback implementations
    
    @Override
    public void onVoiceRecognized(String text) {
        if (callback != null) {
            callback.onVoiceRecognized(text);
        }
        
        // Automatically search YouTube when voice is recognized
        if (steeringWheelListener != null) {
            steeringWheelListener.executeYouTubeSearch(text);
        }
    }
    
    @Override
    public void onError(String errorMessage) {
        if (callback != null) {
            callback.onError(errorMessage);
        }
    }
    
    @Override
    public void onListeningStarted() {
        if (callback != null) {
            callback.onVoiceListeningStarted();
        }
    }
    
    @Override
    public void onListeningStopped() {
        if (callback != null) {
            callback.onVoiceListeningStopped();
        }
    }
    
    // CarSteeringWheelListener.SteeringWheelCallback implementations
    
    @Override
    public void onVoiceButtonPressed() {
        // Voice button pressed on steering wheel
        if (callback != null) {
            callback.onVoiceListeningStarted();
        }
    }
    
    @Override
    public void onYouTubeSearchInitiated(String query) {
        if (callback != null) {
            callback.onYouTubeSearchStarted(query);
        }
    }
    
    @Override
    public void onYouTubeOpened() {
        if (callback != null) {
            callback.onYouTubeAppOpened();
        }
    }
    
    public boolean isListening() {
        return voiceControl != null && voiceControl.isListening();
    }
}
