package tec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

public class UpdateChecker {

    public static void checkUpdate(String currentVersion) {
        String[] currentVersions = currentVersion.split(" ");

        StringBuilder latestVersion = new StringBuilder();
        try {
            URL versionCheckUrl = new URL("https://raw.githubusercontent.com/yoyosource/tec/master/src/main/resources/tec/info/tec.cnf");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(versionCheckUrl.openStream()));

            String inputLine = "";
            while ((inputLine = bufferedReader.readLine()) != null) {
                latestVersion.append(inputLine + "\n");
            }
        } catch (IOException e) {
            return;
        }

        String[] latestVersions = latestVersion.toString().split("\n")[0].substring("Tec Version ".length()).split(" ");

        try {
            if (checkNumbers(currentVersions[0], latestVersions[0])) {
                updateMessage(latestVersion.toString().split("\n")[0]);
            } else if (latestVersions.length == 1 && currentVersions.length == 2) {
                updateMessage(latestVersion.toString().split("\n")[0]);
            } else if (latestVersions.length == 2 && currentVersions.length == 2 && checkAlphaBeta(currentVersions[1], latestVersions[1])) {
                updateMessage(latestVersion.toString().split("\n")[0]);
            }
            return;
        } catch (NumberFormatException e) {

        }
    }

    private static boolean checkNumbers(String currentVersion, String latestVersion) throws NumberFormatException {
        String[] currentVersions = currentVersion.split("\\.");
        String[] latestVersions = latestVersion.split("\\.");

        if (Integer.parseInt(currentVersions[0]) < Integer.parseInt(latestVersions[0])) {
            return true;
        }

        if (Integer.parseInt(currentVersions[1]) < Integer.parseInt(latestVersions[1])) {
            return true;
        }

        return false;
    }

    private static boolean checkAlphaBeta(String currentVersion, String latestVersion) {
        if (currentVersion.equals("ALPHA") && latestVersion.equals("BETA")) {
            return true;
        }

        return false;
    }

    private static void updateMessage(String latestVersion) {
        System.out.println("\nThere is a new Version available on GitHub.\n" + latestVersion + "\nCheck it out on 'https://github.com/yoyosource/tec'");
    }

}
