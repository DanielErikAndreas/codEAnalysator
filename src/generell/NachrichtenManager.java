package generell;

import fehler.Fehler;
import fehler.FehlerStatistik;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

public class NachrichtenManager {
  private ArrayList<Pause> pausenSumme = new ArrayList<Pause>();
  private NachrichtenParser nachrichtenParser;
  private File file;
  private int sollPaketAnzahl;
  private int sollPause;

  private int kommarStellen = 3;
  private int zahlLänge1 = 25;
  private int zahlLänge2 = 10;
  private int zahlLänge3 = 1 + 3 + 1 + kommarStellen + 1;
  private String format = "%." + kommarStellen + "f";

  protected static final int RECHTSBÜNDIG = 1;
  protected static final int LINKSBÜNDIG = 2;

  private String sollNachricht;

  public NachrichtenManager(File _file) {
    file = _file;
    nachrichtenParser = new NachrichtenParser(file);
  }

  /**
   * liest aus einer Datei und generiert die Liste der Pausen
   */
  public void generate(String _sollNachricht) {
    sollNachricht = _sollNachricht;
    int tmpIntPause;
    Pause tmpPausePause;

    if (sollPause > 0)
      for (Message message : nachrichtenParser.getMessages()) {

        // Paket der Pausen hinzufügen
        tmpIntPause = Integer.parseInt(message.getPause());
        tmpPausePause = inPause(tmpIntPause);
        if (tmpPausePause != null)
          tmpPausePause.addAnzahl();
        else
          pausenSumme.add(new Pause(tmpIntPause));
      }

    nachrichtenParser.checkCode(sollNachricht);

  }

  /**
   * gibt das passende Pausenobjekt zurück
   *
   * @param _pause
   * @return
   */
  private Pause inPause(int _pause) {
    for (Pause pause : pausenSumme) {
      if (pause.getPause() == _pause)
        return pause;
    }
    return null;
  }

  /**
   * erstellt die Statistik und schreibt sie in eine txt-Datei
   *
   * @param _file
   */
  public String makeStatista(File _file) {
    Collections.sort(pausenSumme);
    String[] textBlock = getTextBlock();
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < textBlock.length; i++) {
      sb.append(textBlock[i]).append(System.lineSeparator());
    }

    sb.append(System.lineSeparator()).append(System.lineSeparator()).append(System.lineSeparator());

    String outString = sb.toString();

    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    int nummerStringLänge;
    try {
      fileWriter = new FileWriter(_file);
      bufferedWriter = new BufferedWriter(fileWriter);
      bufferedWriter.write(sb.toString());

      nummerStringLänge = (nachrichtenParser.getMessages().size() + "").length();
      for (int i = 0; i < nachrichtenParser.getMessages().size(); i++) {
        sb = new StringBuilder();
        sb.append(passString(RECHTSBÜNDIG, i + "", nummerStringLänge));
        sb.append(": ");
        sb.append(nachrichtenParser.getMessages().get(i).toString()).append(System.lineSeparator());
        bufferedWriter.write(sb.toString());
      }

      outString += "geschrieben in: " + _file.getAbsolutePath();
      System.out.println("geschrieben in: " + _file.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (bufferedWriter != null)
          bufferedWriter.close();
        if (fileWriter != null)
          fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return outString;
  }

  /**
   * erstellt die Statistik und schreibt sie in eine csv-Datei
   *
   * @param _file
   */
  public void makeStatistaCSV(File _file) {
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    FehlerStatistik fs = nachrichtenParser.getFehlerStatistik();
    try {
      fileWriter = new FileWriter(_file);
      bufferedWriter = new BufferedWriter(fileWriter);

      bufferedWriter.write("maske;" + sollNachricht + System.lineSeparator());
      bufferedWriter.write("sollPaketAnzahl;" + sollPaketAnzahl + System.lineSeparator());
      bufferedWriter.write("empfangeneNachrichten;" + nachrichtenParser.getEmpfangeneNachrichten() + System.lineSeparator());
      bufferedWriter.write("verluste;" + (sollPaketAnzahl - nachrichtenParser.getEmpfangeneNachrichten()) + System.lineSeparator());
      bufferedWriter.write("sequenz;" + (nachrichtenParser.getSequenzStart() == 0 && nachrichtenParser.getSequenzEnd() == 0 ?
              "0" : nachrichtenParser.getSequenzStart() + " - " + nachrichtenParser.getSequenzEnd()));
      bufferedWriter.write(System.lineSeparator());
      bufferedWriter.write("sequenzFehlt;" + nachrichtenParser.getFehlendeSequenzen() + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.SEQUENZ_DUPLIKAT + ";" + fs.get(Fehler.Typ.SEQUENZ_DUPLIKAT) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.NICHT_IM_SEQUENZRAUM + ";" + fs.get(Fehler.Typ.NICHT_IM_SEQUENZRAUM) + System.lineSeparator());
      bufferedWriter.write("unterschiedlichePausen;" + pausenSumme.size() + System.lineSeparator());
      bufferedWriter.write("sollPause;" + sollPause + System.lineSeparator());
      bufferedWriter.write("maxPause;" + nachrichtenParser.getMaxPause() + System.lineSeparator());
      bufferedWriter.write("minPause;" + nachrichtenParser.getMinPause() + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.KEIN_FEHLER + ";" + fs.get(Fehler.Typ.KEIN_FEHLER) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT + ";" + fs.get(Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWISCHEN_BIT_FEHLT + ";" + fs.get(Fehler.Typ.ZWISCHEN_BIT_FEHLT) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWISCHEN_BIT_ZU_VIEL + ";" + fs.get(Fehler.Typ.ZWISCHEN_BIT_ZU_VIEL) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWISCHEN_BIT_WECHSEL + ";" + fs.get(Fehler.Typ.ZWISCHEN_BIT_WECHSEL) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.EINBIT_FEHLER + ";" + fs.get(Fehler.Typ.EINBIT_FEHLER) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWEIBIT_FEHLER + ";" + fs.get(Fehler.Typ.ZWEIBIT_FEHLER) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.DREIBIT_FEHLER + ";" + fs.get(Fehler.Typ.DREIBIT_FEHLER) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWISCHEN_BIT_1_FEHLT + ";" + fs.get(Fehler.Typ.ZWISCHEN_BIT_1_FEHLT) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1 + ";" + fs.get(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL + ";" + fs.get(Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWISCHEN_BIT_0_FEHLT + ";" + fs.get(Fehler.Typ.ZWISCHEN_BIT_0_FEHLT) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_0 + ";" + fs.get(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_0) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL + ";" + fs.get(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL) + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.SHIFT + ";" + fs.get(Fehler.Typ.SHIFT) + System.lineSeparator());
      bufferedWriter.write("maxShift;" + nachrichtenParser.getMaxShift() + System.lineSeparator());
      bufferedWriter.write(Fehler.Typ.NACHBITS + ";" + fs.get(Fehler.Typ.NACHBITS) + System.lineSeparator());
      bufferedWriter.write("maxNachbits;" + nachrichtenParser.getMaxNachbit() + System.lineSeparator());


      System.out.println("geschrieben in: " + _file.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (bufferedWriter != null)
          bufferedWriter.close();
        if (fileWriter != null)
          fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }


  public String[] getTextBlock() {
    FehlerStatistik fs = nachrichtenParser.getFehlerStatistik();
    int verluste = sollPaketAnzahl - nachrichtenParser.getEmpfangeneNachrichten();
    String[] retVal = {
            getFile().getName(),
            "Maske: " + sollNachricht,
            "",
            "",
            bezeichnung("Anzahl der Pakete:") + printWert(sollPaketAnzahl) + passString(RECHTSBÜNDIG, "100,000%", zahlLänge3),
            bezeichnung(".. erfolgreich:") + printWert(nachrichtenParser.getEmpfangeneNachrichten()) + anteil(nachrichtenParser.getEmpfangeneNachrichten()),
            bezeichnung(".. Verluste:") + printWert(verluste) + anteil(verluste),
            "",
            "Sequenzen:",
            ".. Raum: " + (nachrichtenParser.getSequenzStart() == 0 && nachrichtenParser.getSequenzEnd() == 0 ?
                    "keiner" : nachrichtenParser.getSequenzStart() + " - " + nachrichtenParser.getSequenzEnd()),
            bezeichnung(".. fehlende") + printWert(nachrichtenParser.getFehlendeSequenzen()) + anteil(nachrichtenParser.getFehlendeSequenzen()),
            bezeichnung(".. Duplikate") + printWert(fs.get(Fehler.Typ.SEQUENZ_DUPLIKAT)) + anteil(fs.get(Fehler.Typ.SEQUENZ_DUPLIKAT)),
            bezeichnung(".. nicht im Raum") + printWert(fs.get(Fehler.Typ.NICHT_IM_SEQUENZRAUM)) + anteil(fs.get(Fehler.Typ.NICHT_IM_SEQUENZRAUM)),
            "",
            "Pausen:",
            bezeichnung(".. unterschiedliche:") + printWert(pausenSumme.size()),
            bezeichnung(".. soll:") + printWert(sollPause),
            bezeichnung(".. max:") + printWert(nachrichtenParser.getMaxPause()),
            bezeichnung(".. min:") + printWert(nachrichtenParser.getMinPause()),
            bezeichnung(".. Ø:") + durchschnitt(nachrichtenParser.getPausenSumme(), nachrichtenParser.getEmpfangeneNachrichten()),
            bezeichnung(".. Spanne:") + printWert(nachrichtenParser.getMaxPause() - nachrichtenParser.getMinPause()),
            "",
            bezeichnung("Übereinstimmungen:") + printWertPlusAnteil(fs.get(Fehler.Typ.KEIN_FEHLER, Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT)),
            bezeichnung("kein Fehler:") + printWertPlusAnteil(fs.get(Fehler.Typ.KEIN_FEHLER)),
            bezeichnung("enthält Nachricht: ") + printWertPlusAnteil(fs.get(Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT)),
            bezeichnung("keine Übereinstimmung: ") + printWertPlusAnteil(fs.get(Fehler.Typ.KEINE_ÜBEREINSTIMMUNG)),
            bezeichnung("Fehler in der Nachricht: ") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BITFEHLER)),
            "",
            "Fehler:",
            bezeichnung("Bit fehlt:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_FEHLT)),
            bezeichnung("Bit zu viel:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_ZU_VIEL)),
            bezeichnung("Bit gekipt:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_WECHSEL)),
            "",
            bezeichnung("EinBitFehler:") + printWertPlusAnteil(fs.get(Fehler.Typ.EINBIT_FEHLER)),
            bezeichnung("ZweiBitFehler:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWEIBIT_FEHLER)),
            bezeichnung("DreiBitFehler:") + printWertPlusAnteil(fs.get(Fehler.Typ.DREIBIT_FEHLER)),
            "",
            bezeichnung("Bit 1 fehlt:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_1_FEHLT)),
            bezeichnung("Bit 1 gekipt:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1)),
            bezeichnung("Bit 1 zu viel:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL)),
            "",
            bezeichnung("Bit 0 fehlt:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_0_FEHLT)),
            bezeichnung("Bit 0 gekipt:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL)),
            bezeichnung("Bit 0 zu viel:") + printWertPlusAnteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL)),
            "",
            bezeichnung("shift:") + printWertPlusAnteil(fs.get(Fehler.Typ.SHIFT)),
            bezeichnung(".. max:") + printWert(nachrichtenParser.getMaxShift()),
            bezeichnung(".. Ø:") + durchschnitt(nachrichtenParser.getShiftSumme(), fs.get(Fehler.Typ.SHIFT)),
            "",
            bezeichnung("Nachbits:") + printWertPlusAnteil(fs.get(Fehler.Typ.NACHBITS)),
            bezeichnung(".. max:") + printWert(nachrichtenParser.getMaxNachbit()),
            bezeichnung(".. Ø:") + durchschnitt(nachrichtenParser.getNachbitsSumme(), fs.get(Fehler.Typ.NACHBITS))
    };
    return retVal;
  }

  private String printWertPlusAnteil(int _wert) {
    return printWert(_wert) + anteil(_wert);
  }

  private String anteil(int _wert) {
    double wert = (double) _wert * 100.0 / (double) sollPaketAnzahl;
    if (wert == 0.0) {
      return "";
    }
    return passString(RECHTSBÜNDIG, String.format(format, wert) + "%", zahlLänge3);
  }

  private String printWert(int _wert) {
    return passString(RECHTSBÜNDIG, NumberFormat.getInstance().format(_wert), zahlLänge2);
  }

  private String printWert(double _wert) {
    return passString(RECHTSBÜNDIG, NumberFormat.getInstance().format(_wert), zahlLänge2);
  }

  private String bezeichnung(String _s) {
    return passString(LINKSBÜNDIG, _s, zahlLänge1);
  }

  private String durchschnitt(long _summe, int _anzahl) {
    double durchschnitt;
    if (_anzahl == 0.0 || _summe == 0.0) {
      durchschnitt = 0;
    } else {
      durchschnitt = (double) _summe / (double) _anzahl;
    }
    String s = NumberFormat.getInstance().format(durchschnitt);
    String splitString[] = s.split(",");
    int addSpace = zahlLänge2 - splitString[0].length();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < addSpace; i++) {
      sb.append(" ");
    }
    sb.append(s);
    return sb.toString();
  }

  /**
   * füllt einen String vorne auf die angegebene Länge mit Spaces aus
   *
   * @param _s
   * @param _länge
   * @return
   */
  private String passString(int _ausrichtung, String _s, int _länge) {
    if (_s.length() >= _länge) {
      return _s;
    }

    int zusatzSpace = _länge - _s.length();
    StringBuffer sb = new StringBuffer();
    if (_ausrichtung == LINKSBÜNDIG) {
      sb.append(_s);
    }
    for (int i = 0; i < zusatzSpace; i++) {
      sb.append(" ");
    }
    if (_ausrichtung == RECHTSBÜNDIG) {
      sb.append(_s);
    }

    return sb.toString();
  }

  /**
   * setzt Start und End des Sequentraumes
   *
   * @param _sequenzRaum
   */
  public boolean setSequenz(String _sequenzRaum) {
    return nachrichtenParser.setSequenzRaum(_sequenzRaum);
  }

  public void setSollPaketAnzahl(int sollPaketAnzahl) {
    this.sollPaketAnzahl = sollPaketAnzahl;
  }

  public ArrayList<Pause> getPausenSumme() {
    return pausenSumme;
  }

  public String getSollNachricht() {
    return sollNachricht;
  }

  public void setSollPause(int sollPause) {
    this.sollPause = sollPause;
  }

  public File getFile() {
    return file;
  }

  public NachrichtenParser getNachrichtenParser() {
    return nachrichtenParser;
  }

}
