package graphen;

import fehler.Fehler;
import generell.Message;
import generell.NachrichtenManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class GraphSequenzen extends Graph {
  private int amplitude[];
  private int amplitudenAnzahl[];
  private int amplitudenFarben[];

  private static final String farbBedeutung[] = {"einmalig", "doppelt", "dreifach", "vierfach"};
  private static final int FARBEN[] = {
          0x0080ff, // blau
          0x00994c, // dunkelgrün
          0xff007f, // maganta
          0xff8000  // orange
  };

  public GraphSequenzen(NachrichtenManager _nachrichtenManager) {
    super(_nachrichtenManager);
    ArrayList<Message> messages = nachrichtenManager.getNachrichtenParser().getMessages();
    amplitude = new int[messages.size()];
    amplitudenFarben  = new int[messages.size()];
    ausdehnungX = messages.size();

    maxYwert = 0;
    for (int i = 0; i < amplitude.length; i++) {
      if (messages.get(i).countFehler(Fehler.Typ.NICHT_EXISTENT, Fehler.Typ.KEINE_ÜBEREINSTIMMUNG) == 0) {
        amplitude[i] = messages.get(i).getSequenzNummer() + 1;
        if (amplitude[i] > maxYwert) {
          maxYwert = amplitude[i];
        }
      }
    }

    amplitudenAnzahl = new int[maxYwert + 1];
    Arrays.fill(amplitudenAnzahl, 0);
    for (int i = 0; i < amplitude.length; i++) {
      if (amplitude[i] > 0) {
        amplitudenAnzahl[amplitude[i]]++;
      }
    }

    for (int i = 0; i < amplitudenFarben.length; i++) {
      if (amplitudenAnzahl[amplitude[i]] > 0) {
        amplitudenFarben[i] = FARBEN[amplitudenAnzahl[amplitude[i]] - 1];
      }
    }

    for (int i = 0; i < amplitude.length; i++) {
      amplitude[i] = yAnpassung(ausdehnungY, maxYwert, amplitude[i]);
    }



    werteNameX = "Pakete";
    werteNameY = "Sequenz";

    breite = paddingLeft + ausdehnungX + paddingRight;
    höhe = paddingTop + ausdehnungY + paddingButom;

    //System.out.println(messages.get(2).countFehler(Fehler.Typ.NICHT_EXISTENT));
  }

  @Override
  public BufferedImage getBufferedImage() {
    super.makeBufferedImage();

    // den Graphen (Grafik) zeichnen
    int countX = 0;
    for (int x = paddingLeft; x < paddingLeft + ausdehnungX; x++) {
      for (int y = paddingTop + ausdehnungY - 1; y >= paddingTop; y--) {
        if (y >= paddingTop + ausdehnungY - amplitude[countX])
          grafik.setRGB(x, y, amplitudenFarben[countX]);
      }
      countX++;
    }

    int werteAbstand = 100;
    int tmpWert = 0;

    // Werte der Skale X (Graf)
    graphWertZeichnen(X, tmpWert, paddingLeft - 1, paddingTop + ausdehnungY);
    tmpWert += werteAbstand;
    for (int x = paddingLeft + werteAbstand; x < paddingLeft + ausdehnungX; x += werteAbstand) {
      graphWertZeichnen(X, tmpWert, x, paddingTop + ausdehnungY);
      tmpWert += werteAbstand;
    }

    // Werte der Skale Y
    werteAbstand = berechneSchritte(akzeptierteSapelSchritte, maxYwert / 5);
    tmpWert = 0;
    for (int y = paddingTop + ausdehnungY; y >= paddingTop; y -= yAnpassung(ausdehnungY, maxYwert, werteAbstand)) {
      graphWertZeichnen(Y, tmpWert, paddingLeft - 1, y);
      tmpWert += werteAbstand;
    }

    // Beschreibung
    int step;
    int posX = paddingLeft + 100;
    for(int i = 0; i < farbBedeutung.length; i++) {
      step = schreibeInGrafik(farbBedeutung[i], LINKSBÜNDIG, Font.PLAIN, 11, posX, paddingTop - 5, FARBEN[i]);
      posX += step + 20;
    }

    return grafik;
  }


}
