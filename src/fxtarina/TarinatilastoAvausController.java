package fxtarina;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @author Noora Jokela & Janne Taipalus
 * @version 2.5.2019
 */
public class TarinatilastoAvausController implements ModalControllerInterface<String> {

    @FXML private TextField textVastaus;
    private String vastaus = null;
    
    
    @FXML private void handleOK() {
        vastaus = textVastaus.getText();
        ModalController.closeStage(textVastaus);
    }
    
    
    @FXML private void handleCancel() {
        ModalController.closeStage(textVastaus);
    }
    
    
    @Override
    public String getResult() {
        return vastaus;
    }
    
    
    @Override
    public void setDefault(String oletus) {
        textVastaus.setText(oletus);
    }
    
    
    /**
     * Mitä tehdään, kun dialogi on näytetty.
     */
    @Override
    public void handleShown() {
        textVastaus.requestFocus();
    }
    
    
    /**
     * Luodaan avausdialogi, jossa kysytään tiedoston nimeä, ja palautetaan annettu nimi tai null.
     * @param modalityStage Mille ollaan modaalisia.
     * @param oletus Mitä nimeä näytetään oletuksena.
     * @return null jos painetaan Cancel, muutoin annettu nimi
     */
    public static String kysyNimi(Stage modalityStage, String oletus)
    {
        return ModalController.showModal(TarinatilastoAvausController.class.getResource("AvausView.fxml"), "Tarinatilasto", modalityStage, oletus);
    }  
}