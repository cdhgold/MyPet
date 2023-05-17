package com.kmetabus.mypet;

import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
// 동물병원 신규등록 웹뷰
public class HosInActivity  extends AppCompatActivity {
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_main);

        myWebView = (WebView) findViewById(R.id.webview);
        // WebView에 WebChromeClient를 설정합니다.
        myWebView.setWebChromeClient(new WebChromeClient() {
            // JavaScript의 alert() 함수를 처리하는 메소드를 재정의(override)합니다.
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                // 여기에서 alert 메시지를 처리할 수 있습니다.
                // 예를 들어, AlertDialog를 표시할 수 있습니다:
                new AlertDialog.Builder(view.getContext())
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm())
                        .setCancelable(false)
                        .create()
                        .show();

                // 자체 처리했으므로 true를 반환합니다.
                return true;
            }
        });

        myWebView.loadUrl("http://kmetabus.com/pet/xml");
        myWebView.getSettings().setJavaScriptEnabled(true);
    }
}
