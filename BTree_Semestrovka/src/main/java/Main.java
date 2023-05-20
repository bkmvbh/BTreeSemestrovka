import java.io.*;
import java.util.Random;

public class Main {

    static int[] array;
    static int lenght = 0;
    static BTree tree = new BTree(5);

    public static void main(String[] args) throws IOException {
        createFile();

        readFile();

        fillArray();

        searchRandomElements();

        deleteRandomElements();
    }

    public static void readFile() throws IOException {
        String fileName = "random_numbers.txt";
        File file = new File(fileName);

        //создаем объект FileReader для объекта File
        FileReader fr = new FileReader(file);
        //создаем BufferedReader с существующего FileReader для построчного считывания
        BufferedReader reader = new BufferedReader(fr);
        // считаем сначала первую строку

        String line = reader.readLine();
        while (line != null) {
            lenght++;
            line = reader.readLine();
        }
        array = new int[lenght];
    }

    public static void fillArray() throws FileNotFoundException {
        String fileName = "random_numbers.txt";
        File file = new File(fileName);

        //создаем объект FileReader для объекта File
        FileReader fr = new FileReader(file);

        //создаем BufferedReader с существующего FileReader для построчного считывания
        BufferedReader reader = new BufferedReader(fr);
        try {
            int count = 0;
            String readerLine = reader.readLine();
            while (readerLine != null) {
                array[count] = Integer.parseInt(readerLine);
                count++;
                readerLine = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int count = 0;
        long sum = 0;
        for (int i = 0; i < lenght; i++) {
            long time = tree.insert(array[i]);
            if (time != 0) {
                count++;
                sum+=time;
            }
        }

        System.out.println("Среднее время добавления: " + sum/count);
    }

    public static void createFile() {
        // Имя файла и количество чисел, которые нужно записать
        String fileName = "random_numbers.txt";
        int numOfNumbers = 10000;
        // Создание объекта Random для генерации случайных чисел
        Random random = new Random();
        try {
            // Создание объекта FileWriter для записи данных в файл
            FileWriter fileWriter = new FileWriter(fileName);
            // Запись первых чисел с переносом строки
            for (int i = 0; i < numOfNumbers - 1; i++) {
                int num = random.nextInt(100000); // Случайное число от 0 до 99999
                fileWriter.write(num + "\n");
            }
            // Запись последнего числа без переноса строки
            int num = random.nextInt(100000); // Случайное число от 0 до 99999
            fileWriter.write(num + "");
            // Закрытие FileWriter
            fileWriter.close();
            System.out.println("Файл " + fileName + " успешно создан.");
        } catch (IOException ex) {
            System.out.println("Ошибка при записи в файл " + fileName);
            ex.printStackTrace();
        }
    }

    public static void searchRandomElements() {
        Random random = new Random();
        int count = 0;
        long sum = 0;
        for (int i = 0; i < 100; i++) {
            int num = random.nextInt(lenght); // Случайное число от 0 до 99999
            tree.search(array[num]);
            long time = tree.getTimeForSearching();
            if (time != 0) {
                count++;
                sum+=time;
            }
        }
        System.out.println("Среднее время поиска: " + sum/count);
    }

    public static void deleteRandomElements() {
        Random random = new Random();
        int count = 0;
        long sum = 0;
        for (int i = 0; i < 1000; i++) {
            int num = random.nextInt(lenght); // Случайное число от 0 до 99999
            long time = tree.remove(array[num]);
            if (time != 0) {
                count++;
                sum+=time;
            }
        }
        System.out.println("Среднее время удаления: " + sum/count);
    }
}