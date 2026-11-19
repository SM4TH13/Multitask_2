package multitask2.model;

public enum MenuItem {
  PLAY("Jouer"), HIGH_SCORES("Scores"), HELP("Aide"), QUIT("Quitter");

  private final String label;

  MenuItem(String label) {
    this.label = label;
  }

  public String label() {
    return label;
  }
}
