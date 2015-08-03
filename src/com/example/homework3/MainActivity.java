/*
 *Homework 3
 *MainActivity.java
 *Marcos Brenes, Dongdong Li 
 */

package com.example.homework3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.example.homework3.trivia.Question;
import com.example.homework3.trivia.TriviaActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button start;
	Button create;
	Button delete_all;
	Button exit;
	String gid = "Glgq3k9ROlI7QH1ES4yU";
	ProgressDialog progress;
	Question q;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		start = (Button) findViewById(R.id.main_activity_button_start);
		create = (Button) findViewById(R.id.main_activity_button_create);
		delete_all = (Button) findViewById(R.id.main_activity_button_delete);
		exit = (Button) findViewById(R.id.main_activity_button_exit);

		start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,
						TriviaActivity.class));

			}
		});

		create.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						CreateQuestionActivity.class);
				startActivity(intent);
			}
		});

		delete_all.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delete_all();
			}
		});

		exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// this is written to test result activity;
		// {
		// Intent intent=new Intent(MainActivity.this,ResultsActivity.class);
		// startActivity(intent);
		// }
	}

	void delete_all() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete Questions")
				.setMessage(
						"Are you sure you want to delete all your questions?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (isConnectedOnline()) {

									progress = new ProgressDialog(
											MainActivity.this);
									progress.setTitle("Deleting Questions");
									progress.setMessage("Deleting...");
									progress.show();
									Log.d("demo1", "1");
									RequestParams params = new RequestParams(
											"POST",
											"http://dev.theappsdr.com/apis/trivia/deleteAll.php");
									params.addParams("gid", gid);
									new DeleteWithPara().execute(params);

									// /progress.dismiss(); in post

								} else {
									Toast.makeText(getApplicationContext(),
											"No Internet Connection",
											Toast.LENGTH_SHORT).show();
								}

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.d("demo1", "clicked calcel");
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();

	}

	private boolean isConnectedOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected())// lots of things in info: wifi
												// blah blah
		{
			return true;
		}
		return false;
	}

	class DeleteWithPara extends AsyncTask<RequestParams, Void, Void> {
		BufferedReader br;

		@Override
		protected Void doInBackground(RequestParams... params) {
			HttpURLConnection con;
			try {
				con = params[0].setupConnection();
				br = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progress.dismiss();
		}

	}

}
