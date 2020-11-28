/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientGUI extends Application {
	// I/O streams 
	ObjectOutputStream toServer = null;
	ObjectInputStream fromServer = null;
	Client client = null;
	Socket socket = null;

	// JavaFX
	Scene itemsScene;
	HashMap<Item, TextField> timerCountdown = new HashMap<Item, TextField>();

	// Decimal formatter
	DecimalFormat df = new DecimalFormat("0.00");

	@Override
	public void start(Stage primaryStage) {
		// TODO: establish socket connections for username and password login

		int port = 5000;
		try {
			socket = new Socket("localhost", port);
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
			System.out.println("Networking established");

			client = new Client("", socket, toServer, fromServer);

			Thread readerThread = new Thread(new IncomingReader(fromServer, client)); // see Canvas's Chat for IncomingReader class
			readerThread.start();

		} catch (IOException e) {
			System.out.println("Networking not established: check server status");
		}

		// items view

		TabPane itemsPane = new TabPane();
		itemsScene = new Scene(itemsPane, 350, 450);
		itemTabsInit(itemsPane);


		// login

		GridPane loginPane = new GridPane();
		loginPane.setHgap(10);
		loginPane.setVgap(10);

		// Create a scene and place it in the stage
		Scene loginScene = new Scene(loginPane, 300, 190);
		primaryStage.setTitle("Login to EHills"); // Set the stage title
		primaryStage.setScene(loginScene); // Place the scene in the stage
		primaryStage.show(); // Display the stage

		// Customer Login:
		Label usernameLabel = new Label("Username: ");
		usernameLabel.setFont(new Font("Cambria", 20));

		TextField usernameTextField = new TextField();
		usernameTextField.setEditable(true);
		usernameTextField.setPrefWidth(150);

		Label passwordLabel = new Label("Password: ");
		passwordLabel.setFont(new Font("Cambria", 20));

		PasswordField passwordTextField = new PasswordField();
		passwordTextField.setPrefWidth(150);

		Button loginButton = new Button("Login");
		loginButton.setMaxWidth(120);

		Button guestButton = new Button("Continue as Guest");
		loginButton.setMaxWidth(300);

		Button loginExitButton = new Button("Exit");
		loginButton.setMaxWidth(120);

		Button loginClearButton = new Button("Clear");
		loginButton.setMaxWidth(120);

		TextArea loginTextArea = new TextArea("Welcome to EHills!");
		loginTextArea.setEditable(false);
		loginTextArea.setPrefWidth(280);
		loginTextArea.setPrefHeight(30);

		loginPane.add(usernameLabel, 1, 1);
		loginPane.add(usernameTextField, 2, 1);
		loginPane.add(passwordLabel, 1, 2);
		loginPane.add(passwordTextField, 2, 2);
		loginPane.add(loginButton, 1, 3);
		loginPane.add(guestButton, 2, 3);
		loginPane.add(loginExitButton, 1, 4);
		loginPane.add(loginClearButton, 2, 4);
		loginPane.add(loginTextArea, 1, 5, 2, 1);


		loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				client.sendLogin(new Login(usernameTextField.getText(), passwordTextField.getText()));
				if(checkLogin()) {
					loginTextArea.setText("Login Successful!");
					client.setName(usernameTextField.getText());
					startClient(primaryStage);
					itemTabsInit(itemsPane);
				}
				else {
					loginTextArea.setText("Incorrect username/password: Please try again");
				}
			}
		});

		guestButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				client.sendLogin(new Login("Guest", ""));
				if(checkLogin()) {
					loginTextArea.setText("Login Successful!");
					client.setName(usernameTextField.getText());
					startClient(primaryStage);
					itemTabsInit(itemsPane);
				}
				else {
					loginTextArea.setText("Incorrect username/password: Please try again");
				}
			}
		});

		loginClearButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				usernameTextField.clear();
				passwordTextField.clear();
				loginTextArea.setText("Welcome to EHills!");
			}
		});

		loginExitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		// timer setup
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

			}
		});


	}

	private boolean checkLogin() {
		while(true) {
			if(client.checkLogin() != null) {
				boolean l = client.checkLogin();
				client.resetLogin();
				return l;
			}
		}
	}

	private void itemTabsInit(TabPane tabPane) {
		tabPane.getTabs().clear();
		Tab welcome = new Tab("Welcome!");
		BorderPane welcomePane = new BorderPane();
		TextArea welcomeText = new TextArea();
		welcomeText.setEditable(false);
		welcomeText.setVisible(true);
		welcomeText.setPrefWidth(400);
		welcomeText.setText("Welcome to EHills!\n" +
				"As a customer, you can bid on items to buy them.\n" +
				"Each item has a buy now price which marks the price you can stop bidding.\n" +
				"Keep track of the timer! When the timer hits 0, the item is sold!\n" +
				"A bid in the last 20 seconds will reset the timer back to 20 seconds.\n" +
				"Happy bidding!");
		welcomePane.setTop(welcomeText);
		welcome.setContent(welcomePane);
		tabPane.getTabs().add(welcome);

		for(Item currentItem : client.getItemsDs()) {
			System.out.println(currentItem);
			GridPane currentItemPane = new GridPane();
			Tab currentTab = new Tab(currentItem.getName());
			currentTab.setContent(currentItemPane);
			currentTab.setClosable(false);

			Label itemInformationTab = new Label("Item Information:");
			Label itemTabLabel = new Label("Name: " + currentItem.getName());
			Label startPrice = new Label("Starting Price: " + df.format(currentItem.getMinimumBid()));
			Label buyNowPrice = new Label("Buy Now Price: " +  df.format(currentItem.getBuyNow()));
			Label separator = new Label("------------------------------------------------------------");
			Label currentInformationTab = new Label("Current Information:");
			Label mostRecentBidder = new Label("Highest Bidder: ");
			TextField highestBidder = new TextField(currentItem.getHighestBidder());
			highestBidder.setEditable(false);
			Label currentPriceLabel = new Label("Current Bid: ");
			TextField currentPrice = new TextField(df.format(currentItem.getCurrentPrice()));
			highestBidder.setEditable(false);
			Label timerLabel = new Label("Time left: ");
			TextField timerCounter = new TextField("" + currentItem.getTimeLeft() + "s");

			currentItemPane.add(itemInformationTab, 0, 0, 1, 1);
			currentItemPane.add(itemTabLabel, 0, 1, 3, 1);
			currentItemPane.add(startPrice, 0, 2, 3, 1);
			currentItemPane.add(buyNowPrice, 0, 3, 3, 1);

			currentItemPane.add(separator, 0, 4, 3, 1);

			currentItemPane.add(currentInformationTab, 0, 5, 1, 1);
			currentItemPane.add(timerLabel, 0, 6, 1, 1);
			currentItemPane.add(timerCounter, 1, 6, 2, 1);
			currentItemPane.add(mostRecentBidder, 0, 7, 1, 1);
			currentItemPane.add(highestBidder, 1, 7, 2, 1);
			currentItemPane.add(currentPriceLabel, 0, 8, 1, 1);
			currentItemPane.add(currentPrice, 1, 8, 2, 1);

			tabPane.getTabs().add(currentTab);
		}
	}

	private void startClient(Stage primaryStage) {
		primaryStage.setScene(itemsScene);
		primaryStage.setTitle("EHills: " + client.getName());
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
