package jalaleddine.abdelbasset.coronatracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import mehdi.sakout.fancybuttons.FancyButton;

public class EmergencyActivity extends AppCompatActivity {
    FancyButton redcrossbutton;
    FancyButton mophbutton;

    FancyButton OpenRedCross;
    FancyButton OpenMOPH;
    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        redcrossbutton = findViewById(R.id.CallRedCrossbutton);
        redcrossbutton.setIconResource(R.drawable.red_cross);
        redcrossbutton.setIconPosition(FancyButton.POSITION_BOTTOM);

        OpenRedCross = findViewById(R.id.OpenRedCrossbutton);
        OpenRedCross.setIconResource(R.drawable.website);
        OpenRedCross.setIconPosition(FancyButton.POSITION_BOTTOM);

        mophbutton = findViewById(R.id.ContactsListButton);
        mophbutton.setIconPosition(FancyButton.POSITION_BOTTOM);
        mophbutton.setIconResource(R.drawable.moph);

        OpenMOPH = findViewById(R.id.OpenMOPHButton);
        OpenMOPH.setIconResource(R.drawable.website);
        OpenMOPH.setIconPosition(FancyButton.POSITION_BOTTOM);
        mWebView = findViewById(R.id.webview);

    }

    @SuppressLint("MissingPermission")
    public void CallRedCross(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:140"));
        isPermissionGranted();
        startActivity(callIntent);
    }

    @SuppressLint("MissingPermission")
    public void CallMOPH(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:01 830 300"));
       isPermissionGranted();
        startActivity(callIntent);

    }
    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void OpenRedCross(View view) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.loadUrl("https://www.redcross.org.lb/");
        mWebView.setVisibility(View.VISIBLE);

    }

    public void OpenMOPH(View view) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.loadUrl("https://www.moph.gov.lb/en");
        mWebView.setVisibility(View.VISIBLE);

    }
}
