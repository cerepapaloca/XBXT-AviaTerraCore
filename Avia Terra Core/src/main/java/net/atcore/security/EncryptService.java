package net.atcore.security;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Getter
public class EncryptService {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final SecretKey secretKey;
    private final byte[] iv;

    public EncryptService() {
        try {
            KeyPairGenerator keyGenRSA = KeyPairGenerator.getInstance("RSA");
            keyGenRSA.initialize(2048); // Tamaño de clave recomendado
            KeyPair pair = keyGenRSA.generateKeyPair();
            this.publicKey = pair.getPublic();
            this.privateKey = pair.getPrivate();


            // Generar una clave secreta para AES
            KeyGenerator keyGenAES = KeyGenerator.getInstance("AES");
            keyGenAES.init(128); // Puedes usar 128, 192 o 256 bits
            this.secretKey = keyGenAES.generateKey();

            // Generar un IV (Vector de Inicialización)
            this.iv = new byte[16]; // Tamaño del IV para AES
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(this.iv);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decryptData(byte[] data)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey); // privateKey debe ser la clave privada del servidor
        return cipher.doFinal(data);
    }

    private static final int ITERATIONS = 65536; //número de iteraciones
    private static final int KEY_LENGTH = 256; //longitud del hash (bits)
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Crea un hash seguro combina el nombre de usuario y la contraseña en la misma contraseña
     */

    @NotNull
    @Contract(pure = true)
    public static String hashPassword(String name, String password) {
        String s = name + password;// combina el nombre de usuario y la contraseña
        PBEKeySpec spec = new PBEKeySpec(s.toCharArray(), password.getBytes(), ITERATIONS, KEY_LENGTH);
        byte[] hash;
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            hash = skf.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(hash);
    }
}