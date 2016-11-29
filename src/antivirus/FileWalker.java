package antivirus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JTextArea;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class FileWalker {

    public static ArrayList filesList;
    private static final String newLine = System.getProperty("line.separator");

    public static ArrayList printFnames(String sDir, JTextArea progressArea) {
        try {
            ArrayList files = new ArrayList();
            Files.find(Paths.get(sDir), 999, (p, bfa) -> bfa.isRegularFile()).forEach((x) -> {
                Logic.setTextToProgressArea(progressArea, "Načítání souboru - " + String.valueOf(x));
                files.add(String.valueOf(x));
            });
            return files;
        } catch (IOException ex) {
            Logger.getLogger(FileWalker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String calcSHA1(File file) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
            try (InputStream input = new FileInputStream(file)) {

                byte[] buffer = new byte[8192];
                int len = input.read(buffer);

                while (len != -1) {
                    sha1.update(buffer, 0, len);
                    len = input.read(buffer);
                }
                String res = new HexBinaryAdapter().marshal(sha1.digest());
                return res.toLowerCase();
            } catch (IOException ex) {
                Logger.getLogger(FileWalker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileWalker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Map readDatabase(JTextArea progressArea) {
        Map<String, String> viruses = new HashMap<>();
        try {
            Scanner in = new Scanner(new File("database.txt"));
            Logic.setTextToProgressArea(progressArea, "Načítání virové databáze...");
            while (in.hasNext()) { // iterates each line in the file
                String line = in.nextLine();
                String lowerLine = line.toLowerCase();
                String[] exploded = lowerLine.split("=");
                viruses.put(exploded[0], exploded[1]);
            }
            Logic.setTextToProgressArea(progressArea, "Virová databáze načtena. Začněte s testováním.");

            in.close(); // don't forget to close resource leaks
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileWalker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return viruses;
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static boolean createZipArchive(Object[] files, JTextArea progressArea) {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        path = path.substring(0, path.length() - 1);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path + "/Quaraneteene.zip");
        } catch (FileNotFoundException ex) {
            return false;
        }

        try {
            try (ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (Object file : files) {
                    Logic.setTextToProgressArea(progressArea, "Zabalování souboru do karantény - " + String.valueOf(file));
                    byte[] buffer = new byte[1024];
                    String fileS = (String) file;
                    File actualFile = new File(fileS);
                    ZipEntry ze = new ZipEntry(fileS);
                    zos.putNextEntry(ze);
                    try (FileInputStream in = new FileInputStream(actualFile)) {
                        int len;
                        while ((len = in.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                    }
                }
                zos.closeEntry();

            }
            Logic.setTextToProgressArea(progressArea, "Všechny soubory byly zabaleny...");
            System.out.println("Done");
            return true;

        } catch (IOException ex) {
            return false;
        }
    }

    public boolean addVirusDefinition(String virusDefinition) {
        String fileName = "database.txt";
        PrintWriter printWriter = null;
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            printWriter = new PrintWriter(new FileOutputStream(fileName, true));
            printWriter.write(virusDefinition + newLine);
        } catch (IOException ioex) {
            return false;
        } finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
        }
        return true;
    }

}
