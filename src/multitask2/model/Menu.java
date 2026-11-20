package multitask2.model;

import java.util.List;
import java.util.Objects;

public final class Menu {
  private final MenuItem[] items = MenuItem.values();
  private int selected;

  public void up() {
    selected = (selected + items.length - 1) % items.length;
  }

  public void down() {
    selected = (selected + 1) % items.length;
  }

  public MenuItem selected() {
    return items[selected];
  }

  public List<MenuItem> items() {
    return List.of(items);
  }
  
  public void select(MenuItem item) {
    Objects.requireNonNull(item);
    selected = List.of(items).indexOf(item);
  }

}
