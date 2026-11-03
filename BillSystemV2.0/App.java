package BillSystem;
import java.util.HashMap;
import java.util.Scanner;
public class App {
    public static void main(String[] args) {
       BillRepository repository = new BillRepository("E:\\java\\111\\账单.txt");
       BillService service = new BillService(repository);
       ConsoleUI ui = new ConsoleUI(service, new Scanner(System.in), repository);
       ui.Start();
    }
}
