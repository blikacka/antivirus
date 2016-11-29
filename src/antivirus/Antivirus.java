package antivirus;

import antivirus.GUI.InitVindows;
import antivirus.GUI.MainWindow;
import antivirus.components.Awt1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Jakub Cieciala <jakub.cieciala@gmail.com>
 */
public class Antivirus {

    public static final String apiKey = "1cbbbbbfade2adc708e9d20c2f35091bc1ee34f7f75aec18360690d740749ba1";

    public static void main(String[] args) {
        InitVindows init = new InitVindows();
        MainWindow mainWindow = init.initWindows();
        Logic antivirusLogic = new Logic();

        JTextArea progressArea = Awt1.getComponentByName(init.testPage, "progressArea");

        JCheckBox virustotalCheck = Awt1.getComponentByName(mainWindow, "virustotalCheck");

        Map viruses = FileWalker.readDatabase(progressArea);

        JTextArea virusDatabaseArea = Awt1.getComponentByName(init.databasePage, "virusDatabaseArea");
        setDatabaseTextArea(viruses, virusDatabaseArea);

        JButton scanButton = Awt1.getComponentByName(init.testPage, "scanButton");
        init.setButtonAction(scanButton, null, null, null, "testPage", new Runnable() {
            @Override
            // Callback after click on scan button
            public void run() {
                JTextField filePath = Awt1.getComponentByName(init.testPage, "filePath");
                try {
                    boolean sendOnVirusTotal = virustotalCheck.isSelected();
                    ArrayList resultAntivirus = antivirusLogic.runLogic(filePath.getText(), viruses, progressArea, sendOnVirusTotal);
                    JTextArea resultArea = Awt1.getComponentByName(init.testPage, "resultArea");
                    resultArea.setText(getResultText(resultAntivirus));
                } catch (Exception ex) {
                    init.setExceptionWindow(mainWindow, ex.getLocalizedMessage());
                }
            }
        });

        JButton saveDatabaseButton = Awt1.getComponentByName(init.databasePage, "saveDatabaseButton");
        init.setButtonAction(saveDatabaseButton, null, null, null, "databasePage", new Runnable() {
            @Override
            // Callback after click on save database button
            public void run() {
                JTextField newDatabaseField = Awt1.getComponentByName(init.databasePage, "newDatabaseField");
                try {
                    FileWalker fiWa = new FileWalker();
                    fiWa.addVirusDefinition(newDatabaseField.getText());
                    virusDatabaseArea.append(newDatabaseField.getText());
                    virusDatabaseArea.update(virusDatabaseArea.getGraphics());
                    newDatabaseField.setText("");
                    init.setExceptionWindow(mainWindow, "Přidáno. Pro aktivaci nových položek restartujte program.");
                } catch (Exception ex) {
                    init.setExceptionWindow(mainWindow, ex.getLocalizedMessage());
                }
            }
        });

    }

    public static void setDatabaseTextArea(Map viruses, JTextArea virusDatabaseArea) {
        String prefixDatabaseArea = "";
        String infixDatabaseArea = "\n----\n";
        String postfixDatabaseArea = "";
        
        Map<String, String> treeMap = new TreeMap<>(viruses);

        StringJoiner joinerViruses = new StringJoiner(infixDatabaseArea, prefixDatabaseArea, postfixDatabaseArea);
        treeMap.forEach((k, v) -> {
            joinerViruses.add(k + "=" + v);
        });

        virusDatabaseArea.setText(joinerViruses.toString());
    }

    public static String getResultText(ArrayList resultAntivirus) {
        String resultText;
        if (resultAntivirus.size() > 0) {
            String prefix = "";
            String infix = "\n\n----\n\n";
            String postfix = "";

            StringJoiner joiner = new StringJoiner(infix, prefix, postfix);
            resultAntivirus.forEach((i) -> {
                joiner.add(i.toString());
            });

            resultText = joiner.toString();
            resultText += "\n\nSoubory byly zkopírovány a zazipovány do karantény.\nCelkem nalezeno hrozeb: " + resultAntivirus.size();
        } else {
            resultText = "Nebyla nalezena žádná hrozba";
        }

        return resultText;
    }

}
