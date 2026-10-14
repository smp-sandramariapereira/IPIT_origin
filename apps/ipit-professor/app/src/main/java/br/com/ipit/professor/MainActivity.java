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
 private String pendingPdf;
 private static final int SAVE_PDF=410;
 private static final String HOST = "appassets.androidplatform.net";
 private static final Set<String> ASSETS = new HashSet<>(Arrays.asList("index.html","styles.css","core.js","app.js"));
 @Override public void onCreate(Bundle state) {
  super.onCreate(state);
  if(state!=null)pendingPdf=state.getString("pendingPdf");
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
  @JavascriptInterface public void exportPdf(String report,boolean whatsapp){
   if(report==null || report.length()>200000)return;
   runOnUiThread(()->{
    try{
     File file=PdfExporter.create(MainActivity.this,report);
     Uri uri=androidx.core.content.FileProvider.getUriForFile(MainActivity.this,getPackageName()+".pdf",file);
     Intent send=new Intent(Intent.ACTION_SEND).setType("application/pdf").putExtra(Intent.EXTRA_STREAM,uri)
      .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
     send.setClipData(android.content.ClipData.newRawUri("Relatório IPIT",uri));
     if(whatsapp){
      try{startActivity(new Intent(send).setPackage("com.whatsapp"));return;}
      catch(android.content.ActivityNotFoundException ignored){}
      try{startActivity(new Intent(send).setPackage("com.whatsapp.w4b"));return;}
      catch(android.content.ActivityNotFoundException ignored){}
      android.widget.Toast.makeText(MainActivity.this,"WhatsApp não disponível. Escolha outro aplicativo.",android.widget.Toast.LENGTH_LONG).show();
     }
     startActivity(Intent.createChooser(send,"Compartilhar PDF"));
    }catch(Exception e){android.widget.Toast.makeText(MainActivity.this,"Não foi possível gerar ou compartilhar o PDF.",android.widget.Toast.LENGTH_LONG).show();}
   });
  }
  @JavascriptInterface public void savePdf(String report){
   if(report==null || report.length()>200000)return;
   runOnUiThread(()->{
    if(pendingPdf!=null){android.widget.Toast.makeText(MainActivity.this,"Conclua ou cancele o salvamento atual.",android.widget.Toast.LENGTH_LONG).show();return;}
    try{
     pendingPdf=PdfExporter.create(MainActivity.this,report).getName();
     Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
      .setType("application/pdf").putExtra(Intent.EXTRA_TITLE,"IPIT-Professor-relatorio.pdf");
     startActivityForResult(intent,SAVE_PDF);
    }catch(Exception e){pendingPdf=null;android.widget.Toast.makeText(MainActivity.this,"Não foi possível abrir o salvamento do PDF.",android.widget.Toast.LENGTH_LONG).show();}
   });
  }
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
 @Override protected void onSaveInstanceState(Bundle out){
  super.onSaveInstanceState(out);
  if(pendingPdf!=null)out.putString("pendingPdf",pendingPdf);
 }
 @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
  super.onActivityResult(requestCode,resultCode,data);
  if(requestCode!=SAVE_PDF)return;
  String name=pendingPdf;pendingPdf=null;
  if(name==null)return;
  File source=new File(new File(getCacheDir(),"pdf-exports"),name);
  if(resultCode!=RESULT_OK||data==null||data.getData()==null){source.delete();return;}
  try(InputStream in=new FileInputStream(source);OutputStream out=getContentResolver().openOutputStream(data.getData())){
   if(out==null)throw new IOException();
   byte[] buffer=new byte[8192];int count;
   while((count=in.read(buffer))!=-1)out.write(buffer,0,count);
   out.flush();
   android.widget.Toast.makeText(this,"PDF salvo no local escolhido.",android.widget.Toast.LENGTH_LONG).show();
  }catch(Exception e){android.widget.Toast.makeText(this,"Não foi possível salvar o PDF.",android.widget.Toast.LENGTH_LONG).show();}
  finally{source.delete();}
 }
 @Override public void onBackPressed() {
  web.evaluateJavascript("window.ipitBack && window.ipitBack()", value -> {
   if (!"true".equals(value)) MainActivity.super.onBackPressed();
  });
 }
 @Override protected void onDestroy() { destroyed=true;teacherApi.disconnect();web.removeJavascriptInterface("IPITAndroid"); web.destroy(); super.onDestroy(); }
}
