package antivirus.GUI;

import antivirus.components.Awt1;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Jakub Cieciala <jakub.cieciala@gmail.com>
 */
public final class InitVindows {

    static JButton prevButton = null;
    public static String actionPage = null;

    static JButton buttonTest;
    static JButton buttonAbout;
    static JButton buttonDatabase;

    public static Test testPage;
    public static About aboutPage;
    public static Database databasePage;

    public InitVindows() {
        this.testPage = new Test();
        this.aboutPage = new About();
        this.databasePage = new Database();
    }

    /**
     * Init main windows
     *
     * @return MainWindow
     */
    public MainWindow initWindows() {
        MainWindow frame = new MainWindow();

        JPanel mainPanel = Awt1.getComponentByName(frame, "mainPanel");
        mainPanel.setLayout(new GridBagLayout());

        buttonTest = Awt1.getComponentByName(frame, "buttonTest");
        buttonAbout = Awt1.getComponentByName(frame, "buttonAbout");
        buttonDatabase = Awt1.getComponentByName(frame, "buttonDatabase");

        setButtonAction(buttonTest, mainPanel, frame, testPage, "tab", null);
        setButtonAction(buttonAbout, mainPanel, frame, aboutPage, "tab", null);
        setButtonAction(buttonDatabase, mainPanel, frame, databasePage, "tab", null);

        JButton fileSearch = Awt1.getComponentByName(testPage, "fileSearch");
        setButtonAction(fileSearch, null, null, null, "testPage", new Runnable() {
            @Override
            public void run() {
                initFileChooser(frame);
            }
        });

        buttonTest.doClick();
        frame.setVisible(true);

        return frame;
    }

    /**
     * Set redraw action for button after click
     *
     * @param button
     * @param mainPanel
     * @param frame
     * @param mainPanelComponent
     * @param action
     * @param callback
     */
    public void setButtonAction(JButton button, JPanel mainPanel, MainWindow frame, Window mainPanelComponent, String action, Runnable callback) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (action) {
                    case "tab":
                        if (prevButton != null) {
                            prevButton.setBackground(null);
                        }

                        button.setBackground(Color.red);
                        prevButton = button;
                        setActionPerformedButtons(mainPanel, frame, mainPanelComponent);
                        break;
                    case "testPage":
                        callback.run();
                        break;
                    case "databasePage":
                        callback.run();
                        break;
                }

            }
        });
    }

    /**
     * Set funtion for button or call in start to default
     *
     * @param mainPanel
     * @param frame
     * @param window
     */
    public void setActionPerformedButtons(JPanel mainPanel, MainWindow frame, Window window) {
        mainPanel.removeAll();
        JPanel aboutMainPanel = Awt1.getComponentByName(window, "mainPanel");
        mainPanel.add(aboutMainPanel);

        mainPanel.revalidate();
        mainPanel.repaint();

        frame.revalidate();
        frame.repaint();
    }

    public void initFileChooser(MainWindow frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            JTextField filePath = Awt1.getComponentByName(testPage, "filePath");
            filePath.setText(selectedFile.getAbsolutePath());
        }
    }

    public void setExceptionWindow(MainWindow frame, String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

}
