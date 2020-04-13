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
import tarinatilasto.Tarina;

/**
 * @author Janne Taipalus ja Noora Jokela
 * @version 2.5.2019
 */
public class TarinamuokkausController implements ModalControllerInterface<Tarina>, Initializable {


    @FXML private ComboBoxChooser<Sarja> editSarja;
    @FXML private Label labelVirhe;
    @FXML private ScrollPane panelTarina;
    @FXML private GridPane gridTarina;
 
 
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        alusta();
    }
 
 
    @FXML private void handleOK() {
        if(tarinaKohdalla != null && tarinaKohdalla.getAvain(1).trim().contentEquals("")) {
            naytaVirhe("Nimi ei saa olla tyhjä!");
            return;
        }
        ModalController.closeStage(labelVirhe);
    }
 
 
    @FXML private void handleCancel() {
        tarinaKohdalla = null;
        ModalController.closeStage(labelVirhe);
    }
 
 
    //-------------------------------------------------

 
    private Tarina tarinaKohdalla;
    private static Tarina aputarina = new Tarina();
    private TextField[] edits;
    private int kentta = 0;
 
 
    /**
     * Luodaan kentät GridPaneen.
     * @param gridTarina Gridpane, johon laitetaan tarinan tiedot.
     * @return Luodut tekstikentät.
     */
    public static TextField[] luoKentat(GridPane gridTarina) {
        gridTarina.getChildren().clear();
        TextField[] edits = new TextField[aputarina.getKenttia()];
     
        for (int i = 0, k = aputarina.ekaKentta(); k < aputarina.getKenttia(); k++, i++) {
            Label label = new Label(aputarina.getKysymys(k));
            gridTarina.add(label, 0, i);
            TextField edit = new TextField();
            edits[k] = edit;
            edit.setId("e"+k);
            gridTarina.add(edit, 1, i);
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
     * Tekee tarvittavat alustukset.
     */
    protected void alusta() {
        edits = luoKentat(gridTarina);
        for (TextField edit : edits) {
            if (edit != null) {
                edit.setOnKeyReleased( e -> kasitteleMuutosTarinaan((TextField)(e.getSource())));
            }
            panelTarina.setFitToHeight(true);
        }
    }


    private void setKentta(int kentta) {
        this.kentta = kentta;
    }


    private void naytaVirhe(String virhe) {
        if (virhe == null || virhe.isEmpty()) {
            labelVirhe.setText("");
            labelVirhe.getStyleClass().removeAll("virhe");
        }
        labelVirhe.setText(virhe);
        labelVirhe.getStyleClass().add("virhe");
    }


    /**
     * Käsittelee muutokset tarinaan.
     * @param edit Tekstikenttä, jonka muutokset käsitellään.
     */
    protected void kasitteleMuutosTarinaan(TextField edit) {
        if (tarinaKohdalla == null) return;
        int k = getFieldId(edit, aputarina.ekaKentta());
        String s = edit.getText();
        String virhe = null;
        virhe = tarinaKohdalla.aseta(k, s);
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
    public Tarina getResult() {
        return tarinaKohdalla;
    }

    /**
     * Mitä tehdään kun dialogi on näytetty
     */
    @Override
    public void handleShown() {
        kentta = Math.max(aputarina.ekaKentta(), Math.min(kentta, aputarina.getKenttia()-1));
        edits[kentta].requestFocus();
    }


    @Override
    public void setDefault(Tarina oletus) {
        tarinaKohdalla = oletus;
        naytaTarina(edits, tarinaKohdalla);
    }
    

    /**
     * Täytetään tarinan tiedot TextField-komponentteihin.
     * @param edits Taulukko TextFieldeistä joihon laitetaan tiedot.
     * @param tarina Tarina jonka tiedot laitetaan kenttiin.
     */
    public static void naytaTarina(TextField[] edits, Tarina tarina) {
        if(tarina == null) return;
        for (int k = tarina.ekaKentta(); k < tarina.getKenttia(); k++) {
            edits[k].setText(tarina.getAvain(k));
        }
    }


    /**
     * Luodaan tarinan kysymisdialogi ja palautetaan sama tietue muutettuna tai null.
     * @param modalityStage Mille ollaan modaalisia, null = sovellukselle.
     * @param oletus Mitä dataa näytetään oletuksena.
     * @param kentta Mikä kenttä saa fokuksen kun näytetään.
     * @return null jos painetaan Cancel, muuten täytetty tietue.
     */
    public static Tarina kysyTarina(Stage modalityStage, Tarina oletus, int kentta) {
        return ModalController.<Tarina, TarinamuokkausController>showModal(TarinamuokkausController.class.getResource("TarinatilastoTarinamuokkausGUI.fxml"), "Tarinatilasto", modalityStage, oletus, ctrl -> ctrl.setKentta(kentta));
    }
}
