package castlecrashers;

import castlecrashers.data.WordRepository;
import castlecrashers.character.*;
import java.util.*;

// Connects GameView to GameState, WordRepository, character choices, and stage rules.
public class GameController {
    private static final String[] STAGES = { "Gate", "Hallway", "Prison", "Treasury", "Throne Room" };
    private final String playerName;
    private final GameState gameState = new GameState();
    private final WordRepository wordRepository = new WordRepository();
    private final List<UnitColor> enemies = new ArrayList<>();

    public GameController() {
        this(UnitColor.BLUE, CharacterClass.WARRIOR);
    }

    public GameController(UnitColor color, CharacterClass characterClass) {
        this(color, characterClass, "Hero");
    }

    public GameController(UnitColor color, CharacterClass characterClass, String playerName) {
        if (playerName == null || playerName.isBlank() || playerName.strip().length() > 24) {
            throw new IllegalArgumentException("Enter a name of 1–24 characters");
        }
        this.playerName = playerName.strip();
        gameState.setCharacter(color, characterClass);
        for (UnitColor candidate : UnitColor.values())
            if (candidate != color)
                enemies.add(candidate);
        Collections.shuffle(enemies);
        loadWord();
    }

    // WordRepository chooses the answer for this stage then the GameState stores it for the battle
    
    private void loadWord() {
        gameState.setAnswer(wordRepository.getRandomWord(getLevel()));
    }

    // GameView submits a complete word here to update the battle outcome and the mistake count
  
    public boolean submitGuess(String guess) {
        if (gameState.isFinished())
            throw new IllegalStateException("This battle has finished");
        String normalized = guess.toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]{" + getWordLength() + "}"))
            throw new IllegalArgumentException("Enter " + getWordLength() + " letters");
        if (normalized.equals(getAnswer())) {
            gameState.solve();
            return true;
        }
        int damage = 0;
        for (int result : calculateResults(normalized))
            if (result == 2)
                damage += 10;
        gameState.damageEnemy(damage);
        gameState.addMistake();
        return false;
    }

    
    public int[] calculateResults(String guess) {
        guess = guess.toUpperCase(Locale.ROOT);
        if (!guess.matches("[A-Z]{" + getWordLength() + "}"))
            throw new IllegalArgumentException("Invalid guess");
        int[] marks = new int[getWordLength()], remaining = new int[26];
        for (int i = 0; i < marks.length; i++) {
            if (guess.charAt(i) == getAnswer().charAt(i))
                marks[i] = 2;
            else
                remaining[getAnswer().charAt(i) - 'A']++;
        }
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] == 2)
                continue;
            int index = guess.charAt(i) - 'A';
            if (remaining[index] > 0) {
                marks[i] = 1;
                remaining[index]--;
            }
        }
        return marks;
    }

    // GameView awards points after the victory animation.
   
    public int awardScore() {
        if (!gameState.isSolved() || gameState.isScoreAwarded())
            return 0;
        int points = (6 - getMistakes()) * 100;
        gameState.addScore(points);
        return points;
    }

    public void nextLevel() {
        if (!isSolved())
            throw new IllegalStateException("Solve the current word first");
        gameState.nextLevel();
        loadWord();
    }

    public void retryLevel() {
        gameState.resetBattle();
        loadWord();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getLevel() {
        return gameState.getLevel();
    }

    public int getMistakes() {
        return gameState.getMistakes();
    }

    public int getMaxMistakes() {
        return 6;
    }

    public int getScore() {
        return gameState.getScore();
    }

    public String getAnswer() {
        return gameState.getAnswer();
    }

    public String getHint() {
        return wordRepository.getHint(getAnswer());
    }

    public int getWordLength() {
        return getLevel() + 5;
    }

    public String getStageName() {
        return STAGES[getLevel() - 1];
    }

    public int getPlayerHp() {
        return gameState.getPlayerHp();
    }

    public int getEnemyHp() {
        return gameState.getEnemyHp();
    }

    public boolean isSolved() {
        return gameState.isSolved();
    }

    public boolean isFinished() {
        return gameState.isFinished();
    }

    public UnitColor getPlayerColor() {
        return gameState.getColor();
    }

    public CharacterClass getPlayerClass() {
        return gameState.getCharacterClass();
    }

    public boolean isFinalBoss() {
        return getLevel() == 5;
    }

    // The last enemy is a traitor from the player's own color.
    public UnitColor getEnemyColor() {
        return isFinalBoss() ? getPlayerColor() : enemies.get(getLevel() - 1);
    }

    public String getEnemyName() {
        if (isFinalBoss())
            return "Arthas Menethil the Fallen Prince";
        return switch (getEnemyClass()) {
            case WARRIOR -> "Tirion Fordring the Protector";
            case ARCHER -> "Nathanos Mariss the Ranger Lord";
            case LANCER -> "Zoval the Warden";
            case MONK -> "Jastor Gallywix the Greedy";
        };
    }

    public CharacterClass getEnemyClass() {
        return CharacterClass.values()[(getLevel() - 1) % 4];
    }
}
