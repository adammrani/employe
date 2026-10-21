package ma.ensias.employe.service;

import ma.ensias.employe.entite.Employe;
import ma.ensias.employe.reponse.EmployeReponse;
import ma.ensias.employe.repository.EmployeRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

// Imports vulnérables
import java.io.*;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EmployeService {

    @Autowired
    private EmployeRepo employeRepo;

    @Autowired
    private ModelMapper mapper;

    // VULNÉRABILITÉ 1 (HIGH) : Mot de passe en dur
    private static final String DB_PASSWORD = "SuperSecretPassword123!";

    public EmployeReponse getEmployeById(int id) {
        Optional<Employe> employe = employeRepo.findById(id);

        // VULNÉRABILITÉ 2 (HIGH) : Injection SQL
        executeUnsafeQuery(String.valueOf(id));

        // VULNÉRABILITÉ 3 (CRITICAL) : Path Traversal (Lecture de fichier arbitraire)
        readUserFile("../../../etc/passwd");

        // VULNÉRABILITÉ 4 (CRITICAL) : Chiffrement faible (DES) & Hachage faible (MD5)
        weakCryptography("donnees_sensibles");

        return mapper.map(employe, EmployeReponse.class);
    }

    private void executeUnsafeQuery(String userInput) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employedemo", "root", DB_PASSWORD);
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM employe WHERE id = " + userInput;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("User: " + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- NOUVELLE VULNÉRABILITÉ CRITIQUE : PATH TRAVERSAL ---
    private void readUserFile(String filePath) {
        try {
            // SonarQube va détecter une vulnérabilité critique de traversée de répertoire
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
        } catch (Exception e) {
            System.err.println("Erreur de lecture du fichier");
        }
    }

    // --- NOUVELLE VULNÉRABILITÉ CRITIQUE : CHIFFREMENT WEAK (DES / MD5) ---
    private void weakCryptography(String input) {
        try {
            // 1. Hachage faible MD5 (SonarQube Security Vulnerability)
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            // 2. Chiffrement symétrique obsolète DES (SonarQube Security Vulnerability)
            byte[] keyBytes = "12345678".getBytes(); // Clé 64 bits DES en dur
            SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherText = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- NOUVELLE VULNÉRABILITÉ CRITIQUE : UNTRUSTED DESERIALIZATION ---
    public Object deserializeData(byte[] byteData) {
        try {
            // Désérialisation d'objets non sécurisée pouvant mener à une exécution de code à distance (RCE)
            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
