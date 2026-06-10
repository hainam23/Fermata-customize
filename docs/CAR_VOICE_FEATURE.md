# Car Voice Control & YouTube Integration

## Overview
This feature enables voice control from car steering wheel buttons, automatically recognizing spoken queries and searching YouTube directly.

## Features
- **Voice Recognition**: Listens to steering wheel voice button presses
- **YouTube Search**: Automatically searches YouTube with recognized voice queries
- **App Integration**: Opens YouTube app or web browser based on device availability
- **Multilingual**: Supports device language settings

## Components

### 1. CarVoiceControl.java
Handles speech recognition using Android's SpeechRecognizer API.

**Key Methods:**
- `startVoiceRecognition()` - Start listening for voice input
- `stopVoiceRecognition()` - Stop listening
- `isListening()` - Check if currently listening

### 2. YouTubeSearchIntegration.java
Manages YouTube app and web integration.

**Key Methods:**
- `searchYouTube(String query)` - Search YouTube with text query
- `playYouTubeVideo(String videoId)` - Play specific video
- `openYouTubeApp()` - Open YouTube app

### 3. CarSteeringWheelListener.java
Listens for steering wheel button presses.

**Supported Buttons:**
- Voice Assist (KEYCODE_VOICE_ASSIST)
- Headset Hook (KEYCODE_HEADSETHOOK)
- Call Button (KEYCODE_CALL)

### 4. CarIntegrationManager.java
Main orchestrator for all car-related features.

## Usage

### Initialize in Activity
```java
public class MainActivity extends AppCompatActivity {
    private CarIntegrationManager carManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        carManager = new CarIntegrationManager(this);
        carManager.initialize(new CarIntegrationManager.CarIntegrationCallback() {
            @Override
            public void onVoiceRecognized(String text) {
                Log.d("CarVoice", "Recognized: " + text);
            }
            
            @Override
            public void onYouTubeSearchStarted(String query) {
                Log.d("CarVoice", "Searching YouTube: " + query);
            }
            
            @Override
            public void onYouTubeAppOpened() {
                Log.d("CarVoice", "YouTube app opened");
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e("CarVoice", "Error: " + errorMessage);
            }
            
            @Override
            public void onVoiceListeningStarted() {
                Log.d("CarVoice", "Listening started");
            }
            
            @Override
            public void onVoiceListeningStopped() {
                Log.d("CarVoice", "Listening stopped");
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (carManager != null) {
            carManager.release();
        }
    }
}
```

## Workflow

1. User presses voice button on steering wheel
2. `CarSteeringWheelListener` detects button press
3. `CarVoiceControl` starts listening for voice input
4. Android system displays voice recognition UI
5. User speaks search query
6. Speech recognized and converted to text
7. `YouTubeSearchIntegration` automatically searches YouTube with the text
8. YouTube app (or web) opens with search results

## Permissions Required

Add to `AndroidManifest.xml`:
```xml
<!-- Voice recognition -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.INTERNET" />

<!-- Media button events -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

<!-- Query YouTube app -->
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />

<!-- Media button receiver -->
<receiver
    android:name="me.aap.fermata.car.CarSteeringWheelListener"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MEDIA_BUTTON" />
    </intent-filter>
</receiver>
```

## Error Handling

The system handles various error scenarios:
- **Audio Errors**: Device microphone issues
- **Network Errors**: Connection problems for voice recognition
- **Permission Errors**: Missing microphone permission
- **No Match**: Voice not recognized
- **Timeout**: User didn't speak in time

All errors are reported via the callback interface.

## Language Support

Voice recognition automatically uses the device's default language/locale. The system supports any language available on the device.

## Performance Considerations

- Voice recognition runs in background without blocking UI
- YouTube search is performed asynchronously
- Resources are properly released on activity destruction
- Minimal battery impact when not actively listening

## Future Enhancements

- [ ] Integration with Fermata's own media player
- [ ] Custom voice commands for playback control
- [ ] Playlist creation from voice search results
- [ ] Integration with other streaming services
- [ ] Voice feedback confirmation
- [ ] Wake word detection for hands-free activation

## Troubleshooting

### Voice recognition not working
- Check microphone permission is granted
- Ensure internet connection for speech recognition
- Test with device language setting

### YouTube app not opening
- Verify YouTube app is installed
- Check QUERY_ALL_PACKAGES permission

### Steering wheel button not detected
- Verify device supports steering wheel controls
- Check media button events are being broadcast
