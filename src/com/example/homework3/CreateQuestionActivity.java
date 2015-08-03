/*
 *Homework 3
 *CreateQuestionActivity.java
 *Marcos Brenes, Dongdong Li 
 */

package com.example.homework3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreateQuestionActivity extends Activity {

	EditText question;
	EditText options;
	EditText url;
	Button add;
	Button submit;
	ArrayList<String> allanswers;
	RadioGroup Alloptions;
	String rightFormatQ;
	int rightIndex=0;
	ProgressDialog progress;
	String gid="Glgq3k9ROlI7QH1ES4yU";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_question);
		
		allanswers=new ArrayList<String>();
		
		question=(EditText) findViewById(R.id.create_activity_edittext_question);
		options=(EditText)findViewById(R.id.create_activity_edittext_option);
		url=(EditText)findViewById(R.id.create_activity_editext_url);
		add=(Button)findViewById(R.id.create_activity_button_add);
		submit=(Button)findViewById(R.id.create_activity_button_submit);
		Alloptions=(RadioGroup)findViewById(R.id.radioGroup1);
		
		
		add.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				while(options.getText().toString().trim().length()!=0)
				{
					allanswers.add(options.getText().toString());
					display();
					options.setText(null);
				}
			}
		});
		
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(question.getText().toString().trim().length()==0||Alloptions.getChildCount()<2||Alloptions.getCheckedRadioButtonId()==-1)
				{
					Toast.makeText(getApplicationContext(), "Please Check everything", Toast.LENGTH_SHORT).show();
				}
				else
				{
					if(isConnectedOnline())
					{rightIndex=Alloptions.indexOfChild(findViewById(Alloptions.getCheckedRadioButtonId()));
					rightFormatQ=question.getText()+";";
					for(String i:allanswers)
					{
						rightFormatQ=rightFormatQ+i+";";
					}
					rightFormatQ=rightFormatQ+ url.getText().toString().trim()+";";
					rightFormatQ+=rightIndex;
				
				
					RequestParams params=new RequestParams("POST", "http://dev.theappsdr.com/apis/trivia/saveNew.php");
					params.addParams("gid", gid);
					params.addParams("q",rightFormatQ);
					progress=new ProgressDialog(CreateQuestionActivity.this);
					progress.setMessage("Creating...");
					progress.show();
					new PostNewQ().execute(params);
					}
					else
					{
						Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		
	}
	
	void display()
	{
		Alloptions.removeAllViews();
		for(String i:allanswers)
		{
			
			RadioButton toBeAdd=new RadioButton(getApplicationContext());
			toBeAdd.setText(i);
			toBeAdd.setTextColor(Color.BLACK);
			Alloptions.addView(toBeAdd);
		}
	}
	private boolean isConnectedOnline()
    {
    	ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo info=cm.getActiveNetworkInfo();
    	if(info!=null&&info.isConnected())//lots of things in info: wifi blah blah
    	{
    		return true;
    	}
    	return false;
    }
	class PostNewQ extends AsyncTask<RequestParams,Void,Void>
    {
    	BufferedReader br;
		@Override
		protected Void doInBackground(RequestParams... params) {
			HttpURLConnection con;
			try {
				con =  params[0].setupConnection();
				br=new BufferedReader(new InputStreamReader(con.getInputStream()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			progress.dismiss();
			finish();
			//super.onPostExecute(result);
		}
		
			
	}
}
