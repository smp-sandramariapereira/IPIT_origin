package br.com.ipit.professor;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainActivity extends Activity {
 private WebView web;
 private SharedPreferences prefs;
 private final TeacherApi teacherApi=new TeacherApi();
 private volatile boolean destroyed=false;
 private static final String HOST = "appassets.androidplatform.net";
 private static final Set<String> ASSETS = new HashSet<>(Arrays.asList("index.html","styles.css","core.js","app.js"));
 @Override public void onCreate(Bundle state) {
  super.onCreate(state);
  prefs = getSharedPreferences("ipit", MODE_PRIVATE);
  web = new WebView(this);
  web.setBackgroundColor(0xfffffdf8);
  setContentView(web);
  web.setOnApplyWindowInsetsListener((view,insets) -> {
   if (android.os.Build.VERSION.SDK_INT >= 30) {
    android.graphics.Insets safe = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.ime() | WindowInsets.Type.displayCutout());
    view.setPadding(safe.left,safe.top,safe.right,safe.bottom);
   } else {
    view.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());
   }
   return insets;
  });
  web.requestApplyInsets();
  WebSettings settings=web.getSettings();
  settings.setJavaScriptEnabled(true);
  settings.setDomStorageEnabled(false);
  settings.setAllowFileAccess(false);
  settings.setAllowContentAccess(false);
  settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
  WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
  web.setWebChromeClient(new WebChromeClient() {
   @Override public boolean onJsConfirm(WebView view,String url,String message,JsResult result){
    new android.app.AlertDialog.Builder(MainActivity.this).setMessage(message)
     .setPositiveButton("Confirmar",(dialog,which)->result.confirm())
     .setNegativeButton("Cancelar",(dialog,which)->result.cancel())
     .setOnCancelListener(dialog->result.cancel()).show();
    return true;
   }
  });
  web.addJavascriptInterface(new LocalStore(), "IPITAndroid");
  web.setWebViewClient(new WebViewClient() {
   @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
    Uri uri=request.getUrl();
    String path=uri.getPath();
    if ("https".equals(uri.getScheme()) && HOST.equals(uri.getHost()) && path!=null && path.startsWith("/assets/")) {
     String name=path.substring(8);
     if (ASSETS.contains(name)) {
      try {
       String mime=name.endsWith(".css")?"text/css":name.endsWith(".js")?"application/javascript":"text/html";
       return new WebResourceResponse(mime,"UTF-8",getAssets().open(name));
      } catch(IOException ignored) {}
     }
    }
    return new WebResourceResponse("text/plain","UTF-8",403,"Blocked",Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));
   }
   @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
    Uri uri=request.getUrl();
    if ("https".equals(uri.getScheme()) && HOST.equals(uri.getHost())) return false;
    if ("https".equals(uri.getScheme()) && ("github.com".equals(uri.getHost()) || "smp-sandramariapereira.github.io".equals(uri.getHost()))) {
     try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); } catch(android.content.ActivityNotFoundException ignored) {}
    }
    return true;
   }
  });
  web.loadUrl("https://"+HOST+"/assets/index.html");
 }
 public class LocalStore {
  @JavascriptInterface public String configureApi(String endpoint,String model,String key){return teacherApi.configure(endpoint,model,key);}
  @JavascriptInterface public void disconnectApi(){teacherApi.disconnect();}
  @JavascriptInterface public void chatApi(String id,String history,String context){
   if(id==null || !id.matches("[0-9]+-[0-9]+"))return;
   teacherApi.chat(history,context,(text,error)->runOnUiThread(()->{
    if(destroyed)return;
    String callback="window.ipitApiResult && window.ipitApiResult("+org.json.JSONObject.quote(id)+","+
      (text==null?"null":org.json.JSONObject.quote(text))+","+(error==null?"null":org.json.JSONObject.quote(error))+")";
    web.evaluateJavascript(callback.replace("\\u2028"," ").replace("\\u2029"," "),null);
   }));
  }
  @JavascriptInterface public String load() { return prefs.getString("plan",""); }
  @JavascriptInterface public boolean save(String json) {
   if (json==null || json.length()>200000) return false;
   try { new org.json.JSONObject(json); return prefs.edit().putString("plan",json).commit(); }
   catch(org.json.JSONException e) { return false; }
  }
  @JavascriptInterface public boolean erase() { return prefs.edit().clear().commit(); }
  @JavascriptInterface public void share(String text) {
   if(text==null || text.length()>200000) return;
   runOnUiThread(() -> {
    Intent send=new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,text);
    startActivity(Intent.createChooser(send,"Compartilhar registro IPIT"));
   });
  }
 }
 @Override public void onBackPressed() {
  web.evaluateJavascript("window.ipitBack && window.ipitBack()", value -> {
   if (!"true".equals(value)) MainActivity.super.onBackPressed();
  });
 }
 @Override protected void onDestroy() { destroyed=true;teacherApi.disconnect();web.removeJavascriptInterface("IPITAndroid"); web.destroy(); super.onDestroy(); }
}
