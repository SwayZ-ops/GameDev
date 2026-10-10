package castlecrashers.data;

import java.util.Random;

// Supplies GameController with stage answers and the hints displayed above the book in GameView.
public class WordRepository {

    private Random random = new Random();

    /** Level 1 Words 6 letters */

    private String[][] level1Words = {

            { "CASTLE", "Behind mine walls doth crown and court find refuge." },

            { "DRAGON", "A winged terror of elder tales, whose breath bringeth ruin." },

            { "ESCAPE", "Seek this, lest these cold walls claim thee forever." },

            { "SHIELD", "Bear me before thee, and steel may fail to taste thy flesh." },

            { "TEMPLE", "Hallowed halls wherein mortals kneel before powers unseen." },

            { "KNIGHT", "Bound by oath, clad in steel, and sworn unto crown and realm." },

            { "PRISON", "A dwelling none seeketh, yet many enter against their will." },

            { "CANDLE", "Small is mine flame, yet darkness fleeth before me." },

            { "BRIDGE", "By mine back may one cross where feet alone cannot." },

            { "THRONE", "Upon me sitteth one whose word may command a realm." },

            { "ARMORY", "Within these walls sleep the tools of battle." },

            { "GUARDS", "They stand watch whilst others slumber." } };

    /** Level 2 Words 7 letters */

    private String[][] level2Words = {

            { "DUNGEON", "Beneath stone and shadow lie chambers where freedom is forgotten." },

            { "MONSTER", "Neither man nor beast wouldst thou gladly meet in darkness." },

            { "KINGDOM", "All lands that bow beneath a single crown." },

            { "PASSAGE", "A hidden way betwixt one chamber and the next." },

            { "ARCHERS", "From afar they loose death upon foes unseen." },

            { "CAPTAIN", "When soldiers march, this soul commandeth their course." },

            { "WARRIOR", "Where battle calleth, this soul answereth with steel." },

            { "GOBLINS", "Cunning little fiends whispered of in darkened tales." },

            { "TORCHES", "Bearers of flame that keep the blackness at bay." },

            { "CHAMBER", "A room by another name, oft hidden behind stone." },

            { "SOLDIER", "One who marcheth beneath banner and command." },

            { "GATEWAY", "Pass through me, and thou shalt leave one place for another." },

            { "HALLWAY", "Long and narrow, I bind many chambers as one." },

            { "TRAPPED", "When every road is barred, thou art thus." } };

    /** Level 3 Words 8 letters */

    private String[][] level3Words = {

            { "TREASURE", "Kings covet me, thieves hunt me, and fools perish seeking me." },

            { "PRISONER", "One whose freedom hath been taken by another." },

            { "FORTRESS", "Stone upon stone standeth defiant against armies." },

            { "DARKNESS", "Where light hath fled, I alone remain." },

            { "GUARDIAN", "I stand betwixt danger and that which I am sworn to keep." },

            { "SKELETON", "When flesh hath vanished, this frame remaineth behind." },

            { "FIREBALL", "A blazing orb fit more for sorcery than mortal hands." },

            { "DUNGEONS", "Many chambers below the earth wherein chains speak louder than men." },

            { "ESCAPING", "To be fleeing whilst freedom draweth ever nearer." },

            { "MIDNIGHT", "The hour when one day dieth and another is born." },

            { "KEYHOLES", "Small openings that await the teeth of iron." },

            { "DOORWAYS", "Thresholds betwixt where thou standest and where thou wouldst go." } };

    /** Level 4 Words 9 letters */

    private final String[][] level4Words = {
            { "SOVEREIGN", "Above lords and vassals, this ruler owneth the highest command." },

            { "TREASURES", "Riches hoarded behind iron, for which many souls would barter honor." },

            { "CROWNLESS", "Bereft of the circlet that once proclaimed thy right to rule." },

            { "MEDALLION", "A disk of honor or remembrance, borne upon a chain." },

            { "GOLDPIECE", "One gleaming coin that may buy bread or tempt betrayal." },

            { "STRONGBOX", "A stout little vault that guardeth what its master prizeth." },

            { "SWORDSMAN", "Steel is his craft, and the blade his chosen tongue." },

            { "FORTIFIED", "Made strong against assault by wall, ditch, and watchful steel." } };

    /** Level 5 Words 10 letters */

    private final String[][] level5Words = {
            { "BATTLEMENT", "Atop the wall, tooth and gap shelter those who defend the keep." },

            { "DRAWBRIDGE", "Raised, I deny passage; lowered, I span the moat." },

            { "STRONGHOLD", "A place of mighty defense wherein a lord may make his final stand." },

            { "KNIGHTHOOD", "An honor bestowed with oath and blade upon the worthy." },

            { "CORONATION", "In this rite, a brow receiveth the weight of a realm." },

            { "ENCHANTERS", "They weave unseen powers into the waking world." },

            { "THRONELESS", "Without the seat from which a monarch once commanded." } };

    // GameController requests a new answer from the word list for the current
    // stage.
    public String getRandomWord(int level) {
        String[][] words = getWordList(level);
        return words[random.nextInt(words.length)][0];
    }

    // GameController forwards this hint to GameView, which displays it above
    // BookWordBoard.
    public String getHint(String word) {
        for (int level = 1; level <= 5; level++) {
            for (String[] entry : getWordList(level))
                if (entry[0].equalsIgnoreCase(word))
                    return entry[1];
        }
        return "No wisdom hath been written for this word.";
    }

    /** Defensive copies let tests and editors inspect every entry safely. */
    public String[][] getWordsForLevel(int level) {
        String[][] entries = getWordList(level), copy = new String[entries.length][];
        for (int i = 0; i < entries.length; i++)
            copy[i] = entries[i].clone();
        return copy;
    }

    private String[][] getWordList(int level) {
        return switch (level) {
            case 1 -> level1Words;
            case 2 -> level2Words;
            case 3 -> level3Words;
            case 4 -> level4Words;
            case 5 -> level5Words;
            default -> throw new IllegalArgumentException("Stage must be 1 through 5");
        };
    }
}
