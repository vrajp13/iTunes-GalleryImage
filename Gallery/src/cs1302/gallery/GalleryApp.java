
package cs1302.gallery;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.text.*;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;

/**
 * Purpose: The GalleryApp class is implementing a GUI application using 
 * JavaFX 8 that displays a gallery of images based on the results of a 
 * search query to the iTunes Search API. A default query is set up
 * when the application opens, and different buttons on the application will
 * allow the users to update images, play/pause, exit, change themes, and 
 * info about the author. 

 * @author Vraj Pragnesh Patel
 *
 */


public class GalleryApp extends Application {

    private BorderPane borderPane;
    private Stage stage = null;
    private Button playPause = new Button("Pause");
    private final String defaultQuery = "rock";
    private MenuBar fileBar = new MenuBar();
    private MenuBar helpBar = new MenuBar();
    private MenuBar themeBar = new MenuBar();
    private ToolBar toolBar = new ToolBar();
    private Images creatingImages = new Images();
    
    /**
     * This start method is overriding the method from Application class
     * and it is creating several aspects of the image gallery and putting
     * them in a border pane in order to display a proper set up of the
     * application. Lastly the new scene created will display the border pane.
     * 
     * @param stage creates the application stage
     */
    @Override
    public void start(Stage stage) {
		this.stage = stage;
		this.borderPane = new BorderPane();
		//Here HBox is created to store the menuBar buttons in a nice order
		HBox menuBar = new HBox();
		//VBox helps set up the menuBar at the top and toolBar right below it
		VBox topPhase = new VBox();
		//These next three statements are using the private file, help, and theme
		//menu bar methods in order to store them into the HBox previously created
		fileMenuBar();
		themeMenuBar();
		helpMenuBar();
	    menuBar.getChildren().addAll(fileBar, themeBar, helpBar);
	    //This textfield is how the response from the search bar will be used in 
	    //gathering images from the iTunes API
		TextField textResponse = new TextField();
		topPhase.getChildren().addAll(menuBar, toolBar(textResponse));
		//These several lines is setting the VBox in border pane at top, images at center
		//and progress bar at bottom
		Text itunes = new Text("Images provided courtsey of iTunes");
		HBox bottom = new HBox();
		bottom.getChildren().addAll(creatingImages.getProgressBar(), itunes);
		borderPane.setBottom(bottom);
		borderPane.setTop(topPhase);
		creatingImages.images(defaultQuery, stage);
		//By using the images class object, here it is using the timeline method, that
		//will allow the application to randomly change images for the specified duration
		creatingImages.constructingImages();
		borderPane.setCenter(creatingImages.getGridPane());
		//Next the borderPane is added to scene which will display it on the stage
		Scene scene = new Scene(borderPane);
		stage.setMaxWidth(520);
		stage.setMaxHeight(520);
		stage.setTitle("[XYZ] Gallery!");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
    } // start
       
    
    /**
     * This private method is creating a toolbar with play/pause button, search bar,
     * and an update images button. These buttons have their respective functionality
     * that allow the user to either stop or play the random images every two seconds, or
     * search a new gallery of images and update them. 
     * 
     * @param text search response used to gather the iTunes link
     * @return the toolbar with all the buttons in store
     */
    private ToolBar toolBar(TextField text){
		Text searchQuery = new Text("Search Query:");
		//The play pause button is setting up the action for when it is pressed
		//it will either pause the randomization or resume playing the randomization
		playPause.setOnAction(event -> {
			//If the button is pressed and the text is play then it will resume playing
			//the randomization and set the text to pause.
			if(playPause.getText().equalsIgnoreCase("Play")) {
				playPause.setText("Pause");
				//This is using the images class object to control the play of timeline
				creatingImages.setTimeline(true);
			}
			//If the button pressed has a text of pause then it will stop randomizing
			//and set the text to play
			else if(playPause.getText().equalsIgnoreCase("Pause")) {
				playPause.setText("Play");
				//This is using the images class object to control the pause of timeline
				creatingImages.setTimeline(false);
			}
		});
		//Creates a button called Update images
		Button updateImages = new Button("Update Images");
		//The update images is setting up the action for when it is pressed it will change the
		//images from the default query to the query that the user wants to change it to
		updateImages.setOnAction(event -> {
			//Gets the text field response and converts it to string to allow the image class
			//object to search the links of search query and display those images to GUI App
			String query = text.getText();
			System.out.println("clicked");
			creatingImages.setProgress();
			creatingImages.images(query, stage);
			stage.sizeToScene();
		});
		//Just creates a separator between play pause button and search bar
		Separator separateOnce = new Separator();
		separateOnce.setOrientation(Orientation.VERTICAL);
		//Adds all the necessary tools to the toolbar for user friendly interaction
		toolBar = new ToolBar(playPause, separateOnce, searchQuery, text, updateImages);
		return toolBar;
	}

    
    /**
     * Creates a file menubar that will have one menu item which will terminate the application
     * 
     * @return menuBar that has one menuItem
     */
    private void fileMenuBar(){
    	//Creates a menuBar with a menu called file and inside that a menuItem 
    	//called exit which will terminate the application
		Menu file = new Menu("File");
		MenuItem exiting = new MenuItem("Exit");
		//When exiting menuItem is pressed then both the program and application terminate
		exiting.setOnAction(event -> {
			System.out.println("Exiting....");
			Platform.exit();
			System.exit(1);
		    });
		//Adds the menuItem to the menu and menuBar
		file.getItems().add(exiting);
		fileBar.getMenus().add(file);
    }
    
    /**
     * Creates a help menubar that will have one menu item which is some information about
     * the author. However, when this menu item is pressed it will open a new window
     * and it will have nothing to do with the parent application window. 
     * 
     * @return menuBar that has one menuItem
     */
    private void helpMenuBar() {
    	//Creates a menuBar with a menu called help and inside that a menuItem 
    	//called about which will open a new window containing info about the author
    	Menu help = new Menu("Help");
		MenuItem about = new MenuItem("About Vraj Pragnesh Patel");
		//A new stage is created for the menu item about
		//This close button will only close the window and not the whole application
		about.setOnAction(event -> {
			//When the about menu item is pressed the original state of timeline is maintained
			creatingImages.playingTimeLine();
			Stage newWindow = new Stage();
			BorderPane newBorderPane = new BorderPane();
			VBox information = new VBox();
			//In a vertical box the image will be stored, after which it will in a border pane
			Image myPhoto = new Image("https://d1b10bmlvqabco.cloudfront.net/photos/j66m61jjhqa2p8/1541305507_375.png");
			ImageView photoView = new ImageView(myPhoto);
			newBorderPane.setTop(photoView);
			BorderPane.setAlignment(newBorderPane.getTop(), Pos.CENTER);
			information.getChildren().addAll(new Text("Name: Vraj Pragnesh Patel"), 
					new Text("Email: vraj.patel4913@gmail.com"), 
					new Text("Application Version: 1.0.1"));
			information.setAlignment(Pos.CENTER);
			//The top of border pane is image, center has info and bottom the close button.
			newBorderPane.setCenter(information);
			newBorderPane.setBottom(closeButton(newWindow));
			//A new scene is created for the new stage and the application modal
			Scene newScene = new Scene(newBorderPane, 450, 450);
			newWindow.setMaxWidth(500);
			newWindow.setMaxHeight(500);
			newWindow.setScene(newScene);
			newWindow.setTitle("About Vraj Pragnesh Patel");
			newWindow.initModality(Modality.APPLICATION_MODAL);
			newWindow.show();
		});
		help.getItems().add(about);
		helpBar.getMenus().add(help);
    }
    
    /**
     * This private button method is creating the closing button for the helpBar 
     * method so it is convinent for the user to just press close to close the
     * application modal without closing the whole GUI Application
     * 
     * @param newWindow the stage where the close button needs to added
     * @return button that already has action set on it
     */
    private Button closeButton(Stage newWindow) {
		Button close = new Button("Close");
		//Sets the close button to have some action when it is pressed.
		close.setOnAction(event -> {
			System.out.println("Exiting...");
			//When it is closed then the original state of the timeline will be maintained
			creatingImages.playingTimeLine();
			newWindow.close();
		});
		return close;
    }
    
    /**
     * Creates a theme menubar that will have three menu item which will change the overall
     * theme of the application
     * 
     * @return menubar with three menu items
     */
    private void themeMenuBar() {
    	//Creates a menuBar with a menu called theme which has three menuItems that will
    	//change the overall theme of the application
    	Menu theme = new Menu("Themes");
		MenuItem theme1 = new MenuItem("Theme1");
		MenuItem defaultTheme = new MenuItem("Default");
		MenuItem theme2 = new MenuItem("Theme2");
		//When the menuItem default is pressed then it will revert back to the original theme
		defaultTheme.setOnAction(event -> {
			helpBar.setStyle(null);
			fileBar.setStyle(null);
			themeBar.setStyle(null);
			toolBar.setStyle(null);
			borderPane.setStyle(null);
		});
		//When the menuItem theme1 is pressed then it will change to a new background theme color
		theme1.setOnAction(event -> {
			helpBar.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, olive 0%, maroon 100%);");
			fileBar.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, olive 0%, maroon 100%);");
			themeBar.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, olive 0%, maroon 100%);");
			toolBar.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, olive 0%, maroon 100%);");
			borderPane.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, olive 0%, maroon 100%);");
		});
		//When the menuItem theme2 is pressed then it will change to another new theme color
		theme2.setOnAction(event -> {
			helpBar.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, cadetblue 0%, firebrick 100%);");
			fileBar.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, cadetblue 0%, firebrick 100%);");
			themeBar.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, cadetblue 0%, firebrick 100%);");
			toolBar.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, cadetblue 0%, firebrick 100%);");
			borderPane.setStyle("-fx-background-color: "
					+ "linear-gradient(from 0% 0% to 100% 100%, cadetblue 0%, firebrick 100%);");
		});
		//Stores the three menuitem in proper order so that it could be displayed properly
		theme.getItems().addAll(defaultTheme, theme1, theme2);
		themeBar.getMenus().add(theme);
    }
    
    /**
     * This main method launches the overall application
     * 
     * @param args start method used as the first argument
     */
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

