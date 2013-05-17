package com.cordova2.gcm; //Edit this to match the name of your application

import java.util.Iterator;

import com.google.android.gcm.*;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import com.plugin.GCM.GCMPlugin;


public class GCMIntentService extends GCMBaseIntentService {

  public static final String ME="GCMReceiver";
  static String re_message=null;

  public GCMIntentService() {
    super("GCMIntentService");
  }
  private static final String TAG = "GCMIntentService";
  
  /*
  private static void generateNotification(Context context, String message) {
	  int icon = R.drawable.ic_action_search;
	  long when = System.currentTimeMillis();
	
	  NotificationManager notificationManager = (NotificationManager) context
	    .getSystemService(Context.NOTIFICATION_SERVICE);
	  //Notification notification = new Notification(icon, message, when);
	  Notification notification = new Notification.Builder(context)
	  								.setSmallIcon(icon)
	  								.setTicker(message)
	  								.setWhen(when)
	  								.build();

	  String title = context.getString(R.string.app_name);
	  Intent notificationIntent = new Intent(context, MainActivity.class);
	  re_message=message;

	  notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	  PendingIntent intent = PendingIntent.getActivity(context, 0,notificationIntent, 0);

	  notification.setLatestEventInfo(context, title, message, intent);
	  notification.flags |= Notification.FLAG_AUTO_CANCEL;
	  notificationManager.notify(0, notification);
  }
  */
  

  @Override
  public void onRegistered(Context context, String regId) {

    Log.v(ME + ":onRegistered", "Registration ID arrived!");
    Log.v(ME + ":onRegistered", regId);

    JSONObject json;

    try
    {
      json = new JSONObject().put("event", "registered");
      json.put("regid", regId);

      Log.v(ME + ":onRegisterd", json.toString());

      // Send this JSON data to the JavaScript application above EVENT should be set to the msg type
      // In this case this is the registration ID
      GCMPlugin.sendJavascript( json );

    }
    catch( JSONException e)
    {
      // No message to the user is sent, JSON failed
      Log.e(ME + ":onRegisterd", "JSON exception");
    }
  }

  @Override
  public void onUnregistered(Context context, String regId) {
    Log.d(TAG, "onUnregistered - regId: " + regId);
  }

  @Override
  protected void onMessage(Context context, Intent intent) {
    Log.d(TAG, "onMessage - context: " + context);
    
    Vibrator vi = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    vi.vibrate(500);

    // Extract the payload from the message
    Bundle extras = intent.getExtras();
    if (extras != null) {
      try
      {
        Log.v(ME + ":onMessage extras ", "message : "+extras.getString("message"));
        
        Iterator <String> iterator = extras.keySet().iterator();
        while( iterator.hasNext()){
        	String key = iterator.next();
        	String value = extras.get(key).toString();
        	if(key.equals("msg")){
        		setNotification(context, key, value);
        	}
        	Log.d("PRINT", " KEY :: " + key + " :: VALUE :: " + value);
        }

        JSONObject json;
        json = new JSONObject().put("event", "message");


        // My application on my host server sends back to "EXTRAS" variables message and msgcnt
        // Depending on how you build your server app you can specify what variables you want to send
        //
        json.put("message", extras.getString("message"));
        json.put("msgcnt", extras.getString("msgcnt"));

        Log.v(ME + ":onMessage ", json.toString());

        GCMPlugin.sendJavascript( json );
        // Send the MESSAGE to the Javascript application
      }
      catch( JSONException e)
      {
        Log.e(ME + ":onMessage", "JSON exception");
      }        	
    }


  }

  @Override
  public void onError(Context context, String errorId) {
    Log.e(TAG, "onError - errorId: " + errorId);
  }

	  private void setNotification(Context context, String title, String message) {
			NotificationManager notificationManager = null;
			Notification notification = null;
			try {
				notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notification = new Notification(R.drawable.ic_launcher,
						message, System.currentTimeMillis());
				Intent intent = new Intent(context, MainActivity.class);
				PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
				// replace the title
				title = "rematch";
				
				notification.setLatestEventInfo(context, title, message, pi);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(0, notification);
			} catch (Exception e) {
				Log.e(ME, "[setNotification] Exception : " + e.getMessage());
			}
		}


}
