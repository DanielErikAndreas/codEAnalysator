package graphen;

import generell.Massage;
import generell.NachrichtenManager;
import fehler.Fehler;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphMassageFehlerverteilung extends Graph {
  private int amplitude[][];
  private int balkenbreite = 6;
  private int balkenabstand = 2;

  private static final String farbBedeutung[] = {"Bit fehlt", "Bit zu viel", "Bit gewechselt"};
  private static final int farbArray[] = {
          0xff007f, // maganta
          0xff8000, // orange
          0x00994c// dunkel grün
  };

  public GraphMassageFehlerverteilung(NachrichtenManager _nachrichtenManager) {
    super(_nachrichtenManager);
    amplitude = new int[nachrichtenManager.getSollNachricht().length()][3];

    // Array mit der Anzahl der Fehler auffüllen
    ausdehnungX = amplitude.length * (balkenbreite * 2 + balkenabstand * 2);
    for (Massage massage : nachrichtenManager.getNachrichtenParser().getMassages()) {
      for (Fehler fehler : massage.getFehlerList().getFehlerList()) {
        if (fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_0_FEHLT || fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_1_FEHLT) {
          amplitude[fehler.getIndex()][0]++;
        } else if (fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL || fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL) {
          amplitude[fehler.getIndex()][1]++;
        } else if (fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_0 || fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1) {
          amplitude[fehler.getIndex()][2]++;
        }
      }
    }

    // größten Wert ermitteln
    maxYwert = 0;
    int tmpWert = 0;
    for (int i = 0; i < amplitude.length; i++) {
      tmpWert = amplitude[i][0] + amplitude[i][1] + amplitude[i][2];
      if (tmpWert > maxYwert) {
        maxYwert = tmpWert;
      }
    }

    // Werte des Arrays an die Höhe anpassen
    for (int i = 0; i < amplitude.length; i++) {
      amplitude[i][0] = yAnpassung(ausdehnungY, maxYwert, amplitude[i][0]);
      amplitude[i][1] = yAnpassung(ausdehnungY, maxYwert, amplitude[i][1]);
      amplitude[i][2] = yAnpassung(ausdehnungY, maxYwert, amplitude[i][2]);
    }

    werteNameX = "Nachrichtenstellen";
    werteNameY = "Anzahl der Fehler";

    breite = paddingLeft + ausdehnungX + paddingRight;
    höhe = paddingTop + ausdehnungY + paddingButom;
  }

  @Override
  public BufferedImage getBufferedImage() {
    super.makeBufferedImage();

    int step = balkenbreite * 2 + balkenabstand * 2;
    int balkenMittelPos = paddingLeft + balkenbreite + balkenabstand;

    // den Graphen (Grafik) zeichnen
    int tmpWert = 0;
    int tmpX1, tmpY1, tmpX2, tmpY2;
    for (int i = 0; i < amplitude.length; i++) {
      tmpWert = amplitude[i][0] + amplitude[i][1] + amplitude[i][2];
      if (tmpWert > 0) {
        tmpX1 = balkenMittelPos - balkenbreite;
        tmpX2 = balkenMittelPos + balkenbreite;

        if (amplitude[i][0] > 0) {
          tmpY1 = paddingTop + ausdehnungY - 1;
          tmpY2 = paddingTop + ausdehnungY - amplitude[i][0];
          block(tmpX1, tmpY1, tmpX2, tmpY2, farbArray[0]);
        }
        if (amplitude[i][1] > 0) {
        tmpY1 = paddingTop + ausdehnungY - 1 - amplitude[i][0];
        tmpY2 = paddingTop + ausdehnungY - amplitude[i][0] - amplitude[i][1];
        block(tmpX1, tmpY1, tmpX2, tmpY2, farbArray[1]);
        }
        if (amplitude[i][2] > 0) {
          tmpY1 = paddingTop + ausdehnungY - 1 - amplitude[i][0] - amplitude[i][1];
          tmpY2 = paddingTop + ausdehnungY - amplitude[i][0] - amplitude[i][1] - amplitude[i][2];
          block(tmpX1, tmpY1, tmpX2, tmpY2, farbArray[2]);
        }
      }
      balkenMittelPos += step;
    }


    // Werte der Skale X (Graf)
    balkenMittelPos = paddingLeft + balkenbreite + balkenabstand;
    for (int i = 0; i < amplitude.length; i++) {
      graphWertZeichnen(X, nachrichtenManager.getSollNachricht().charAt(i) + "", balkenMittelPos, paddingTop + ausdehnungY);
      balkenMittelPos += step;
    }


    int werteAbstand = 100;
    tmpWert = 0;
    // Werte der Skale Y
    werteAbstand = berechneSchritte(akzeptierteSapelSchritte, maxYwert / 5);
    tmpWert = 0;
    for (int y = paddingTop + ausdehnungY; y >= paddingTop; y -= yAnpassung(ausdehnungY, maxYwert, werteAbstand)) {
      graphWertZeichnen(Y, tmpWert, paddingLeft - 1, y);
      tmpWert += werteAbstand;
    }

    // Beschreibung
    int posX = paddingLeft + 100;
    for(int i = 0; i < farbBedeutung.length; i++) {
      step = schreibeInGrafik(farbBedeutung[i], LINKSBÜNDIG, Font.PLAIN, 11, posX, paddingTop - 5, farbArray[i]);
      posX += step + 20;
    }

    return grafik;
  }


}
