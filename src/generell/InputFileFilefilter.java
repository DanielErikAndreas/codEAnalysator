package generell;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class InputFileFilefilter extends FileFilter {
  public final static String akzeptierteTypen[] = {"txt", "xml"};

  @Override
  public boolean accept(File _file) {
    if (_file.isDirectory()) {
      return true;
    }

    String dateiTyp = getDateiTyp(_file);
    if (dateiTyp != null) {
      boolean isAkzeptierterTyp = false;
      for (int i = 0; i < akzeptierteTypen.length; i++) {
        isAkzeptierterTyp = isAkzeptierterTyp || dateiTyp.equals(akzeptierteTypen[i]);
      }
      return isAkzeptierterTyp;
    }
    return false;
  }

  @Override
  public String getDescription() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < akzeptierteTypen.length; i++) {
      sb.append("*.").append(akzeptierteTypen[i]).append(" ");
    }
    return sb.toString();
  }

  public static String getDateiTyp(File _filef) {
    String dateiTyp = null;
    String s = _filef.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 && i < s.length() - 1) {
      dateiTyp = s.substring(i + 1).toLowerCase();
    }
    return dateiTyp;
  }
}
