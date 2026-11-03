package BillSystem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.io.IOException;
import java.util.*;

public class BillService {
    private final BillRepository billRepository;
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }
    //---------------------------1.账单管理所用方法---------------------------
    //-----------得到随机账单ID---------------
    public int getRandomBillId() {
        Random random = new Random();
        int billId;
        while (true) {
            billId = random.nextInt(1000) + 1;
            if (!billRepository.isIdExist(billId)){
                break;
            }
        }
        return billId;
    }
    //--------------获得账单日期-----------------
    public LocalDateTime getBillCreateTime(String createTimeStr) throws DateTimeFormatException {
        LocalDateTime createTime;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
        createTime = readTime(dateTimeFormatter,createTimeStr);
        return createTime;
    }
        //--------------------判断账单日期是否合法-----------------
        //------利用正则表达式以及异常处理，双重判断时间格式是否正确，是否为有效时间格式
        //参数传入Scanner对象是为了代码解耦
        private LocalDateTime readTime (DateTimeFormatter dateTimeFormatter,String createTimeStr) throws DateTimeFormatException {
            //^表示以什么开头，$表示以什么结尾，两个符号表示强制从头到尾必须完全匹配，但是matches()其实本身就有这种功能，所以这里可以省略，但是要养成习惯
            String str = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
            //trim()方法用于移除字符串首尾的空格,并保留中间的空格字符，避免用户输入空格导致的异常
            if (createTimeStr.trim().matches(str)) {
                try {
                    return LocalDateTime.parse(createTimeStr.trim(), dateTimeFormatter);
                } catch (DateTimeParseException e) {
                    throw new DateTimeFormatException("您输入的时间格式有误，请重新输入");
                }
            } else {
                throw new DateTimeFormatException("您输入的时间格式有误，正确格式为：yyyy-MM-dd HH:mm:ss，请重新输入");
            }
        }
        //-------------------判断输入的账单类型是否合法-----------------
        public String checkBillType(String billType) throws BillTypeException {
            if (!(billType.equals("支出") || billType.equals("收入"))) {
                throw new BillTypeException("您输入的账单类型有误，请重新输入");
            }
            return billType;
        }
        //-------------------判断输入的账单金额是否合法-----------------
        //判断金额输入是否为有效金额
        public BigDecimal checkAmount(String Amount) throws BillAmountException {
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
        //-------------------判断输入的账单备注是否合法-----------------
        public String checkComment(String comment) throws BillCommentException {
            if (!((comment.equals("吃喝") || comment.equals("玩乐") || comment.equals("交通") || comment.equals("其他支出") ||
                    comment.equals("兼职") || comment.equals("工资") || comment.equals("其他收入")))) {
                throw new BillCommentException("您输入的备注有误，请重新输入");
            }
            return comment;
        }
        //-------------------创建账单-----------------
        public void createBill(int billId, LocalDateTime createTime, String billType, BigDecimal billAmount, String comment) {
           Bill bill = new Bill(billId, createTime, billType, billAmount, comment);
           billRepository.save(bill);
        }
        //-----------------判断账单是否为空-----------------
        public boolean isBillEmpty() {
            return billRepository.isEmpty();
        }
        //-----------------判断账单ID是否存在-----------------
        public boolean isBillIdExist(int billId) {
            return billRepository.isIdExist(billId);
        }
        //-----------------通过账单ID获取账单-----------------
        public Bill getBillById(int billId) {
            return billRepository.get(billId);
        }
        //-----------------修改账单-----------------
        public void modifyBill(int billId, LocalDateTime createTime, String billType, BigDecimal billAmount, String comment) {
            Bill bill = billRepository.get(billId);
            bill.setCreateTime(createTime);
            bill.setBillType(billType);
            bill.setBillAmount(billAmount);
            bill.setComment(comment);
            billRepository.save(bill);
        }
        //---------------------2.删除账单方法---------------------
    //----------删除所有账单方法----------
    public void deleteAllBill() {
        billRepository.clear();
    }
    //----------根据账单ID删除账单方法----------
    public void deleteBill(int billId) {
        billRepository.remove(billId);
    }
    //----------------查询所有账单方法---------------
    public List<Bill> queryAllBill() {
       List<Bill> billList = billRepository.getallbill();
       billList.sort(Comparator.comparing(Bill::getCreateTime).reversed());
       return billList;
    }
    //----------------查询所有支出或收入账单方法---------------
    public List<Bill> queryBillByType(String billType) {
        List<Bill> billList = new ArrayList<>();
        List<Bill> allbill = billRepository.getallbill();
        for(Bill bill : allbill){
            if(bill.getBillType().equals(billType)){
                billList.add(bill);
            }
        }
        billList.sort(Comparator.comparing(Bill::getCreateTime).reversed());
        return billList;
    }
    //----------------查询某日账单方法---------------
    public List<Bill> queryDayBill(String date) {
        List<Bill> dayBillList = new ArrayList<>();
        List<Bill> allbill = billRepository.getallbill();
        for(Bill bill : allbill){
            if(bill.getCreateTime().toLocalDate().toString().equals(date)){
                dayBillList.add(bill);
            }
        }
        dayBillList.sort(Comparator.comparing(Bill::getCreateTime).reversed());
        return dayBillList;
    }
    //------------------4.收支统计方法---------------------
    //无日期限制时统计所有账单方法----------
    public BigDecimalBox statisticsAllBillAmount() {
        List<Bill> allBill = queryAllBill();
        BigDecimal BillOutAmount = BigDecimal.ZERO;
        BigDecimal BillIncomeAmount = BigDecimal.ZERO;
        for(Bill bill : allBill){
            if(bill.getBillType().equals("支出")){
                BillOutAmount = BillOutAmount.add(bill.getBillAmount());
            }
            else{
                BillIncomeAmount = BillIncomeAmount.add(bill.getBillAmount());
            }
        }
        return new BigDecimalBox(BillOutAmount, BillIncomeAmount);
    }
    //-----------------无日期限制时统计指定类型账单总金额方法-----------------
    public BigDecimal statisticsTypeBillTypeAmount(String billType) {
        List<Bill> allBill = queryBillByType(billType);
        BigDecimal BillAmount = BigDecimal.ZERO;
        for(Bill bill : allBill){
            BillAmount = BillAmount.add(bill.getBillAmount());
        }
        return BillAmount;
    }
    //------------------有日期限制时统计该日期的账单总金额方法---------------------
    public BigDecimalBox statisticsdayBillAmount(String date) {
        List<Bill> dayBillList = queryDayBill(date);
        BigDecimal dayBillOutAmount = BigDecimal.ZERO;
        BigDecimal dayBillIncomeAmount = BigDecimal.ZERO;
        for(Bill bill : dayBillList){
              if(bill.getBillType().equals("支出")){
                dayBillOutAmount = dayBillOutAmount.add(bill.getBillAmount());
              }
              else{
                dayBillIncomeAmount = dayBillIncomeAmount.add(bill.getBillAmount());
              }
        }
        return new BigDecimalBox(dayBillOutAmount, dayBillIncomeAmount);
    }
    //------------------检查统计日期是否合法---------------------
    public void checkDate(String date) throws DateTimeFormatException {
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
    //---------------消费分析方法---------------------
    //分析所有账单方法
    public HashMap<String, BigDecimal> analysisAllBill() {
        //创建HashMap集合，键值为账单备注，键值为该账单金额
        HashMap<String, BigDecimal> billCommentAmount = new HashMap<>();
        List<Bill> allBill = queryAllBill();
        for(Bill bill : allBill){
            if(billCommentAmount.containsKey(bill.getComment())){
                billCommentAmount.put(bill.getComment(), billCommentAmount.get(bill.getComment()).add(bill.getBillAmount()));
            }
            else{
                billCommentAmount.put(bill.getComment(), bill.getBillAmount());
            }
        }
        return billCommentAmount;
    }
    //分析指定区间内订单
    public HashMap<String, BigDecimal> analysisRangeBill(String startDate, String endDate) {
        HashMap<String, BigDecimal> billCommentAmount = new HashMap<>();
        List<Bill> billList = queryAllBill();
        LocalDate startdate = LocalDate.parse(startDate);
        LocalDate enddate = LocalDate.parse(endDate);
        for(Bill bill : billList) {
            if(startdate.compareTo(bill.getCreateTime().toLocalDate())<=0&&enddate.compareTo(bill.getCreateTime().toLocalDate())>=0){
                if(billCommentAmount.containsKey(bill.getComment())){
                    billCommentAmount.put(bill.getComment(), billCommentAmount.get(bill.getComment()).add(bill.getBillAmount()));
                }
                else{
                    billCommentAmount.put(bill.getComment(), bill.getBillAmount());
                }
            }
        }
        return billCommentAmount;
    }
    //------------统计区间内账单-----------------
    public BigDecimalBox statistisrangebill( String startDate, String endDate) {
        List<Bill> billList = queryAllBill();
        BigDecimal outAmount = BigDecimal.ZERO;
        BigDecimal inAmount = BigDecimal.ZERO;
        LocalDate startdate = LocalDate.parse(startDate);
        LocalDate enddate = LocalDate.parse(endDate);
        for(Bill bill : billList) {
            if(startdate.compareTo(bill.getCreateTime().toLocalDate())<=0&&enddate.compareTo(bill.getCreateTime().toLocalDate())>=0){
                if(bill.getBillType().equals("支出")){
                    outAmount = outAmount.add(bill.getBillAmount());
                }else{
                    inAmount = inAmount.add(bill.getBillAmount());
                }
            }
        }
        return new BigDecimalBox(outAmount, inAmount);
    }
    //------------------退出时候保存所有账单---------------------
    public void saveAll() throws IOException {
        billRepository.saveAll();
    }
}
