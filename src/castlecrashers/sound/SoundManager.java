package castlecrashers.sound;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

// Re-uses effects and sounds for each stage. 
public final class SoundManager {
    private static final Map<String, AudioClip> CLIPS = new HashMap<>();
    private static final double BATTLE_VOLUME = 0.36;
    private static MediaPlayer musicPlayer;
    private static Timeline musicFade;
    private static boolean audioUnavailable;
    private static String currentTrack;

    private SoundManager() {
    }

    public static void playTilePlacement() {
        playSound("tile_placement.wav");
    }

    public static void playSpearAttack() {
        playSound("spear_attack.mp3");
    }

    public static void playMonkAttack() {
        playSound("monk_fist.wav");
    }

    public static void playArrowHit() {
        playSound("arrow_hit.wav");
    }

    public static void playSwordClash() {
        playSound("sword_clash.wav");
    }

    public static void playWordSubmit() {
        playSound("word_submit.wav");
    }

    public static void playDeath() {
        playSound("character_death.wav");
    }

    // GameView calls the effect methods above this cache avoids reloading the same audio for battles
    
    private static void playSound(String fileName) {
        if (audioUnavailable)
            return;
        try {
            AudioClip clip = CLIPS.computeIfAbsent(fileName, name -> {
                String url = audioUrl(name);
                if (url == null)
                    throw new IllegalStateException("Missing sound file");
                return new AudioClip(url);
            });
            clip.play();
        } catch (LinkageError error) {
            disableAudio(error);
        } catch (RuntimeException error) {
            System.err.println("Could not play sound " + fileName + ": " + error.getMessage());
        }
    }

    private static String audioUrl(String fileName) {
        URL resource = SoundManager.class.getResource("/sounds/" + fileName);
        if (resource != null)
            return resource.toExternalForm();
        File file = new File("resources/sounds/", fileName);
        return file.isFile() ? file.toURI().toString() : null;
    }

    public static void playMenuMusic() {
        startMusic("menu_music.wav", true, 0.45);
    }

    // AppManager shares one continuous loop across all three character setup screens.
    public static void playSelectionMusic() {
        if (musicPlayer != null && "selection_music.wav".equals(currentTrack))
            return;
        startMusic("selection_music.wav", true, 0.35);
    }

    // Goes to AppManager passes the current level when a cutscene ends and the battle starts

    public static void playBattleMusic(int level) {
        String track = switch (level) {
            case 1 -> "gate_theme.wav";
            case 2 -> "hallway_theme.wav";
            case 3 -> "prison_theme.wav";
            case 4 -> "treasury_theme.wav";
            case 5 -> "boss_theme.wav";
            default -> throw new IllegalArgumentException("Stage must be 1 through 5");
        };
        startMusic(track, true, BATTLE_VOLUME);
    }

    public static void playGameOverMusic() {
        startMusic("game_over_music.wav", false, 0.55);
    }

    // AppManager starts the fanfare after the final stage is cleared.
    public static void playVictoryMusic() {
        startMusic("victory_music.wav", true, 0.45);
    }

    // Start music immediately in a volume. Fade only when stopping switching battles
    private static void startMusic(String fileName, boolean loop, double volume) {
        if (audioUnavailable)
            return;
        stopMusic();
        try {
            String url = audioUrl(fileName);
            if (url == null)
                throw new IllegalStateException("Missing music file");
            musicPlayer = new MediaPlayer(new Media(url));
            currentTrack = fileName;
            musicPlayer.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
            musicPlayer.setVolume(volume);
            musicPlayer.play();
        } catch (LinkageError error) {
            disableAudio(error);
        } catch (RuntimeException error) {
            stopMusic();
            System.err.println("Could not play music " + fileName + ": " + error.getMessage());
        }
    }

    // GameView calls this when the enemy dies, before offering the next stage.
    //for fadeing enemy death
    public static void fadeOutAndStop(double seconds) {
        cancelFade();
        if (musicPlayer == null)
            return;
        if (seconds <= 0) {
            stopMusic();
            return;
        }
        musicFade = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(musicPlayer.volumeProperty(), musicPlayer.getVolume())),
                new KeyFrame(Duration.seconds(seconds), new KeyValue(musicPlayer.volumeProperty(), 0.0)));
        musicFade.setOnFinished(event -> stopMusic());
        musicFade.play();
    }

    public static void stopMusic() {
        cancelFade();
        currentTrack = null;
        if (musicPlayer != null) {
            MediaPlayer previous = musicPlayer;
            musicPlayer = null;
            try {
                previous.stop();
                previous.dispose();
            } catch (LinkageError error) {
                disableAudio(error);
            } catch (RuntimeException error) {
                System.err.println("Could not close music: " + error.getMessage());
            }
        }
    }

    
     //ONLY FOR BLOCKED SYSTEMS IF AUDIO IS NOT ALLOWED BYPASS
     
    private static void disableAudio(LinkageError error) {
        if (audioUnavailable)
            return;
        audioUnavailable = true;
        stopMusic();
        CLIPS.clear();
        System.err.println("Audio is unavailable for this session. The game will continue without sound. "
                + error.getMessage());
    }

    private static void cancelFade() {
        if (musicFade != null) {
            musicFade.stop();
            musicFade = null;
        }
    }
}
