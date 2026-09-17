package id.to.laperto.app;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.webkit.*;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS = "tlt_native";
    private static final String PREF_URL = "web_url";
    private static final int NOTIF_REQ = 4801;

    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private ActivityResultLauncher<Intent> filePicker;
    private String trustedHost = "";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(10,104,65));
        getWindow().setNavigationBarColor(Color.WHITE);
        createNotificationChannel();
        requestNotificationPermission();

        filePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (fileCallback == null) return;
            Uri[] out = null;
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri u = result.getData().getData();
                if (u != null) out = new Uri[]{u};
            }
            fileCallback.onReceiveValue(out);
            fileCallback = null;
        });

        String url = getSharedPreferences(PREFS, MODE_PRIVATE).getString(PREF_URL, "");
        if (url == null || url.isBlank()) showUrlSetup();
        else openWeb(url);
    }

    private void showUrlSetup() {
        EditText input = new EditText(this);
        input.setHint("https://nama-site.netlify.app");
        input.setSingleLine(true);
        input.setPadding(48,24,48,24);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Alamat To, Laper To")
            .setMessage("Masukkan URL HTTPS To, Laper To di Netlify. Cukup sekali pada perangkat ini.")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Simpan & buka", null)
            .setNegativeButton("Keluar", (d,w) -> finish())
            .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String raw = input.getText().toString().trim();
            Uri uri;
            try { uri = Uri.parse(raw); } catch(Exception e) { input.setError("URL tidak valid"); return; }
            if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null || uri.getHost().isBlank()) {
                input.setError("Gunakan URL HTTPS, contoh https://nama-site.netlify.app");
                return;
            }
            String url = raw.endsWith("/") ? raw : raw + "/";
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(PREF_URL, url).apply();
            dialog.dismiss();
            openWeb(url);
        }));
        dialog.show();
    }

    private boolean isTrustedUrl(Uri uri) {
        if (uri == null) return false;
        return "https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null && uri.getHost().equalsIgnoreCase(trustedHost);
    }

    @SuppressWarnings("SetJavaScriptEnabled")
    private void openWeb(String url) {
        Uri base = Uri.parse(url);
        trustedHost = base.getHost() == null ? "" : base.getHost();

        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setMediaPlaybackRequiresUserGesture(true);
        s.setUserAgentString(s.getUserAgentString() + " ToLaperToAndroid/5.8");

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.addJavascriptInterface(new AndroidPushBridge(), "AndroidPush");
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                Uri u = req.getUrl();
                if (isTrustedUrl(u)) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); }
                catch(Exception ignored) { Toast.makeText(MainActivity.this, "Link tidak dapat dibuka", Toast.LENGTH_SHORT).show(); }
                return true;
            }

            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                try { new AndroidPushBridge().requestToken(); } catch(Exception ignored) {}
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView w, ValueCallback<Uri[]> cb, FileChooserParams p) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = cb;
                Intent i = p.createIntent();
                try { filePicker.launch(i); }
                catch(Exception e) {
                    fileCallback = null;
                    Toast.makeText(MainActivity.this, "Pemilih file tidak tersedia", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        webView.loadUrl(url);
    }

    public class AndroidPushBridge {
        @JavascriptInterface public boolean configureFirebase(String json) {
            boolean ok = ToLaperApplication.saveAndInitializeFirebase(MainActivity.this, json);
            if (ok) requestToken();
            return ok;
        }

        @JavascriptInterface public void requestToken() {
            try {
                if (FirebaseApp.getApps(MainActivity.this).isEmpty()) return;
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) return;
                    String token = task.getResult();
                    getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString("fcm_token", token).apply();
                    dispatchTokenToWeb(token);
                });
            } catch(Exception ignored) {}
        }

        @JavascriptInterface public String getToken() {
            return getSharedPreferences(PREFS, MODE_PRIVATE).getString("fcm_token", "");
        }
        @JavascriptInterface public String getDeviceName() {
            return (Build.MANUFACTURER + " " + Build.MODEL).trim();
        }
        @JavascriptInterface public String getAppVersion() { return "5.8-android"; }
        @JavascriptInterface public String getWebsiteUrl() {
            return getSharedPreferences(PREFS, MODE_PRIVATE).getString(PREF_URL, "");
        }
    }

    private void dispatchTokenToWeb(String token) {
        if (webView == null) return;
        runOnUiThread(() -> {
            String safe = JSONObject.quote(token);
            webView.evaluateJavascript(
                "window.dispatchEvent(new CustomEvent('tlt-native-fcm-token',{detail:{token:" + safe + "}}));",
                null
            );
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel c = new NotificationChannel(
                "to_laper_to_orders", "Pesanan To, Laper To", NotificationManager.IMPORTANCE_HIGH
            );
            c.setDescription("Pesanan masuk, perubahan status, pembayaran, dan pengingat makan");
            c.enableVibration(true);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIF_REQ);
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
