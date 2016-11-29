package antivirus;

import com.kanishka.virustotal.dto.FileScanReport;
import com.kanishka.virustotal.exception.APIKeyNotFoundException;
import com.kanishka.virustotal.exception.UnauthorizedAccessException;
import com.kanishka.virustotalv2.VirusTotalConfig;
import com.kanishka.virustotalv2.VirustotalPublicV2;
import com.kanishka.virustotalv2.VirustotalPublicV2Impl;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Jakub Cieciala <jakub.cieciala@gmail.com>
 */
public class Logic {

    public static final int IS_DIR = 1;
    public static final int IS_FILE = 2;
    ArrayList<String> filePaths;
    Map<String, String> fileHashes;
    Map<String, String> viruses;
    Map<String, String> virusFiles;
    ArrayList virusFilesToMessage;
    public static DefaultCaret caret;

    public ArrayList runLogic(String path, Map viruses, JTextArea progressArea, boolean sendOnVirusTotal) throws IOException, InterruptedException {

        filePaths = new ArrayList<String>();
        fileHashes = new HashMap<>();
        virusFiles = new HashMap<>();
        virusFilesToMessage = new ArrayList<>();

        int fileType = checkFile(path);

        if (fileType == IS_DIR) {
            filePaths = FileWalker.printFnames(path, progressArea);
        }

        if (fileType == IS_FILE) {
            Logic.setTextToProgressArea(progressArea, "Načítání souboru - " + String.valueOf(path));
            filePaths.add(new File(path).getAbsolutePath());

        }

        Logic.setTextToProgressArea(progressArea, "Soubory načteny...");

        filePaths.forEach((file) -> {
            Logic.setTextToProgressArea(progressArea, "Kontrola souboru - " + String.valueOf(file));
            String fileHash = FileWalker.calcSHA1(new File(file));
            if (FileWalker.getKeysByValue(viruses, FileWalker.calcSHA1(new File(file))).size() > 0) {
                virusFilesToMessage.add(
                        "Soubor: " + file
                        + "\nVirus: " + FileWalker.getKeysByValue(viruses, fileHash)
                        + "\nHash: " + fileHash);
                virusFiles.put(file, fileHash);
            } else {
                fileHashes.put(file, fileHash);
            }
        });

        if (sendOnVirusTotal) {
            Logic.setTextToProgressArea(progressArea, "Posílání hashů ke kontrole na virustotal.com...");

            for (Map.Entry<String, String> entry : fileHashes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Logic.setTextToProgressArea(progressArea, "Soubor - " + key);
                try {
                    VirusTotalConfig.getConfigInstance().setVirusTotalAPIKey(Antivirus.apiKey);
                    VirustotalPublicV2 virusTotalRef = new VirustotalPublicV2Impl();
                    FileScanReport report = virusTotalRef.getScanReport(value);

                    if (report.getPositives() > 0) {
                        virusFiles.put(key, value);
                        virusFilesToMessage.add(
                                "Soubor: " + key
                                + "\nVirus: VIRUSTOTAL"
                                + "\nHash: " + value
                                + "\nURL: " + report.getPermalink()
                                + "\nPočet nálezů: " + report.getPositives() + " / " + report.getTotal());
                    }
                } catch (APIKeyNotFoundException ex) {
                    System.err.println("API Key not found! " + ex.getMessage());
                } catch (UnsupportedEncodingException ex) {
                    System.err.println("Unsupported Encoding Format!" + ex.getMessage());
                } catch (UnauthorizedAccessException ex) {
                    System.err.println("Invalid API Key " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("Something Bad Happened! " + ex.getMessage());
                }

                Logic.setTextToProgressArea(progressArea, "Soubor odeslán. Začíná 16 s pauza (omezení API)...");
                Thread.sleep(16000);
                Logic.setTextToProgressArea(progressArea, "Pauza skončila. Pokračuje se...");

            }

            Logic.setTextToProgressArea(progressArea, "Posílání hashů dokončeno.");
        }
        // Zazpuju zavirované soubory
        if (virusFiles.size() > 0) {
            FileWalker.createZipArchive(virusFiles.keySet().toArray(), progressArea);
        }

        Logic.setTextToProgressArea(progressArea, "Testování dokončeno.");

        return virusFilesToMessage;
    }

    public int checkFile(String path) {
        Path file = new File(path).toPath();

        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Cesta k souboru či složce není validní!");
        }

        if (Files.isDirectory(file)) {
            return IS_DIR;
        }

        if (Files.isRegularFile(file)) {
            return IS_FILE;
        }

        throw new IllegalArgumentException("Soubor či složka nejsou validní!");
    }

    public static void setTextToProgressArea(JTextArea progressArea, String text) {
        String actualText = "".equals(progressArea.getText()) ? "Spouštění" : progressArea.getText();
        progressArea.setText(actualText + "\n" + text);
        trunkTextArea(progressArea);
        progressArea.update(progressArea.getGraphics());
    }

    public static void trunkTextArea(JTextArea txtWin) {
        int numLinesToTrunk = txtWin.getLineCount() - 4;
        if (numLinesToTrunk > 0) {
            try {
                int posOfLastLineToTrunk = txtWin.getLineEndOffset(numLinesToTrunk - 1);
                txtWin.replaceRange("", 0, posOfLastLineToTrunk);
            } catch (BadLocationException ex) {
            }
        }
    }
    
}
