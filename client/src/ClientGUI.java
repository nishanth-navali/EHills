/*
 * EE422C Final Project submission by
 * Nishanth Navali
 * nan686
 * 16160
 * Fall 2020
 * Slip days used: 1
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ClientGUI extends Application {

    // Client
    Client client = null;

    // I/O streams
    ObjectOutputStream toServer = null;
    ObjectInputStream fromServer = null;
    Socket socket = null;
    static String IP_Address = "localhost";

    // JavaFX
    Scene itemsScene;
    ArrayList<ItemGUI> itemsDisplay = new ArrayList<>();
    MediaPlayer startup = new MediaPlayer(new Media(new File("audio/StartupSound.mp3").toURI().toString()));
    MediaPlayer badLogin = new MediaPlayer(new Media(new File("audio/BadLogin.mp3").toURI().toString()));

    // Decimal formatter
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void start(Stage primaryStage) {

        // establish networking with the server
        int port = 5000;
        try {
            // connect via Socket
            socket = new Socket(IP_Address, port);

            // initialize Object I/O streams
            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());

            // testing
            System.out.println("Networking established");

            // create new client and pass all information to it
            client = new Client("", socket, toServer, fromServer, this);

        } catch (IOException e) {
            System.out.println("Networking not established: check server status. Please restart");
            System.exit(0);
        }

        // ITEMS VIEW: load up items GUIs
        TabPane itemsPane = new TabPane();
        itemsScene = new Scene(itemsPane, 360, 450);
        itemsScene.getStylesheets().add("stylesheet/styles.css");
        itemTabsInit(itemsPane);

        // LOGIN VIEW: create GUI specifically for logging in with U/P or as guest

        GridPane loginPane = new GridPane();
        loginPane.setHgap(10);
        loginPane.setVgap(10);

        // Create a scene and place it in the stage
        Scene loginScene = new Scene(loginPane, 340, 200);
        loginScene.getStylesheets().add("stylesheet/styles.css");
        primaryStage.setTitle("Login to EHills"); // Set the stage title
        primaryStage.setScene(loginScene); // Place the scene in the stage
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/hills.png")));
        primaryStage.show(); // Display the stage

        // Username elements
        Label usernameLabel = new Label("Username: ");
        usernameLabel.setFont(new Font("Cambria", 20));
        TextField usernameTextField = new TextField();
        usernameTextField.setEditable(true);
        usernameTextField.setPrefWidth(150);

        // Password elements
        Label passwordLabel = new Label("Password: ");
        passwordLabel.setFont(new Font("Cambria", 20));
        PasswordField passwordTextField = new PasswordField();
        passwordTextField.setPrefWidth(150);

        // Button to log in
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(120);

        // Button to log in as a guest
        Button guestButton = new Button("Continue as Guest");
        loginButton.setMaxWidth(300);

        // Button to quit
        Button loginExitButton = new Button("Exit");
        loginButton.setMaxWidth(120);

        // Button to clear text in U/P fields
        Button loginClearButton = new Button("Clear");
        loginButton.setMaxWidth(120);

        // TextArea to display incorrect login
        TextArea loginTextArea = new TextArea("Welcome to EHills!");
        loginTextArea.setEditable(false);
        loginTextArea.setPrefWidth(280);
        loginTextArea.setPrefHeight(30);

        // add all elements to the login GridPane
        loginPane.add(usernameLabel, 1, 1);
        loginPane.add(usernameTextField, 2, 1);
        loginPane.add(passwordLabel, 1, 2);
        loginPane.add(passwordTextField, 2, 2);
        loginPane.add(loginButton, 1, 3);
        loginPane.add(guestButton, 2, 3);
        loginPane.add(loginExitButton, 1, 4);
        loginPane.add(loginClearButton, 2, 4);
        loginPane.add(loginTextArea, 1, 5, 2, 1);

        // LOGIN BUTTON ACTION
        loginButton.setOnAction(event -> {
            // send to server
            client.sendLogin(new Login(usernameTextField.getText(), passwordTextField.getText()).encrypt());
            loginButton.setDisable(true);
            loginClearButton.setDisable(true);
            loginExitButton.setDisable(true);
            guestButton.setDisable(true);
            badLogin.stop();
            // if login is successful
            if (checkLogin()) {
                loginTextArea.setText("Login Successful!");
                client.setName(usernameTextField.getText());
                startClient(primaryStage);
                itemTabsInit(itemsPane);
            } else {
                loginTextArea.setText("Incorrect username/password: Please try again");
                badLogin.play();
            }
            loginButton.setDisable(false);
            loginClearButton.setDisable(false);
            loginExitButton.setDisable(false);
            guestButton.setDisable(false);
        });

        // CONTINUE AS GUEST BUTTON ACTION
        guestButton.setOnAction(event -> {
            // send to server
            client.sendLogin(new Login("Guest", ""));

            // just a formality
            if (checkLogin()) {
                loginTextArea.setText("Login Successful!");
                if (usernameTextField.getText().equals("")) {
                    client.setName("Guest");
                }
                else {
                    client.setName(usernameTextField.getText());
                }
                startClient(primaryStage);
                itemTabsInit(itemsPane);
            } else {
                loginTextArea.setText("Incorrect username/password: Please try again");
            }
        });

        // CLEAR BUTTON ACTION
        loginClearButton.setOnAction(event -> {
            usernameTextField.clear();
            passwordTextField.clear();
            loginTextArea.setText("Welcome to EHills!");
        });

        // EXIT BUTTON ACTION
        loginExitButton.setOnAction(event -> System.exit(0));
    }

    /**
     * check server response after logging in
     *
     * @return true if login was valid, else false
     */
    private boolean checkLogin() {
        Boolean loginStatus;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loginStatus = client.checkLogin();
        if (loginStatus == null) {
            System.out.println("Login not received from server");
            return false;
        } else {
            client.resetLogin();
            System.out.println("sending login status: " + loginStatus);
            return loginStatus;
        }
    }

    /**
     * initializes the items view using ArrayList of items from client
     *
     * @param tabPane is the pane that holds all of the item views
     */
    private void itemTabsInit(TabPane tabPane) {
        tabPane.getTabs().clear();

        // Initialize a nice landing page for the user
        Tab welcome = new Tab("Welcome!");
        BorderPane welcomePane = new BorderPane();
        TextArea welcomeText = new TextArea();
        welcomeText.setEditable(false);
        welcomeText.setVisible(true);
        welcomeText.setPrefWidth(200);
        welcomeText.setText("Welcome to EHills!\n" +
                "As a customer, you can bid on items to \nbuy them. " +
                "Each item has a buy now price \nwhich marks the price you can stop \nbidding. " +
                "Also, keep track of the timer! \nWhen the timer hits 0, the item is sold!\n" +
                "A bid in the last 20 seconds will reset \nthe timer back to 20 seconds.\n" +
                "Happy bidding!");
        welcomePane.setTop(welcomeText);
        welcome.setContent(welcomePane);
        tabPane.getTabs().add(welcome);

        // loop through ArrayList of items to initialize a tab for each
        for (Item currentItem : client.getItemsDs()) {
            System.out.println(currentItem);
            currentItem.startTimer();
            ItemGUI currentItemGUI = new ItemGUI(currentItem, this.client);
            GridPane currentItemPane = currentItemGUI.init();
            Tab currentTab = new Tab(currentItem.getName());
            currentTab.setContent(currentItemPane);
            currentTab.setClosable(false);
            itemsDisplay.add(currentItemGUI);
            tabPane.getTabs().add(currentTab);
        }
    }

    /**
     * switches from login view to item view
     *
     * @param primaryStage - the main stage for JavaFX
     */
    private void startClient(Stage primaryStage) {
        primaryStage.setScene(itemsScene);
        primaryStage.setTitle("EHills: " + client.getName());
        startup.play();
    }

    /**
     * Update the items display with the latest information
     */
    public void updateItems() {
        for (ItemGUI itemGUI : itemsDisplay) {
            itemGUI.updateLatestItemInformation();
        }
    }

    // main
    public static void main(String[] args) {
        if (args.length != 0) {
            ClientGUI.IP_Address = args[0];
        }
        launch(args);
    }
}
