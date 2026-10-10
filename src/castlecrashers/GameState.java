package castlecrashers;

import castlecrashers.character.CharacterClass;
import castlecrashers.character.UnitColor;

// Stores battle data for GameController; GameView reads it through the controller's getters.
public class GameState {
    private int level = 1, mistakes, score, enemyHp = 100;
    private String answer = "";
    private boolean solved, scoreAwarded;
    private UnitColor color = UnitColor.BLUE;
    private CharacterClass characterClass = CharacterClass.WARRIOR;

    public int getLevel() {
        return level;
    }

    public int getMistakes() {
        return mistakes;
    }

    public int getScore() {
        return score;
    }

    public String getAnswer() {
        return answer;
    }

    public int getMaxMistakes() {
        return 6;
    }

    public int getPlayerHp() {
        return (6 - mistakes) * 20;
    }

    public int getEnemyHp() {
        return enemyHp;
    }

    public boolean isSolved() {
        return solved;
    }

    public boolean isFinished() {
        return solved || mistakes >= 6;
    }

    public boolean isScoreAwarded() {
        return scoreAwarded;
    }

    public UnitColor getColor() {
        return color;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public void setCharacter(UnitColor color, CharacterClass characterClass) {
        this.color = color;
        this.characterClass = characterClass;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void addMistake() {
        if (mistakes < 6)
            mistakes++;
    }

    // Partial letter damage leaves one HP so only solving the word can defeat the
    // enemy.
    public void damageEnemy(int damage) {
        enemyHp = Math.max(1, enemyHp - damage);
    }

    public void solve() {
        solved = true;
        enemyHp = 0;
    }

    public void addScore(int points) {
        score += points;
        scoreAwarded = true;
    }

    public void nextLevel() {
        if (level >= 5)
            throw new IllegalStateException("Final stage already reached");
        level++;
        resetBattle();
    }

    // GameController calls this for a new stage or retry while keeping the
    // accumulated score.
    public void resetBattle() {
        mistakes = 0;
        enemyHp = 100;
        solved = false;
        scoreAwarded = false;
    }

    public void resetGame() {
        level = 1;
        score = 0;
        answer = "";
        resetBattle();
    }
}
