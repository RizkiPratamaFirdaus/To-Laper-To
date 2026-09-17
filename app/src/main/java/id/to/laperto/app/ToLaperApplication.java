package id.to.laperto.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONObject;

public class ToLaperApplication extends Application {
    public static final String PREFS = "tlt_native";

    @Override public void onCreate() {
        super.onCreate();
        initializeFirebaseFromPrefs(this);
    }

    public static boolean saveAndInitializeFirebase(Context context, String json) {
        try {
            JSONObject o = new JSONObject(json);
            String projectId = o.getString("projectId");
            String appId = o.getString("appId");
            String apiKey = o.getString("apiKey");
            String senderId = o.getString("senderId");

            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString("firebase_project_id", projectId)
                .putString("firebase_app_id", appId)
                .putString("firebase_api_key", apiKey)
                .putString("firebase_sender_id", senderId)
                .apply();

            return initializeFirebase(context, projectId, appId, apiKey, senderId);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean initializeFirebaseFromPrefs(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String projectId = p.getString("firebase_project_id", "");
        String appId = p.getString("firebase_app_id", "");
        String apiKey = p.getString("firebase_api_key", "");
        String senderId = p.getString("firebase_sender_id", "");
        if (projectId.isEmpty() || appId.isEmpty() || apiKey.isEmpty() || senderId.isEmpty()) return false;
        return initializeFirebase(context, projectId, appId, apiKey, senderId);
    }

    private static synchronized boolean initializeFirebase(Context context, String projectId, String appId, String apiKey, String senderId) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                    .setProjectId(projectId)
                    .setApplicationId(appId)
                    .setApiKey(apiKey)
                    .setGcmSenderId(senderId)
                    .build();
                FirebaseApp.initializeApp(context, options);
            }
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
