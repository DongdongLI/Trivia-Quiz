/*
 *Homework 3
 *ResultsActivity.java
 *Marcos Brenes, Dongdong Li 
 */

package com.example.homework3;

import com.example.homework3.trivia.TriviaActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ResultsActivity extends Activity {
	Button quit;
	Button tryAgain;
	ProgressBar rate;
	TextView rate_text;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		quit = (Button) findViewById(R.id.results_activity_button_quit);
		tryAgain = (Button) findViewById(R.id.results_activity_button_tryagain);
		rate = (ProgressBar) findViewById(R.id.results_activity_progressbar);
		rate_text = (TextView) findViewById(R.id.results_activity_textview_rate);
		rate.setMax(100);
		
		// //////////////////////////////
		int correct = getIntent().getExtras().getInt(TriviaActivity.CORRECT_KEY);
		int all = getIntent().getExtras().getInt(TriviaActivity.TOTAL_KEY);;
		
		rate.setProgress((int) (correct * 1.0 / (all * 1.0) * 100));
		rate_text.setText((int) (correct * 1.0 / (all * 1.0) * 100) + "%");
		// ////////////////////////////////
		
		if (correct == all)
			((TextView)findViewById(R.id.results_activity_textview_encourage)).setText("You got all the correct Answers!");
		
		quit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tryAgain.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ResultsActivity.this,
						TriviaActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
