import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Scanner;

public class Hash_Salt {

    static HashMap<String, String[]> usuaris = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String opcio;

        do {
            System.out.println("\n--- HASH I SALT ---");
            System.out.println("1. Calcul de hashes");
            System.out.println("2. Integritat dels missatges");
            System.out.println("3. Registrar usuari");
            System.out.println("4. Iniciar sessio");
            System.out.println("0. Sortir");
            System.out.print("Tria una opcio: ");

            opcio = scanner.nextLine();

            switch (opcio) {
                case "1":
                    hash01(scanner);
                    break;

                case "2":
                    try {
                        hash02(scanner);
                    } catch (IOException e) {
                        System.out.println(
                                "Error amb el fitxer: " + e.getMessage()
                        );
                    }
                    break;

                case "3":
                    registrar(scanner);
                    break;

                case "4":
                    iniciarSessio(scanner);
                    break;

                case "0":
                    System.out.println("Programa finalitzat.");
                    break;

                default:
                    System.out.println("Opcio incorrecta.");
            }

        } while (!opcio.equals("0"));

        scanner.close();
    }

    public static void hash01(Scanner scanner) {

        System.out.print("Entra text: ");
        String textIn = scanner.nextLine();

        System.out.print("Entra text2: ");
        String textIn2 = scanner.nextLine();

        try {
            byte[] bytesIn = textIn.getBytes(StandardCharsets.UTF_8);
            byte[] bytesIn2 = textIn2.getBytes(StandardCharsets.UTF_8);

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            byte[] hashResult = sha256.digest(bytesIn);
            byte[] hashResult2 = sha256.digest(bytesIn2);

            StringBuilder textOut = new StringBuilder();
            StringBuilder textOut2 = new StringBuilder();

            for (int i = 0; i < hashResult.length; i++) {
                if (i > 0) {
                    textOut.append("-");
                }
                textOut.append(String.format("%02X", hashResult[i] & 0xFF));
            }

            for (int i = 0; i < hashResult2.length; i++) {
                if (i > 0) {
                    textOut2.append("-");
                }
                textOut2.append(String.format("%02X", hashResult2[i] & 0xFF));
            }

            if (textOut.toString().equals(textOut2.toString())) {
                System.out.println("Tenen el mateix Hash");
            } else {
                System.out.println("Son diferents");
            }

            System.out.println("Hash " + textIn + " " + textOut);
            System.out.println("Hash " + textIn2 + " " + textOut2);

            String hashOriginal = textOut.toString();

            System.out.println();
            System.out.println("Cambia la primera paraula");

            textIn = scanner.nextLine();
            bytesIn = textIn.getBytes(StandardCharsets.UTF_8);
            hashResult = sha256.digest(bytesIn);

            textOut = new StringBuilder();

            for (int i = 0; i < hashResult.length; i++) {
                if (i > 0) {
                    textOut.append("-");
                }
                textOut.append(String.format("%02X", hashResult[i] & 0xFF));
            }

            System.out.println("Hash " + textIn + " " + textOut);
            System.out.println("Hash abans del canvi: " + hashOriginal);

            if (hashOriginal.equals(textOut.toString())) {
                System.out.println("El hash es el mateix.");
            } else {
                System.out.println("El hash ha canviat.");
            }

            System.out.println("\nRepetim 3 vegades el hash del text actual:");

            for (int repeticio = 1; repeticio <= 3; repeticio++) {
                byte[] hashRepetit = sha256.digest(bytesIn);
                StringBuilder textRepetit = new StringBuilder();

                for (int i = 0; i < hashRepetit.length; i++) {
                    if (i > 0) {
                        textRepetit.append("-");
                    }

                    textRepetit.append(
                            String.format("%02X", hashRepetit[i] & 0xFF)
                    );
                }

                System.out.println("Intent " + repeticio + ": " + textRepetit);

                if (textOut.toString().equals(textRepetit.toString())) {
                    System.out.println("El resultat es mante.");
                } else {
                    System.out.println("El resultat ha canviat.");
                }
            }

        } catch (NoSuchAlgorithmException e) {
            System.out.println(
                    "No s'ha trobat l'algoritme SHA-256: " + e.getMessage()
            );
        }
    }

    public static void hash02(Scanner scanner) throws IOException {

        try {
            System.out.print("Entra text: ");
            String textIn = scanner.nextLine();

            Files.writeString(
                    Path.of("missatge.txt"),
                    textIn,
                    StandardCharsets.UTF_8
            );

            byte[] bytesIn = textIn.getBytes(StandardCharsets.UTF_8);

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            byte[] hashResult = sha256.digest(bytesIn);

            StringBuilder textOut = new StringBuilder();

            for (int i = 0; i < hashResult.length; i++) {
                if (i > 0) {
                    textOut.append("-");
                }
                textOut.append(String.format("%02X", hashResult[i] & 0xFF));
            }

            String hashOriginal = textOut.toString();

            Files.writeString(
                    Path.of("missatge.hash"),
                    hashOriginal,
                    StandardCharsets.UTF_8
            );

            System.out.println("Missatge i hash guardats.");
            System.out.println("Hash original: " + hashOriginal);

            System.out.print("Escriu una versio modificada del missatge: ");
            String missatgeCanviat = scanner.nextLine();

            Files.writeString(
                    Path.of("missatgeCanviat.txt"),
                    missatgeCanviat,
                    StandardCharsets.UTF_8
            );

            System.out.print(
                    "Receptor, quin fitxer vols llegir "
                    + "(missatge.txt o missatgeCanviat.txt)? "
            );

            String nomFitxer = scanner.nextLine();

            String missatgeRebut = Files.readString(
                    Path.of(nomFitxer),
                    StandardCharsets.UTF_8
            );

            byte[] bytesRebuts = missatgeRebut.getBytes(StandardCharsets.UTF_8);

            byte[] hashResultRebut = sha256.digest(bytesRebuts);

            StringBuilder textOutRebut = new StringBuilder();

            for (int i = 0; i < hashResultRebut.length; i++) {
                if (i > 0) {
                    textOutRebut.append("-");
                }

                textOutRebut.append(
                        String.format("%02X", hashResultRebut[i] & 0xFF)
                );
            }

            String hashRebut = textOutRebut.toString();

            String hashGuardat = Files.readString(
                    Path.of("missatge.hash"),
                    StandardCharsets.UTF_8
            );

            System.out.println("Hash original: " + hashGuardat);
            System.out.println("Hash rebut:    " + hashRebut);

            if (hashGuardat.equals(hashRebut)) {
                System.out.println("El missatge coincideix amb l'original.");
            } else {
                System.out.println("S'ha detectat una modificacio.");
            }

        } catch (NoSuchAlgorithmException e) {
            System.out.println(
                    "No s'ha trobat l'algoritme SHA-256: " + e.getMessage()
            );
        }
    }

    public static void registrar(Scanner scanner) {

        System.out.print("Nom d'usuari: ");
        String nom = scanner.nextLine();

        if (usuaris.containsKey(nom)) {
            System.out.println("Aquest usuari ja existeix.");
            return;
        }

        System.out.print("Contrasenya: ");
        String contrasenya = scanner.nextLine();

        try {
            SecureRandom generador = new SecureRandom();

            byte[] salt = new byte[16];
            generador.nextBytes(salt);

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            sha256.update(salt);

            byte[] hashResult = sha256.digest(
                    contrasenya.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder saltText = new StringBuilder();

            for (int i = 0; i < salt.length; i++) {
                saltText.append(String.format("%02X", salt[i] & 0xFF));
            }

            StringBuilder hashText = new StringBuilder();

            for (int i = 0; i < hashResult.length; i++) {
                hashText.append(
                        String.format("%02X", hashResult[i] & 0xFF)
                );
            }

            String[] dades = {
                    saltText.toString(),
                    hashText.toString()
            };

            usuaris.put(nom, dades);

            System.out.println("Usuari registrat correctament.");
            System.out.println("Salt: " + dades[0]);
            System.out.println("Hash: " + dades[1]);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("No s'ha trobat l'algoritme SHA-256.");
        }
    }

    public static void iniciarSessio(Scanner scanner) {

        System.out.print("Nom d'usuari: ");
        String nom = scanner.nextLine();

        System.out.print("Contrasenya: ");
        String contrasenya = scanner.nextLine();

        if (!usuaris.containsKey(nom)) {
            System.out.println("Usuari no registrat.");
            return;
        }

        String[] dades = usuaris.get(nom);

        String saltGuardada = dades[0];
        String hashGuardat = dades[1];

        byte[] salt = new byte[saltGuardada.length() / 2];

        for (int i = 0; i < salt.length; i++) {
            String parella = saltGuardada.substring(i * 2, i * 2 + 2);
            salt[i] = (byte) Integer.parseInt(parella, 16);
        }

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(salt);

            byte[] hashResult = sha256.digest(
                    contrasenya.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hashText = new StringBuilder();

            for (int i = 0; i < hashResult.length; i++) {
                hashText.append(
                        String.format("%02X", hashResult[i] & 0xFF)
                );
            }

            String hashCalculat = hashText.toString();

            if (hashGuardat.equals(hashCalculat)) {
                System.out.println("Acces permes. Benvingut, " + nom);
            } else {
                System.out.println("Contrasenya incorrecta.");
            }

        } catch (NoSuchAlgorithmException e) {
            System.out.println("No s'ha trobat l'algoritme SHA-256.");
        }
    }
}