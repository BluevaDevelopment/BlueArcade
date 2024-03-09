package net.blueva.arcade.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateManager {

    public static String onlineVersion = "";

    public static boolean isUpdateAvailable(String currentVersion) throws IOException {
        String url = "https://blueva.net/api/arcade/version.txt";
        onlineVersion = getOnlineVersion(url);

        return compareVersions(onlineVersion, currentVersion) > 0;
    }

    private static String getOnlineVersion(String url) throws IOException {
        StringBuilder result = new StringBuilder();

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }

        return result.toString().trim();
    }

    private static int compareVersions(String version1, String version2) {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");

        for (int i = 0; i < Math.min(v1.length, v2.length); i++) {
            int cmp = Integer.compare(Integer.parseInt(v1[i]), Integer.parseInt(v2[i]));
            if (cmp != 0) {
                return cmp;
            }
        }

        return Integer.compare(v1.length, v2.length);
    }
}