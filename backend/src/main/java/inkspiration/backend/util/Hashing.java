package inkspiration.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Hashing {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hash(String data) {
        return encoder.encode(data);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}