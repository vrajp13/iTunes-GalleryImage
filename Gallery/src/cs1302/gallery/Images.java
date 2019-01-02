package cs1302.gallery;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.io.InputStreamReader;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;


/**
 * This is the images class that will allow the Gallery App class to access
 * its methods for finding images from the iTunes API and adding it onto a grid
 * pane so that it is visually and efficiently presented on the GUI Application.
 * This class has a method that uses JSon to gather all of the iTunes link based
 * on the search query and stores the images onto the gridPane and afterwards it
 * will use keyFrame and timeLine inorder to randomly change an image every two 
 * seconds in the gridPane.
 * 
 * @author Vraj Pragnesh Patel
 *
 */
public class Images {

	private GridPane gridPane;
	private ProgressBar progressBar;
	private Float progress;
    private int increment;
    private int modifiedSize;
    private int origModifiedSize;
    private String[] modifiedUrls;
    private String[] origModifiedUrls;
    private Random rand;
    private boolean originalFunc;
    private Timeline timeline;
    private KeyFrame keyFrame;
    
    /**
     * This is the Images class constructor that declares all of the instance variables
     * for the class object so that it is easy for the methods to access them and provided
     * them to the GUI Application
     */
    public Images() {
    	rand = new Random();
    	gridPane = new GridPane();
    	gridPane.setMinSize(500, 400);
    	progressBar = new ProgressBar();
    	progress = 0.0F;
        increment = 20;
        modifiedSize = 0;
        origModifiedSize = 0;
        modifiedUrls = new String[0];
        origModifiedUrls = new String[0];
        originalFunc = true;
        timeline = new Timeline();
        keyFrame = null;
    }
	
    /**
     * This private constructingImages method is using keyFrame and timeline in order
     * to keep on randomizing images to place into the borderPane of the GUI Application.
     * The the timeline event handler is using a private method randomImages, which will
     * at random row and col change an image every two seconds as long as the timeline
     * is playing. 
     */
    protected void constructingImages() {
		try {
	    		EventHandler<ActionEvent> handler = event -> {
	    		//Generating rand row and col values for the image to replace with
				int randRow = rand.nextInt(4);
				int randCol = rand.nextInt(5);
				//Pulling the imageview from the url link provided in the array
				ImageView textView = new ImageView(new Image(origModifiedUrls[increment]));
				System.out.println(LocalTime.now());
				System.out.println(origModifiedUrls[increment]);
				//Using the randomImages method to place the image view at a rand row and col
				randomImages(randCol, randRow, textView);
				increment++;
				//Once the array has reached the maximum size than it will start uploading images
				//from 21st image to the array length
				if(increment == origModifiedSize) {
					increment = 20;
				}
			};
				//Keyframe is using the eventHanlder and the duration to play the handler
				//every two seconds
				keyFrame = new KeyFrame(Duration.seconds(2), handler);
				timeline.setCycleCount(Timeline.INDEFINITE);
				timeline.getKeyFrames().add(keyFrame);
				timeline.play();
    	}catch(Exception e) {
    		System.err.println("Error");
    	}
	}
	
    /**
     * This protected method will either play or pause the timeLine depending on whether
     * it is true or false. If it is true and it will set the timeLine to play or it will
     * pause the timeLine
     * 
     * @param originalFunc determining whether to pause or play the timeLine
     */
	protected void setTimeline(boolean originalFunc) {
		this.originalFunc = originalFunc;
		playingTimeLine();
	}
	
	/**
	 * This basically returns the gridPane so any other classes can access it
	 * @return GridPane for other methods/classes to use
	 */
	public GridPane getGridPane() {
		return gridPane;
	}
	
	/**
	 * This basically returns the progressBar so any other classes or methods can
	 * access it.
	 * 
	 * @return ProgressBar for other methods/classes to use
	 */
	public ProgressBar getProgressBar() {
		return progressBar;
	}
	
	
	/**
	 * This protected method is going to pull the url links from the iTunes API depending
	 * on what the user has searched. By using several of the Json objects it will pull
	 * the links and convert into array of URLs which will be done by a private method.
	 * If the number of distinct url links are greater than 21 links than it will add
	 * images to the borderPane and continue with the timeLine and randomizing, but 
	 * if the number of url links are less than 21 links than it will portray an alert
	 * dialogue
	 * 
	 * @param text search query entered by the user
	 */
	protected void images(String text, Stage stage){
		try {	
			String link = "https://itunes.apple.com/search?term=";
			String itunesUrl = "";
			//The URL encoder is going to pull the iTunes links based on the search query
			//and it will particularly pull 100 image URL links
			itunesUrl = link + URLEncoder.encode(text, "UTF-8")+ "&limit=100";
			//In the next few lines the URL links is converted to a Json Array 
			//which will be checked by a private method to make that the links are distinct
		    URL url = new URL(itunesUrl);
		    InputStreamReader reader = new InputStreamReader(url.openStream());
		    JsonParser jp = new JsonParser();
		    JsonElement je = jp.parse(reader);
		    JsonObject root = je.getAsJsonObject();                      
		    JsonArray results = root.getAsJsonArray("results"); 
		    int numResults = results.size();
		    //This private method will ensure that all URL links are distinct
		    checkingUrls(results, numResults);
		    //If the modified Url array length is less than 21 then it will pop up an alert message
		    //Else it will update the image by adding the first twenty to the gridPane
		    if(modifiedSize < 21){
		    	System.out.println("Error, Try Again! :(");     
		    	//Uses this method to print out an alert message and depending on the orig state
		    	//The timeline will continue to occur in the background while the message is
		    	//portrayed
		    	creatingAlert();
		    	playingTimeLine();
		    }
		    else{
		    	//Every time clears the gridPane and uses a method to add the first 20 images
		    	//to the grid pane
		    	gridPane.getChildren().clear();
		    	showingImages(modifiedUrls, stage);
		    	playingTimeLine();
		    }
		}catch(Exception e){
		    System.err.println(e);
		}
    }
	
	/**
	 * This private method will help several methods with determining whether to 
	 * play the timeLine or pause depending on the original state of the timeline
	 * after the changes were made
	 */
	protected void playingTimeLine() {
		if(originalFunc == true)
    		timeline.play();
    	else
    		timeline.pause();
	}

	/**
	 * This private method will help with displaying the alert message to the GUI
	 * if the number of images are actually less than 21 images.
	 */
	private void creatingAlert() {
		//By using the alert object, a pop window will open with a error message 
		//for letting the user know what is wrong
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("Look, an Error Dialog");
		alert.setContentText("Sorry, not enough images. Try Again!");
		alert.showAndWait();	
	}
	
	/**
	 * This private method is going to use the Json array and store the distinct
	 * URL links into an string array, so that no duplicate URL links can be displayed
	 * when the GUI application is running. This method also makes sures that if the 
	 * number of distinct URL links are below 21 than it will revert back to using the
	 * links that was used in the previous search query so that the timeLine never stops
	 * unless specified by the user.
	 * 
	 * @param results Json array storing the URL links
	 * @param numResults the number of URL links gathering by Json
	 */
    private void checkingUrls(JsonArray results, int numResults) {
    	try {
    		//Creating a temperary string url that will gather all of the links from Json array
	    	String[] originalUrls = new String[numResults];
	    	for(int i = 0; i < numResults; i++){
	    		//This is gathering the links from the Json array and if the links are not null
	    		//than it will be stored into the temp string array
	    		JsonObject result = results.get(i).getAsJsonObject();    
			    JsonElement artworkUrl100 = result.get("artworkUrl100");
			    if (artworkUrl100 != null) {
					String artUrl = artworkUrl100.getAsString();
					originalUrls[i] = artUrl;
			    }
	    	}
	    	//Streams will be used to store distinct string URL into an instance string array
	    	modifiedUrls = Arrays.stream(originalUrls)
	    			.distinct()
	    			.toArray(String[] :: new);
	    	modifiedSize = modifiedUrls.length;
	    	//If the distinct URLs are less than 21 then the origModifiedURLs from the previous
	    	//Search query will not change, and if it is more than 21 than it will change
	    	if(modifiedSize >= 21) {
	    		origModifiedUrls = modifiedUrls;
	    		origModifiedSize = origModifiedUrls.length;
	    	}
	    	increment = 20;
    	}catch(Exception e) {
    		System.err.println("Error");
    	}
    	
    }
    
    /**
     * This private method allows the GUI application to post the first 20 images to
     * the borderPane in a 4 by 5 square so that it is easier for the user to see the
     * following search query images. This method also uses thread in order to show
     * increase the progress bar as the images are being stored into the gridPane. 
     * This thread allows the GUI thread to also run this method's thread at the same time
     * to show the progress bar increase as the images are being gathered.
     * 
     * @param modifiedUrls string array containing the distinct URL links
     * @param stage the GUI stage for properly setting the scene for the images
     */
    private void showingImages(String[] modifiedUrls, Stage stage) {
    	try {
    		//New thread is created, which contains the loop for the addition of images
    		//to the gridPane and the increase of progress bar
			Thread t = new Thread(() -> {
				int row = 0,column = 0;
				//This loop is going to iterate 20 times for the gridPane to store the
				//first 20 links in correct order
				for (int i = 0; i <20;i++) {                       
					System.out.println(modifiedUrls[i]);
					Image textImage = new Image(modifiedUrls[i]);
					ImageView textView = new ImageView(textImage);
					//This snnipet of code makes sure that the GUI thread is refreshing
					//the progressBar as it increases
					Platform.runLater(() ->
						progressBar.setProgress(progress+=0.05F)
					);
					if(column % 5 == 0 && column != 0){
					    row++;
					    column = 0;
					}
					int finalRow = finalValue(row);
					int finalCol = finalValue(column);
					//This snippet of code makes sure that when the GUI thread and this 
					//thread runs the images are added to the gridPane along with the progressBar
					Platform.runLater(() -> {
						gridPane.add(textView, finalCol, finalRow);
					});
					//Make sure that the gridPane is not visible
					gridPane.setVisible(false);
					timeline.pause();
					column++;
				}
				//Once the progress bar has reached 100% then the gridPane will be visible
				if(Math.round(progressBar.getProgress()) == 1.0) {
					gridPane.setVisible(true);
					playingTimeLine();
				}
		    });
			t.setDaemon(true);
		    t.start();		
    	}catch(Exception e) {
    		System.err.println("error");
    	}
    }
    
    /**
     * This method sets the progress value used for increasing the progress to its default
     * value, which is 0.0F
     */
    public void setProgress() {
    	progress = 0.0F;
    }
    
    /**
     * This private method allows any int value to be converted to a final value for usage
     * inside any anonymous class or lambda expression
     * 
     * @param num integer value for the conversion to final Value
     * @return an integer that could be used as final
     */
    private int finalValue(int num) {
    	int finalVal = num;
    	return finalVal;
    }   
    
    /**
     * This private method allows the GUI Application to randomly change an image 
     * for the required amount of time by first finding the image at the given
     * random row and col. After the image's row and col match with the random value
     * then that image will be removed and the new image will be added to the random
     * location
     * 
     * @param randRow random row value used for finding the old image
     * @param randCol random column value used for finding the old image
     * @param textView new image that will replace the old image
     */
    private void randomImages(int randRow, int randCol, ImageView textView) {
    	try {
	    	Node image = null;
	    	//Gets all the gridPane children and store it inside a node list
	    	ObservableList<Node> tempChild = gridPane.getChildren();
	    	//This for each loop checks if the children's row and column match with
	    	//the random row and column. If it matches than it removes the old image
	    	//and adds the new image at the random location
	    	for(Node tempNode : tempChild) {
	    		if(GridPane.getRowIndex(tempNode) == randRow && 
	    				GridPane.getColumnIndex(tempNode) == randCol) {
	    			image = tempNode;
	    			//Removing the old image
	    			gridPane.getChildren().remove(image);
	    			//Adding the new image
	    			gridPane.add(textView, randCol, randRow);
	    			break;
	    		}
	    	}
    	}catch(Exception e) {
    		System.err.println("Error");
    	}
    }

}
