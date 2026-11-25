package multitask2;

import java.util.Optional;
import java.util.function.Function;

import com.github.forax.zen.Application;
import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.Event;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import multitask2.controller.Controller;
import multitask2.model.Game;
import multitask2.model.Menu;
import multitask2.model.MenuItem;
import multitask2.model.Model;
import multitask2.model.games.Avoid;
import multitask2.model.games.Balance;
import multitask2.model.games.Fly;
import multitask2.view.GeneralView;
import multitask2.view.View;
import multitask2.view.Viewers;
import multitask2.view.games.AvoidView;
import multitask2.view.games.BalanceView;
import multitask2.view.games.FlyView;
import multitask2.view.games.GameView;

/**
 * Point d'entrée de l'application de mini-jeux multitâches.
 *
 * <p>
 * Cette classe crée l'interface graphique, le modèle partagé par les mini-jeux
 * et le contrôleur qui orchestre les mises à jour et le rendu. Elle enregistre
 * les mini-jeux avec leur vue respective.
 * </p>q
 */
public final class Main {

  /** 
   * Constructeur privé afin d'empêcher l'instanciation de cette classe
   * utilitaire.
   */
  private Main() {
    super();
  }

  /**
   * Lance la boucle de l'application graphique.
   *
   * @param args arguments de la ligne de commande ; ils ne sont pas utilisés
   *             actuellement
   */
  public static void main(String[] args) {
    Application.run(GeneralView.BACKGROUND, Main::game);
  }

  
  private static void game(ApplicationContext context) {
  	try {
  		var viewers = new Viewers();
  		var view = new GeneralView(context, viewers);
  		if(runMenu(context, view) == 1) {
  			int score;
  			do {
  				score = partie(context, view, viewers);
  			}while(runMenuEnd(context, view, score) == 1);
  		}
  	}finally {
  		context.dispose();
  		return;
  	}
  }
  
  
  
  
  /**
   * Construit les composants de la partie et exécute le contrôleur jusqu'à la fin
   * du jeu. <br>
   * Le contexte graphique est libéré même si l'exécution du contrôleur se termine
   * à la suite d'une exception.
   *
   * @param context contexte graphique fourni par la bibliothèque Zen
   */
  private static int partie(ApplicationContext context, View view, Viewers viewers) {
    var model = new Model();
    //addGame(model, viewers, new Balance(), 0, BalanceView::new);
    //addGame(model, viewers, new Avoid(), 5, AvoidView::new);
    addGame(model, viewers, new Fly(), 0, FlyView::new);
    var controller = new Controller(model, view, context);
    controller.run();
    return model.time();
  }
  
  
  
  
  private static int runMenu(ApplicationContext context, GeneralView view) {
    var menu = new Menu();
    while (true) {
      view.renderMenu(menu);
      var choice = handleEvent(context.pollOrWaitEvent(16), menu, view);
      if (choice.isPresent() && choice.get() == MenuItem.PLAY) {
        return 1;
      }
      /*if (choice.isPresent() && choice.get() == MenuItem.HELP) {
      	//une fonction pour afficher les touches pour jouer
        return runMenu(context, view);
      }*/
      if (choice.isPresent() && choice.get() == MenuItem.QUIT) {
        return 0;
      }
    }
  }
  
  private static int runMenuEnd(ApplicationContext context, GeneralView view, int score) {
    var menu = new Menu();
    view.renderMenuEnd(menu, score);
    try {
    	Thread.sleep(3000); 
    	while(context.pollEvent() != null) {
    		
    	}
    } catch (InterruptedException e) {
    	Thread.currentThread().interrupt(); 
    }
    context.pollOrWaitEvent(3000);
    while (true) {
      view.renderMenuEnd(menu, score);

      var choice = handleEvent(context.pollOrWaitEvent(16), menu, view);
      if (choice.isPresent() && choice.get() == MenuItem.PLAY) {
        return 1;
      }/*
      if (choice.isPresent() && choice.get() == MenuItem.HIGH_SCORES) {
      	//une fonction pour afficher le meilleur score
        return runMenuEnd(context, view, score);
      }
      if (choice.isPresent() && choice.get() == MenuItem.HELP) {
      	//une fonction pour afficher les touches pour jouer
        return runMenuEnd(context, view, score);
      }*/
      if (choice.isPresent() && choice.get() == MenuItem.QUIT) {
        return 0;
      }
    }
  }
  
  private static Optional<MenuItem> handleEvent(Event event, Menu menu, GeneralView view) {
    return switch (event) {
      case KeyboardEvent k when k.action() == KeyboardEvent.Action.KEY_PRESSED ->
          handleKey(k.key(), menu);
      case PointerEvent p -> handlePointer(p, menu, view);
      case null, default -> Optional.empty();
    };
  }

  private static Optional<MenuItem> handleKey(KeyboardEvent.Key key, Menu menu) {
    return switch (key) {
      case UP -> { menu.up(); yield Optional.empty(); }
      case DOWN -> { menu.down(); yield Optional.empty(); }
      case SPACE -> Optional.of(menu.selected());
      case ESCAPE -> Optional.of(MenuItem.QUIT);
      default -> Optional.empty();
    };
  }

  private static Optional<MenuItem> handlePointer(PointerEvent p, Menu menu, GeneralView view) {
    var hit = view.itemAt(menu, p.location().x(), p.location().y());
    hit.ifPresent(menu::select);
    return p.action() == PointerEvent.Action.POINTER_DOWN ? hit : Optional.empty();
  }

  /**
   * Enregistre un mini-jeu dans le modèle et associe à cette instance sa vue
   * graphique.
   *
   * @param <G>     type concret du mini-jeu
   * @param model   modèle auquel ajouter le mini-jeu
   * @param viewers registre dans lequel enregistrer sa vue
   * @param game    instance du mini-jeu à enregistrer
   * @param time    temps, en secondes depuis le début de la partie, auquel
   *                activer le mini-jeu
   * @param factory fonction construisant une vue à partir de l'instance du
   *                mini-jeu
   */
  private static <G extends Game> void addGame(Model model, Viewers viewers, G game, int time,
      Function<G, GameView> factory) {
    model.addGame(game, time);
    viewers.addGame(game, factory.apply(game));
  }
}
