package me.aap.fermata.car;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handles YouTube search integration for voice queries from car steering wheel
 */
public class YouTubeSearchIntegration {
    
    private static final String YOUTUBE_APP_PACKAGE = "com.google.android.youtube";
    private static final String YOUTUBE_SEARCH_URL = "https://www.youtube.com/results?search_query=";
    private static final String YOUTUBE_APP_SEARCH_INTENT = "com.google.android.youtube.SEARCH";
    
    private final Context context;
    
    public YouTubeSearchIntegration(@NonNull Context context) {
        this.context = context;
    }
    
    /**
     * Search YouTube with the recognized voice text
     * Opens YouTube app if available, otherwise opens web browser
     */
    public void searchYouTube(@NonNull String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            
            // Try to open YouTube app first
            if (isYouTubeAppInstalled()) {
                openYouTubeApp(encodedQuery);
            } else {
                // Fallback to web browser
                openYouTubeWeb(encodedQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Open YouTube app with search query
     */
    private void openYouTubeApp(String encodedQuery) {
        Intent intent = new Intent(YOUTUBE_APP_SEARCH_INTENT);
        intent.setPackage(YOUTUBE_APP_PACKAGE);
        intent.putExtra("query", encodedQuery);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // Fallback to web if app intent fails
            openYouTubeWeb(encodedQuery);
        }
    }
    
    /**
     * Open YouTube in web browser
     */
    private void openYouTubeWeb(String encodedQuery) {
        Uri uri = Uri.parse(YOUTUBE_SEARCH_URL + encodedQuery);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Check if YouTube app is installed
     */
    private boolean isYouTubeAppInstalled() {
        try {
            context.getPackageManager().getApplicationInfo(YOUTUBE_APP_PACKAGE, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Play a specific YouTube video
     */
    public void playYouTubeVideo(String videoId) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, 
                Uri.parse("https://www.youtube.com/watch?v=" + videoId));
            intent.setPackage(YOUTUBE_APP_PACKAGE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                // Fallback to web
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(webIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Open YouTube app directly
     */
    public void openYouTubeApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setPackage(YOUTUBE_APP_PACKAGE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            // Open web version
            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
