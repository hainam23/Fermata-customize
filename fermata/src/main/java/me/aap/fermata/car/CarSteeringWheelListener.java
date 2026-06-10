package me.aap.fermata.car;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.view.KeyEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Listens to car steering wheel control buttons
 * Specifically handles voice button press events
 */
public class CarSteeringWheelListener extends BroadcastReceiver {
    
    // Key codes for various car steering wheel buttons
    private static final int KEYCODE_VOICE_ASSIST = KeyEvent.KEYCODE_VOICE_ASSIST;
    private static final int KEYCODE_CALL = KeyEvent.KEYCODE_CALL;
    private static final int KEYCODE_HEADSETHOOK = KeyEvent.KEYCODE_HEADSETHOOK;
    
    private final CarVoiceControl voiceControl;
    private final YouTubeSearchIntegration youtubeIntegration;
    private final Context context;
    private final SteeringWheelCallback callback;
    
    public interface SteeringWheelCallback {
        void onVoiceButtonPressed();
        void onYouTubeSearchInitiated(String query);
        void onYouTubeOpened();
    }
    
    public CarSteeringWheelListener(@NonNull Context context,
                                   @NonNull CarVoiceControl voiceControl,
                                   @NonNull YouTubeSearchIntegration youtubeIntegration,
                                   @Nullable SteeringWheelCallback callback) {
        this.context = context;
        this.voiceControl = voiceControl;
        this.youtubeIntegration = youtubeIntegration;
        this.callback = callback;
    }
    
    /**
     * Register the steering wheel button listener
     */
    public void register() {
        IntentFilter filter = new IntentFilter();
        // Register for media button events
        context.registerReceiver(this, filter, Context.RECEIVER_NOT_EXPORTED);
    }
    
    /**
     * Unregister the listener
     */
    public void unregister() {
        try {
            context.unregisterReceiver(this);
        } catch (IllegalArgumentException e) {
            // Already unregistered
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event != null) {
                handleSteeringWheelButton(event);
            }
        }
    }
    
    /**
     * Handle steering wheel button press
     */
    private void handleSteeringWheelButton(KeyEvent event) {
        int keyCode = event.getKeyCode();
        
        // Only handle key down events
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return;
        }
        
        switch (keyCode) {
            case KEYCODE_VOICE_ASSIST:
            case KEYCODE_HEADSETHOOK:
            case KEYCODE_CALL:
                handleVoiceButtonPress();
                break;
        }
    }
    
    /**
     * Handle voice button press from steering wheel
     */
    private void handleVoiceButtonPress() {
        if (callback != null) {
            callback.onVoiceButtonPressed();
        }
        
        // Start voice recognition
        voiceControl.startVoiceRecognition();
    }
    
    /**
     * Execute YouTube search with voice query
     */
    public void executeYouTubeSearch(String query) {
        if (callback != null) {
            callback.onYouTubeSearchInitiated(query);
        }
        
        // Search YouTube with the recognized text
        youtubeIntegration.searchYouTube(query);
    }
    
    /**
     * Open YouTube app directly
     */
    public void openYouTubeApp() {
        if (callback != null) {
            callback.onYouTubeOpened();
        }
        
        youtubeIntegration.openYouTubeApp();
    }
}
