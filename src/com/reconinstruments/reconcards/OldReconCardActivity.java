package com.reconinstruments.reconcards;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParserException;

import com.reconinstruments.motorcycle.R;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class OldReconCardActivity extends Activity {
	
	private HashMap<Integer, ReconCard> cards = new HashMap<Integer, ReconCard>();
	private ImageView imageOne, imageTwo;
	private int currentCard, cardAnimationDuration;
	private String cardDirection;
	private Handler cardHandler;
	private NextCardRunnable mNextCardRunnable;
	private boolean imageToggle = false;
	
	boolean flagShortPress = false;
	boolean flagLongPress  = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
	@Override
    public void onResume() {
		super.onResume();
		
		cardHandler = new Handler();
			
		imageOne = (ImageView) findViewById(R.id.image_one);
		imageTwo = (ImageView) findViewById(R.id.image_two);
        
        prepareCardsFromXml();
        
        setImageIndex(1);	
	}
    
    public void setImageIndex(int x) {
    	// cancel pending nextCardRunnable
    	cardHandler.removeCallbacksAndMessages(null);
    	
    	currentCard = x;
    	ReconCard nextCard = cards.get(currentCard);
    	cardDirection = nextCard.getNextCardAnimation();
    	cardAnimationDuration = nextCard.getNextCardAnimationDuration();

    	// select which imageView will we use
    	if( imageToggle ) {
    		this.animateImage(imageOne, nextCard.getImageResourceId(), nextCard.getNextCardAnimation(), nextCard.getNextCardAnimationDuration());
    		Log.v("ReconCardActivity", "Card: " + Integer.toString(currentCard) + " care of imageOne");
    	}
    	else {
    		this.animateImage(imageTwo, nextCard.getImageResourceId(), nextCard.getNextCardAnimation(), nextCard.getNextCardAnimationDuration());
    		Log.v("ReconCardActivity", "Card: " + Integer.toString(currentCard) + " care of imageTwo");
    	}
    	imageToggle = !imageToggle;
    	
    	if(nextCard.getNextCard() > -1) {
			mNextCardRunnable = new NextCardRunnable(nextCard.getNextCard());
			cardHandler.postDelayed(mNextCardRunnable, nextCard.getNextCardDelay());
		}
    }
    
    public void animateImage(ImageView image, int imageResourceId, String cardDirection, int cardAnimationDuration) {
    	Display display = getWindowManager().getDefaultDisplay(); 
    	int width = display.getWidth();
    	int height = display.getHeight();
    	
    	int xStart = 0;
    	int yStart = 0;
    	
    	if ( cardDirection.equals("up") ) {
        	xStart = 0;
        	yStart = height;
        	Log.v("ReconCardActivity", "up");
    	}
    	else if ( cardDirection.equals("down") ) {
    		xStart = 0;
    		yStart = 0 - height;
    		Log.v("ReconCardActivity", "down");
    	}
    	else if ( cardDirection.equals("left") ) {
    		xStart = width;
    		yStart = 0;
    		Log.v("ReconCardActivity", "left");
    	}
		else if ( cardDirection.equals("right") ) {
			xStart = 0 - width;
			yStart = 0;
			Log.v("ReconCardActivity", "right");
		}
    	
    	Log.v( "ReconCardActivity", "xStart " + Integer.toString(xStart) + " yStart " + Integer.toString(yStart) );
    	Log.v( "ReconCardActivity", "Card Animation: " + cardDirection.toString() + "for " + Integer.toString(cardAnimationDuration) + " milliseconds");
    	
    	// populate the image
    	image.setImageResource(imageResourceId);
    	image.bringToFront();
    	
    	// define the transition
    	TranslateAnimation slide;
    	slide = new TranslateAnimation( Animation.RELATIVE_TO_PARENT, xStart, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, yStart, Animation.RELATIVE_TO_PARENT, 0 );
    	slide.setDuration(cardAnimationDuration);  
        image.startAnimation(slide);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (flagShortPress) {
                Log.d("Test", "Short");
                int nextCard = cards.get(currentCard).getSelectIndex();
            	Log.v("ReconCardActivity", "NextCard: " + Integer.toString(nextCard));
            	if (nextCard > -1) {
            		setImageIndex(nextCard);
            	}
            }
            
            flagShortPress = true;
            flagLongPress  = false;
            return true;
        }

        return true;
    }
    
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	int nextCard = cards.get(currentCard).getSelectHoldIndex();
        	Log.v("ReconCardActivity", "NextCard: " + Integer.toString(nextCard));
        	if (nextCard > -1) {
        		setImageIndex(nextCard);
        	}

            flagShortPress = false;
            flagLongPress  = true;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (cards.size() < 1)
    	{return false;}
    	
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
        	int nextCard = cards.get(currentCard).getRightIndex();
        	Log.v("ReconCardActivity", "NextCard: " + Integer.toString(nextCard));
        	if (nextCard > -1) {
        		setImageIndex(nextCard);
        	}
        }
        else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
        	int nextCard = cards.get(currentCard).getLeftIndex();
        	Log.v("ReconCardActivity", "NextCard: " + Integer.toString(nextCard));
        	if (nextCard > -1) {
        		setImageIndex(nextCard);
        	}
        }
        else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
        	int nextCard = cards.get(currentCard).getUpIndex();
        	Log.v("ReconCardActivity", "NextCard: " + Integer.toString(nextCard));
        	if (nextCard > -1) {
        		setImageIndex(nextCard);
        	}
        }
        else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
        	int nextCard = cards.get(currentCard).getDownIndex();
        	Log.v("ReconCardActivity", "NextCard: " + Integer.toString(nextCard));
        	if (nextCard > -1) {
        		setImageIndex(nextCard);
        	}
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
        	int nextCard = cards.get(currentCard).getBackIndex();
        	Log.v("ReconCardActivity", "NextCard: " + Integer.toString(nextCard));
        	if (nextCard > -1) {
        		setImageIndex(nextCard);
        	} else {
        		finish();
        	}
        }
        else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	int nextCard = cards.get(currentCard).getSelectIndex();
        	Log.v("ReconCardActivity", "NextCard: " + Integer.toString(nextCard));
        	if (nextCard > -1) {
        		setImageIndex(nextCard);
        	}
        }
        else {
        	return false;
        }
        return true;
    }
    
    public void prepareCardsFromXml() {
		XmlResourceParser todolistXml = getResources().getXml(R.xml.stack);

		int eventType = -1;
		while (eventType != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {

				String strNode = todolistXml.getName();
				if (strNode.equals("ReconCard")) {
					int id = todolistXml.getAttributeIntValue(null, "id", 0);
					int up = todolistXml.getAttributeIntValue(null, "up", -1);
					int down = todolistXml.getAttributeIntValue(null, "down", -1);
					int left = todolistXml.getAttributeIntValue(null, "left", -1);
					int right = todolistXml.getAttributeIntValue(null, "right", -1);
					int img = todolistXml.getAttributeResourceValue(null, "img", 0);
					int select = todolistXml.getAttributeIntValue(null, "select", -1);
					int selectHold = todolistXml.getAttributeIntValue(null, "select-hold", -1);
					int back = todolistXml.getAttributeIntValue(null, "back", -1);
					int next = todolistXml.getAttributeIntValue(null, "next", -1);
					int delay = todolistXml.getAttributeIntValue(null, "delay", 1000);
					String animation = todolistXml.getAttributeValue(null, "animation");
					int animationDuration = todolistXml.getAttributeIntValue(null, "animation-duration", 1000);
					
					ReconCard temp = new ReconCard(this, img, left, right, up, down, select, selectHold, back, next, delay, animation, animationDuration);
					if (id > 0) {
						cards.put(id, temp);
					}
				}
			}

			try {
				eventType = todolistXml.next();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
    
    class NextCardRunnable implements Runnable {

		private int nextCard;
		
		public NextCardRunnable(int nextCard) {
			this.nextCard = nextCard;
		}
		
		@Override
		public void run() {
			setImageIndex(nextCard);
		}
		
	}
    
}