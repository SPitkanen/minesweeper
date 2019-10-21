/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import minesweeper.gui.GameView;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        GameView gv = new GameView(10, 10);

        VBox vb = new VBox();
        vb.getChildren().addAll(l, gv.getView());
        Scene scene = new Scene(vb);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
