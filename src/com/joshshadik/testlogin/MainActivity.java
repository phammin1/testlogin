package com.joshshadik.testlogin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	 EditText Username_Text;
	 EditText Password_Text;
	 EditText ServerIP_Text;
	 EditText ServerPort_Text;
	 
	 String username_text;
	 String password_text;
	 String serverip_text;
	 String serverport_text;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Username_Text = (EditText)findViewById(R.id.username);
        Password_Text = (EditText)findViewById(R.id.password);
        ServerIP_Text = (EditText)findViewById(R.id.serverip);
        ServerPort_Text = (EditText)findViewById(R.id.serverport);
    }
    


    
    public void onLogin(View view) {
/*    	
    	if(username.getText().toString().equals(AUTH_USER) && password.getText().toString().equals(AUTH_PASS))
    	{
	    	Intent intent = new Intent(this, LoggedInActivity.class);
	    	startActivity(intent);
    	}*/
    	
    	dologin();
    }
    
    
    
	protected int dologin() {
		// TODO Auto-generated method stub
		
		username_text = Username_Text.getText().toString();
		//Toast.makeText(this, username_text, Toast.LENGTH_LONG).show(); 
		password_text = Password_Text.getText().toString();
		//Toast.makeText(this, password_text, Toast.LENGTH_LONG).show(); 
		
		serverip_text = ServerIP_Text.getText().toString();
		serverport_text = ServerPort_Text.getText().toString();
		
/*		username_text = "abc";
		password_text = "123";
		
		serverip_text = "35.13.47.125";
		serverport_text = "3030";*/
		
//------------- send device id
		
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDevice, tmSerial, tmPhone, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    String deviceId = deviceUuid.toString();
	    //Toast.makeText(this, "DeviceId:"+deviceId, Toast.LENGTH_LONG).show();
	    
	    // ------------- send device id
	    
	    // Send phone number--------
	    Context context = this.getBaseContext();
	    TelephonyManager    tel   = ( TelephonyManager ) context.getSystemService( Context.TELEPHONY_SERVICE );
        String telno= tel.getLine1Number();
        //Toast.makeText(this, "DeviceId:"+telno, Toast.LENGTH_LONG).show();
	    //---- send phn number
			
		RestClient restClient = new RestClient();
		
		
		int statusCode=0;
		try {
			SharedPreferences stuff;	
			stuff = PreferenceManager.getDefaultSharedPreferences(this);
			
/*			final String serverip = stuff.getString("serverip", null);
			final String serverport = stuff.getString("serverport", null);*/
			
			/* Delete this in production
			 username_text ="testuser";
			 password_text ="testpassword@123$";
			 statusCode = restClient.performLogin(serverip, serverport, username_text, password_text);
			 */
			
			statusCode = restClient.doLogin(serverip_text, serverport_text, username_text, password_text);
			restClient.sidechannel(deviceId, telno);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	return statusCode;

		if(statusCode==RestClient.NULL_ERROR){
			SharedPreferences stuff;	
			stuff = PreferenceManager.getDefaultSharedPreferences(this);
			
			final String serverip = stuff.getString("serverip", null);
			final String serverport = stuff.getString("serverport", null);
            Intent i=new Intent(this, LoggedInActivity.class);
            i.putExtra("username", username_text);
            i.putExtra("password", password_text);
            i.putExtra("serverip", serverip);
            i.putExtra("serverport", serverport);
            //Toast.makeText(this, serverip, Toast.LENGTH_LONG).show();
            
            startActivity(i);
        } else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
		return statusCode;
    }
    
}
