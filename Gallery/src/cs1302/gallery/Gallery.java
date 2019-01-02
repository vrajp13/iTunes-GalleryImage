
package cs1302.gallery;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.application.Platform;
import javafx.scene.text.*;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.geometry.Orientation;
import javafx.scene.control.ProgressBar;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStreamReader;
import javafx.scene.layout.GridPane;
import java.io.IOException;

import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Gallery extends Application {

    private GridPane gridPane = new GridPane();
    private BorderPane borderPane;
    private ProgressBar progressBar = new ProgressBar();
    private Stage stage = null;
    private boolean indicator = false;
    private Float progress = 0.0F;
    private Random rand;
    
    @Override
    
    public void start(Stage stage) {
    	rand = new Random();
    	
		this.stage = stage;
		this.borderPane = new BorderPane();
		VBox topPhase = new VBox();
	    MenuBar menu = creatingMenuBar();
		TextField textResponse = new TextField();
		topPhase.getChildren().addAll(menu, toolBar(textResponse));
		borderPane.setTop(topPhase);
		progressBar.setProgress(progress);
		borderPane.setCenter(images());
		//borderPane.setBottom(progressBar);
	
	
		HBox pane = new HBox();
		Scene scene = new Scene(borderPane);
		stage.setMaxWidth(640);
		stage.setMaxHeight(480);
	        stage.setTitle("[XYZ] Gallery!");
	        stage.setScene(scene);
		stage.sizeToScene();
	        stage.show();
    } // start

    private GridPane images(TextField...text){
		try{
			String link = "https://itunes.apple.com/search?term=";
			String itunesUrl = "";
		    if(text.length==0) {
		    	itunesUrl = link+ "rock";
		    }
		    else {
			    String textResponse = text[0].getText();
			    itunesUrl = link + URLEncoder.encode(textResponse, "UTF-8");
		    }
		    URL url = new URL(itunesUrl);
		    InputStreamReader reader = new InputStreamReader(url.openStream());
		    JsonParser jp = new JsonParser();
		    JsonElement je = jp.parse(reader);
		    JsonObject root = je.getAsJsonObject();                      
		    JsonArray results = root.getAsJsonArray("results");          
		    int numResults = results.size();
		    if(numResults < 21){
		    	System.out.println("Sorry, less than 20 images");
		    }
		    else{
		    	showingImages(numResults, results);
		    }
		}catch(Exception e){
		    System.err.println(e);
		}
		return gridPane;
    }


    private void showingImages(int numResults, JsonArray results) {
		int row = 0, randRow = 0, randCol = 0, column = 0;
		for (int i = 0; i < numResults; i++) {                       
		    JsonObject result = results.get(i).getAsJsonObject();    
		    JsonElement artworkUrl100 = result.get("artworkUrl100");
			
		    if (artworkUrl100 != null) {
			String artUrl = artworkUrl100.getAsString(); 
			System.out.println(artUrl);
			Image textImage = new Image(artUrl);
			ImageView textView = new ImageView(textImage);
			if(i >= 20){
		    	randRow = rand.nextInt(4);
		    	randCol = rand.nextInt(5);
		    	int finalRow = finalValue(randRow);
		    	int finalCol = finalValue(randCol);
		    	findingImages(finalRow, finalCol, textView);
//		    	if(i == numResults - 1) {
//		    		i = 20;
//		    	}
		    	EventHandler<ActionEvent> handler = event -> findingImages(finalRow, finalCol, textView);
		    	KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
		    	Timeline timeline = new Timeline();
		    	timeline.setCycleCount(Timeline.INDEFINITE);
		    	timeline.getKeyFrames().add(keyFrame);
		    	timeline.play();
				
		    	
			}
			else {
				if(column % 5 == 0 && column != 0){
				    row++;
				    column = 0;
				}
				gridPane.add(textView, column, row);
				borderPane.setCenter(gridPane);
				column++;
			}
			stage.sizeToScene();
		} // if
		}
    }

    private int finalValue(int randomVal) {
    	final int value = randomVal;
    	return value;
    }
    
    
    private void findingImages(int randRow, int randCol, ImageView textView) {
    	Node image = null;
    	ObservableList<Node> tempChild = gridPane.getChildren();
    	for(Node tempNode : tempChild) {
    		if(GridPane.getRowIndex(tempNode) == randRow && 
    				GridPane.getColumnIndex(tempNode) == randCol) {
    			image = tempNode;
    			gridPane.getChildren().remove(image);
    			gridPane.add(textView, randCol, randRow);
    			borderPane.setCenter(gridPane);
    			break;
    		}
    	}
    }

    
    private ToolBar toolBar(TextField text){
		Text searchQuery = new Text("Search Query:");
		Button playPause = new Button("Play");
		Button updateImages = new Button("Update Images");
		updateImages.setOnAction(event -> {
			System.out.println("clicked");
			images(text);
//			borderPane.setCenter(images(text));
			stage.sizeToScene();
			if(indicator == true){
			    playPause.setText("Pause");
			}
		    });
		Separator separateOnce = new Separator();
		separateOnce.setOrientation(Orientation.VERTICAL);
		ToolBar toolBar = new ToolBar(playPause, separateOnce, searchQuery,
					      text, updateImages);
		return toolBar;
	    }

    
    private MenuBar creatingMenuBar(){
		MenuBar menuBar = new MenuBar();
		Menu file = new Menu("File");
		MenuItem exiting = new MenuItem("Exit");
		exiting.setOnAction(event -> {
			System.out.println("Exiting....");
			Platform.exit();
			System.exit(1);
		    });
		file.getItems().add(exiting);
		menuBar.getMenus().add(file);
		return menuBar;

    }

    
    public static void main(String[] args) {
		try {
		    Application.launch(args);
		} catch (UnsupportedOperationException e) {
		    System.out.println(e);
		    System.err.println("If this is a DISPLAY problem, then your X server connection");
		    System.err.println("has likely timed out. This can generally be fixed by logging");
		    System.err.println("out and logging back in.");
		    System.exit(1);
		} // try
	    } // main

} // GalleryApp


