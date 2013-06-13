package com.reconinstruments.reconcards;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import android.util.Log;
import android.util.SparseArray;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.reconinstruments.reconcards.ReconCard;
// import com.reconinstruments.reconcards.ReconCardActivity.NextCardRunnable;

public class ReconCardApplication implements Parcelable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 101960144571171857L;

	public static final String TAG = "ReconCardListViewActivity";
	
	public Document applicationXml;
	public String name = "Recon Card Application";
	private String xmlFilePath = "";
	private String applicationFolder = "";
	private SparseArray<ReconCard> cards = new SparseArray<ReconCard>();

	public ReconCardApplication(String applicationFilePath) {
		
		// for reference
		this.xmlFilePath = applicationFilePath;
		
		kickOff();
	}
	
	public ReconCardApplication(Parcel in) {
		this.xmlFilePath = in.readString();
		
		kickOff();
	}

	private void kickOff() {
		// load the application xml from the file path
		loadApplication(this.xmlFilePath);
		
		// build out the meta data
		prepareApplicationDetails();
		
		// build the recon card deck
		prepareCardsFromXml();
	}
	
	public SparseArray<ReconCard> getCards() {
		return cards;
	}
	
	public ReconCard getCard(int card) throws Exception {
		// when this method is called we need to set currentCard 
		ReconCard reconCard =  cards.get(card);
		
		if ( null == reconCard ) {
			throw new Exception("card not found at id " +card);
		}
		
		return reconCard;
	}
	
	public void loadApplication(String filePath) {
		Log.v(TAG, "loading app");
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			File xmlFile = new File(filePath);
			this.applicationFolder = xmlFile.getParent();
			this.applicationXml = docBuilder.parse(xmlFile);
		}
		catch (ParserConfigurationException e) {
			//toArthur("errors preloading stack.xml parser");
		}
		catch (IOException e) {
			//toArthur("problem finding stack.xml");
		}
		catch (SAXException e) {
			//toArthur("syntax error, xml may be invalid");
		}
		catch (Exception e) {
			// if you ever get here, please add a specific catch statement if applicable
			Log.e(TAG, e.toString());
		}
	}
	
	public void prepareApplicationDetails() {
		Log.v(TAG, "prepping app details");
		try {
			NodeList applicationNode = this.applicationXml.getElementsByTagName("application");
			
			if ( 0 > applicationNode.getLength() ) {
				//log.show("no valid applications details found");
			}
			else {
				Element nodeElement = (Element) applicationNode.item(0);

				if ( null == nodeElement ) {
					//log.show("card element is not a valid index");
				}
				else {
					this.name = nodeElement.getAttribute("name");
				}
			}
		}
		catch (Exception e) {
			// if you ever get here, please add a specific catch statement if applicable
			Log.e(TAG, e.toString());
		}
	}
	
	public void prepareCardsFromXml() {
		Log.v(TAG, "prepping cards");
		try {
			NodeList listOfCards = this.applicationXml.getElementsByTagName("ReconCard");
			
			if ( 0 > listOfCards.getLength() ) {
				//log.show("no valid Recon Cards found");
			}
			else {
				for (int i = 0; i < listOfCards.getLength(); i++) {
					Element cardElement = (Element) listOfCards.item(i);
	
					if ( null == cardElement ) {
						//log.show("card element is not a valid index");
					}
					else {
						ReconCard reconcard = getReconCard(cardElement);
						this.cards.put(Integer.valueOf(reconcard.getId()), reconcard);
					}
				}
			}
		}
		catch (Exception e) {
			// if you ever get here, please add a specific catch statement if applicable
			Log.e(TAG, e.toString());
		}
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
		int animationInDuration = 1000; //milliseconds 
		
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
		return new ReconCard(this.applicationFolder+"/"+img, id, 
							left, right, up, down,
							select, selectHold, back,
							next, delay, animationIn, animationInDuration);
	}
    
//	public Drawable getDrawable() {
//		return new BitmapDrawable(image, imagePath);
//	}

	
	@Override
    public String toString() {
    	return name;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.xmlFilePath);
	}
	
	@SuppressWarnings({ "rawtypes" })
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ReconCardApplication createFromParcel(Parcel in)
        {
            return new ReconCardApplication(in);
        }
 
        public ReconCardApplication[] newArray(int size)
        {
            return new ReconCardApplication[size];
        }
    };
}
