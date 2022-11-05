/*
Name: Andy Masuda
Email: andyxmasuda@gmail.com
Date: 12/9/2021
Project Name: Final Project - Minesweeper
Course: CS 17.11
Description: This is the application class for a javaFX program.
    Included are 2 methods: start and main. The start method sets
    the scene before the main function which launches the
    application. Closing the application will end the program.
 */
package edu.srjc.masuda.andy.Minesweeper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Minesweeper extends Application
{

    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Minesweeper.class.getResource("UILayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setOnCloseRequest(t ->
        {
            Platform.exit();
            System.exit(0);
        });
        stage.setTitle("Minesweeper");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}