package Module1_final;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.CheckedInputStream;

public class Main {

    private static final String CIPHER = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,:-!? ";
    private static String path;

    public static void main(String[] args) throws IOException {
        System.out.println("Здравствуйте! Назовите своё имя");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        System.out.println("Очень приятно, " + name + "!");
        System.out.println("Что бы вы хотели сделать?");
        System.out.println("Нажмите 1, чтобы зашифровать текст");
        System.out.println("Нажмите 2, чтобы расшифровать текст");
        System.out.println("Нажмите 3, чтобы взломать шифр перебором");
        System.out.println("Нажмите 4, чтобы провести анализ шифра");
        int number = sc.nextInt();

        if (number == 2) {
            System.out.println("Отлично! Введите ключ");
            int key = sc.nextInt();
            System.out.println("Укажите путь к файлу");
            Scanner input = new Scanner(System.in);
            path = input.nextLine();
            String text = textFromFile(path);
            String result = caesarCipher(text, key, false);
            writeToFile(result);
            System.out.println("Файл расшифрован");

        } else if (number == 1) {
            System.out.println("Отлично! Введите число от 1 до " + Integer.MAX_VALUE);
            int key = sc.nextInt();
            System.out.println("Укажите путь к файлу");
            Scanner input = new Scanner(System.in);
            path = input.nextLine();
            String text = textFromFile(path);
            String result = caesarCipher(text, key, true);
            writeToFile(result);
            System.out.println("Файл зашифрован");

        } else if (number == 3) {
            System.out.println("Укажите путь к файлу");
            Scanner input = new Scanner(System.in);
            path = input.nextLine();
            String text = textFromFile(path);
            String result = bruteForce(text);
            if (result == null) {
                System.out.println("Извините, не удалось расшифровать текст");
            } else {
                writeToFile(result);
                System.out.println("Файл расшифрован");
            }

        } else if (number == 4) {
            System.out.println("Укажите путь к зашифрованному файлу");
            Scanner input = new Scanner(System.in);
            path = input.nextLine();
            String cipherText = textFromFile(path);
            System.out.println("Укажите путь к файлу для анализа");
            path = input.nextLine();
            String normalText = textFromFile(path);
            String result = analysis(cipherText, normalText);
            writeToFile(result);
            System.out.println("Анализ проведен, файл расшифрован");

        } else System.out.println("Вы ввели неверное значение, попробуйте заново!");
    }

    public static String caesarCipher(String text, int key, boolean isEncode) throws IOException {

        char[] input = text.toCharArray();
        char[] alpha = CIPHER.toCharArray();
        char[] result = new char[input.length];
        key = isEncode ? key : Math.abs(CIPHER.length() - (key % alpha.length));
        for (int i = 0; i < input.length; i++) {
            if (CIPHER.contains(Character.toString(input[i]))) {
                int j = CIPHER.indexOf(input[i]);
                result[i] = alpha[(j + key) % alpha.length];
            } else {
                result[i] = input[i];
            }
        }

        return String.valueOf(result);
    }

    public static String textFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Извините, файл не найден";
    }

    public static void writeToFile(String content) throws IOException {
        String newPath = path.replace(".txt", "_1.txt");
        Files.write(Paths.get(newPath), content.getBytes());
    }

    /**
     * @param text - encrypted text
     * @return - decrypted text or @null if the text could not be decrypted
     * @throws IOException
     */
    public static String bruteForce(String text) throws IOException {
        char[] alpha = CIPHER.toCharArray();
        for (int i = 1; i <= CIPHER.length(); i++) {
            String result = caesarCipher(text, i, false);
            if (Pattern.compile("(.([.!?]\\s)([А-ЯA-Z]))|(.,\\s.)").matcher(result).find()) {
                return result;
            }
        }
        return null;
    }

    public static HashMap<Character, Double> symbolCount(String text) {

        char[] symbols = CIPHER.toCharArray();
        int counter = 0;

        HashMap<Character, Double> symbolFrequency = new HashMap<>();

        for (int i = 0; i < CIPHER.length(); i++) {
            symbolFrequency.put(symbols[i], 0.0);
        }

        for (Character letter : text.toCharArray()) {
            if (symbolFrequency.containsKey(letter)) {
                symbolFrequency.put(letter, symbolFrequency.get(letter)+ 1);
            } else{
                counter++;
            }

        }

        for (Character key : symbolFrequency.keySet()) {
            symbolFrequency.put(key, symbolFrequency.get(key)/(text.length()-counter));
        }
        return symbolFrequency;

    }

    public static LinkedHashMap <Character, Double> mapSort (HashMap<Character, Double> map){
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
    public static String analysis (String cipherText,String additionalText){
        //System.out.println(mapSort(symbolCount(cipherText)));
        //System.out.println(mapSort(symbolCount(additionalText)));

        ArrayList <Character> ciphered = new ArrayList<Character>(mapSort(symbolCount(cipherText)).keySet());
        ArrayList <Character> normal = new ArrayList<Character>(mapSort(symbolCount(additionalText)).keySet());

        char [] forText = cipherText.toCharArray();
        char [] decoded = new char[forText.length];
        char [] sortedCiph = ciphered.toString().toCharArray();
        char [] sortedNorm = normal.toString().toCharArray();

        for (int i = 0; i < cipherText.length() ; i++) {
            for (int j = 0; j <sortedCiph.length; j++) {
                if (forText[i] == sortedCiph[j]){
                    decoded[i] = sortedNorm[j];
                }
            }
        }

        return String.valueOf(decoded);
    }
}
