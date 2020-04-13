package fxtarina;

import java.net.URL;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import fi.jyu.mit.ohj2.Mjonot;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.stage.Stage;
import tarinatilasto.Sarja;
import tarinatilasto.Tietue;

/**
 * @author Janne Taipalus ja Noora Jokela
 * @version 24.7.2019
 * @param <TYPE> Minkä tyyppisiä olioita käsitellään
 *
 */
public class TarinatilastoTietuemuokkausController<TYPE extends Tietue> implements ModalControllerInterface<TYPE>, Initializable {

    @FXML private ScrollPane panelTietue;
    @FXML private GridPane gridTietue;
    @FXML Label labelVirhe;
 
 
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        //
    }
 
 
    @FXML private void handleOK() {
        if(tietueKohdalla != null && tietueKohdalla.getAvain(tietueKohdalla.ekaKentta()).trim().contentEquals("")) {
            naytaVirhe("Ei saa olla tyhjä!");
            return;
        }
        ModalController.closeStage(labelVirhe);
    }  
 
 
    @FXML private void handleCancel() {
        tietueKohdalla = null;
        ModalController.closeStage(labelVirhe);
    }
 
 
    //-------------------------------------------------

    private TYPE tietueKohdalla;
    private static String sarjaNimi = null;
    private TextField[] edits;
    private int kentta = 0;
 
     
     /**
      * Luo tekstikentät GridPaneen.
      * @param gridTietue Mihin tietueen tiedot luodaan.
      * @param aputietue Aputietue.
      * @return Luodut tekstikentät.
      */
     public static <TYPE extends Tietue> TextField[] luoKentat(GridPane gridTietue, TYPE aputietue) {
         gridTietue.getChildren().clear();
         TextField[] edits = new TextField[aputietue.getKenttia()];
     
         for (int i = 0, k = aputietue.ekaKentta(); k < aputietue.getKenttia(); k++, i++) {
             Label label = new Label(aputietue.getKysymys(k));
             gridTietue.add(label, 0, i);
             TextField edit = new TextField();
             edits[k] = edit;
             edit.setId("e"+k);
             gridTietue.add(edit, 1, i);
         }
         return edits;
     }
 
     
     /**
      * Tyhjennetään tekstikentät.
      * @param edits Tyhjennettävät kentät.
      */
     public static void tyhjenna(TextField[] edits) {
         for(TextField edit : edits) {
             if(edit != null) edit.setText("");
         }
     }
     
 
     /**
      * Palautetaan komponentin id:stä saatava luku.
      * @param obj Tutkittava komponentti.
      * @param oletus Mikä arvo annetaan, jos id ei ole kunnollinen.
      * @return Komponentin id lukuna.
      */
     public static int getFieldId(Object obj, int oletus) {
         if ( !( obj instanceof Node)) return oletus;
         Node node = (Node)obj;
        return Mjonot.erotaInt(node.getId().substring(1), oletus);
     }

     
     /**
      * Tekee tarvittavat muut alustukset.
      */
     protected void alusta() {
         edits = luoKentat(gridTietue, tietueKohdalla);
         for (TextField edit : edits) {
             if (edit != null) edit.setOnKeyReleased( e -> kasitteleMuutosTietueeseen((TextField)(e.getSource())));
         }
     }

     
     private void setKentta(int kentta) {
         this.kentta = kentta;
     }

     
     private void naytaVirhe(String virhe) {
         if (virhe == null || virhe.isEmpty()) {
             labelVirhe.setText("");
             labelVirhe.getStyleClass().removeAll("virhe");
            return;
         }
         labelVirhe.setText(virhe);
         labelVirhe.getStyleClass().add("virhe");
     }


     /**
      * Käsittelee muutokset tietueeseen.
      * @param edit Tekstikenttä, jota muutetaan.
      */
     protected void kasitteleMuutosTietueeseen(TextField edit) {
         if (tietueKohdalla == null) return;
         int k = getFieldId(edit, tietueKohdalla.ekaKentta());
         String s = edit.getText();
         String virhe = null;
         virhe = tietueKohdalla.aseta(k, s);
         if (virhe == null) {
             Dialogs.setToolTipText(edit, "");
             edit.getStyleClass().removeAll("virhe");
             naytaVirhe(virhe);
         }
         else {
             Dialogs.setToolTipText(edit, virhe);
             edit.getStyleClass().add("virhe");
             naytaVirhe(virhe);
         }
     }


     @Override
     public TYPE getResult() {
         return tietueKohdalla;
     }

     
     /**
      * Mitä tehdään kun dialogi on näytetty
     */
     @Override
     public void handleShown() {
         kentta = Math.max(tietueKohdalla.ekaKentta(), Math.min(kentta, tietueKohdalla.getKenttia()-1));
         edits[kentta].requestFocus();
     }


     @Override
     public void setDefault(TYPE oletus) {
         tietueKohdalla = oletus;
         alusta();
         naytaTietue(edits, tietueKohdalla);
     }


     /**
      * Täytetään tarinan tiedot TextField-komponentteihin.
      * @param edits Taulukko Textfieldeistä, joihin tiedot laitetaan.
      * @param tietue Tietue jonka tiedot laitetaan kenttiin.
      */
     public static void naytaTietue(TextField[] edits, Tietue tietue) {
         if(tietue == null) return;
         for (int k = tietue.ekaKentta(); k < tietue.getKenttia(); k++) {
             if(k == 2 && tietue.getKenttia()>6) {
                 edits[2].setText(sarjaNimi);
                 edits[2].setDisable(true);
                 continue;
             }
             edits[k].setText(tietue.getAvain(k));
         }
     }


     /**
      * Luodaan tietueen kysymisdialogi ja palautetaan sama tietue muutettuna tai null.
      * @param modalityStage Mille ollaan modaalisia, null = sovellukselle.
      * @param oletus Mitä dataa näytetään oletuksena.
      * @param kentta Mikä kenttä saa fokuksen, kun se näytetään.
      * @param sarjanimi Sarjan nimi, annetaan tarinan yhteydessä, muulloin null.
      * @return null jos painetaan Cancel, muuten täytetty tietue.
      */
     public static<TYPE extends Tietue> TYPE kysyTietue(Stage modalityStage, TYPE oletus, int kentta, String sarjanimi) {
         sarjaNimi = sarjanimi;
         return ModalController.<TYPE, TarinatilastoTietuemuokkausController<TYPE>>showModal(TarinatilastoTietuemuokkausController.class.getResource("TarinatilastoTietuemuokkausGUI.fxml"), "Tarinatilasto", modalityStage, oletus, ctrl -> ctrl.setKentta(kentta));
     }
}