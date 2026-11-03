package BillSystem;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
public class BillRepository {
    private Map<Integer, Bill> billMap = new HashMap<>();
    private String filePath;

    public BillRepository() {
    }
//----------构造时加载本地文件----------
    public BillRepository( String filePath) {
        this.filePath = filePath;
        // 从本地文件加载账单数据
        loadAll();
    }
//----------从本地文件加载所有账单----------
    private void loadAll() {
        try {
            // 从本地文件加载账单数据
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
            billMap = (Map<Integer, Bill>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("本地账单文件为空或格式错误");
        }
    }

    //---------------------------1.账单管理所用方法---------------------------
    //----------存储账单----------
    public void save(Bill bill) {
        billMap.put(bill.getBillId(), bill);
    }
//----------根据账单ID获取账单----------
    public Bill get(int billId) {
        return billMap.get(billId);
    }
//------------判断ID是否存在-----------------
    public boolean isIdExist(int billId) {
        return billMap.containsKey(billId);
    }
    //------------判断账单是否为空-----------------
    public boolean isEmpty() {
        return billMap.isEmpty();
    }
//----------用户退出后保存所有账单----------
    public void saveAll() throws IOException {
        // 实现将 billMap 保存到本地文件
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
        oos.writeObject(billMap);
        oos.close();
    }
    //------------------2.删除账单方法------------------
    //----------删除所有账单方法----------
    public void clear() {
        billMap.clear();
    }
    //----------根据账单ID删除账单方法----------
    public void remove(int billId) {
        billMap.remove(billId);
    }
    //------------------3.查询账单方法----------------
    //----------查询所有账单方法----------
    public List<Bill> getallbill() {
        List<Bill> billList = new ArrayList<>(billMap.values());
        return billList;
    }
}
