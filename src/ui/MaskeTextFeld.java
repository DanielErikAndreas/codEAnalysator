package ui;

import generell.CodeGenerator;
import javafx.scene.control.TextField;

public class MaskeTextFeld extends TextField {
  @Override
  public void replaceText(int start, int end, String text)
  {
    String up = CodeGenerator.großbuchstaben(text);
    if (validate(up))
    {
      super.replaceText(start, end, up);
    }
  }

  @Override
  public void replaceSelection(String text)
  {
    String up = CodeGenerator.großbuchstaben(text);
    if (validate(up))
    {
      super.replaceSelection(up);
    }
  }

  private boolean validate(String text)
  {
    return text.matches("[01X]*");
  }
}
