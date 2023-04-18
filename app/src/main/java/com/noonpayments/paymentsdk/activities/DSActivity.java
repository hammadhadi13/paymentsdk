package com.noonpayments.paymentsdk.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.noonpayments.paymentsdk.R;

import java.util.Set;


public class DSActivity extends AppCompatActivity {

    private WebView webView;
    String url3DS;
    String responseOrderId = "";
    String responseMerchantId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ds);

        Bundle extras = getIntent().getExtras();
        url3DS = extras.getString("url3ds");

        initView();
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebViewClient());

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url3DS);
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            String url = uri.toString();
            if (url.contains("noonappsdkresponse") || url.contains("noonpayments.com/sdk/response")) {
                //get the data
                try {
                    Set<String> paramNames = uri.getQueryParameterNames();
                    for (String key : paramNames) {
                        if (key.equals("orderId"))
                            responseOrderId = uri.getQueryParameter(key);
                        else if (key.equals("merchantReference"))
                            responseMerchantId = uri.getQueryParameter(key);
                    }
                    doComplete();
                    return false;
                } catch (Exception ex) {
                    responseOrderId = "";
                    responseMerchantId = "";
                    doComplete();
                }
            }
            view.loadUrl(url);
            return true;
        }
    }

    private void doComplete() {
        //return the result
        Intent resultIntent = new Intent();
        resultIntent.putExtra("orderid", responseOrderId);
        resultIntent.putExtra("merchantid", responseMerchantId);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}