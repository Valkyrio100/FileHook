package application;

import java.io.File;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

/**
 * Main class of the application.
 * Controls everything related to the display and user interface.
 *
 */
public class Main extends Application {

	private static AnchorPane root;
	private static Stage primaryStage;

	//Parsing for scripts with ruby text
	public static boolean blueSkyMode = false;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("FileHook");
			HookController hook = new HookController();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/hook.fxml"));
			fxmlLoader.setController(hook);
			Main.root = (AnchorPane) fxmlLoader.load();

			Scene scene = new Scene(Main.root);
			primaryStage.setResizable(false);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			//Code to listen to enter key input
			scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
				Node numberField = Main.root.lookup("#numberlabel");
				if (!numberField.isFocused() && event.getCode() == KeyCode.ENTER)
					hook.hookClick(null);
			});

			primaryStage.setScene(scene);
			primaryStage.setAlwaysOnTop(true);
			
			Main.setTooltips();
			
			primaryStage.show();
			
			Main.primaryStage = primaryStage;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	//Set tooltips of various elements
	public static void setTooltips(){
		Node nextArrow = Main.root.lookup("#nextScript");
		Tooltip.install(nextArrow, Main.generateStandardTooltip("Go to the next file in alphabetical order.")); 
		Node numberField = Main.root.lookup("#numberlabel");
		Tooltip.install(numberField, Main.generateStandardTooltip("Input number of line and press enter.")); 
		Node blueSkyMode = Main.root.lookup("#blueSkyMode");
		Tooltip.install(blueSkyMode, Main.generateStandardTooltip("Parsing for scripts with ruby text.")); 
		Node alwaysOnTopMode = Main.root.lookup("#alwaysOnTopMode");
		Tooltip.install(alwaysOnTopMode, Main.generateStandardTooltip("Keep the application on the foreground.")); 
	}
	
	//Function to generate tooltips with the same parameters
	private static Tooltip generateStandardTooltip(String text){
		Tooltip tooltip = new Tooltip(text);
		tooltip.setWrapText(true);
		tooltip.setMaxWidth(150);
		tooltip.setFont(new Font(13));
		return tooltip;
	}

	//Control of the filename label
	public static void showFileName(String name) {
		Node fileNameLabel = Main.root.lookup("#fileName");
		((Label) fileNameLabel).setText(name);
	}

	//Control of the labels related to the current line
	public static void setLine(int i, String string, int totalLines) {
		Node numberField = Main.root.lookup("#numberlabel");
		((TextField) numberField).setText(Integer.toString(i));
		Node lineLabel = Main.root.lookup("#lineLabel");
		((Label) lineLabel).setText(string);
		Node totalLabel = Main.root.lookup("#labelTotal");
		((Label) totalLabel).setText(i + "/" + totalLines);
	}

	//Function to obtain the contents of the number line label
	public static int getNumberLine() {
		Node numberField = Main.root.lookup("#numberlabel");
		try {
			return new Integer(((TextField) numberField).getText());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	//Control of selection script window
	public static File selectScript(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open script file...");
		return fileChooser.showOpenDialog(primaryStage);
	}
	
	//Control of the "recent list of files" side menu
	public static void showRecent(RecentFiles recentFiles) {
		Node recentList = Main.root.lookup("#recentList");
		@SuppressWarnings("unchecked")
		ListView<String> viewList = ((ListView<String>) recentList);
		//set action for when item selected with a double click
		viewList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent click) {

				if (click.getClickCount() == 2) {
					String currentItemSelected = viewList.getSelectionModel().getSelectedItem();
					HookController.openRecent(currentItemSelected);
				}
			}
		});
		if (viewList.getItems() != null)
			viewList.getItems().clear();
		if (recentFiles != null && recentFiles.getList() != null)
			for (String[] line_path : recentFiles.getList())
				viewList.getItems().add(line_path[0] + " : " + line_path[1]);
		Node recentPane = Main.root.lookup("#paneRecent");
		recentPane.setVisible(true);
	}
	public static void closeRecent() {
		Node recentPane = Main.root.lookup("#paneRecent");
		recentPane.setVisible(false);
	}

	//Control of the "delta features" side menu
	public static void showDelta() {
		Node recentPane = Main.root.lookup("#paneDelta");
		recentPane.setVisible(true);
	}
	public static void closeDelta() {
		Node recentPane = Main.root.lookup("#paneDelta");
		recentPane.setVisible(false);
	}

	public static void toogleAlwaysOnTop() {
		Node topFire = Main.root.lookup("#alwaysOnTopMode");
		primaryStage.setAlwaysOnTop(((CheckBox)topFire).isSelected());
	}

}
