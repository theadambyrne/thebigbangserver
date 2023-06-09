package CS4442.OS.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Jokes {
    private String urlString = "https://v2.jokeapi.dev/joke/Miscellaneous?type=single&safe-mode";

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public String getJoke() {
        try {
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String jsonResponse = response.toString();
                String joke = jsonResponse.substring(jsonResponse.indexOf("joke") + 8,
                        jsonResponse.indexOf("flags") - 7);

                joke = joke.replace("\\\\n", "");

                connection.disconnect();

                return joke;
            } else {
                connection.disconnect();

                return "Failed to get joke";
            }

            // Close the connection
        } catch (IOException e) {
            return "Failed to get joke";
        }
    }

}
