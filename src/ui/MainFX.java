package ui;

import generell.GrafikBuilder;
import generell.NachrichtenManager;
import generell.NachrichtenParser;
import generell.SetUp;
import graphen.GraphFehlerverteilung;
import graphen.GraphMassageFehlerverteilung;
import graphen.GraphPausenlänge;
import graphen.GraphPausenverteilung;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;

public class MainFX extends Application {
  private static final String name = "codEAnalysator";
  private static final String version = "v1.0";

  private static NachrichtenManager nachrichtenManager;

  private static FileChooser fileChooser;

  private static TextField textFieldSelect;
  private static TextField textFieldNachrichten;
  private static TextField textFieldSollpause;
  private static TextField textFieldSequenz;
  private static TextField textFieldMaske;

  private static CheckBox checkBoxStatistik;
  private static CheckBox checkBoxBitfehlerverteilung;
  private static CheckBox checkBoxFehlerverteilung;
  private static CheckBox checkBoxPausenlänge;
  private static CheckBox checkBoxPausenverteilung;


  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    SetUp setUp = new SetUp();
    // (0) standard
    // (1) /home/studi/usrp-Dateien/fakeProtokoll_fehlerTest.txt
    // (2) /home/studi/usrp-Dateien/1001Bits_001.xml
    // (3) /home/studi/usrp-Dateien/fakeProtokoll_test_Einzelnachricht.txt
    // (4) /home/studi/BIT-Fehler_Statistik_Rohdaten/2017-11-30_1000000_Nachrichten_S1M_F433920KH_B50_P20ms.txt
    // (5) /home/studi/BIT-Fehler_Statistik_Rohdaten/2017-11-30_80000_Narichten_S1M_F433920KH_B100_P100ms.txt
    int setUpNummer = 6;


    StackPane root = new StackPane();
    //Scene scene = new Scene(root, 300, 250);
    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("root.css").toExternalForm());

    GridPane gridpane = new GridPane();
    gridpane.getStyleClass().add("pane");


    Button buttonSelect = new Button();
    buttonSelect.setText("Auswahl");
    HBox.setHgrow(buttonSelect, Priority.ALWAYS);
    buttonSelect.setMaxWidth(Double.MAX_VALUE);
    fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(".txt oder .xml", "*.txt", "0.xml");
    fileChooser.getExtensionFilters().add(extFilter);
    buttonSelect.setOnAction(e -> {
      File selectesFile = fileChooser.showOpenDialog(primaryStage);
      if (selectesFile != null)
        textFieldSelect.setText(fileChooser.showOpenDialog(primaryStage).getAbsolutePath());
    });
    textFieldSelect = new TextField();
    textFieldSelect.setText(setUp.getSelect(setUpNummer));
    textFieldSelect.getStyleClass().add("textFeldlang");

    Label labelNachrichten = new Label("Nachrichten");
    labelNachrichten.getStyleClass().add("labels");
    textFieldNachrichten = new TextField();
    textFieldNachrichten.setText(setUp.getNachrichten(setUpNummer));
    textFieldNachrichten.getStyleClass().add("textFeld");

    Label labelSollpause = new Label("Sollpause");
    labelSollpause.getStyleClass().add("labels");
    textFieldSollpause = new TextField();
    textFieldSollpause.setText(setUp.getSollPause(setUpNummer));
    textFieldSollpause.getStyleClass().add("textFeld");

    Label labelSequenz = new Label("Sequenzraum");
    labelSequenz.getStyleClass().add("labels");
    textFieldSequenz = new TextField();
    textFieldSequenz.setText(setUp.getSequenz(setUpNummer));
    textFieldSequenz.getStyleClass().add("textFeld");

    GridPane checkGridpane = new GridPane();
    checkGridpane.getStyleClass().add("paneCheckBox");
    checkBoxStatistik = new CheckBox("Statistik");
    checkBoxStatistik.setSelected(setUp.isStatistik(setUpNummer));
    checkBoxBitfehlerverteilung = new CheckBox("Bitfehlerverteilung");
    checkBoxBitfehlerverteilung.setSelected(setUp.isBitfehlerVerteilung(setUpNummer));
    checkBoxFehlerverteilung = new CheckBox("Fehlerverteilung");
    checkBoxFehlerverteilung.setSelected(setUp.isFehlerVerteilung(setUpNummer));
    checkBoxPausenlänge = new CheckBox("Pausenlänge");
    checkBoxPausenlänge.setSelected(setUp.isPausenlänge(setUpNummer));
    checkBoxPausenverteilung = new CheckBox("Pausenverteilung");
    checkBoxPausenverteilung.setSelected(setUp.isPausenVerteilung(setUpNummer));
    checkGridpane.add(checkBoxStatistik, 0, 0);
    checkGridpane.add(checkBoxBitfehlerverteilung, 1, 0);
    checkGridpane.add(checkBoxFehlerverteilung, 2, 0);
    checkGridpane.add(checkBoxPausenlänge, 0, 1);
    checkGridpane.add(checkBoxPausenverteilung, 1, 1);

    Label labelMaske = new Label("Maske");
    labelMaske.getStyleClass().add("labels");
    textFieldMaske = new TextField();
    textFieldMaske.setText(setUp.getMaske(setUpNummer));
    textFieldMaske.getStyleClass().add("textFeldlang");

    Button buttonStart = new Button();
    buttonStart.setText("analysieren");
    buttonStart.setOnAction(e -> {
      analysieren();
    });
    HBox.setHgrow(buttonStart, Priority.ALWAYS);
    buttonStart.setMaxWidth(Double.MAX_VALUE);


    gridpane.add(buttonSelect, 0, 0);
    gridpane.add(textFieldSelect, 1, 0, 2, 1);
    gridpane.add(labelNachrichten, 0, 1);
    gridpane.add(textFieldNachrichten, 1, 1);
    gridpane.add(labelSollpause, 0, 2);
    gridpane.add(textFieldSollpause, 1, 2);
    gridpane.add(labelSequenz, 0, 3);
    gridpane.add(textFieldSequenz, 1, 3);

    gridpane.add(checkGridpane, 2, 1, 1, 3);

    gridpane.add(labelMaske, 0, 4);
    gridpane.add(textFieldMaske, 1, 4, 2, 1);

    gridpane.add(buttonStart, 0, 5, 3, 1);

    root.getChildren().add(gridpane);
    primaryStage.setTitle(name + " " + version);
    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void analysieren() {
    File file = new File(textFieldSelect.getText());
    Alert alert;
    if (file.isFile()) {
      String fileTyp = NachrichtenParser.getFileTyp(file);
      if (fileTyp.equals("txt") || fileTyp.equals("xml")) {
        nachrichtenManager = new NachrichtenManager(file);
        nachrichtenManager.setSollPaketAnzahl(Integer.parseInt(textFieldNachrichten.getText()));
        nachrichtenManager.setSollPause(Integer.parseInt(textFieldSollpause.getText()));
        if (nachrichtenManager.setSequenz(textFieldSequenz.getText())) {
          nachrichtenManager.generate(textFieldMaske.getText());
          nachrichtenManager.makeStatista(wechselDateiEndung(file, "_STATISTIK.txt"));
          GrafikBuilder grafikBuilder = new GrafikBuilder(nachrichtenManager);
          grafikBuilder.setStatistik(checkBoxStatistik.isSelected());

          if (checkBoxBitfehlerverteilung.isSelected()) {
            grafikBuilder.addGraph(new GraphMassageFehlerverteilung(nachrichtenManager));
          }
          if (checkBoxFehlerverteilung.isSelected()) {
            grafikBuilder.addGraph(new GraphFehlerverteilung(nachrichtenManager));
          }
          if (checkBoxPausenlänge.isSelected()) {
            grafikBuilder.addGraph(new GraphPausenlänge(nachrichtenManager));
          }
          if (checkBoxPausenverteilung.isSelected()) {
            grafikBuilder.addGraph(new GraphPausenverteilung(nachrichtenManager));
          }

          grafikBuilder.makeGrafik(wechselDateiEndung(file, "_GRAFIK.png"));
        } else {
          alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("Warnung");
          alert.setHeaderText("Sequenz: kein akzeptiertes Format!");
          alert.setContentText(null);
          alert.showAndWait();
        }
        System.out.println(nachrichtenManager);
      } else {
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warnung");
        alert.setHeaderText("Kein akzeptierter Datentyp!");
        alert.setContentText(null);
        alert.showAndWait();
      }
    } else {
      alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Warnung");
      alert.setHeaderText("Kann Datei nicht finden!");
      alert.setContentText(null);
      alert.showAndWait();
    }
  }

  public static File wechselDateiEndung(File _file, String _typ) {
    String absolutPath = _file.getAbsolutePath();
    int ende = absolutPath.length();
    int anfang = 0;
    for (int i = ende - 1; i >= 0; i--) {
      if (absolutPath.charAt(i) == '.') {
        anfang = i;
        break;
      }
    }
    String neuPath = absolutPath.substring(0, anfang);
    return new File(neuPath + _typ);
  }


}