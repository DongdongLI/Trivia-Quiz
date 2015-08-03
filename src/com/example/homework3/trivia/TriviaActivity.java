/*
 *Homework 3
 *TriviaActivity.java
 *Marcos Brenes, Dongdong Li 
 */

package com.example.homework3.trivia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.example.homework3.R;
import com.example.homework3.ResultsActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class TriviaActivity extends Activity {
	
	ArrayList<Question> questionsList;
	ProgressDialog progressDialog;
	
	TextView questionNumberTextView, timerTextView, questionTextView;
	RadioGroup radioGroup;
	RelativeLayout pictureContainerLayout;
	Button quitButton, nextButton;
	ImageView questionImageView;
	CountDownTimer countDownTimer;
	GetImageAsyncTask pictureAsyncTask;
	
	int currentQuestionIndex = 0;
	int numberCorrect = 0;
	int totalNumberOfQuestions = 0;
	int rightAnswerID;
	
	public static final String CORRECT_KEY = "CORRECT";
	public static final String TOTAL_KEY = "TOTAL";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trivia);
		
		pictureContainerLayout = (RelativeLayout) findViewById(R.id.trivia_activity_imageview_container);
		
		questionNumberTextView	= (TextView) findViewById(R.id.trivia_activity_ques_num_textbox);
		timerTextView			= (TextView) findViewById(R.id.trivia_activity_timer_textbox);
		questionTextView		= (TextView) findViewById(R.id.trivia_activity_question_textview);
		radioGroup				= (RadioGroup) findViewById(R.id.trivia_activity_radio_group);
		quitButton				= (Button) findViewById(R.id.trivia_activity_button_quit);
		nextButton				= (Button) findViewById(R.id.trivia_activity_button_next);
		
		if (isConnectedOnline()) new GetQuestionsAsyncTask().execute();
		else Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
		
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nextQuestion();
			}
		});
		
		quitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		countDownTimer = new CountDownTimer(24000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				timerTextView.setText("Time Left: " + (millisUntilFinished / 1000) + " seconds");
				
			}
			@Override
			public void onFinish() {
				nextQuestion();	
			}
		};
	}
	
	private void nextQuestion() {
		countDownTimer.cancel();
		if (pictureAsyncTask != null)
			pictureAsyncTask.cancel(true);
		
		if (currentQuestionIndex < questionsList.size() - 1) {
			if (radioGroup.getCheckedRadioButtonId() == rightAnswerID) {
				numberCorrect++;
				//Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
			}
			currentQuestionIndex++;
			displayQuestion(currentQuestionIndex);
		} else {
			if (radioGroup.getCheckedRadioButtonId() == rightAnswerID)
				numberCorrect++;
			Intent intent = new Intent(this, ResultsActivity.class);
			intent.putExtra(CORRECT_KEY, numberCorrect);
			intent.putExtra(TOTAL_KEY, questionsList.size());
			startActivity(intent);
			finish();
		}
	}
	
	/**
	 * Display the corresponding question
	 * on the screen, takes care of downloading
	 * the picture as well
	 * @param indexNumber
	 */
	private void displayQuestion(int indexNumber) {
		
		radioGroup.removeAllViews();
		pictureContainerLayout.removeAllViews();
		
		Question currentQuestion = questionsList.get(indexNumber);
		
		questionNumberTextView.setText("Q" + (indexNumber + 1));
		questionTextView.setText(currentQuestion.getQuestion());
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		for (String q : currentQuestion.getPossibleAnswer()) {
			RadioButton radioButton = new RadioButton(this);
			radioButton.setLayoutParams(params);
			radioButton.setText(q);
			radioGroup.addView(radioButton);
		}
		
		rightAnswerID = radioGroup.getChildAt(currentQuestion.getRightIndex()).getId();
		
		if (currentQuestion.hasURL()) {
			// download picture
			pictureAsyncTask = new GetImageAsyncTask();
			pictureAsyncTask.execute(currentQuestion.getUrl());
			
		} else  {
			questionImageView = new ImageView(getApplicationContext());
			pictureContainerLayout.addView(questionImageView);
			questionImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_image));
		}
		
		countDownTimer.start();
	}
	/**
	 * Check to see if the device has a network connection
	 * @return
	 */
	 private boolean isConnectedOnline() {
	    	ConnectivityManager connectivityManager = 
	    			(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    	
	    	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
	    	if (networkInfo != null && networkInfo.isConnected())
	    		return true;
	    	return false;
	 }
	 
	 /**
	  * Downlaod an image from a specified url
	  * assigns bitmap to the imageview on the display
	  */
	 private class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
		 
		 @Override
		 protected void onPreExecute() {
	 		super.onPreExecute();
	 		ProgressBar progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleLarge);
	 		TextView loadingTextView = new TextView(getApplicationContext());
	 		
	 		loadingTextView.setText("Loading");
	 		loadingTextView.setTextColor(Color.BLACK);
	 		
	 		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 		params.addRule(RelativeLayout.CENTER_IN_PARENT);
	 		params.addRule(RelativeLayout.ABOVE, loadingTextView.getId());
	 		
	 		progressBar.setLayoutParams(params);
	 		
	 		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	 		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	 		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	 		
	 		loadingTextView.setLayoutParams(params);
	 		
	 		pictureContainerLayout.addView(progressBar);
	 		pictureContainerLayout.addView(loadingTextView);
	 	}
    	
		@Override
		protected Bitmap doInBackground(String... params) {

			AndroidHttpClient client = AndroidHttpClient.newInstance("AndroidAgent");
			HttpGet request = new HttpGet(params[0]);
			
			try {
				HttpResponse response = client.execute(request);
				InputStream inputStream = response.getEntity().getContent();
				Bitmap image = BitmapFactory.decodeStream(inputStream);
				return image;
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				client.close();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				// Set the image to the view
				pictureContainerLayout.removeAllViews();
				questionImageView = new ImageView(getApplicationContext());
				pictureContainerLayout.addView(questionImageView);
				questionImageView.setImageBitmap(result);
			}
			else
				Log.d("demo", "null data");
						
		}
	 }
	/**
	 *  Downloads the questions from sever
	 *  Parses correctly formated questions
	 *  to Question Objects
	 */
	private class GetQuestionsAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
		
		ArrayList<String> questionsStringList;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			questionsList = new ArrayList<Question>();
			
			progressDialog = new ProgressDialog(TriviaActivity.this);
			progressDialog.setCancelable(false);
			progressDialog.setTitle("Loading Questions");
			progressDialog.setMessage("Loading ...");
			progressDialog.show();
			questionsStringList = new ArrayList<String>();

		}
		
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			BufferedReader reader = null;
			
			try {
				URL url = new URL("http://dev.theappsdr.com/apis/trivia/getAll.php");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET"); // or "POST"
				
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = ""; 
				
				while ((line = reader.readLine()) != null) {

					// check for 3 semicolons if found url
					// check for int at end of question
					 
					if (line.length() > 0) {
						String[] splits = line.split(";");
						char lastChar = line.charAt(line.length() - 1);
						
						if (splits.length > 3)
							if (Character.isDigit(lastChar))
								if (!splits[splits.length - 1].contains(" "))
									if (Integer.valueOf(splits[splits.length - 1]).intValue() > -1) 
										questionsStringList.add(line);
					}
				}
				
				return questionsStringList;
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
    	
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			if (result != null) {
				for (String r: result) {
					Question q = new Question(r);
					if (q.getRightIndex() < q.getPossibleAnswer().size())
						if (q.getPossibleAnswer().size() > 1) {
							questionsList.add(q);
							Log.i("demo", q.toString());
						}
				}
				progressDialog.dismiss();
				displayQuestion(currentQuestionIndex);
			}
			else
				Log.wtf("demo", "null data");	
		}
    }
}