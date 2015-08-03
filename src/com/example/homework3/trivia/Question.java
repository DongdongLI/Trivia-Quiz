/*
 *Homework 3
 *Question.java
 *Marcos Brenes, Dongdong Li 
 */

package com.example.homework3.trivia;

import java.util.ArrayList;

public class Question {
	private String question;
	private ArrayList<String> possibleAnswer;
	private int numOfOptions;
	private int rightIndex;
	private String url="";
	
	public Question(String s) {
		String[] params=s.split(";");
		int len=params.length;
		question=params[0];
		rightIndex=Integer.valueOf(params[len-1]);
		possibleAnswer=new ArrayList<String>();
		url=params[len-2];
		numOfOptions=len-3;
		
		for(int i=1;i<numOfOptions+1;i++)
		{
			possibleAnswer.add(params[i]);
		}
	}
	
	public String getQuestion() {
		return question;
	}

	public ArrayList<String> getPossibleAnswer() {
		return possibleAnswer;
	}

	public int getRightIndex() {
		return rightIndex;
	}

	public String getUrl() {
		return url;
	}

	public boolean isURL(String s)
	{
		if(s.substring(0, 4).equals("http"))
			return true;
		return false;
	}
	
	public boolean hasURL() {
		if (url.length() > 0)
			if (url.contains("http"))
				if (!url.contains(" "))
					return true;
		return false;
	}

	@Override
	public String toString() {
		return "Question [question=" + question + ", possibleAnswer="
				+ possibleAnswer + ", numOfOptions=" + numOfOptions
				+ ", rightIndex=" + rightIndex + ", url=" + url + "]";
	}
	
}
