package com.reconinstruments.reconcards;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.reconinstruments.reconcards.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class ReconCardActivity extends Activity {

	public static final String TAG = "ReconCardActivity";

	private SparseArray<ReconCard> cards = new SparseArray<ReconCard>();
	private ImageView imageOne, imageTwo;
	private int currentCard;
	private Handler cardHandler;
	private NextCardRunnable mNextCardRunnable;
	private boolean imageToggle = false;
	
	boolean flagShortPress = false;
	boolean flagLongPress  = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void onResume() {		
		cardHandler = new Handler();
		
		imageOne = (ImageView) findViewById(R.id.image_one);
		imageTwo = (ImageView) findViewById(R.id.image_two);

		File root = new File("/mnt/storage/card");
		if ( !root.exists() ) {
			toArthur("card folder does not exist");
		}
		else {
			File[] files = root.listFiles();
			File xmlFile = null;

			for (int i = files.length; i <= 0; i--) {
				if (files[i].getName().equals("stack.xml"))
					xmlFile = files[i];
			}

			prepareCardsFromXml(xmlFile);

			setCard(1);
			super.onResume();
		}
	}
	
	public void onPause() {
		super.onPause();
		cardHandler.removeCallbacks(mNextCardRunnable);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (cards.size() < 1) return false;
		
		int nextCard = -1;
		ReconCard thisCard = cards.get(currentCard);
		
		switch(keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			nextCard = thisCard.getRightIndex();
			break;
			
		case KeyEvent.KEYCODE_DPAD_LEFT:
			nextCard = thisCard.getLeftIndex();
			break;
			
		case KeyEvent.KEYCODE_DPAD_UP:
			nextCard = thisCard.getUpIndex();
			break;
			
		case KeyEvent.KEYCODE_DPAD_DOWN:
			nextCard = thisCard.getDownIndex();
			break;
			
		case KeyEvent.KEYCODE_BACK:
			nextCard = thisCard.getBackIndex();
			break;
			
		case KeyEvent.KEYCODE_DPAD_CENTER:
			nextCard = thisCard.getSelectIndex();
			break;
		}
		
		if (nextCard > -1) {
			Log.d(TAG, "next card: " + nextCard);
			setCard(nextCard);
		} else if (nextCard == -1){
			finish();
		}
		
		return true;
	}
	
	public void setCard(int x) {
		cardHandler.removeCallbacks(mNextCardRunnable);
		currentCard = x;
		ReconCard nextCard = cards.get(currentCard);
		if ( null == nextCard ) {
			toArthur("next card not found for card id " +x);
		}
		else {
	    	// select which imageView will we use
	    	if( imageToggle ) {
	    		this.showImage(imageOne, nextCard.getDrawable(), nextCard.getNextCardAnimation(), nextCard.getNextCardAnimationDuration());
	    		Log.v(TAG, "card: " + Integer.toString(currentCard) + " care of imageOne");
	    	}
	    	else {
	    		this.showImage(imageTwo, nextCard.getDrawable(), nextCard.getNextCardAnimation(), nextCard.getNextCardAnimationDuration());
	    		Log.v(TAG, "card: " + Integer.toString(currentCard) + " care of imageTwo");
	    	}
	    	imageToggle = !imageToggle;
			
			if(nextCard.getNextCard() > -1) {
				mNextCardRunnable = new NextCardRunnable(nextCard.getNextCard());
				cardHandler.postDelayed(mNextCardRunnable, nextCard.getNextCardDelay());
			}
		}
	}
	
	public void showImage(ImageView image, Drawable imageDrawable, String cardDirection, int cardAnimationDuration) {
    	Display display = getWindowManager().getDefaultDisplay(); 
    	int width = display.getWidth();
    	int height = display.getHeight();
    	
    	int xStart = 0;
    	int yStart = 0;
    	boolean animationRequested = true;
    	
    	if ( cardDirection.equals("up") ) {
        	xStart = 0;
        	yStart = height;
        	Log.v(TAG, "up");
    	}
    	else if ( cardDirection.equals("down") ) {
    		xStart = 0;
    		yStart = 0 - height;
    		Log.v(TAG, "down");
    	}
    	else if ( cardDirection.equals("left") ) {
    		xStart = width;
    		yStart = 0;
    		Log.v(TAG, "left");
    	}
		else if ( cardDirection.equals("right") ) {
			xStart = 0 - width;
			yStart = 0;
			Log.v(TAG, "right");
		}
		else if ( cardDirection.equals("fade-in") ) {
			xStart = 0 - width;
			yStart = 0;
			Log.v(TAG, "right");
		}
		else if ( cardDirection.equals("fade-out") ) {
			xStart = 0 - width;
			yStart = 0;
			Log.v(TAG, "right");
		}
		else {
			Log.v(TAG, "no animation requested");
			animationRequested = false;
		}

    	// populate the image
    	image.setImageDrawable(imageDrawable);
    	image.bringToFront();

    	// only animate if it's requested
    	if ( animationRequested ) {
    		Animation animation;
    		animation = prepareAnimation(cardDirection, xStart, yStart);
	    	
	    	// define the transition
	    	try {
		    	animation.setDuration(cardAnimationDuration);  
		        image.startAnimation(animation);
	    	}
	    	catch(IllegalArgumentException e) {
	    		toArthur("card animation duration must be > 0");
	    	}
    	}
    }
	
	public Animation prepareAnimation(String cardDirection, int xStart, int yStart) {
		if ( cardDirection.equals("fade-in") || cardDirection.equals("fade-out") ) {
    		ScaleAnimation animation;

    		if ( cardDirection.equals("fade-in") ) {
	    		animation = new ScaleAnimation(xStart, 0, yStart, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	    		//scale in
    		}
    		else {
	    		//scale out
	    		animation = new ScaleAnimation(0, xStart, 0, yStart, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    		}

    		return animation;
    	}
    	else {
    		// sliding!
	    	TranslateAnimation animation;
	    	animation = new TranslateAnimation( Animation.RELATIVE_TO_PARENT, xStart, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, yStart, Animation.RELATIVE_TO_PARENT, 0 );
	    	
			return animation;
    	}
	}
	
	class NextCardRunnable implements Runnable {

		private int nextCard;
		
		public NextCardRunnable(int nextCard) {
			this.nextCard = nextCard;
		}
		
		@Override
		public void run() {
			setCard(nextCard);
		}
		
	}

	public void prepareCardsFromXml(File dir) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("/mnt/storage/card/hahastack.xml"));
			
			//Log.v(TAG, doc.getTextContent());

			NodeList listOfCards = doc.getElementsByTagName("ReconCard");
			
			if ( 0 > listOfCards.getLength() ) {
				toArthur("no valid Recon Cards found");
			}
			else {
				for (int i = 0; i < listOfCards.getLength(); i++) {
					Element cardElement = (Element) listOfCards.item(i);
	
					if ( null == cardElement ) {
						toArthur("card element is not a valid index");
					}
					else {
						ReconCard reconcard = getReconCard(cardElement);
						cards.put(Integer.valueOf(reconcard.getId()), reconcard);
					}
				}
			}
		}
		catch (ParserConfigurationException e) {
			toArthur("errors preloading stack.xml parser");
		}
		catch (IOException e) {
			toArthur("problem finding stack.xml");
		}
		catch (SAXException e) {
			toArthur("syntax error, xml may be invalid");
		}
		catch (Exception e) {
			// if you ever get here, please add a specific catch statement if applicable
			Log.e(TAG, e.toString());
		}
	}
	
	/*
	 * It's a new age, Irish toast ;)
	 * Make a toast, and advertise it [in logcat]
	 */
	private void toArthur(String text) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;

		Log.v(TAG, "Toast: " + text);
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	private ReconCard getReconCard(Element e) {
		String upStr = e.getAttribute("up");
		String downStr = e.getAttribute("down");
		String leftStr = e.getAttribute("left");
		String rightStr = e.getAttribute("right");
		String selectStr = e.getAttribute("select");
		String selectHoldStr = e.getAttribute("select-hold");
		String backStr = e.getAttribute("back");
		String idStr = e.getAttribute("id");
		String nextStr = e.getAttribute("next");
		String delayStr = e.getAttribute("delay");
		String animationInDurationStr = e.getAttribute("animation-in-duration");
		
		int up = Integer.MIN_VALUE;
		int down = Integer.MIN_VALUE;
		int left = Integer.MIN_VALUE;
		int right = Integer.MIN_VALUE;
		int select = Integer.MIN_VALUE;
		int selectHold = Integer.MIN_VALUE;
		int back = Integer.MIN_VALUE;
		int id = Integer.MIN_VALUE;
		int next = Integer.MIN_VALUE;
		int delay = 0;
		int animationInDuration = 0;
		
		if(upStr.length() > 0) up = Integer.parseInt(upStr);
		if(downStr.length() > 0) down = Integer.parseInt(downStr);
		if(leftStr.length() > 0) left = Integer.parseInt(leftStr);
		if(rightStr.length() > 0) right = Integer.parseInt(rightStr);
		if(selectStr.length() > 0) select = Integer.parseInt(selectStr);
		if(selectHoldStr.length() > 0) selectHold = Integer.parseInt(selectHoldStr);
		if(backStr.length() > 0) back = Integer.parseInt(backStr);
		if(idStr.length() > 0) id = Integer.parseInt(idStr);
		if(nextStr.length() > 0) next = Integer.parseInt(nextStr);
		if(delayStr.length() > 0) delay = Integer.parseInt(delayStr);
		if(animationInDurationStr.length() > 0) animationInDuration = Integer.parseInt(animationInDurationStr);
		
		String img = e.getAttribute("img");
		String animationIn = e.getAttribute("animation-in");
		//String animationOut = e.getAttribute("animation-out");
		
		Log.v(TAG, "looking for image: " + img.toString());

		//return new ReconCard(this, "/mnt/storage/card/"+img, id, left, right, up, down, select, back, next, delay);
		return new ReconCard(this, "/mnt/storage/card/"+img, id, left, right, up, down, select, selectHold, back, next, delay, animationIn, animationInDuration);
	}
}
