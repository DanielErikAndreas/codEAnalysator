package ui;

import generell.*;
import graphen.GraphFehlerverteilung;
import graphen.GraphMassageFehlerverteilung;
import graphen.GraphPausenlänge;
import graphen.GraphPausenverteilung;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Main {
  private static String version = "v1.0";
  private static NachrichtenManager nachrichtenManager;
  private static JTextField textFieldSelect;

  public static void main(String[] args) {
    //System.out.println(Integer.parseInt("0", 2) - Integer.parseInt("100010000100001000010", 2));
    //System.out.println(Integer.parseInt("FF", 16));
    //100010000100001000010

    //System.out.println(Fehler.Typ.size);

    /*
    String fonts[] =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    for ( int i = 0; i < fonts.length; i++ )
    {
      System.out.println(fonts[i]);
    }
    */

    //System.out.println("Ø");

    SetUp setUp = new SetUp();
    // (0) standard
    // (1) /home/studi/usrp-Dateien/fakeProtokoll_fehlerTest.txt
    // (2) /home/studi/usrp-Dateien/1001Bits_001.xml
    // (3) /home/studi/usrp-Dateien/fakeProtokoll_test_Einzelnachricht.txt
    // (4) /home/studi/BIT-Fehler_Statistik_Rohdaten/2017-11-30_1000000_Nachrichten_S1M_F433920KH_B50_P20ms.txt
    // (5) /home/studi/BIT-Fehler_Statistik_Rohdaten/2017-11-30_80000_Narichten_S1M_F433920KH_B100_P100ms.txt
    int setUpNummer = 1;


    JFrame frame = new JFrame("CodeAnalysator " + version);
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new InputFileFilefilter());


    //panelSetup.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    // Werte Nachrichten, Pause
    JPanel panelRamen = new JPanel(new GridBagLayout());
    panelRamen.setBorder(new EmptyBorder(10, 10, 10, 10));
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(5, 5, 5, 5);

    c.gridx = 0;
    c.gridy = 0;
    JButton buttonSelect = new JButton("Auswahl");
    buttonSelect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent _actionEvent) {
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
          textFieldSelect.setText(chooser.getSelectedFile().getAbsolutePath());
        }
      }
    });
    panelRamen.add(buttonSelect, c);

    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 2;
    textFieldSelect = new JTextField(45);
    textFieldSelect.setMargin(new Insets(3, 5, 4, 5));
    textFieldSelect.setText(setUp.getSelect(setUpNummer));
    panelRamen.add(textFieldSelect, c);

    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    JLabel labelNachrichten = new JLabel("Nachrichten");
    panelRamen.add(labelNachrichten, c);

    c.gridx = 1;
    c.gridy = 1;
    JTextField textFieldNachrichten = new JTextField(10);
    textFieldNachrichten.setMargin(new Insets(3, 5, 4, 5));
    textFieldNachrichten.setText(setUp.getNachrichten(setUpNummer));
    panelRamen.add(textFieldNachrichten, c);

    c.gridx = 0;
    c.gridy = 2;
    JLabel labelSollpause = new JLabel("Sollpause");
    panelRamen.add(labelSollpause, c);

    c.gridx = 1;
    c.gridy = 2;
    JTextField textFieldSollpause = new JTextField(10);
    textFieldSollpause.setMargin(new Insets(3, 5, 4, 5));
    textFieldSollpause.setText(setUp.getSollPause(setUpNummer));
    panelRamen.add(textFieldSollpause, c);

    c.gridx = 0;
    c.gridy = 3;
    JLabel labelSequenz = new JLabel("Sequenzraum");
    panelRamen.add(labelSequenz, c);

    c.gridx = 1;
    c.gridy = 3;
    JTextField textFieldSequenz = new JTextField(10);
    textFieldSequenz.setMargin(new Insets(3, 5, 4, 5));
    textFieldSequenz.setText(setUp.getSequenz(setUpNummer));
    panelRamen.add(textFieldSequenz, c);


    // checkBoxen
    c.gridx = 2;
    c.gridy = 1;
    c.gridheight = 3;
    JPanel panelCheckBoxen = new JPanel(new GridLayout(0, 2));
    panelCheckBoxen.setBorder(BorderFactory.createTitledBorder("Grafik generieren"));
    panelRamen.add(panelCheckBoxen, c);
    c.gridheight = 1;

    JCheckBox checkBoxStatistik = new JCheckBox("Statistik");
    checkBoxStatistik.setSelected(setUp.isStatistik(setUpNummer));
    panelCheckBoxen.add(checkBoxStatistik);

    JCheckBox checkBoxBitfehlerverteilung = new JCheckBox("Bitfehlerverteilung");
    checkBoxBitfehlerverteilung.setSelected(setUp.isFehlerSummenVerteilung(setUpNummer));
    panelCheckBoxen.add(checkBoxBitfehlerverteilung);

    JCheckBox checkBoxFehlerverteilung = new JCheckBox("Fehlerverteilung");
    checkBoxFehlerverteilung.setSelected(setUp.isFehlerVerteilung(setUpNummer));
    panelCheckBoxen.add(checkBoxFehlerverteilung);

    JCheckBox checkBoxPausenlänge = new JCheckBox("Pausenlänge");
    checkBoxPausenlänge.setSelected(setUp.isPausenlänge(setUpNummer));
    panelCheckBoxen.add(checkBoxPausenlänge);

    JCheckBox checkBoxPausenverteilung = new JCheckBox("Pausenverteilung");
    checkBoxPausenverteilung.setSelected(setUp.isPausenVerteilung(setUpNummer));
    panelCheckBoxen.add(checkBoxPausenverteilung);


    // Maske
    c.gridx = 0;
    c.gridy = 4;
    JLabel labelMaske = new JLabel("Maske");
    panelRamen.add(labelMaske, c);

    c.gridx = 1;
    c.gridy = 4;
    c.gridwidth = 2;
    JTextField textFieldMaske = new JTextField(45);
    textFieldMaske.setMargin(new Insets(3, 5, 4, 5));
    textFieldMaske.setText(setUp.getMaske(setUpNummer));
    panelRamen.add(textFieldMaske, c);

    // StartButton
    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 3;
    c.fill = 1;
    JButton buttonStart = new JButton("analysieren");
    panelRamen.add(buttonStart, c);


    frame.add(panelRamen);


    buttonStart.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent _actionEvent) {
        File file = new File(textFieldSelect.getText());
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
              JOptionPane.showMessageDialog(frame, "Sequenz: kein akzeptiertes Format!");
            }
            System.out.println(nachrichtenManager);
          } else {
            JOptionPane.showMessageDialog(frame, "Kein akzeptierter Datentyp!");
          }
        } else {
          JOptionPane.showMessageDialog(frame, "Kann Datei nicht finden!");
        }

      }
    });

    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
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

  public static boolean isNumber(String _s) {
    for (int i = 0; i < _s.length(); i++) {
      if (_s.charAt(i) < '0' || _s.charAt(i) > '9')
        return false;
    }
    return true;
  }


}
