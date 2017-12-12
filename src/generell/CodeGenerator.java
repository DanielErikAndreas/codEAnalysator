package generell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Allon on 11.12.2017.
 */
public class CodeGenerator {
  private ArrayList<String> muster = new ArrayList<String>();
  private String musterString;
  private int seqStart, seqEnd;
  private boolean isSequenz = false;
  private File outFile;

  public static final int RECHTSBÜNDIG = 1;
  public static final int LINKSBÜNDIG = 2;

  public CodeGenerator(File _outFile) {
    outFile = _outFile;
  }

  public void generate() {
    int sequenzBitLänge = sequenzBitLänge();
    int maxSequenzNachMuster = (int) Math.pow(2, sequenzBitLänge) - 1;
    if (maxSequenzNachMuster < seqEnd) {
      seqEnd = maxSequenzNachMuster;
    }

    char[] musterChars = new char[musterString.length()];
    musterString.getChars(0, musterString.length(), musterChars, 0);

    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    int charDynCount = 0;
    char[] tmpChars = new char[musterChars.length];
    String tmpSequenzStrin = null;
    try {
      fileWriter = new FileWriter(outFile);
      bufferedWriter = new BufferedWriter(fileWriter);

      for (int sequenz = seqStart; sequenz <= seqEnd; sequenz++) {
        tmpSequenzStrin = auffüllen(Integer.toBinaryString(sequenz), "0", sequenzBitLänge, RECHTSBÜNDIG);

        for (int i = 0; i < musterChars.length; i++) {

          if (musterChars[i] == 'X') {
            tmpChars[i] = tmpSequenzStrin.charAt(charDynCount++);
          } else {
            tmpChars[i] = musterChars[i];
          }
        }

        bufferedWriter.write(tmpChars);
        if (sequenz < seqEnd)
          bufferedWriter.write(System.lineSeparator());
        charDynCount = 0;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bufferedWriter.close();
        fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


    System.out.println("test");
  }


  public static String auffüllen(String _string, String _füller, int _länge, int _richtung) {
    StringBuilder sb = new StringBuilder();
    if (_richtung == LINKSBÜNDIG) {
      sb.append(_string);
    }
    for (int i = 0; i < _länge - _string.length(); i++) {
      sb.append(_füller);
    }
    if (_richtung == RECHTSBÜNDIG) {
      sb.append(_string);
    }
    return sb.toString();
  }

  private int[] schnittPunkte() {
    ArrayList<Integer> schnittPunkteListe = new ArrayList<Integer>();
    boolean inDynFeld = false;
    for (int i = 0; i < musterString.length(); i++) {
      if (musterString.charAt(i) == 'X' && !inDynFeld) {
        schnittPunkteListe.add(i);
        inDynFeld = true;
      } else if ((musterString.charAt(i) != 'X' || i == musterString.length() - 1) && inDynFeld) {
        schnittPunkteListe.add(i);
        inDynFeld = false;
      }
      if (i == musterString.length() - 1 && inDynFeld) {
        schnittPunkteListe.add(i + 1);
      }
    }

    int[] schnittPunkte = new int[schnittPunkteListe.size()];
    for (int i = 0; i < schnittPunkte.length; i++) {
      schnittPunkte[i] = schnittPunkteListe.get(i);
    }

    return schnittPunkte;
  }

  private int sequenzBitLänge() {
    int sequenzBitLänge = 0;
    for (String s : muster) {
      if (s.charAt(0) == 'X') {
        sequenzBitLänge += s.length();
      }
    }
    return sequenzBitLänge;
  }

  public void setMuster(String _muster) {
    musterString = _muster;
    boolean isInXSegment = false;
    int tmpStart = 0;
    for (int i = 0; i < musterString.length(); i++) {
      if (musterString.charAt(i) == 'X' && !isInXSegment) {
        if (i > 0)
          muster.add(musterString.substring(tmpStart, i));
        tmpStart = i;
        isInXSegment = true;
      } else if (musterString.charAt(i) != 'X' && isInXSegment) {
        muster.add(musterString.substring(tmpStart, i));
        tmpStart = i;
        isInXSegment = false;
      }
      if (i == musterString.length() - 1) {
        muster.add(musterString.substring(tmpStart, i + 1));
      }
    }
  }

  public void setSequenz(int _start, int _end) {
    seqStart = _start;
    seqEnd = _end;

    if (seqEnd > 0) {
      isSequenz = true;
    } else {
      isSequenz = false;
    }
  }

  public boolean setSequenz(String _sequenz) {
    int sequenzRaum[] = NachrichtenParser.parsSequenzRaum(_sequenz);
    if (sequenzRaum[0] >= 0 && sequenzRaum[1] >= 0) {
      setSequenz(sequenzRaum[0], sequenzRaum[1]);
    } else {
      System.out.println("Sequenz: kein akzeptiertes Format!");
      return false;
    }
    return true;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("m(");
    for (int i = 0; i < muster.size(); i++) {
      if (i < muster.size() - 1) {
        sb.append(muster.get(i)).append(", ");
      } else {
        sb.append(muster.get(i)).append("), ");
      }
    }
    sb.append("s(").append(seqStart).append("-").append(seqEnd).append(")");
    return sb.toString();
  }
}
