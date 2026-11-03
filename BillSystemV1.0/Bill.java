//----------此类为账单类顶级父类，负责定义账单的基本属性和方法-------------
package v1;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;
public class Bill implements Serializable {
    //----------账单属性----------
    private static final long serialVersionUID = 539517000L;
    private int billId;
    private LocalDateTime createTime;
    private String billType;
    private BigDecimal billAmount;
    private String comment;

    public Bill() {
    }
    public Bill(int billId, LocalDateTime createTime, String billType, BigDecimal billAmount, String comment) {
        this.billId = billId;
        this.createTime = createTime;
        this.billType = billType;
        this.billAmount = billAmount;
        this.comment = comment;
    }
    public int getBillId() {
        return billId;
    }
    public void setBillId(int billId) {
        this.billId = billId;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public String getBillType() {
        return billType;
    }
    public void setBillType(String billType) {
        this.billType = billType;
    }
    public BigDecimal getBillAmount() {
        return billAmount;
    }
    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment)  {
        this.comment = comment;
    }
    @Override
    public String toString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String str = createTime.format(dateTimeFormatter);
        return "账单ID：" + billId + ", 创建时间：" + str + ", 账单类型：" + billType + ", 账单金额：" + billAmount + ", 备注：" + comment;
    }
}
