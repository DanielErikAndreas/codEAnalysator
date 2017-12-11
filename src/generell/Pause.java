package generell;

public class Pause implements Comparable {
  private int pause;
  private int anzahl;

  public Pause(int _pause) {
    pause = _pause;
    anzahl = 1;
  }

  public void addAnzahl() {
    anzahl++;
  }

  public int getPause() {
    return pause;
  }

  public int getAnzahl() {
    return anzahl;
  }

  @Override
  public int compareTo(Object o) {
    Pause p = (Pause) o;
    return (new Integer(pause)).compareTo(new Integer(p.getPause()));
  }

  @Override
  public String toString() {
    return "[" + anzahl + ", " + pause + "]";
  }
}
