package fxtarina;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.print.PrinterJob;
import javafx.scene.web.WebEngine;

/**
 * @author Noora Jokela & Janne Taipalus
 * @version 2.5.2019
 */
public class TarinatilastoTulostusController implements ModalControllerInterface<String> {

    @FXML TextArea tulostusalue;
    
    
    @FXML private void handleOK()
    {
        ModalController.closeStage(tulostusalue);
    }
    
    
    @FXML private void handleTulosta() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if ( job != null && job.showPrintDialog(null) ) {
            WebEngine webEngine = new WebEngine();
            webEngine.loadContent("<pre>" + tulostusalue.getText() + "</pre>");
            job.endJob();
        }
    }
    
    
    @Override
    public String getResult()
    {
        return null;
    }
    
    
    @Override
    public void setDefault(String oletus)
    {
        //if(oletus==null) return;
        tulostusalue.setText(oletus);
    }
    
    
    /**
     * Mitä tehdään, kun dialogi on näytetty.
     */
    @Override
    public void handleShown()
    {
        //
    }
    
    
    /**
     * @return Alue, johon tulostetaan.
     */
    public TextArea getTextArea()
    {
        return tulostusalue;
    }
    
    
    /**
     * Näyttää tulostusalueessa tekstin.
     * @param tulostus Tulostettava teksti.
     * @return Kontrolleri, jolta voidaan pyytää lisää tietoja.
     */
    public static TarinatilastoTulostusController tulosta(String tulostus) {
        TarinatilastoTulostusController tulostusCtrl = ModalController.showModeless(TarinatilastoTulostusController.class.getResource("TulostusView.fxml"), "Tulostus", tulostus);
        return tulostusCtrl;
    }   
}