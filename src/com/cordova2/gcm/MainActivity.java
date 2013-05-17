package com.cordova2.gcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.webkit.CookieManager;

import org.apache.cordova.*;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends DroidGap {
	
    final static String SENDER_ID = "19793081062"; // GCM Project_number
    final static String SERVER_API_KEY = "AIzaSyC7Vo9b1OK7IMGJrHcv1agtjoEc1OlnbKE"; // Server API KEY of API_ACCESS Page  
    static final Handler mHandler = new Handler();
    
    AsyncTask<String, Void, Void> regIDInsertTask;
    ProgressDialog loagindDialog;
    String regId ;
    String myResult ;

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	CookieManager.setAcceptFileSchemeCookies(true);
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
        
        // GCMRegistrar Class has Client registration information 
        // You can get client ID from here if you need
        GCMRegistrar.checkDevice(this); 
        GCMRegistrar.checkManifest(this); 
        
        regId = GCMRegistrar.getRegistrationId(this); 
 
        // if Client ID is null, that means it is not registered. Thus, Try to register Server
        if (regId.equals("") || regId==null) { 
            GCMRegistrar.register(MainActivity.this, SENDER_ID); 
        } else { 
            Log.v("Main", "Already registered (" + regId + ")"); 
        } 
        sendAPIkey();
    }
    
    private void sendAPIkey() {
		String  myNum="THIS IS MESSAGE FOR MATCHING";
		Log.v("Main", "In sendAPIKey, After Registering  (" + regId + ")");
		regIDInsertTask = new regIDInsertTask().execute(regId, myNum);
	}
    
    /** Inner Class **/
    private class regIDInsertTask extends AsyncTask<String, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loagindDialog = ProgressDialog.show(MainActivity.this, "Registering Key...",
					"Please wait..", true, false);
		}

		@Override
		protected Void doInBackground(String... params) {
				HttpPostData(params[0] , params[1]);
			return null;
		}
		
		protected void onPostExecute(Void result) {
			loagindDialog.dismiss();
		}
    }
    
    public void HttpPostData(String reg_id , String pnum) { 
        try { 
             URL url = new URL("http://websys1.stern.nyu.edu/websysS13/websysS1310/gcm_reg_insert.php");       // URL ¼³Á¤ 
             HttpURLConnection http = (HttpURLConnection) url.openConnection();   //connect
             //-------------------------- 
             //   set transfer mode - basic setting 
             //-------------------------- 
             http.setDefaultUseCaches(false);                                            
             http.setDoInput(true);                        
             http.setDoOutput(true);                     
             http.setRequestMethod("POST");         

             http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
             StringBuffer buffer = new StringBuffer(); 
             buffer.append("reg_id").append("=").append(reg_id).append("&");      //set value for PHP
             buffer.append("pnum").append("=").append(pnum);
            
             OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8"); 
             PrintWriter writer = new PrintWriter(outStream); 
             writer.write(buffer.toString()); 
             writer.flush(); 
             InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");  
             BufferedReader reader = new BufferedReader(tmp); 
             StringBuilder builder = new StringBuilder(); 
             String str; 
             while ((str = reader.readLine()) != null) {    
                  builder.append(str + "\n");                 
       } 
             
              myResult = builder.toString();              
            
        } catch (MalformedURLException e) { 
        	e.printStackTrace();
            System.err.println(e.getMessage());
        } catch (IOException e) { 
        	e.printStackTrace();
        	System.err.println(e.getMessage());
        } // try end
    } // HttpPostData end 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
