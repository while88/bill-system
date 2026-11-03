//----------此类为此系统入口类，负责初始化系统并启动主循环----------
package v1;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileInputStream;
public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashMap<Integer,Bill> billMap = new HashMap<>();
        BillManager billManager = new BillManager();
        System.out.println("正在加载本地账单数据...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("E:\\java\\111\\账单.txt"));
            billMap = (HashMap<Integer,Bill>)ois.readObject();
            ois.close();
            System.out.println("本地账单数据加载成功，当前账单数量为：" + billMap.size() + "条");
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("本地文件为空,正在准备接受新数据");
        }

        loop: while (true) {
            System.out.println("----------------欢迎使用账单管理系统！-----------------");
            System.out.println("1:账单管理(增加,修改)");
            System.out.println("2:删除账单");
            System.out.println("3:查询账单");
            System.out.println("4:收支统计");
            System.out.println("5:消费分析");
            System.out.println("6:退出系统");
            System.out.println("请输入您要进行的操作：");
            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> billManager.billmanager(billMap);
                case "2" -> billManager.deletebill(billMap);
                case "3" -> billManager.querybill(billMap);
                case "4" -> billManager.statistics(billMap);
                case "5" -> billManager.analysis(billMap);
                case "6" -> {
                    try {
                        billManager.exit(billMap);
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("账单数据保存失败，请检查文件权限");
                    }
                    finally {
                        break loop;
                    }
                }
                default -> System.out.println("您输入的操作有误，请重新输入");
            }
        }

    }
}
