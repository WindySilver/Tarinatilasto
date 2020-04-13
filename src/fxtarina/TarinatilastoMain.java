package fxtarina;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import tarinatilasto.Tarinatilasto;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;


/**
 * Pääohjelma tarinatilasto-ohjelman käynnistämiseksi
 * @author Noora Jokela & Janne Taipalus
 * @version 2.5.2019
 */
public class TarinatilastoMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader ldr = new FXMLLoader(getClass().getResource("TarinatilastoGUIView.fxml"));
            final Pane root = ldr.load();
            final TarinatilastoGUIController tarinatilastoCtrl = (TarinatilastoGUIController) ldr.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("tarinatilasto.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Tarinatilasto");
            primaryStage.show();
            primaryStage.setOnCloseRequest((event) -> {
                if (!tarinatilastoCtrl.voikoSulkea()) event.consume();
            });
            
            Tarinatilasto tarinatilasto = new Tarinatilasto();
            tarinatilastoCtrl.setTarinatilasto(tarinatilasto);
            
            Application.Parameters params = getParameters();
            if (params.getRaw().size()>0) tarinatilastoCtrl.lueTiedosto(params.getRaw().get(0));
            else if(!tarinatilastoCtrl.avaa()) Platform.exit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Käynnistetään käyttöliittymä
     * @param args Ei käytössä
     */
    public static void main(String[] args) {
        launch(args);
    }
}