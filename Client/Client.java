package Client;

import sun.net.www.protocol.http.HttpURLConnection;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Client {

    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public void connect() {
        HttpURLConnection con = null;
        try {
            URL url = new URL("file:\\Server\\Server.java.login(?)");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("param1", "val");

        assert con != null;
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "client/server");
        try {
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(Client.getParamsString(parameters));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }
}
