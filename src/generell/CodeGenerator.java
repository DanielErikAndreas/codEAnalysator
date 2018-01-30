package generell;

import graphen.Graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Allon on 11.12.2017.
 */
public class CodeGenerator {
  private String muster;
  private int seqStart, seqEnd, distanz;
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

    char[] musterChars = new char[muster.length()];
    muster.getChars(0, muster.length(), musterChars, 0);

    distanz = Graph.distanz(seqStart, seqEnd) + 1;

    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    int charDynCount = 0;
    char[] tmpChars = new char[musterChars.length];
    String tmpSequenzString = null;
    try {
      fileWriter = new FileWriter(outFile);
      bufferedWriter = new BufferedWriter(fileWriter);

      for (int sequenz = seqStart; sequenz <= seqEnd; sequenz++) {
        tmpSequenzString = auffüllen(Integer.toBinaryString(sequenz), "0", sequenzBitLänge, RECHTSBÜNDIG);

        for (int i = 0; i < musterChars.length; i++) {

          if (musterChars[i] == 'X') {
            tmpChars[i] = tmpSequenzString.charAt(charDynCount++);
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
        if (bufferedWriter != null)
          bufferedWriter.close();
        if (fileWriter != null)
          fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


    //System.out.println("test");
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

  private int sequenzBitLänge() {
    int sequenzBitLänge = 0;
    for (int i = 0; i < muster.length(); i++) {
      if (muster.charAt(i) == 'X') {
        sequenzBitLänge++;
      }
    }
    return sequenzBitLänge;
  }

  public void setMuster(String _muster) {
    muster = _muster;
  }

  public void setSequenz(int _start, int _end) {
    seqStart = _start;
    seqEnd = _end;
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
    sb.append("m(").append(muster);
    sb.append("), s(").append(seqStart).append("-").append(seqEnd).append(")");
    return sb.toString();
  }

  public static String großbuchstaben(String _s) {
    char[] großbuchstaben = new char[_s.length()];
    for (int i = 0; i < großbuchstaben.length; i++) {
      großbuchstaben[i] = _s.charAt(i);
      if (_s.charAt(i) >= 'a' && _s.charAt(i) <= 'z') {
        großbuchstaben[i] -= 32;
      }
    }
    return new String(großbuchstaben, 0, großbuchstaben.length);
  }

}
