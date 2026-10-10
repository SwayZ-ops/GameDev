package castlecrashers.character;

// classes of diff characters, used to read their filenames and frame sizes for unitcolor.
public enum CharacterClass {
    WARRIOR("Warrior", "Warrior_Idle", "Warrior_Run", "Warrior_Attack1", 192),
    ARCHER("Archer", "Archer_Idle", "Archer_Run", "Archer_Shoot", 192),
    LANCER("Lancer", "Lancer_Idle", "Lancer_Run", "Lancer_Right_Attack", 320),
    MONK("Monk", "Idle", "Run", "Heal", 192);

    public final String folder, idle, run, attack;
    public final int frameSize;

    CharacterClass(String folder, String idle, String run, String attack, int frameSize) {
        this.folder = folder;
        this.idle = idle;
        this.run = run;
        this.attack = attack;
        this.frameSize = frameSize;
    }

    @Override
    public String toString() {
        return folder;
    }
}
