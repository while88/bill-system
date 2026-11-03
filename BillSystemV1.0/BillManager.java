//----------此类属于此系统核心逻辑层，负责管理账单----------
package v1;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;
import java.time.format.ResolverStyle;
import java.util.Scanner;
import java.util.Random;
import java.util.HashMap;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
public class BillManager {
    //利用HashMap存储账单，key为账单id，以多态方式存储value为账单对象
    Random random = new Random();
    Scanner sc = new Scanner(System.in);
    public BillManager() {
    }
    //----------------------------主方法区------------------------------------------
    //------------1.账单管理 ---------------
    public void billmanager(HashMap<Integer, Bill> billMap){
        System.out.println("----------------账单管理(增加,修改)-----------------");
        System.out.println("1:增加账单");
        System.out.println("2:修改账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> addbill(billMap);
            case "2" -> {
                System.out.println("请先查找对应的账单，再根据ID修改");
                querybill(billMap);
                modifybill(billMap);
            }
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    //------------2.删除账单 ---------------
    public void deletebill(HashMap<Integer, Bill> billMap) {
        System.out.println("----------------删除账单-----------------");
        System.out.println("1:删除所有账单");
        System.out.println("2:按照ID删除指定账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> deleteallbill(billMap);
            case "2" -> {
                System.out.println("请先查找对应的账单，再根据ID删除");
                querybill(billMap);
                System.out.println("请输入您要删除的账单ID：");
                int billId = Integer.parseInt(sc.nextLine());
                deletebill(billMap, billId);
            }
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    //------------3.查询账单 ---------------
    public void querybill(HashMap<Integer, Bill> billMap) {
        System.out.println("----------------查询账单-----------------");
        System.out.println("1:查询所有账单");
        System.out.println("2:查询所有支出账单");
        System.out.println("3:查询所有收入账单");
        System.out.println("4:按日期查询当日账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> queryallbill(billMap);
            case "2" -> querybill(billMap, "支出");
            case "3" -> querybill(billMap, "收入");
            case "4" -> {
                while (true) {
                    System.out.println("请输入您要查询的日期（格式：yyyy-MM-dd）：");
                    String date = sc.nextLine().trim();
                    try {
                        checkDateValid(date);
                        querydaybill(billMap, date);
                        break;
                    } catch (DateTimeFormatException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    //-----------收支统计---------------
    public void statistics(HashMap<Integer, Bill> billMap) {
        System.out.println("----------------收支统计-----------------");
        System.out.println("1:统计所有账单");
        System.out.println("2:统计所有支出账单");
        System.out.println("3:统计所有收入账单");
        System.out.println("4:统计单日账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> statisticsbill(billMap, "支出", "收入");
            case "2" -> statisticsbill(billMap, "支出");
            case "3" -> statisticsbill(billMap, "收入");
            case "4" -> {
                while (true) {
                    System.out.println("请输入您要统计的日期（格式：yyyy-MM-dd）：");
                    String date = sc.nextLine().trim();
                    try {
                        checkDateValid(date);
                        statisticsdaybill(billMap, date);
                        break;
                    } catch (DateTimeFormatException e) {
                        System.out.println("日期格式错误，请重新输入");
                    }
                }
            }
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    //--------------消费分析---------------
    public void analysis(HashMap<Integer, Bill> billMap) {
        System.out.println("----------------消费分析-----------------");
        System.out.println("1:分析所有账单");
        System.out.println("2:分析区间内账单");
        System.out.println("请输入您要进行的操作：");
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> analysisbill(billMap);
            case "2" -> {
                while (true) {
                    System.out.println("请输入您要分析的日期区间起始日期（格式：yyyy-MM-dd）：");
                    String startDate = sc.nextLine().trim();
                    try {
                        checkDateValid(startDate);
                    } catch (DateTimeFormatException e) {
                        System.out.println("日期格式错误，请重新输入");
                        continue;
                    }
                    System.out.println("请输入您要分析的日期区间结束日期（格式：yyyy-MM-dd）：");
                    String endDate = sc.nextLine().trim();
                    try {
                        checkDateValid(endDate);
                        analysisbill(billMap, startDate, endDate);
                        break;
                    } catch (DateTimeFormatException e) {
                        System.out.println("日期格式错误，请重新输入");
                    }
                }

            }
            default -> System.out.println("您输入的操作有误，请重新输入");
        }
    }

    //--------------退出系统---------------
    public void exit(HashMap<Integer, Bill> billMap) throws IOException, ClassNotFoundException {
        System.out.println("----------------退出系统-----------------");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("E:\\java\\111\\账单.txt"));
        oos.writeObject(billMap);
        oos.close();
        System.out.println("谢谢使用");
    }

    //----------------------------主方法区------------------------------------------
    //----------------------1.账单管理----------------
    //-----------增加账单方法-----------
    private void addbill(HashMap<Integer, Bill> billMap)  {
        int billId;
        LocalDateTime createTime;
        String comment;
        while (true) {
            billId = random.nextInt(1000) + 1;
            if (!isIdExist(billId, billMap)) {
                break;
            }
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
        while (true) {
            System.out.println("请输入账单创建时间：(格式：yyyy-MM-dd HH:mm:ss)");
            try {
                createTime = readTime(sc, dateTimeFormatter);
                break;
            } catch (DateTimeFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("请输入账单类型：(支出/收入)");
        String billType = sc.nextLine();
        while (!(billType.equals("支出") || billType.equals("收入"))) {
            System.out.println("您输入的账单类型有误，请重新输入");
            billType = sc.nextLine();
        }
        BigDecimal billAmount ;
        while (true) {
            System.out.println("请输入账单金额");
            try {
                String amount = sc.nextLine().trim();
                billAmount = cheakAmount(amount);
                break;
            } catch (BillAmountException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            System.out.println("请输入备注(支出:吃喝(日常饮食)/玩乐(娱乐+娱乐饮食)/交通/其他支出等  收入:兼职/工资/其他收入等)");
            comment = sc.nextLine().trim();
            if (!((comment.equals("吃喝") || comment.equals("玩乐") || comment.equals("交通") || comment.equals("其他支出") ||
                    comment.equals("兼职") || comment.equals("工资") || comment.equals("其他收入")))) {
                System.out.println("您输入的备注有误，请重新输入");
            }
            else{
                break;
            }
        }
        Bill bill = new Bill(billId, createTime, billType, billAmount, comment);
        billMap.put(billId, bill);
        System.out.println("账单添加成功");
    }

    //------利用正则表达式以及异常处理，双重判断时间格式是否正确，是否为有效时间格式
    //参数传入Scanner对象是为了代码解耦
    private LocalDateTime readTime(Scanner sc, DateTimeFormatter dateTimeFormatter) throws DateTimeFormatException {
        //^表示以什么开头，$表示以什么结尾，两个符号表示强制从头到尾必须完全匹配，但是matches()其实本身就有这种功能，所以这里可以省略，但是要养成习惯
        String str = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
        //trim()方法用于移除字符串首尾的空格,并保留中间的空格字符，避免用户输入空格导致的异常
        String createTimeStr = sc.nextLine().trim();
        if (createTimeStr.matches(str)) {
            try {
                return LocalDateTime.parse(createTimeStr, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                throw new DateTimeFormatException("您输入的时间格式有误，请重新输入");
            }
        } else {
            throw new DateTimeFormatException("您输入的时间格式有误，正确格式为：yyyy-MM-dd HH:mm:ss，请重新输入");
        }
    }

    //判断ID是否已经存在
    private boolean isIdExist(int billId, HashMap<Integer, Bill> billMap) {
        return billMap.containsKey(billId);
    }
    //判断金额输入是否为有效金额
    private BigDecimal cheakAmount(String Amount) throws BillAmountException {
        BigDecimal billAmount;
        if(Amount==null || Amount.trim().isEmpty()){
            throw new BillAmountException("您输入的金额为空，请重新输入(非空格)");
        }
        else {
            try {
                billAmount = new BigDecimal(Amount);
                if(billAmount.compareTo(BigDecimal.ZERO) <= 0){
                    throw new BillAmountException("您输入的金额必须大于0，请重新输入");
                }
            } catch (NumberFormatException e) {
                throw new BillAmountException("您输入的金额有误，请重新输入(数字)");
            }
        }
        return billAmount;
    }
    //----------修改账单方法----------
    private void modifybill(HashMap<Integer, Bill> billMap) {
        LocalDateTime createTime;
        BigDecimal billAmount;
        String comment;
        if (billMap.isEmpty()) {
            System.out.println("当前账单管理系统为空，无法修改账单");
            return;
        }
        Bill bill = null;
        while (true) {
            System.out.println("请输入要修改的账单ID");
            int id = Integer.parseInt(sc.nextLine());
            bill = billMap.get(id);
            if (bill == null) {
                System.out.println("您输入的账单ID不存在，请重新输入");
            } else {
                break;
            }
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
        while (true) {
            System.out.println("请输入修改后的账单创建时间：(格式：yyyy-MM-dd HH:mm:ss)");
            try {
                createTime = readTime(sc, dateTimeFormatter);
                break;
            } catch (DateTimeFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("请输入修改后的账单类型：(支出/收入)");
        String billType = sc.nextLine();
        while (!(billType.equals("支出") || billType.equals("收入"))) {
            System.out.println("您输入的账单类型有误，请重新输入");
            billType = sc.nextLine();
        }
        while(true) {
            System.out.println("请输入修改后的账单金额");
            try {
                String amount = sc.nextLine().trim();
                billAmount = cheakAmount(amount);
                break;
            } catch (BillAmountException e) {
                System.out.println(e.getMessage());
            }
        }
        while(true) {
            System.out.println("请输入修改后的备注(支出:吃喝(日常饮食)/玩乐(娱乐+娱乐饮食)/交通/其他支出等  收入:兼职/工资/其他收入等)");
            comment = sc.nextLine().trim();
            if (!((comment.equals("吃喝") || comment.equals("玩乐") || comment.equals("交通") || comment.equals("其他支出") ||
                    comment.equals("兼职") || comment.equals("工资") || comment.equals("其他收入")))) {
                System.out.println("您输入的备注有误，请重新输入");
            } else {
                break;
            }
        }
        bill.setCreateTime(createTime);
        bill.setBillType(billType);
        bill.setBillAmount(billAmount);
        bill.setComment(comment);
        System.out.println("账单修改成功");
    }

    //-------------2.删除账单方法--------------
    //----------删除所有账单方法----------
    private void deleteallbill(HashMap<Integer, Bill> billMap) {
        billMap.clear();
        System.out.println("所有账单删除成功");
    }

    //---------根据ID删除账单-------
    private void deletebill(HashMap<Integer, Bill> billMap, int billId) {
        billMap.remove(billId);
        System.out.println("账单删除成功");
    }

    //------------------3.查询账单方法----------------
//----------查询所有账单方法----------
    private void queryallbill(HashMap<Integer, Bill> billMap) {
        if (billMap.isEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        List<Bill> billList = new ArrayList<>(billMap.values());
        billList.sort(Comparator.comparing(Bill::getCreateTime).reversed());
        for (Bill bill : billList) {
            System.out.println(bill.toString());
        }
    }

    //----------查询所有支出或收入账单方法----------
    private void querybill(HashMap<Integer, Bill> billMap, String billType) {
        if (billMap.isEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        List<Bill> inoutBillList = new ArrayList<>();
        Set<Map.Entry<Integer, Bill>> billSet = billMap.entrySet();
        for (Map.Entry<Integer, Bill> entry : billSet) {
            if (entry.getValue().getBillType().equals(billType)) {
                inoutBillList.add(entry.getValue());
            }
        }
        if (inoutBillList.isEmpty()) {
            System.out.println("当前账单管理系统中没有" + billType + "账单");
            return;
        }
        inoutBillList.sort(Comparator.comparing(Bill::getCreateTime).reversed());
        for (Bill bill : inoutBillList) {
            System.out.println(bill.toString());
        }
    }

    //---------查询某日账单方法----------
    private void querydaybill(HashMap<Integer, Bill> billMap, String date) {
        if (billMap.isEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        List<Bill> dayBillList = new ArrayList<>();
        Set<Map.Entry<Integer, Bill>> billSet = billMap.entrySet();
        for (Map.Entry<Integer, Bill> entry : billSet) {
            if (entry.getValue().getCreateTime().toLocalDate().toString().equals(date)) {
                dayBillList.add(entry.getValue());
            }
        }
        if (dayBillList.isEmpty()) {
            System.out.println("该日期没有账单");
            return;
        }
        dayBillList.sort(Comparator.comparing(Bill::getCreateTime).reversed());
        for (Bill bill : dayBillList) {
            System.out.println(bill.toString());
        }
    }

    //------------------4.收支统计方法---------------------
    //----------统计所有账单或支出或收入账单方法------------
    private void statisticsbill(HashMap<Integer, Bill> billMap, String... billTypes) {
        if (billTypes.length == 2) {
            queryallbill(billMap);
        } else {
            if (billTypes[0].equals("支出")) {
                querybill(billMap, "支出");
            } else {
                querybill(billMap, "收入");
            }
        }
        statisticsbillamount(billMap, billTypes);
    }

    //----------统计某种类型账单总金额方法----------
    private void statisticsbillamount(HashMap<Integer, Bill> billMap, String... billTypes) {
        if (billMap.isEmpty()){
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        List<Bill> billList = new ArrayList<>(billMap.values());
        //无日期限制时统计所有账单的总金额
        BigDecimal outAmount = BigDecimal.ZERO;
        BigDecimal inAmount = BigDecimal.ZERO;
        //有日期限制时统计该日期的账单总金额
        BigDecimal dayOutAmount = BigDecimal.ZERO;
        BigDecimal dayInAmount = BigDecimal.ZERO;
        if (billTypes.length == 3) {
            for (Bill bill : billList) {
                if (bill.getCreateTime().toLocalDate().toString().equals(billTypes[0])) {
                    if (bill.getBillType().equals("支出")) {
                        dayOutAmount = dayOutAmount.add(bill.getBillAmount());
                    } else {
                        dayInAmount = dayInAmount.add(bill.getBillAmount());
                    }
                }
            }
            System.out.println("该日期的" + billTypes[1] + "账单总金额为：" + dayOutAmount);
            System.out.println("该日期的" + billTypes[2] + "账单总金额为：" + dayInAmount);
            return;
        }
        for (Bill bill : billList) {
            if (bill.getBillType().equals("支出")) {
                outAmount = outAmount.add(bill.getBillAmount());
            } else {
                inAmount = inAmount.add(bill.getBillAmount());
            }
        }
        if (billTypes.length == 2) {
            System.out.println("所有" + billTypes[0] + "账单总金额为：" + outAmount);
            System.out.println("所有" + billTypes[1] + "账单总金额为：" + inAmount);
        } else if (billTypes.length == 1) {
            if (billTypes[0].equals("支出")) {
                System.out.println("所有支出账单总金额为：" + outAmount);
            } else {
                System.out.println("所有收入账单总金额为：" + inAmount);
            }
        }
    }

    //----------统计单日账单方法----------
    private void statisticsdaybill(HashMap<Integer, Bill> billMap, String date) {
        querydaybill(billMap, date);
        statisticsbillamount(billMap, date, "支出", "收入");
    }

    //----------查询与统计单日账单的日期校验方法----------
    private void checkDateValid(String date) throws DateTimeFormatException {
        if(date==null){
            throw new DateTimeFormatException("您输入的日期不能为空,请重新输入");
        }
        //.withResolverStyle(ResolverStyle.STRICT)把智能模式切换为严格模式校验日期格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
        String str = "^\\d{4}-\\d{2}-\\d{2}$";
        if (date.matches(str)) {
            try {
                LocalDate.parse(date, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                throw new DateTimeFormatException("您输入的日期格式有误,请重新输入");
            }
        }
        else {
            throw new DateTimeFormatException("您输入的日期格式有误,请重新输入");
        }
    }

    //------------------5.消费分析---------------------
    //----------消费分析方法----------
    //分析所有账单
    private void analysisbill(HashMap<Integer, Bill> billMap, String... datas) {
        if (billMap.isEmpty()) {
            System.out.println("当前账单管理系统为空,请先添加账单");
            return;
        }
        if (datas.length == 0) {
            statisticsbillamount(billMap, "支出", "收入");
            //创建HashMap集合，键值为账单备注，键值为该账单金额
            HashMap<String, BigDecimal> commentMap = new HashMap<>();
            for (Bill bill : billMap.values()) {
                if (commentMap.containsKey(bill.getComment())) {
                    commentMap.put(bill.getComment(), commentMap.get(bill.getComment()).add(bill.getBillAmount()));
                } else {
                    commentMap.put(bill.getComment(), bill.getBillAmount());
                }
            }
            System.out.println("----------------所有账单分析-----------------");
            //遍历commentMap集合，输出所有账单备注及对应金额
            for (String comment : commentMap.keySet()) {
                System.out.println(comment + "账单总金额为：" + commentMap.get(comment));
            }
        }
        if(datas.length == 2){
            statistisrangebill(billMap, datas[0], datas[1]);
            HashMap<String, BigDecimal> commentMap = new HashMap<>();
            for (Bill bill : billMap.values()) {
                if(bill.getCreateTime().toLocalDate().compareTo(LocalDate.parse(datas[0]))>=0&&
                        bill.getCreateTime().toLocalDate().compareTo(LocalDate.parse(datas[1]))<=0){
                    if (commentMap.containsKey(bill.getComment())) {
                        commentMap.put(bill.getComment(), commentMap.get(bill.getComment()).add(bill.getBillAmount()));
                    } else {
                        commentMap.put(bill.getComment(), bill.getBillAmount());
                    }
                }
            }
            System.out.println("----------------该日期区间内的账单分析-----------------");
            //遍历commentMap集合，输出所有账单备注及对应金额
            for (String comment : commentMap.keySet()) {
                System.out.println(comment + "账单总金额为：" + commentMap.get(comment));
            }
        }
    }
    //------------统计区间内账单-----------------
    private void statistisrangebill(HashMap<Integer, Bill> billMap, String... datas) {
        List<Bill> billList = new ArrayList<>(billMap.values());
        BigDecimal outAmount = BigDecimal.ZERO;
        BigDecimal inAmount = BigDecimal.ZERO;
        LocalDate startDate = LocalDate.parse(datas[0]);
        LocalDate endDate = LocalDate.parse(datas[1]);
        for(Bill bill : billList) {
            if(startDate.compareTo(bill.getCreateTime().toLocalDate())<=0&&endDate.compareTo(bill.getCreateTime().toLocalDate())>=0){
                if(bill.getBillType().equals("支出")){
                    outAmount = outAmount.add(bill.getBillAmount());
                }else{
                    inAmount = inAmount.add(bill.getBillAmount());
                }
            }
        }
        System.out.println("该日期区间内的支出账单总金额为：" + outAmount);
        System.out.println("该日期区间内的收入账单总金额为：" + inAmount);
    }
}