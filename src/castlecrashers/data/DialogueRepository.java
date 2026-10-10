package castlecrashers.data;

import castlecrashers.GameController;
import java.util.List;

// Dialogues for each enemy cutscene 
public final class DialogueRepository {
    public record Line(boolean hero, String text) {
    }

    private DialogueRepository() {
    }

    // THe view requests the current stage's lines using the controller's level and the chosen color
   
    public static List<Line> beforeBattle(GameController game) {
        String house = game.getPlayerColor().toString();
        return switch (game.getLevel()) {
            case 1 -> List.of(new Line(false, "Halt! No banner passes this gate without the king's leave."),
                    new Line(true, "Then let him hear me. I came to free this castle, not to bow before it."),
                    new Line(false,
                            "Fine words. The seal will judge them. Six failures, and your journey ends here."),
                    new Line(true, "Stand ready. I will find the word that breaks your guard."));

            case 2 ->
                List.of(new Line(false, "So the gate has fallen. Do you think that makes you a conqueror?"),
                        new Line(true, "No. But it means your prisoners are one step closer to freedom."),
                        new Line(false, "These halls answer to the throne. You will go no farther."),
                        new Line(true, "Then I will give them a different answer."));

            case 3 -> List.of(new Line(false,
                    "Listen closely. Every chain that rattles around us belongs to someone who challenged the crown."),
                    new Line(true, "A shepherd with no flock is just an idiot with a stick."),
                    new Line(false, "There is an empty cell waiting for you."),
                    new Line(true, "I will be a prisoner no longer."));

            case 4 -> List.of(new Line(false, "Let me make it worth your while, name your price."),
                    new Line(true, "You cling to such that which has been stolen."),
                    new Line(false, "You would empty a kingdom's treasury for these peasants?"),
                    new Line(true, "A kingdom is its people."));

            case 5 -> List.of(
                    new Line(true, "Those colors... the bearings of " + house + "? You were one of us."),
                    new Line(false,
                            "I still am. I simply had the courage to take the throne our house deserved."),
                    new Line(true,
                            "You turned our banner into a threat. You imprisoned the people we swore to protect."),
                    new Line(false,
                            "They needed a ruler, not another servant. Join me, and there is a place for you beside me brother."),
                    new Line(true, "We share a color, but you are not my brother."),
                    new Line(false, "Insolent worm let me show you true power."));
            default -> throw new IllegalArgumentException("Stage must be 1 through 5");
        };
    }
}
