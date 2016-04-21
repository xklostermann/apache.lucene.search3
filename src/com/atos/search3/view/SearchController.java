package com.atos.search3.view;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.UpgradeIndexMergePolicy;
import org.controlsfx.control.textfield.CustomTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

import com.atos.search3.*;
import com.atos.search3.view.*;
import com.atos.search3.model.*;

public class SearchController {

	@FXML
	private TextField textfieldSelectedDirectory;
	@FXML
	private Button path;
	@FXML
	private Label giflabel;
	@FXML
	private TextField searchfield;
	@FXML
	private TableColumn<Searchresults, Integer> No;
	@FXML
	private TableColumn<Searchresults, String> Type;
	@FXML
	private TableColumn<Searchresults, String> Name;
	@FXML
	private TableView<Searchresults> searchResult;
	// @FXML
	// private final ObservableList<Searchresults> data =
	// FXCollections.observableArrayList();
	@FXML
	private ProgressBar bar;
	@FXML
	private RadioButton button1;
	@FXML
	private RadioButton button2;
	@FXML
	private ToggleGroup group;
	
	private static MainApp mainApp;
	private Stage stage;
	private SearchFiles serachfiles;

	@FXML
	private void initialize() {
		
		System.out.println(Name);
		Name.setCellValueFactory(cellData -> cellData.getValue().NameProperty());
		Type.setCellValueFactory(cellData -> cellData.getValue().TypeProperty());
		No.setCellValueFactory(cellData -> cellData.getValue().NoProperty().asObject());
		button1.setToggleGroup(group);
		button2.setToggleGroup(group);
	
		
		System.out.println("Tu som");
		searchResult.setEditable(true);
		searchResult.getSelectionModel().setCellSelectionEnabled(true);  // selects cell only, not the whole row
		searchResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
		 @Override
		 public void handle(MouseEvent click) {
		  if (click.getClickCount() == 2) {
		   @SuppressWarnings("rawtypes")
		   TablePosition pos = searchResult.getSelectionModel().getSelectedCells().get(0);
		   int row = pos.getRow();
		   int col = pos.getColumn();
		   @SuppressWarnings("rawtypes")
		   TableColumn column = pos.getTableColumn();
		   String val = column.getCellData(row).toString(); 
		   System.out.println("Selected Value, " + val + ", Column: " + col + ", Row: " + row);
		   try {
			   
			   
			   if(button1.isSelected()){
			Runtime.getRuntime().exec("explorer.exe /select,"+val);
			   }else{
				   if(button2.isSelected())
				   {
					   ProcessBuilder pb = new ProcessBuilder("Notepad.exe", val);
					   pb.start();
				   }
			   }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
		  }
		 }
		});
		
	
		
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		searchResult.setItems(mainApp.getPersonData());
	}

	// public void addtable(int no, String type, String name) {
	//
	//
	// }

	public String getSearchfield() {

		return textfieldSelectedDirectory.getText();

	}

	public SearchController() {

	}

	public String getsearch() {

		return searchfield.getText();

	}

	@FXML
	private void Index() {

		System.out.println(textfieldSelectedDirectory.getText());

		IndexFiles.start(textfieldSelectedDirectory.getText());
		
	

	}

	@FXML
	private void search() {

		
		
		// addtable("Ahoj","Ahoj","Ahoj");

		// data.add(new Searchresults("Ahoj", "Ahoj", 10));
		// searchResult.setItems(data);
		System.out.println("Somtu");

		System.out.println(searchfield.getText());
		MainApp.data.removeAll(MainApp.data);
		SearchFiles.start(searchfield.getText());
		System.out.println(searchfield.getText());
	}

	private void setPath(File selectedDirectory) {
		String dir = selectedDirectory.getAbsolutePath();

//		System.out.println(dir);
//
//		System.out.println(textfieldSelectedDirectory.getText());

		textfieldSelectedDirectory.setText(dir);

		// textfieldSelectedDirectory.getParent().layout();

		System.out.println(textfieldSelectedDirectory.getText());
	}

	@FXML
	private void button() {
		path.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {

			}
		});
	}

	@FXML
	private void handlePath() {

		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Search folder");
		File selectedDirectory = directoryChooser.showDialog(stage);

		setPath(selectedDirectory);

	}

	public static void addtable(int no, String type, String name) {
		Searchresults results = new Searchresults(name, type, no);
		mainApp.getPersonData().add(results);

	}

	@FXML
	public void openexplorer() {
		
		

	}
	
	
	
	
	
}
