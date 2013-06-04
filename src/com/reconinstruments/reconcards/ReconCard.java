package com.reconinstruments.reconcards;

import java.io.File;
import java.io.Serializable;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ReconCard implements Serializable {

	/**
	 * required for serialization
	 */
	private static final long serialVersionUID = -765887146556379608L;
	private int nextCard, nextCardDelay, nextCardAnimationDuration;
	private String nextCardAnimation;
	private int id, leftCard, rightCard, upCard, downCard, selectCard, selectHoldCard, backCard;

	private String imagePath;
	private Resources image;
	
	public ReconCard(String image, int id, int left, int right, int up, int down, int select, int selectHold, int back, int nextCard, int nextCardDelay, String nextCardAnimation, int nextCardAnimationDuration) {
		this.leftCard = left;
		this.rightCard = right;

		this.upCard = up;
		this.downCard = down;
		this.selectCard = select;
		this.selectHoldCard = selectHold;
		this.backCard = back;
		this.imagePath = image;
		this.id = id;
		this.nextCard = nextCard;
		this.nextCardDelay = nextCardDelay;
		this.nextCardAnimation = nextCardAnimation;
		this.nextCardAnimationDuration = nextCardAnimationDuration;
	}
	
	public String imagePath() {
		return this.imagePath;
	}
	
	public Drawable getDrawable() throws Exception {
		File file = new File(imagePath);
		
		if ( !file.exists() ) {
			throw new Exception("file not found at " + imagePath);
		}
		
		return new BitmapDrawable(image, imagePath);
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getLeftIndex() {
		return leftCard;
	}
	
	public int getRightIndex() {
		return rightCard;
	}
	
	public int getUpIndex() {
		return upCard;
	}
	
	public int getDownIndex() {
		return downCard;
	}

	public int getBackIndex() {
		return backCard;
	}
	
	public int getSelectIndex() {
		return selectCard;
	}
	
	public int getSelectHoldIndex() {
		return selectHoldCard;
	}
	
	public int getNextCard() {
		return this.nextCard;
	}
	
	public int getNextCardDelay() {
		return this.nextCardDelay;
	}

	public String getNextCardAnimation() {
		return this.nextCardAnimation;
	}
	
	public int getNextCardAnimationDuration() {
		return this.nextCardAnimationDuration;
	}
	
}