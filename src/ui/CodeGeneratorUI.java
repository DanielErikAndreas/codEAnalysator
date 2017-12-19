package ui;

import generell.CodeGenerator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class CodeGeneratorUI {
  public static boolean isAtiv = false;
  public static String currentPath = System.getProperty("user.home");

  private TextField textFieldBitFehlt;
  private TextField textFieldBitGewechselt;
  private TextField textFieldBitZuViel;


  public void getWindow(Application _application) {
    if (!isAtiv) {
      isAtiv = true;
      Stage stage = new Stage();
      VBox box = new VBox();

      Scene scene = new Scene(box);
      stage.setTitle("codEGenerator");
      scene.getStylesheets().add(_application.getClass().getResource("root.css").toExternalForm());

      GridPane gridPane = new GridPane();
      gridPane.getStyleClass().add("pane");

      Button buttonZiel = new Button("Auswahl");
      HBox.setHgrow(buttonZiel, Priority.ALWAYS);
      buttonZiel.setMaxWidth(Double.MAX_VALUE);
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File(currentPath));
      TextField textFieldZiel = new TextField();
      textFieldZiel.setText(currentPath + File.separator + "generatedCode.txt");
      textFieldZiel.getStyleClass().add("textFeldlang");
      buttonZiel.setOnAction(e -> {
        File selectesFile = fileChooser.showSaveDialog(stage);
        if (selectesFile != null) {
          textFieldZiel.setText(returnTextFileString(selectesFile));
          fileChooser.setInitialDirectory(selectesFile.getParentFile());
        }
      });

      Label labelMaske = new Label("Maske");
      labelMaske.getStyleClass().add("labels");
      TextField textFieldMaske = new MaskeTextFeld();
      textFieldMaske.setText("101010101010101011111111XXXXXXXX111100001010111101101100111100000101");
      textFieldMaske.getStyleClass().add("textFeldlang");

      Label labelSequenzraum = new Label("Sequenzraum");
      labelSequenzraum.getStyleClass().add("labels");
      TextField textFieldSequenzraum = new SequenzTextFeld();
      textFieldSequenzraum.setText("1000000");
      textFieldSequenzraum.getStyleClass().add("textFeldlang");


      // Fehlerangabe
      Label labelFehlerAnzahl = new Label("Fehleranzahl");
      labelFehlerAnzahl.getStyleClass().add("labels");

      Label labelEinBitFehler = new Label("Ein Bit Fehler");
      labelEinBitFehler.getStyleClass().add("labels");
      TextField textFieldEinBitFehler = new NummerTextFeld();
      textFieldEinBitFehler.setText("30");
      textFieldEinBitFehler.getStyleClass().add("textFeld");

      Label labelZweiBitFehler = new Label("Zwei Bit Fehler");
      labelZweiBitFehler.getStyleClass().add("labels");
      TextField textFieldZweiBitFehler = new NummerTextFeld();
      textFieldZweiBitFehler.setText("20");
      textFieldZweiBitFehler.getStyleClass().add("textFeld");

      Label labelDreiBitFehler = new Label("Drei Bit Fehler");
      labelDreiBitFehler.getStyleClass().add("labels");
      TextField textFieldDreiBitFehler = new NummerTextFeld();
      textFieldDreiBitFehler.setText("10");
      textFieldDreiBitFehler.getStyleClass().add("textFeld");

      Label labelFrei = new Label("");
      labelFrei.setPrefWidth(50);

      // Fehleranteil
      Label labelFehlerAnteil = new Label("Anteil von 100");
      labelFehlerAnteil.getStyleClass().add("labels");

      Label labelBitFehlt = new Label("Bit fehlt");
      labelBitFehlt.getStyleClass().add("labels");
      textFieldBitFehlt = new NummerTextFeld();
      textFieldBitFehlt.setText("50");
      textFieldBitFehlt.getStyleClass().add("textFeld");

      Label labelZweiBitGewechselt = new Label("Bit gekippt");
      labelZweiBitGewechselt.getStyleClass().add("labels");
      textFieldBitGewechselt = new NummerTextFeld();
      textFieldBitGewechselt.setText("25");
      textFieldBitGewechselt.getStyleClass().add("textFeld");

      Label labelBitZuViel = new Label("Bit zu viel");
      labelBitZuViel.getStyleClass().add("labels");
      textFieldBitZuViel = new NummerTextFeld();
      textFieldBitZuViel.setText("25");
      textFieldBitZuViel.getStyleClass().add("textFeld");


      Button buttonGenerieren = new Button();
      buttonGenerieren.setText("generieren");
      buttonGenerieren.setOnAction(e -> {
        CodeGenerator codeGenerator = new CodeGenerator(new File(textFieldZiel.getText()));
        if(codeGenerator.setSequenz(textFieldSequenzraum.getText())){
          codeGenerator.setWerte(
                  Integer.parseInt(textFieldEinBitFehler.getText()),
                  Integer.parseInt(textFieldZweiBitFehler.getText()),
                  Integer.parseInt(textFieldDreiBitFehler.getText()),
                  Integer.parseInt(textFieldBitFehlt.getText()),
                  Integer.parseInt(textFieldBitGewechselt.getText()),
                  Integer.parseInt(textFieldBitZuViel.getText()));
          codeGenerator.setMuster(textFieldMaske.getText());
          codeGenerator.generate();
        } else {
          MainFX.warnung("Sequenz: kein akzeptiertes Format!");
        }
      });
      HBox.setHgrow(buttonGenerieren, Priority.ALWAYS);
      buttonGenerieren.setMaxWidth(Double.MAX_VALUE);

      gridPane.add(buttonZiel, 0, 0);
      gridPane.add(textFieldZiel, 1, 0, 4, 1);

      gridPane.add(labelMaske, 0, 1);
      gridPane.add(textFieldMaske, 1, 1, 4, 1);
      gridPane.add(labelSequenzraum, 0, 2);
      gridPane.add(textFieldSequenzraum, 1, 2, 4, 1);

      gridPane.add(labelFrei, 2, 3);

      gridPane.add(labelFehlerAnzahl, 1, 3);
      gridPane.add(labelEinBitFehler, 0, 4);
      gridPane.add(textFieldEinBitFehler, 1, 4);
      gridPane.add(labelZweiBitFehler, 0, 5);
      gridPane.add(textFieldZweiBitFehler, 1, 5);
      gridPane.add(labelDreiBitFehler, 0, 6);
      gridPane.add(textFieldDreiBitFehler, 1, 6);

      gridPane.add(labelFehlerAnteil, 4, 3);
      gridPane.add(labelBitFehlt, 3, 4);
      gridPane.add(textFieldBitFehlt, 4, 4);
      gridPane.add(labelZweiBitGewechselt, 3, 5);
      gridPane.add(textFieldBitGewechselt, 4, 5);
      gridPane.add(labelBitZuViel, 3, 6);
      gridPane.add(textFieldBitZuViel, 4, 6);

      gridPane.add(buttonGenerieren, 0, 7, 6, 1);


      box.getChildren().add(gridPane);
      stage.setScene(scene);
      stage.setResizable(false);
      stage.show();
      stage.setOnCloseRequest(e -> {
        isAtiv = false;
      });
    }
  }

  public static String returnTextFileString(File _file) {
    String fileNameKomplett = _file.getAbsolutePath();
    if(!fileNameKomplett.matches(".*.txt")) {
      fileNameKomplett += ".txt";
    }
    return fileNameKomplett;
  }

}
