package edu.aau.utzon;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class PoiContentActivity extends Activity{
	WebView webview;
	
	@Override
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		String html = "<html><body>" +
				"<img src=\"file:///android_asset/androidmarker.png\" align=\"left\">" +
				"The path of the righteous man is beset on all sides by the iniquities of the selfish and the tyranny of evil men. Blessed is he who, in the name of charity and good will, shepherds the weak through the valley of darkness, for he is truly his brother's keeper and the finder of lost children. And I will strike down upon thee with great vengeance and furious anger those who would attempt to poison and destroy My brothers. And you will know My name is the Lord when I lay My vengeance upon thee." +
				"</body></html>";
		String mime = "text/html";
		String encoding = "utf-8";
		
		setContentView(R.layout.poi_content);
		
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadDataWithBaseURL(null, html, mime, encoding, null);
		
		//myWebView.loadUrl("http://www.example.com");
	}
}
