import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class GerarHash {
    private static String salt = "@#1237Zt";
    private static Integer iterationCount = 403;
    private static Integer keyLength = 512;

    public static String getHashSenha(String senha) throws Exception {
        byte[] result;
        try {
            result = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA512")
                .generateSecret(new PBEKeySpec(senha.toCharArray(),
                                salt.getBytes(),
                                iterationCount,
                                keyLength))
                .getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception("Problema ao gerar o hash");
        }
        return Base64.getEncoder().encodeToString(result);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hash admin123: " + getHashSenha("admin123"));
        System.out.println("Hash user123: " + getHashSenha("user123"));
    }
}