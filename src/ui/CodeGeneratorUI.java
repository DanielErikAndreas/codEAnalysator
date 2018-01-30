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
      //textFieldMaske.setText("");
      textFieldMaske.getStyleClass().add("textFeldlang");

      Label labelSequenzraum = new Label("Sequenzraum");
      labelSequenzraum.getStyleClass().add("labels");
      TextField textFieldSequenzraum = new SequenzTextFeld();
      //textFieldSequenzraum.setText("1000000");
      textFieldSequenzraum.getStyleClass().add("textFeldlang");


      Label labelFrei = new Label("");
      labelFrei.setPrefWidth(50);


      Button buttonGenerieren = new Button();
      buttonGenerieren.setText("generieren");
      buttonGenerieren.setOnAction(e -> {
        CodeGenerator codeGenerator = new CodeGenerator(new File(textFieldZiel.getText()));
        if(codeGenerator.setSequenz(textFieldSequenzraum.getText())){
          codeGenerator.setMuster(textFieldMaske.getText());
          codeGenerator.generate();
          //System.out.println("stop");
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

      gridPane.add(buttonGenerieren, 0, 3, 6, 1);


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
