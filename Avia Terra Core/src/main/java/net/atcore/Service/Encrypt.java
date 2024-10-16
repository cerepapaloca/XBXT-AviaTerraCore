package net.atcore.Service;

import com.github.games647.craftapi.model.auth.Verification;
import lombok.Getter;
import org.bukkit.entity.Player;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.UUID;

@Getter
public class Encrypt {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final SecretKey secretKey;
    private final byte[] iv;

    public Encrypt() {
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

    public void encryptConnection(Verification verification, Player player) {
        String realUsername = verification.getName();
        if (realUsername == null) {
            throw new RuntimeException();
        }

        //setPremiumUUID(session.getUuid());
        SimulateOnlineMode.FakeStartPacket(realUsername, UUID.fromString("d422b2e3-3aac-3cda-815d-4482a6988619"), player);
    }

    public SecretKey decryptSharedSecret(byte[] encryptedSharedSecret)
        // Inicializa el cifrador RSA con la clave privada del servidor
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        return new SecretKeySpec(decrypt(privateKey, encryptedSharedSecret), "AES");
    }

    public byte[] decrypt(PrivateKey key, byte[] data)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public byte[] decryptToken(byte[] encryptedToken) throws Exception {
        // Inicializa el cifrador RSA con la clave privada del servidor
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey); // privateKey debe ser la clave privada del servidor

        // Realiza el descifrado y devuelve el token
        return cipher.doFinal(encryptedToken);
    }

    public byte[] encrypt(byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CFB8/PKCS5Padding");
        IvParameterSpec ivParams = new IvParameterSpec(this.iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
        return cipher.doFinal(plaintext);
    }

    public byte[] decrypt(byte[] ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CFB8/PKCS5Padding");
        IvParameterSpec ivParams = new IvParameterSpec(this.iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
        return cipher.doFinal(ciphertext);
    }

}

