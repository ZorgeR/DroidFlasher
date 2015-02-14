package com.zlab.DroidFlasher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    public String APP_NAME = "DroidFlasher";
    private Controller mController;
    public static Stage globalStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        globalStage = primaryStage;

        URL mainUIlocation = getClass().getResource("/layout/Main.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(mainUIlocation);

        mController = new Controller();
        mController.initialize(getClass().getResource("/layout/Main.fxml"), fxmlLoader.getResources());
        fxmlLoader.setController(mController);

        Parent root = fxmlLoader.load();

        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        startApp();
    }
    public void startApp(){

        mController.logToConsole("Initialize UI...\n");
        mController.initConfiguration();
        mController.initToggleBtn();
        mController.initBtn();

        mController.logToConsole("Initialize inventory...\n");

    }


}
