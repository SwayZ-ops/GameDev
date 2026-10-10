# Castle Crashers

A JavaFX word-guessing battle game. Choose a faction, enter your name, pick a class, and clear five castle stages by finding each hidden word.

## Run locally

The project was developed with JDK 26 and JavaFX SDK 26.0.2 on Windows.

1. Install Git LFS before cloning, then run `git lfs pull` in the clone to download the audio files.
2. Install JDK 26 and extract the JavaFX 26.0.2 Windows SDK into `lib/javafx-sdk-26.0.2/`.
3. Open the repository folder in VS Code with Java language and debugger support installed.
4. Wait for Java project loading, then select **Run Castle Crashers** from Run and Debug.

The JavaFX SDK and compiled output are intentionally excluded from Git. The included VS Code configuration sets `src` as the source directory, `build` as the output directory, and adds `resources` to the runtime classpath. Adjust the JavaFX paths if using a different SDK or operating system.

## Gameplay

- Start at the Gate and advance through Hallway, Prison, Treasury, and Throne Room.
- Answers grow from six to ten letters. Each stage allows six attempts.
- Gray: no unmatched copy of that letter in the answer. Yellow: a matching letter in another position. Green: correct letter and position.
- Each green letter in an incorrect guess deals 10 damage. A wrong complete guess costs 20 player HP. Solve the word to defeat the enemy.
- Use the physical keyboard or on-screen keys. The home menu includes a How to Play book.

## Code map

- `src/castlecrashers/Main.java`: application entry point.
- `src/castlecrashers/AppManager.java`: screens, navigation, fades, and music selection.
- `src/castlecrashers/GameController.java`: guessing rules and progression.
- `src/castlecrashers/GameState.java`: current battle state and score.
- `src/castlecrashers/ui/`: menu, selection, guide, dialogue, battle, victory, and reusable UI.
- `src/castlecrashers/animation/`: sprite-sheet animation.
- `src/castlecrashers/data/`: words, hints, and dialogue.
- `src/castlecrashers/sound/`: music and sound effects.
- `resources/`: stylesheets, images, and audio. Audio is stored through Git LFS.

Art and audio include third-party assets supplied for this project. This repository does not grant additional rights to those assets.
