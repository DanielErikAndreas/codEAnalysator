package generell;

import fehler.Fehler;
import fehler.FehlerStatistik;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class NachrichtenParser {
  private File file;
  private ArrayList<Massage> massages = new ArrayList<Massage>();
  private boolean isSequenz;
  private int sequenzStart = 0;
  private int sequenzEnd = 0;
  private int fehlendeSequenzen = 0;
  private int empfangeneNachrichten = 0;
  private long pausenSumme = 0;
  private int maxPause = 0;
  private int minPause = 0x7fffffff;
  private long shiftSumme = 0;
  private int maxShift = 0;
  private long nachbitsSumme = 0;
  private long maxNachbits = 0;
  private FehlerStatistik fehlerStatistik = new FehlerStatistik();

  public NachrichtenParser(File _file) {
    file = _file;

    String fileTyp = getFileTyp(file);
    if (fileTyp.equals("xml")) {
      try {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        DefaultHandler handler = new DefaultHandler() {
          boolean protocol = false;
          boolean decodings = false;
          boolean decoding = false;
          boolean participants = false;
          boolean participant = false;
          boolean messages = false;
          boolean message = false;
          boolean message_types = false;
          boolean message_type = false;
          boolean ruleset = false;

          Massage tmpMassage;

          public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("protocol")) protocol = true;
            if (qName.equalsIgnoreCase("decodings")) decodings = true;
            if (qName.equalsIgnoreCase("decoding")) decoding = true;
            if (qName.equalsIgnoreCase("participants")) participants = true;
            if (qName.equalsIgnoreCase("participant")) participant = true;
            if (qName.equalsIgnoreCase("messages")) messages = true;
            if (qName.equalsIgnoreCase("message")) {
              message = true;
              tmpMassage = new Massage(
                      attributes.getValue("bits"),
                      attributes.getValue("decoding_index"),
                      attributes.getValue("message_type_id"),
                      attributes.getValue("modulator_index"),
                      attributes.getValue("pause"));
              massages.add(tmpMassage);
            }
            if (qName.equalsIgnoreCase("message_types")) message_types = true;
            if (qName.equalsIgnoreCase("message_type")) message_type = true;
            if (qName.equalsIgnoreCase("ruleset")) ruleset = true;
          }

          public void endElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("protocol")) protocol = false;
            if (qName.equalsIgnoreCase("decodings")) decodings = false;
            if (qName.equalsIgnoreCase("decoding")) decoding = false;
            if (qName.equalsIgnoreCase("participants")) participants = false;
            if (qName.equalsIgnoreCase("participant")) participant = false;
            if (qName.equalsIgnoreCase("messages")) messages = false;
            if (qName.equalsIgnoreCase("message")) message = false;
            if (qName.equalsIgnoreCase("message_types")) message_types = false;
            if (qName.equalsIgnoreCase("message_type")) message_type = false;
            if (qName.equalsIgnoreCase("ruleset")) ruleset = false;
          }

          public void characters(char[] ch[], int start, int length) throws SAXException {
          }
        };
        parser.parse(file, handler);
      } catch (Exception e) {
        e.printStackTrace();
      }
      //System.out.println("testStop2");
    } else if (fileTyp.equals("txt")) {
      FileReader fileReader = null;
      BufferedReader bufferedReader = null;

      try {
        fileReader = new FileReader(file);
        bufferedReader = new BufferedReader(fileReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
          massages.add(new Massage(line));
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          bufferedReader.close();
          fileReader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }

  /**
   * >>>>>>>>>>>>>>>>
   * >>>>>>>>>>>>>>>>
   * untersucht die Codes und detektiert die FehlerTyp
   */
  public void checkCode(String _sollNachricht) {
    Massage.setSollNachricht(_sollNachricht);
    ArrayList<Integer> startDynFeld = new ArrayList<Integer>();
    ArrayList<Integer> endDynFeld = new ArrayList<Integer>();
    boolean inDynFeld = false;
    for (int i = 0; i < _sollNachricht.length(); i++) {
      if (_sollNachricht.charAt(i) == 'X' && !inDynFeld) {
        inDynFeld = true;
        startDynFeld.add(i);
      } else if (_sollNachricht.charAt(i) != 'X' && inDynFeld) {
        inDynFeld = false;
        endDynFeld.add(i);
      }
    }

    if (inDynFeld) {
      inDynFeld = false;
      endDynFeld.add(_sollNachricht.length());
    }

    Massage.setStartEndDynFeld(startDynFeld, endDynFeld);

    // Fehleranalyse
    Massage tmpMassage;
    if (isSequenz) {
      Sequenzraum sequenzraum = new Sequenzraum(sequenzStart, sequenzEnd);
      for (Massage massage : massages) {
        countValues(massage.checkCode());
        int sequenzKlasse = sequenzraum.sequenzCount(massage.getSequenzNummer());
        if (sequenzKlasse < 0) {
          massage.addFehler(Fehler.Typ.NICHT_IM_SEQUENZRAUM, 0);
        } else if (sequenzKlasse > 1) {
          massage.addFehler(Fehler.Typ.SEQUENZ_DUPLIKAT, 0);
        }
        fehlerStatistik.checkMassage(massage);
      }

      // fehlende Sequenzen ersetzen
      int listIndex = 0;
      for (int i = sequenzStart; i < sequenzraum.get().length; i++) {
        if (sequenzraum.get()[i] == 0) {
          tmpMassage = new Massage("0");
          tmpMassage.setSequenzNummer(i);
          tmpMassage.addFehler(Fehler.Typ.NICHT_EXISTENT, 0);
          massages.add(listIndex, tmpMassage);
          fehlendeSequenzen++;
          fehlerStatistik.checkMassage(tmpMassage);
        }
        listIndex++;
      }

    } else {
      for (Massage massage : massages) {
        countValues(massage.checkCode());
        fehlerStatistik.checkMassage(massage);
      }
    }
  }

  private void countValues(Massage _massage) {
    empfangeneNachrichten++;

    int pause = Integer.parseInt(_massage.getPause());
    pausenSumme += pause;
    if (pause > maxPause) {
      maxPause = pause;
    }
    if (pause < minPause) {
      minPause = pause;
    }

    int shift = _massage.getShift();
    shiftSumme += shift;
    if (shift > maxShift) {
      maxShift = shift;
    }

    int nachBits = _massage.countFehler(Fehler.Typ.NACH_BIT_0_ZU_VIEL, Fehler.Typ.NACH_BIT_1_ZU_VIEL);
    nachbitsSumme += nachBits;
    if (nachBits > maxNachbits) {
      maxNachbits = nachBits;
    }
  }


  public static String getFileTyp(File _file) {
    String name = _file.getName();
    int end = name.length();
    int start = 0;
    for (int i = end - 1; i >= 0; i--) {
      if (name.charAt(i) == '.') {
        start = i + 1;
        break;
      }
      if (i == 0) {
        return null;
      }
    }
    return name.substring(start, end);
  }

  public ArrayList<Massage> getMassages() {
    return massages;
  }

  public int getAnzahlMassages(Fehler.Typ _fehlerTyp) {
    int retVal = 0;
    for (Massage massage : massages) {
      if (massage.countFehler(_fehlerTyp) > 0)
        retVal++;
    }
    return retVal;
  }

  public boolean setSequenzRaum(String _sequenzRaum) {
    int[] sequenzRaum = parsSequenzRaum(_sequenzRaum);
    if (sequenzRaum[0] >= 0 && sequenzRaum[1] >= 0) {
      sequenzStart = sequenzRaum[0];
      sequenzEnd = sequenzRaum[1];
      if (sequenzEnd > 0) {
        isSequenz = true;
      } else {
        isSequenz = false;
      }
    } else {
      System.out.println("Sequenz: kein akzeptiertes Format!");
      return false;
    }
    return true;
  }

  public static int[] parsSequenzRaum(String _sequenzRaum) {
    boolean isSplit = false;
    int[] seqenzRaum = new int[2];

    for (int i = 0; i < _sequenzRaum.length(); i++) {
      if (_sequenzRaum.charAt(i) == '-') {
        isSplit = true;
        break;
      }
    }

    if (isSplit) {
      String sequenzSplit[] = _sequenzRaum.split("-");
      int wert1 = parsSequenz(sequenzSplit[0]);
      int wert2 = parsSequenz(sequenzSplit[1]);
      if (wert1 >= 0 && wert2 >= 0) {
        seqenzRaum[0] = wert1;
        seqenzRaum[1] = wert2;
      } else {
        seqenzRaum[0] = -1;
        seqenzRaum[1] = -1;
      }
    } else {
      int wert = parsSequenz(_sequenzRaum);
      if (wert >= 0) {
        seqenzRaum[0] = 0;
        seqenzRaum[1] = wert;
      } else {
        seqenzRaum[0] = -1;
        seqenzRaum[1] = -1;
      }
    }
    return seqenzRaum;
  }

  public static int parsSequenz(String _wert) {
    int retVal = 0;

    if (_wert.matches("[0-9abcdefx]*")) {
      int länge = _wert.length();

      // ist es eine Hex, oder Binär- Darstellung
      if (länge >= 3 && (_wert.substring(0, 2).equalsIgnoreCase("0x") || _wert.substring(0, 2).equalsIgnoreCase("0b"))) {
        if (_wert.substring(0, 2).equalsIgnoreCase("0x")) {
          retVal = Integer.parseInt(_wert.substring(2), 16);
        } else if (_wert.substring(0, 2).equalsIgnoreCase("0b")) {
          retVal = Integer.parseInt(_wert.substring(2), 2);
        }
      } else {
        retVal = Integer.parseInt(_wert);
      }
    } else {
      retVal = -1;
    }
    return retVal;
  }

  /**
   * brüft, ob der String nur aus Ziffern besteht
   *
   * @param _wert
   * @return
   */
  public static boolean isAkzeptSymbol(String _wert) {
    boolean isNummer = true;
    for (int i = 0; i < _wert.length(); i++) {
      char c = _wert.charAt(i);
      if (!((c >= '0' && c <= '9') || c == 'a' || c == 'A' ||
              c == 'b' || c == 'B' || c == 'c' || c == 'C' ||
              c == 'd' || c == 'D' || c == 'e' || c == 'E' ||
              c == 'f' || c == 'F' || c == 'x' || c == 'X'))
        return false;
    }
    return isNummer;
  }

  public int getEmpfangeneNachrichten() {
    return empfangeneNachrichten;
  }

  public long getPausenSumme() {
    return pausenSumme;
  }

  public int getMaxPause() {
    return maxPause;
  }

  public int getMinPause() {
    return minPause;
  }

  public int getMaxShift() {
    return maxShift;
  }

  public long getShiftSumme() {
    return shiftSumme;
  }

  public int getSequenzStart() {
    return sequenzStart;
  }

  public int getSequenzEnd() {
    return sequenzEnd;
  }

  public int getFehlendeSequenzen() {
    return fehlendeSequenzen;
  }

  public FehlerStatistik getFehlerStatistik() {
    return fehlerStatistik;
  }

  public long getNachbitsSumme() {
    return nachbitsSumme;
  }

  public long getMaxNachbit() {
    return maxNachbits;
  }
}
