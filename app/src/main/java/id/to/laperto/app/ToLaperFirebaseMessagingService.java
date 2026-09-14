package id.to.laperto.app;

import android.app.*;
import android.content.*;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ToLaperFirebaseMessagingService extends FirebaseMessagingService {
    @Override public void onNewToken(String token) {
        super.onNewToken(token);
        getSharedPreferences("tlt_native", MODE_PRIVATE).edit().putString("fcm_token", token).apply();
    }

    @Override public void onMessageReceived(RemoteMessage msg) {
        super.onMessageReceived(msg);
        String title = msg.getNotification()!=null ? msg.getNotification().getTitle() : msg.getData().get("title");
        String body = msg.getNotification()!=null ? msg.getNotification().getBody() : msg.getData().get("body");
        if (title==null) title="To, Laper To";
        if (body==null) body="Ada pembaruan pesanan.";
        showNotification(title, body);
    }

    private void showNotification(String title, String body) {
        Intent launch = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent pi = PendingIntent.getActivity(this, 4801, launch, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, "to_laper_to_orders")
            .setSmallIcon(R.drawable.ic_stat_food)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setDefaults(NotificationCompat.DEFAULT_ALL);
        try { NotificationManagerCompat.from(this).notify((int)(System.currentTimeMillis()%100000), b.build()); } catch(SecurityException ignored) {}
    }
}
