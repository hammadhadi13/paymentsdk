package com.noonpayments.paymentsdk.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.noonpayments.paymentsdk.databinding.ActivityDsBinding;

import java.util.Set;


public class DSActivity extends AppCompatActivity {

    //    private WebView webView;
    String url3DS;
    String responseOrderId = "";
    String responseMerchantId = "";
    private ActivityDsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        url3DS = extras.getString("url3ds");

        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        binding.webview.setWebViewClient(new MyWebViewClient());
        binding.webview.getSettings().setLoadsImagesAutomatically(true);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webview.loadUrl(url3DS);
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