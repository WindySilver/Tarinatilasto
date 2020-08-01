package fxtarina;

import static fxtarina.TarinatilastoTietuemuokkausController.getFieldId;
import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ListChooser;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.StringGrid;
import fi.jyu.mit.fxgui.TextAreaOutputStream;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import tarinatilasto.Osa;
import tarinatilasto.SailoException;
import tarinatilasto.Sarja;
import tarinatilasto.Tarina;
import tarinatilasto.Tarinatilasto;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;

/**
 * Luokka käyttöliittymän tapahtumien hoitamiseksi
 * @author Noora Jokela & Janne Taipalus
 * @version 1.8.2020
 */
public class TarinatilastoGUIController implements Initializable {
    
      @FXML private TextField tarinaHaku;
      @FXML private TextField osaHaku;
      @FXML private ComboBoxChooser<String> tarinahakuKentta;
      @FXML private ComboBoxChooser<String> osahakuKentta;
      @FXML private ComboBoxChooser<Sarja> chooserSarjat;
      @FXML private StringGrid<Osa> osaGrid;
      @FXML private GridPane gridTarina;
      @FXML private Label virheilmoitus;
      @FXML private ScrollPane panelTarina;
      @FXML private ListChooser<Tarina> chooserTarinat;
      
      
      @FXML private void handleTarinahaku() {
          String hakusana = tarinaHaku.getText();
          if (hakusana.isEmpty()) naytaVirhe(null);
          haeTarina(0);
      }
      
      
      @FXML private void handleOhjeet() {
          ModalController.showModal(TarinatilastoGUIController.class.getResource("TarinatilastoOhjeGUI.fxml"), "Tarinatilasto", null, "");
      }
      
      
      @FXML private void handleVaihdaTarinanSarjaa() {
          vaihdaTarinanSarjaa();
      }
      
      
      @FXML private void handleOsahaku() {
          String hakusana = osaHaku.getText();
          if (hakusana.isEmpty()) naytaVirhe(null);
          haeOsa();
      }
    
      
      @FXML private void handleMuokkaaSarjaa() {
        muokkaaSarjaa();
      }
          
    
      @FXML private void handleMuokkaaTarinaa() {
          muokkaaTarinaa(kentta);
      }
      
      
      @FXML private void handleMuokkaaOsia() {
          muokkaaOsaa();
      }
      
      
      @FXML private void handleUusiSarja() {
          uusiSarja();
      }
      
      
      @FXML private void handlePoistaSarja() {
          poistaSarja();
      }
      
      
      @FXML private void handleUusiTarina() {
          uusiTarina();
      }
      
      
      @FXML private void handleTallenna() {
          tallenna();
      }


    @FXML private void handleUusiOsa() {
          uusiOsa();
      }


    @FXML private void handlePoistaOsa() {
          poistaOsa();
      }
      
    
      @FXML private void handleAvaa() throws IOException {
          tallenna();
          avaa();
      }
      

      @FXML private void handlePoistaTarina() {
            poistaTarina();
        }
        
      
        @FXML private void handleTulosta() {
            TarinatilastoTulostusController tulostusCtrl = TarinatilastoTulostusController.tulosta(null); 
            tulostaValitut(tulostusCtrl.getTextArea());
        }

        
        //Poistettu käytöstä, koska komento jumittaa ohjelman ainakin Ubuntu 18.04:llä
        //ja sen avaamasta TIM-sivusta ei ole enää paljoakaan hyötyä, koska se vaatii kirjautumisen.
        @FXML private void handleApua() {
              apua();
        }
        
        
        @FXML private void handleLopeta() {
            tallenna();
            Platform.exit();
        }

        
      @FXML private void handleTietoja() {
          ModalController.showModal(TarinatilastoGUIController.class.getResource("TietojaView.fxml"), "Tarinatilasto", null, "");
        }


    //--------------------------------------------------------------------------------------------
    //Ei käyttöliittymään suoraan liittymää (@FXML-alkuista) koodia tästä eteenpäin!
      
      
      private Tarinatilasto tarinatilasto;
      private Tarina tarinaKohdalla;
      private Sarja sarjaKohdalla;
      private String tiedostonNimi = "";
      private TextField edits[];
      private int kentta = 0;
      private static Osa apuosa = new Osa();
      private static Tarina aputarina = new Tarina();
      
      
      @Override
      public void initialize(URL url, ResourceBundle bundle) {
          alusta();
      }
      
      
      private void tallenna() {
          try {
            tarinatilasto.tallenna();
          } catch (SailoException e) {
            e.printStackTrace();
          }
      }
      
      
      /**
       * Tarkistetaan, onko tallennus tehty.
       * @return true, jos saa sulkea sovelluksen, false jos ei saa.
       */
      public boolean voikoSulkea() {
          tallenna();
          return true;
      }
      
      
      /**
       * Avataan uusi tiedosto.
       * @return true tai false
       * @throws IOException jos tiedoston luvussa on ongelmia
       */
      public boolean avaa() throws IOException {
          String uusiNimi = TarinatilastoAvausController.kysyNimi(null, tiedostonNimi);
          if (uusiNimi == null) return false;
          alusta();
          lueTiedosto(uusiNimi);
          return true;        
      }
    
    
      private void muokkaaSarjaa() {
          if (sarjaKohdalla == null) return;
          try {
              Sarja sarja;
              sarja = TarinatilastoSarjaController.kysySarja(null, sarjaKohdalla.clone());
              if(sarja == null) return;
              tarinatilasto.korvaaTaiLisaa(sarja);
              haeSarja(sarja.getTunnusNro());
          }catch (CloneNotSupportedException e) {
              //
          }catch (SailoException e) {
              Dialogs.showMessageDialog(e.getMessage());
          }
      }
    
    
      private void muokkaaTarinaa(int k) {
          if (tarinaKohdalla == null) return;
          try {
              Tarina tarina;
              tarina = TarinatilastoTietuemuokkausController.kysyTietue(null, tarinaKohdalla.clone(), k, tarinatilasto.annaSarja(tarinaKohdalla.getSarjaNumero()).getNimi());
              if (tarina == null) return;
              tarinatilasto.korvaaTaiLisaa(tarina);
              haeTarina(tarina.getIdNumero());
          }catch(CloneNotSupportedException e) {
              //
          }catch (SailoException e) {
            Dialogs.showMessageDialog(e.getMessage());
          }
      }
    
    
      /**
       * Alustaa tietokantatiedoston lukemalla sen valitun nimisestä tiedostosta.
       * @param nimi Tiedosto, josta tarinoiden tiedot luetaan.
       * @return null jos onnistuu, muuten virhe tekstinä
       * @throws IOException jos tiedoston luvussa on ongelmia
       */
      protected String lueTiedosto(String nimi) throws IOException {
          tiedostonNimi=nimi;
          setTitle("Tiedosto: " + tiedostonNimi);
          try {
              tarinatilasto.lueTiedostosta(nimi);
              haeSarja(0);
              return null;
          } catch (SailoException e) {
              haeSarja(0);
              String virhe = e.getMessage();
              if (virhe != null) Dialogs.showMessageDialog(virhe);
              return virhe;
          }
      }
    
    
      /**
       * Asettaa käytettävän tarinatilaston.
       * @param tarinatilasto Tarinatilasto, jota käytetään tässä käyttöliittymässä
       */
      public void setTarinatilasto(Tarinatilasto tarinatilasto) {
          this.tarinatilasto=tarinatilasto;
          naytaTarina();
      }

      
      /**
       * Tulostaa listassa olevat tarinat tekstialueeseen.
       * @param teksti Alue, johon tulostetaan.
       */
      public void tulostaValitut(TextArea teksti) {
          try(PrintStream os = TextAreaOutputStream.getTextPrintStream(teksti)) {
              os.println("Tulostetaan kaikki tarinat");
              for(Tarina tarina : chooserTarinat.getObjects())
              {
                  tulosta(os, tarina);
                  os.println("\n\n");
              }
          }        
      }

    
      /**
       * Tulostaa valitun sarjan tarinoiden ja niiden osien tiedot.
       * @param os Tietovirta, johon tulostetaan.
       * @param tarina Tulostettava tarina.
       */
      public void tulosta(PrintStream os, final Tarina tarina) {
          os.println("-------------------------------");
          tarina.tulosta(os, sarjaKohdalla.getNimi());
          os.println("-------------------------------");
          List<Osa> osat = tarinatilasto.annaOsat(tarina);
          for (Osa osa: osat)
              osa.tulosta(os);
      }

    
      private void setTitle(String title)
      {
          ModalController.getStage(tarinaHaku).setTitle(title);
      }
    
    
      /**
       * Näyttää virheilmoituksen.
       * @param virhe Virheilmoitus, joka näytetään.
       */
      private void naytaVirhe(String virhe) {
          if (virhe == null || virhe.isEmpty()) { virheilmoitus.setText(""); virheilmoitus.getStyleClass().removeAll("virhe"); return; }
          virheilmoitus.setText(virhe);
          virheilmoitus.getStyleClass().add("virhe");
      }
      
      
      private void lisaaOsaHakuehdot() {
          tarinahakuKentta.add("Osan nimi", null);
          tarinahakuKentta.add("Osan sanamäärä", null);
          tarinahakuKentta.add("Osan sivumäärä", null);
      }
      
      
      /**
       * Tekee tarvittavat alustukset.
       */
      protected void alusta( ) {
          chooserTarinat.clear();
          chooserTarinat.addSelectionListener(e -> naytaTarina());
          chooserSarjat.clear();
          chooserSarjat.addSelectionListener(e -> naytaSarja());
          tarinahakuKentta.clear();
          osahakuKentta.clear();
          for (int k = aputarina.ekaKentta(); k < aputarina.getKenttia(); k++) tarinahakuKentta.add(aputarina.getKysymys(k), null);
          lisaaOsaHakuehdot();
          tarinahakuKentta.getSelectionModel().select(0);
          for(int k = apuosa.ekaKentta(); k<apuosa.getKenttia();k++) osahakuKentta.add(apuosa.getKysymys(k), null);
          osahakuKentta.getSelectionModel().clearAndSelect(0);
          
          edits = TarinatilastoTietuemuokkausController.luoKentat(gridTarina, aputarina);
          for (TextField edit : edits)
              if (edit != null) {
                  edit.setEditable(false);
                  edit.setOnMouseClicked(e -> { if (e.getClickCount() > 1)
                      if(edit == edits[2]) vaihdaTarinanSarjaa();
                      else muokkaaTarinaa(getFieldId(e.getSource(), 0)); });
                  edit.focusedProperty().addListener((a,o,n)-> kentta = getFieldId(edit, kentta));
                  edit.setOnKeyPressed(( e -> { if (e.getCode() == KeyCode.F2 ) muokkaaTarinaa(kentta);} ));
              }
          
          int ensimmainen = apuosa.ekaKentta();
          int lkm = apuosa.getKenttia();
          String[] headings = new String[lkm-ensimmainen];
          for (int i =0, k = ensimmainen; k<lkm;i++, k++) headings[i] = apuosa.getKysymys(k);
          osaGrid.initTable(headings);
          osaGrid.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
          osaGrid.setEditable(false);
          osaGrid.setPlaceholder(new Label("Ei vielä osia."));
          osaGrid.setColumnSortOrderNumber(1);
          osaGrid.setColumnSortOrderNumber(2);
          osaGrid.setColumnWidth(0, 60);
          osaGrid.setColumnWidth(2, 80);
          osaGrid.setColumnWidth(3, 80);
          
          osaGrid.setOnMouseClicked( e -> {if (e.getClickCount()> 1) muokkaaOsaa();});
          osaGrid.setOnKeyPressed( e -> {if ( e.getCode() == KeyCode.F2 ) muokkaaOsaa();});
      }
    
    
      private void vaihdaTarinanSarjaa() {
          if (tarinaKohdalla == null) return;
          TarinatilastoSarjavaihtoController.kysyTarina(null, tarinaKohdalla, tarinatilasto.annaSarjat(), 0);
          haeTarina(tarinaKohdalla.getIdNumero());
      }
    
      
      /**
       * Näyttää ComboBoxChooserissa valitun sarjan tiedot.
       */
      protected void naytaSarja() {
          sarjaKohdalla = chooserSarjat.getSelectedObject();
          if (sarjaKohdalla == null) return;
          haeTarina(0);
      }

    
      /**
       * Laittaa listasta valitun tarinan tiedot tekstikenttiin.
       */
      protected void naytaTarina() {
          tarinaKohdalla = chooserTarinat.getSelectedObject();
          
          if (tarinaKohdalla == null) return;
          TarinatilastoTietuemuokkausController.naytaTietue(edits, tarinaKohdalla);
          edits[2].setDisable(false);
          edits[2].setText(sarjaKohdalla.getNimi());
          naytaOsat(tarinaKohdalla);
      }
    
    
      private void muokkaaOsaa() {
          int r = osaGrid.getRowNr();
          if (r<0) return;
          Osa osa = osaGrid.getObject();
          if (osa == null) return;
          int k = osaGrid.getColumnNr() + osa.ekaKentta();
          try {
              osa = TarinatilastoTietuemuokkausController.kysyTietue(null, osa.clone(), k, null);
              if (osa == null) return;
              tarinatilasto.korvaaTaiLisaa(osa);
              naytaOsat(tarinaKohdalla);
              osaGrid.selectRow(r);
          }catch (CloneNotSupportedException e) {
              //
          }catch (SailoException e) {
              Dialogs.showMessageDialog("Ongelmia lisäämisessä: " + e.getMessage()); 
          }
      }
    
    
      /**
       * Näyttää tarinan osat.
       * @param tarina Tarina, jonka osat näytetään.
       */
      private void naytaOsat(Tarina tarina) {
          osaGrid.clear();
          if(tarina == null) return;
        
          List<Osa> osat = tarinatilasto.annaOsat(tarina);
          if(osat.size()== 0) return;
          for(Osa osa: osat)
          {
              naytaOsa(osa);
          }
      }


      private void naytaOsa(Osa osa) {
          String[] rivi = osa.toString().split("\\|");
          osaGrid.add(osa, rivi[2], rivi[3], rivi[4], rivi[5]);
      }


      /**
       * Hakee sarjan tiedot listaan.
       * @param tunnusNro Tunnusnumero sarjalle joka aktivoidaan haun jälkeen.
       */
      protected void haeSarja(int tunnusNro) {
          chooserSarjat.clear();
        
          int index = 0;
          Sarja sarja;
          for (int i = 0;i<tarinatilasto.getSarjojenPituus();i++) {
              try {
                  sarja = tarinatilasto.annaSarja(i);
              }catch (IndexOutOfBoundsException e) {
                  continue;
              }
              if(sarja.getTunnusNro() == tunnusNro) index=i;
              chooserSarjat.add(sarja.getNimi(), sarja);
          }       
          chooserSarjat.setSelectedIndex(index);       
      }

    
      /**
       * Hakee tarinan tiedot listaan
       * @param tnr Tunnusnumero tarinalle joka aktivoidaan haun jälkeen
       */
      protected void haeTarina(int tnr) {
          int tnro = tnr;
          if ( tnro <= 0) {
              Tarina kohdalla = tarinaKohdalla;
              if (kohdalla != null) tnro = kohdalla.getIdNumero();
          }
          int k = tarinahakuKentta.getSelectionModel().getSelectedIndex() + aputarina.ekaKentta();
          String ehto = tarinaHaku.getText();
          if (ehto.indexOf('*') < 0) ehto = "*" + ehto + "*";
        
          chooserTarinat.clear();
          sarjaKohdalla = chooserSarjat.getSelectedObject();
          
          Collection<Tarina> tarinat;
          int index = 0;
          
          if(k>aputarina.getKenttia()-1) {
              k = k-(aputarina.getKenttia()-aputarina.ekaKentta()-apuosa.ekaKentta());
              try {
                  Collection<Osa> osat = tarinatilasto.etsiOsa(ehto, k);
                  List<Integer> tarinaIDt = new ArrayList<>();
                  for (Osa osa:osat) {
                      int ID = osa.getTarinaNro();
                      if(eiOle(ID, tarinaIDt)) tarinaIDt.add(ID);
                  }
                  Collections.sort(tarinaIDt);
                  k = 1;
                  tarinat = tarinatilasto.etsiTarina(tarinaIDt, k);
                  int i = 0;
                  for (Tarina tarina:tarinat) {
                      if (tarina.getIdNumero() == tnro) index = i;
                      if(tarina.getSarjaNumero() == sarjaKohdalla.getTunnusNro()) chooserTarinat.add(tarina.getAvain(1), tarina);
                      i++;
                      }
              
                  }catch(SailoException ex) {
                      Dialogs.showMessageDialog("Tarinan hakemisessa ongelmia! " + ex.getMessage());
                  }
              }
              else {
                  try {
                      tarinat = tarinatilasto.etsiTarina(ehto, k);
                      int i = 0;
                      for (Tarina tarina:tarinat) {
                          if (tarina.getIdNumero() == tnro) index = i;
                          if(tarina.getSarjaNumero() == sarjaKohdalla.getTunnusNro()) chooserTarinat.add(tarina.getAvain(1), tarina);
                          i++;
                      }
                  } catch (SailoException ex) {
                      Dialogs.showMessageDialog("Tarinan hakemisessa ongelmia! " + ex.getMessage());
                  }
              }
              chooserTarinat.setSelectedIndex(index);
          }
      
      
          private boolean eiOle(int iD, List<Integer> lista) {
              for(int i = 0;i<lista.size();i++) {
                  if(iD==lista.get(i)) return false;
              }
              return true;
          }


          /**
           * Hakee osan annetulla hakusanalla.
           */
          protected void haeOsa() {
              Tarina kohdalla = tarinaKohdalla;
              if(tarinaKohdalla == null) return;
              int k = osahakuKentta.getSelectionModel().getSelectedIndex() + apuosa.ekaKentta();
              String ehto = osaHaku.getText();
              if (ehto.indexOf('*') < 0) ehto = "*" + ehto + "*";
              
              osaGrid.clear();
              Collection<Osa> osat;
              try {
                  osat = tarinatilasto.etsiOsa(ehto, k, kohdalla.getIdNumero());
                  for(Osa osa:osat) {
                      naytaOsa(osa);
                  }
              }catch (SailoException ex) {
                  Dialogs.showMessageDialog("Osan hakemisessa ongelmia!" + ex.getMessage());
              }
          }
    
    
          /**
           * Luo uuden sarjan.
           */
          private void uusiSarja() {
              Sarja uusi = new Sarja();
              try {
                  uusi = TarinatilastoSarjaController.kysySarja(null, uusi);
                  if (uusi == null) return;
                  uusi.rekisteroi();
                  tarinatilasto.lisaa(uusi);
              } catch (SailoException ex) {
                  Dialogs.showMessageDialog("Ongelmia uuden luomisessa " + ex.getMessage());
                  return;
              }
              haeSarja(uusi.getTunnusNro());        
          }
    
        
          /**
           * Luo uuden osan.
           */
          private void uusiOsa() {
              if (tarinaKohdalla == null) return;      
    
              try {
                  Osa uusi = new Osa(tarinaKohdalla.getIdNumero());
                  uusi = TarinatilastoTietuemuokkausController.kysyTietue(null, uusi, 0, null);
                  if (uusi == null) return;
                  uusi.rekisteroi();
                  tarinatilasto.lisaa(uusi);
                  naytaOsat(tarinaKohdalla);
                  osaGrid.selectRow(1000);
              }catch (SailoException e)
              {
                  Dialogs.showMessageDialog("Lisääminen epäonnistui: " + e.getMessage()); 
              }
          }
        
        
          /**
           * Luo uuden tarinan.
           */
          private void uusiTarina() {
              if(sarjaKohdalla==null) {
                  Dialogs.showMessageDialog("Ei sarjoja, joihin lisätä tietoja! Luo sarja ensin!");
                  return;
              }
              try {
                  Tarina uusi = new Tarina();
                  uusi = TarinatilastoTietuemuokkausController.kysyTietue(null, uusi, 1, sarjaKohdalla.getNimi());
                  uusi.rekisteroi();
                  uusi.aseta(2,sarjaKohdalla.getTunnusNro()+"");
                  tarinatilasto.lisaa(uusi);
                  haeTarina(uusi.getIdNumero());
              }catch (SailoException ex) {
                  Dialogs.showMessageDialog("Ongelmia uuden luomisessa " + ex.getMessage());
                  return;
              }
          }
        
          
          /**
           * Poistetaan osien taulukosta valittu osa.
           */
          private void poistaOsa() {
              int rivi = osaGrid.getRowNr();
              if (rivi < 0) return;
              Osa osa = osaGrid.getObject();
              if ( osa == null) return;
              tarinatilasto.poistaOsa(osa);
              naytaOsat(tarinaKohdalla);
              int osia = osaGrid.getItems().size();
              if (rivi >= osia) rivi = osia - 1;
              osaGrid.getFocusModel().focus(rivi);
              osaGrid.getSelectionModel().select(rivi);
          }
        
          
          private void poistaTarina() {
              Tarina tarina = tarinaKohdalla;
              if (tarina == null) return;
              if (!Dialogs.showQuestionDialog("Poisto", "Poistetaanko tarina: " + tarina.getAvain(1) + "?", "Kyllä", "Ei")) return;
              tarinatilasto.poistaTarina(tarina);
              int index = chooserTarinat.getSelectedIndex();
              haeTarina(0);
              chooserTarinat.setSelectedIndex(index);
          }
        
        
          private void poistaSarja() {
              Sarja sarja = sarjaKohdalla;
              if(sarja == null) return;
              if (!Dialogs.showQuestionDialog("Poisto", "Poistetaanko sarja \"" + sarja.getNimi() + "\"? Poistat samalla kaikki sarjan tarinat!", "Kyllä", "Ei")) return;
              int sarjaID = sarja.getTunnusNro();
              for (int i = 0;i<tarinatilasto.getTarinoita();i++) {
                  Tarina tarina = tarinatilasto.annaTarina(i);
                  if (tarina.getIdNumero() == sarjaID) {
                      tarinatilasto.poistaTarina(tarina);
                      i--;
                  }
              }
              tarinatilasto.poistaSarja(sarja);
              int index = chooserSarjat.getSelectedIndex();
              haeSarja(0);
              chooserSarjat.setSelectedIndex(index);
              haeTarina(0);
          }
        
          
          /**
           * Näytetään Tarinatilaston suunnitelma erillisessä selaimessa.
           */
          private void apua() {
              Desktop desktop = Desktop.getDesktop();
              try {
                  URI uri = new URI("https://tim.jyu.fi/view/kurssit/tie/ohj2/2019k/ht/noemjoke");
                  desktop.browse(uri);
              } catch (URISyntaxException e) {
                  return;
              } catch (IOException e) {
                  return;
              }
          }
}