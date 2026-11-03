package BillSystem;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private Scanner sc;
    private BillService service;
    private BillRepository repository;

    public ConsoleUI() {
    }

    public ConsoleUI(BillService service, Scanner sc, BillRepository repository) {
        this.service = service;
        this.sc = sc;
        this.repository = repository;
    }

    public Scanner getSc() {
        return sc;
    }

    public void setSc(Scanner sc) {
        this.sc = sc;
    }

    public BillService getService() {
        return service;
    }

    public void setService(BillService service) {
        this.service = service;
    }

    public void Start() {
        System.out.println("正在加载本地文件数据...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("数据加载完成，当前账单数量为：" + repository.getallbill().size());
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
                case "1" -> billmanager();
                case "2" -> deletebill();
                case "3" -> querybill();
                case "4" -> statistics();
                case "5" -> analysis();
                case "6" -> {
                    try {
                        exit();
                        break loop;
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("账单数据保存失败，请检查文件权限");
                    }
                }

                default -> System.out.println("您输入的操作有误，请重新输入");
            }
        }
    }

    public void billmanager() {
        System.out.println("----------------账单管理(增加,修改)-----------------");
        System.out.println("1:增加账单");
        System.out.println("2:修改账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> addbill();
            case "2" -> modifybill();
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    public void deletebill() {
        System.out.println("----------------删除账单-----------------");
        System.out.println("1:删除所有账单");
        System.out.println("2:按照ID删除指定账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> deleteallbill();
            case "2" -> deletebillById();
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    public void querybill() {
        System.out.println("----------------查询账单-----------------");
        System.out.println("1:查询所有账单");
        System.out.println("2:查询所有支出账单");
        System.out.println("3:查询所有收入账单");
        System.out.println("4:按日期查询当日账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> queryallbill();
            case "2" -> querybill("支出");
            case "3" -> querybill("收入");
            case "4" -> {
                while (true) {
                    System.out.println("请输入您要查询的日期（格式：yyyy-MM-dd）：");
                    String date = sc.nextLine().trim();
                    try {
                        service.checkDate(date);
                        querydaybill(date);
                        break;
                    } catch (DateTimeFormatException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    public void statistics() {
        System.out.println("----------------收支统计-----------------");
        System.out.println("1:统计所有账单");
        System.out.println("2:统计所有支出账单");
        System.out.println("3:统计所有收入账单");
        System.out.println("4:统计单日账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> statisticsallbillamount();
            case "2" -> statisticbillType("支出");
            case "3" -> statisticbillType("收入");
            case "4" -> {
                while (true) {
                    System.out.println("请输入您要统计的日期（格式：yyyy-MM-dd）：");
                    String date = sc.nextLine().trim();
                    try {
                        service.checkDate(date);
                        statisticsdaybillamount(date);
                        break;
                    } catch (DateTimeFormatException e) {
                        System.out.println("日期格式错误，请重新输入");
                    }
                }
            }
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    public void analysis() {
        System.out.println("----------------消费分析-----------------");
        System.out.println("1:分析所有账单");
        System.out.println("2:分析区间内账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> analysisallbill();
            case "2" -> {
                while (true) {
                    System.out.println("请输入您要分析的日期区间起始日期（格式：yyyy-MM-dd）：");
                    String startDate = sc.nextLine().trim();
                    try {
                        service.checkDate(startDate);
                    } catch (DateTimeFormatException e) {
                        System.out.println("日期格式错误，请重新输入");
                        continue;
                    }
                    System.out.println("请输入您要分析的日期区间结束日期（格式：yyyy-MM-dd）：");
                    String endDate = sc.nextLine().trim();
                    try {
                        service.checkDate(endDate);
                        analysisrangebill(startDate, endDate);
                        break;
                    } catch (DateTimeFormatException e) {
                        System.out.println("日期格式错误，请重新输入");
                    }
                }

            }
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    public void exit() throws IOException, ClassNotFoundException {
        System.out.println("----------------退出系统-----------------");
        try {
            service.saveAll();
            System.out.println("账单数据已保存到本地文件");
        } catch (IOException e) {
            System.out.println("保存账单数据到本地文件失败");
        }
        System.out.println("谢谢使用");
    }

    //----------------------------主方法区------------------------------------------
    //----------------------1.账单管理----------------
    //-----------增加账单方法-----------
    private void addbill() {
        int billId = service.getRandomBillId();
        LocalDateTime createTime;
        String billType;
        BigDecimal billAmount;
        String comment;
        while (true) {
            System.out.println("请输入账单创建时间：(格式：yyyy-MM-dd HH:mm:ss)");
            try {
                createTime = service.getBillCreateTime(sc.nextLine().trim());
                break;
            } catch (DateTimeFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            System.out.println("请输入账单类型：(支出/收入)");
            try {
                billType = service.checkBillType(sc.nextLine().trim());
                break;
            } catch (BillTypeException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            System.out.println("请输入账单金额");
            try {
                billAmount = service.checkAmount(sc.nextLine().trim());
                break;
            } catch (BillAmountException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            System.out.println("请输入备注(支出:吃喝(日常饮食)/玩乐(娱乐+娱乐饮食)/交通/其他支出等  收入:兼职/工资/其他收入等)");
            try {
                comment = service.checkComment(sc.nextLine().trim());
                break;
            } catch (BillCommentException e) {
                System.out.println(e.getMessage());
            }
        }
        service.createBill(billId, createTime, billType, billAmount, comment);
        System.out.println("账单添加成功");
    }

    //----------修改账单方法----------
    private void modifybill() {
        System.out.println("请先查询要修改的账单ID");
        querybill();
        int id;
        LocalDateTime createTime;
        BigDecimal billAmount;
        String comment;
        String billType;
        Bill bill;
        if (service.isBillEmpty()) {
            System.out.println("当前账单管理系统为空，无法修改账单");
            return;
        }
        while (true) {
            System.out.println("请输入要修改的账单ID");
            id = Integer.parseInt(sc.nextLine());
            if (!service.isBillIdExist(id)) {
                System.out.println("您输入的账单ID不存在，请重新输入");
            } else {
                bill = service.getBillById(id);
                break;
            }
        }
        while (true) {
            System.out.println("请输入修改后的账单创建时间：(格式：yyyy-MM-dd HH:mm:ss)");
            try {
                createTime = service.getBillCreateTime(sc.nextLine().trim());
                break;
            } catch (DateTimeFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            System.out.println("请输入修改后的账单类型：(支出/收入)");
            try {
                billType = service.checkBillType(sc.nextLine().trim());
                break;
            } catch (BillTypeException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            System.out.println("请输入修改后的账单金额");
            try {
                String amount = sc.nextLine().trim();
                billAmount = service.checkAmount(amount);
                break;
            } catch (BillAmountException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            System.out.println("请输入修改后的备注(支出:吃喝(日常饮食)/玩乐(娱乐+娱乐饮食)/交通/其他支出等  收入:兼职/工资/其他收入等)");
            try {
                comment = service.checkComment(sc.nextLine().trim());
                break;
            } catch (BillCommentException e) {
                System.out.println(e.getMessage());
            }
        }
        service.modifyBill(id, createTime, billType, billAmount, comment);
        System.out.println("账单修改成功");
    }
    //-------------2.删除账单方法--------------
    //----------删除所有账单方法----------
    private void deleteallbill() {
        service.deleteAllBill();
        System.out.println("所有账单删除成功");
    }

    //---------根据ID删除账单-------
    private void deletebillById() {
        System.out.println("请先查找对应的账单，再根据ID删除");
        querybill();
        int billId;
        System.out.println("请输入要删除的账单ID");
        billId = Integer.parseInt(sc.nextLine());
        service.deleteBill(billId);
        System.out.println("账单删除成功");
    }
    //------------------3.查询账单方法----------------
//----------查询所有账单方法----------
    private void queryallbill() {
        if (service.isBillEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        List<Bill> billList = service.queryAllBill();
        for (Bill bill : billList) {
            System.out.println(bill);
        }
    }
    //----------查询所有支出或收入账单方法----------
    private void querybill(String billType) {
        if (service.isBillEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        List<Bill> billList = service.queryBillByType(billType);
        if(billList.isEmpty()){
            System.out.println("当前账单管理系统中不存在"+billType+"账单");
        }
        for (Bill bill : billList) {
            System.out.println(bill);
        }
    }
    //---------查询某日账单方法----------
    private void querydaybill(String date) {
        if (service.isBillEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        List<Bill> dayBillList = service.queryDayBill(date);
        if(dayBillList.isEmpty()){
            System.out.println("当前账单管理系统中不存在"+date+"日的账单");
        }
        for (Bill bill : dayBillList) {
            System.out.println(bill);
        }
    }
    //----------------------4.收支统计---------------------
    //------------------收支统计方法---------------------
    //----------统计所有账单支出收入方法----------
    private void statisticsallbillamount() {
         queryallbill();
        if (service.isBillEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        BigDecimalBox allBillAmount = service.statisticsAllBillAmount();
        System.out.println("所有账单支出总金额为："+allBillAmount.getBillOutAmount());
        System.out.println("所有账单收入总金额为："+allBillAmount.getBillIncomeAmount());
    }
    //------------无日期限制时统计指定类型账单总金额方法----------
    private void statisticbillType(String billType) {
        querybill(billType);
        if (service.isBillEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        BigDecimal typeAmount = service.statisticsTypeBillTypeAmount(billType);
        System.out.println("所有"+billType+"账单总金额为："+typeAmount);
    }
    //------------有日期限制时统计该日期的账单总金额方法----------
    private void statisticsdaybillamount(String date) {
        querydaybill(date);
        if (service.isBillEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        BigDecimalBox dayBillAmount = service.statisticsdayBillAmount(date);
        System.out.println("该日期支出总金额为："+dayBillAmount.getBillOutAmount());
        System.out.println("该日期收入总金额为："+dayBillAmount.getBillIncomeAmount());
    }
    //------------------5.消费分析---------------------
    //----------消费分析方法----------
    //分析所有账单
    private void analysisallbill() {
        statisticsallbillamount();
        HashMap<String, BigDecimal> billCommentAmount = service.analysisAllBill();
        //遍历集合，输出所有账单备注及对应金额
        for (String comment : billCommentAmount.keySet()) {
            System.out.println(comment + "的账单总金额为：" + billCommentAmount.get(comment));
        }
    }
    //分析指定区间内账单
    private void analysisrangebill(String startDate, String endDate) {
        statistisrangebill(startDate, endDate);
        HashMap<String, BigDecimal> billCommentAmount = service.analysisRangeBill(startDate, endDate);
        //遍历集合，输出所有账单备注及对应金额
        for (String comment : billCommentAmount.keySet()) {
            System.out.println(comment + "的账单总金额为：" + billCommentAmount.get(comment));
        }
    }
    //------------统计区间内账单-----------------
    private void statistisrangebill( String startDate, String endDate) {
        BigDecimalBox billAmount = service.statistisrangebill(startDate, endDate);
        System.out.println("该日期区间内的支出账单总金额为：" + billAmount.getBillOutAmount());
        System.out.println("该日期区间内的收入账单总金额为：" + billAmount.getBillIncomeAmount());
    }
}
