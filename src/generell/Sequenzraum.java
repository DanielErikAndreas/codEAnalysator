package generell;

import java.util.Arrays;

public class Sequenzraum {
  private int[] sequenzRaum;

  public Sequenzraum(int _von, int _bis) {
    // 01 23456 s2 e6 -> 6-2+1=5
    int[] sequenzRaumOut = new int[_von];
    int[] sequenzRaumIn = new int[_bis - _von + 1];
    sequenzRaum = new int[_bis + 1];
    Arrays.fill(sequenzRaumOut, -1);
    Arrays.fill(sequenzRaumIn, 0);
    System.arraycopy(sequenzRaumOut, 0, sequenzRaum, 0, sequenzRaumOut.length);
    System.arraycopy(sequenzRaumIn, 0, sequenzRaum, sequenzRaumOut.length, sequenzRaumIn.length);
  }

  public int sequenzCount(int _sequenz) {
    if (_sequenz < sequenzRaum.length) {
      if(sequenzRaum[_sequenz] >= 0) {
        return ++sequenzRaum[_sequenz];
      } else {
        return -1;
      }
    } else {
      return -1;
    }
  }

  public int[] get() {
    return sequenzRaum;
  }
}
