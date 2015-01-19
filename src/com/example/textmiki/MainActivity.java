package com.example.textmiki;

import java.util.List;
import java.util.Locale;

import com.example.textmiki.R; 
//import com.example.glass_test.util.SystemUiHider;
//import com.google.android.glass.app.Card;
//import com.google.android.glass.app.Card.ImageLayout;

import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.glass.touchpad.GestureDetector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle; 

public class MainActivity extends Activity { 
	
	TextToSpeech tts;
	private GestureDetector mGestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		  
		
		//Keep screen on without dim
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        displaySpeechRecognizer();
         
        //Original layout
      	setContentView(R.layout.activity_main); 
        
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			
    		@Override
    		public void onInit(int status) {
    				//tts.speak("Main menu", TextToSpeech.QUEUE_FLUSH, null);
    			} 	
    		});
        
	}
        
	private static final int SPEECH_REQUEST = 0;

	private void displaySpeechRecognizer() {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    startActivityForResult(intent, SPEECH_REQUEST);
	} 

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	        Intent data) {
	    if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
	        List<String> results = data.getStringArrayListExtra(
	                RecognizerIntent.EXTRA_RESULTS);
	        String spokenText = results.get(0);
	        
	        tts.speak("You said " + spokenText, TextToSpeech.QUEUE_FLUSH, null);
	        // Do something with spokenText.
	        TextView tv = (TextView) findViewById(R.id.textView1);
            tv.setText(spokenText);
            
            //Send text
            
    	    new HttpAsyncTask().execute("http://02d791c.netsolhost.com//glassAPI/textAPI/miki.php?message=" + spokenText.replaceAll("\\s+","%20")); 
	           
	    }
	    super.onActivityResult(requestCode, resultCode, data); 
	  
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		//Main Click
		if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
			//User tapped touchpad, do something
			
            return true;
		}
		//Camera click
		if (keycode == KeyEvent.KEYCODE_CAMERA) {

			return true;
		}
		//Swipe down
		if (keycode == KeyEvent.KEYCODE_BACK) {
			//User swiped down, do something
 
			finish();
			return true;
		}
		
		return false;
	}
	
	public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try { 
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
 
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
 
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;   
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
           // etResponse.setText(result); 
       }
    }


}
