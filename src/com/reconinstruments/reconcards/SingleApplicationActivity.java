package com.reconinstruments.reconcards;
 
import com.example.androidlistviewactivity.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.reconinstruments.reconcards.ReconCardApplication;
 
public class SingleApplicationActivity extends Activity{
	public static final String TAG = "SingleListItemActivity";

	private ImageView imageOne, imageTwo, tempImage;
	private int currentCard;
	private Handler cardHandler;
	private NextCardRunnable mNextCardRunnable;
	private boolean imageToggle = false;
	private ReconCardApplication application;
	
	boolean flagShortPress = false;
	boolean flagLongPress  = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_list_item_view);
	}
    
    @Override
	public void onResume() {		
		cardHandler = new Handler();
		
		imageOne = (ImageView) findViewById(R.id.image_one);
		imageTwo = (ImageView) findViewById(R.id.image_two);

        Bundle extras = getIntent().getExtras();
        Intent intentIncoming = getIntent();
        if(extras != null) {
            application = (ReconCardApplication) intentIncoming.getParcelableExtra("app");

			setCard(1);
		}
		super.onResume();
    }
    
    public void onPause() {
		super.onPause();
		cardHandler.removeCallbacks(mNextCardRunnable);
	}
    
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (application.getCards().size() < 1) return false;
		
		Log.d(TAG,  "IN LONG PRESS");
//		
//		int nextCard = -1;
//		
//		try {
//			ReconCard thisCard = application.getCard(currentCard);
//	
//			switch(keyCode) {
//			case KeyEvent.KEYCODE_DPAD_CENTER:
//				nextCard = thisCard.getSelectIndex();
//				break;
//			}
//	
//			if (nextCard > -1) {
//				Log.d(TAG, "next card: " + nextCard);
//				setCard(nextCard);
//			} else if (nextCard == -1){
//				finish();
//			}
//		}
//		catch ( Exception e ) {
//			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//		}
//		
		return true;
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (application.getCards().size() < 1) return false;
		event.startTracking();
		int nextCard = -1;
		try {
			ReconCard thisCard = application.getCard(currentCard);
			
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
		}
		catch ( Exception e ) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		return true;
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
			Log.v(TAG, "fade-in");
		}
		else if ( cardDirection.equals("fade-out") ) {
			xStart = 0 - width;
			yStart = 0;
			Log.v(TAG, "fade-out");
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
	    		Toast.makeText(getApplicationContext(), "card animation duration must be > 0", Toast.LENGTH_LONG).show();
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
    
	public void setCard(int currentCard) {
		this.currentCard = currentCard;
		cardHandler.removeCallbacks(mNextCardRunnable);
		try { 
			ReconCard nextCard = application.getCard(currentCard);

	    	// select which imageView will we use
	    	tempImage = (imageToggle) ? imageOne : imageTwo;
	    	
	    	// invert imageToggle for next time around
	    	imageToggle = !imageToggle;
	    	
	    	try {
	    		Drawable nextBitmap = nextCard.getDrawable();
	    
		    	// show image
		    	this.showImage(tempImage, nextBitmap, nextCard.getNextCardAnimation(), nextCard.getNextCardAnimationDuration());
				
		    	// if next="" an int, show it now!
				if(nextCard.getNextCard() > -1) {
					mNextCardRunnable = new NextCardRunnable(nextCard.getNextCard());
					cardHandler.postDelayed(mNextCardRunnable, nextCard.getNextCardDelay());
				}
	    	}
	    	catch ( Exception e ) {
	    		Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
	    	}
		}
		catch ( Exception e ) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	class NextCardRunnable implements Runnable {

		private int nextCard;
		
		public NextCardRunnable(int nextCard) {
			this.nextCard = nextCard;
		}
		
		@Override
		public void run() {
			Log.v(TAG, "RUNNING " +nextCard);
			setCard(nextCard);
		}
	}
	
}