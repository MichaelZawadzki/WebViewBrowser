package com.sample.webviewbrowser;

import android.app.Activity;
import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class WebViewBrowserActivity extends Activity {
	private WebView     m_webView;
	private EditText    m_urlField;
	private Button      m_goButton;
	private ProgressBar m_progressBar;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        Log.d("DEBUG", "Starting");
        
        // Create reference to UI elements
        m_webView     = (WebView)     findViewById(R.id.webview_compontent);
        m_urlField    = (EditText)    findViewById(R.id.url);
        m_goButton    = (Button)      findViewById(R.id.go_button);
        m_progressBar = (ProgressBar) findViewById(R.id.progressbar);
                        
        m_progressBar.setVisibility(View.GONE);
                
        m_webView.getSettings().setJavaScriptEnabled(true);
        m_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                
        m_webView.setWebChromeClient(new WebChromeClient() {
          public void onProgressChanged(WebView view, int progress) {
        	  m_progressBar.setProgress(progress);
              if(progress == 100) {
            	  m_progressBar.setVisibility(View.GONE);
              }
          }                     
        });
        
        // workaround so that the default browser doesn't take over
        m_webView.setWebViewClient(new MyWebViewClient());
        //m_webView.setHttpAuthUsernamePassword(host, realm, "curri", "culum");
        
        
        // Setup click listener
        m_goButton.setOnClickListener( new OnClickListener() {
        	public void onClick(View view) {
        		openURL();
        	}
        });
                
        // Set cursor to end of text
        m_urlField.setSelection(m_urlField.getText().length());
        
        // Setup key listener
        m_urlField.setOnKeyListener( new OnKeyListener() {
        	public boolean onKey(View view, int keyCode, KeyEvent event) {
        		if(keyCode==KeyEvent.KEYCODE_ENTER) {
        			openURL();        			
        			return true;
        		} else {
        			return false;
        		}
        	}
        });
 
    }
    
    /** Hide the virtual keyboard when loading a URL **/
	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(m_urlField.getWindowToken(),0);	
	}
	
	private void startProgressBar()	{
		m_progressBar.setVisibility(View.VISIBLE);
    	m_progressBar.setProgress(0);
	}
		
    /** Opens the URL in a browser */
	private void openURL(){
		openURL(m_urlField.getText().toString());
	}
	
	/** Opens the URL in a browser */
    private void openURL(String url) {
    	m_webView.loadUrl(url);
     	m_webView.requestFocus();
    	
     	m_urlField.setText(url);
     	
    	startProgressBar();
    	hideKeyboard();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && m_webView.canGoBack()) {
            
            WebBackForwardList list = m_webView.copyBackForwardList();
            int size = list.getSize();
            int index = list.getCurrentIndex();
                                    
            if ((size > 0) && (index >= 0)) {            
            	WebHistoryItem page = list.getItemAtIndex(index-1);
                m_urlField.setText(page.getUrl());  
            }
       
            m_webView.goBack();
                       
            startProgressBar();
            hideKeyboard();
            
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            openURL(url);            
            return true;
        }
        
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }
        
        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            handler.proceed("curri", "culum");
        }
    }    
}