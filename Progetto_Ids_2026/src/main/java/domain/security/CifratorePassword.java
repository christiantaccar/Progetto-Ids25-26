package domain.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * Cifra e verifica le password. Non memorizza mai la password in chiaro:
 * il valore prodotto ha la forma "salt:hash", entrambi codificati in Base64.
 *
 * Nota: SHA-256 con salt e' sufficiente per lo scopo didattico di questo
 * progetto; in un sistema reale si userebbe un algoritmo pensato per le
 * password (bcrypt, scrypt, Argon2), che e' volutamente lento.
 */
public final class CifratorePassword {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SEPARATORE = ":";

    private CifratorePassword() {
        // classe di sola utilita': non istanziabile
    }

    /** Restituisce la rappresentazione cifrata della password, con salt casuale. */
    public static String cifra(String password) {
        Objects.requireNonNull(password, "Password obbligatoria");

        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);

        String saltCodificato = Base64.getEncoder().encodeToString(salt);
        return saltCodificato + SEPARATORE + hash(password, salt);
    }

    /** Verifica se la password in chiaro corrisponde alla forma cifrata data. */
    public static boolean verifica(String password, String passwordCifrata) {
        Objects.requireNonNull(password, "Password obbligatoria");
        Objects.requireNonNull(passwordCifrata, "Password cifrata obbligatoria");

        String[] parti = passwordCifrata.split(SEPARATORE);
        if (parti.length != 2) {
            throw new IllegalArgumentException("Formato della password cifrata non valido");
        }

        byte[] salt = Base64.getDecoder().decode(parti[0]);
        return confrontoATempoCostante(hash(password, salt), parti[1]);
    }

    private static String hash(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] risultato = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(risultato);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo di hashing non disponibile", e);
        }
    }

    /**
     * Confronto che impiega sempre lo stesso tempo indipendentemente da dove
     * le due stringhe differiscono: evita di rivelare informazioni sull'hash.
     */
    private static boolean confrontoATempoCostante(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int differenza = 0;
        for (int i = 0; i < a.length(); i++) {
            differenza |= a.charAt(i) ^ b.charAt(i);
        }
        return differenza == 0;
    }
}
