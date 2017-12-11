package graphen;

import generell.Massage;
import generell.NachrichtenManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GraphPausenlänge extends Graph {
  private int amplitude[];

  public GraphPausenlänge(NachrichtenManager _nachrichtenManager) {
    super(_nachrichtenManager);
    ArrayList<Massage> massages = nachrichtenManager.getNachrichtenParser().getMassages();
    maxYwert = nachrichtenManager.getNachrichtenParser().getMaxPause();

    ausdehnungX = massages.size();
    amplitude = new int[ausdehnungX];
    for (int i = 0; i < amplitude.length; i++) {
      amplitude[i] = yAnpassung(ausdehnungY, maxYwert, Integer.parseInt(massages.get(i).getPause()));
    }

    werteNameX = "Pakete";
    werteNameY = "Pausenlänge in Sampels";

    breite = paddingLeft + ausdehnungX + paddingRight;
    höhe = paddingTop + ausdehnungY + paddingButom;
  }

  @Override
  public BufferedImage getBufferedImage() {
    super.makeBufferedImage();

    // den Graphen (Grafik) zeichnen
    int countX = 0;
    for (int x = paddingLeft; x < paddingLeft + ausdehnungX; x++) {
      for (int y = paddingTop + ausdehnungY - 1; y >= paddingTop; y--) {
        if (y >= paddingTop + ausdehnungY - amplitude[countX])
          grafik.setRGB(x, y, Color.RED.getRGB());
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

    return grafik;
  }

}
