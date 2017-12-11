package graphen;

import generell.NachrichtenManager;
import generell.Pause;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GraphPausenverteilung extends Graph {
  private int amplitude[];

  public GraphPausenverteilung(NachrichtenManager _nachrichtenManager) {
    super(_nachrichtenManager);
    ArrayList<Pause> pausen = nachrichtenManager.getPausenSumme();
    maxYwert = häufigstePause(nachrichtenManager.getPausenSumme());

    ausdehnungX = pausen.size();
    amplitude = new int[ausdehnungX];
    for (int i = 0; i < amplitude.length; i++) {
      amplitude[i] = paddingTop + ausdehnungY - yAnpassung(ausdehnungY, maxYwert, pausen.get(i).getAnzahl());
    }

    werteNameX = "Pausenlänge in Sampels";
    werteNameY = "Pakete";

    breite = paddingLeft + ausdehnungX + paddingRight;
    höhe = paddingTop + ausdehnungY + paddingButom;
  }

  public BufferedImage getBufferedImage() {
    super.makeBufferedImage();

    // den Graphen (Grafik) zeichnen
    int countX = 0;
    for (int x = paddingLeft; x < paddingLeft + ausdehnungX; x++) {
      for (int y = paddingTop + ausdehnungY - 1; y >= paddingTop; y--) {
        if (y >= amplitude[countX])
          grafik.setRGB(x, y, Color.RED.getRGB());
      }
      countX++;
    }

    int werteAbstand = 100;
    int tmpWert = 0;

    // Wert der Skale X (Verteilung) häufigstePauseIndex
    int tmpIndex = häufigstePauseIndex(nachrichtenManager.getPausenSumme());
    graphWertZeichnen(X, nachrichtenManager.getPausenSumme().get(tmpIndex).getPause(), paddingLeft + tmpIndex, paddingTop + ausdehnungY);

    // Werte der Skale Y
    werteAbstand = berechneSchritte(akzeptierteSapelSchritte, maxYwert / 5);
    tmpWert = 0;
    for (int y = paddingTop + ausdehnungY; y >= paddingTop; y -= yAnpassung(ausdehnungY, maxYwert, werteAbstand)) {
      graphWertZeichnen(Y, tmpWert, paddingLeft - 1, y);
      tmpWert += werteAbstand;
    }

    return grafik;
  }
}
