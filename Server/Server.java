package Server;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Date;

public class Server {
    private final Base64.Encoder encoder = Base64.getEncoder();

    public SecretKey generateKey(String outputFile, String pass) {
        SecureRandom srandom = new SecureRandom();
        SecretKey key = null;

        byte[] salt = new byte[8];
        srandom.nextBytes(salt);

        char[] password = pass.toCharArray();
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WITHHMACSHA256");
            KeySpec spec = new PBEKeySpec(password, salt, 10000, 128);
            key = factory.generateSecret(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            assert key != null;
            out.write(key.getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }

    static private byte[] processFile(Mac mac, InputStream in)
            throws java.io.IOException
    {
        byte[] ibuf = new byte[1024];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            mac.update(ibuf, 0, len);
        }
        return mac.doFinal();
    }

    public Date login(String pass) {
        Date date = new Date();
        String current = date.toString().replace(" ", "-").replace(":", "_");
        current = current.substring(0, current.length() - 13);

        Mac mac = null;
        try {
            mac = Mac.getInstance("HMACSHA256");
            mac.init(generateKey(current, pass));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        try (FileInputStream in = new FileInputStream(current)) {
            byte[] macb = new byte[0];
            try {
                macb = processFile(mac, in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return date;
    }
}
