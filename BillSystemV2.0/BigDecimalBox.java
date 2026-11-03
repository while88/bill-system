package BillSystem;
import java.math.BigDecimal;
public class BigDecimalBox {
    private BigDecimal billOutAmount;
    private BigDecimal billIncomeAmount;
    public BigDecimalBox() {
    }
    public BigDecimalBox(BigDecimal billOutAmount, BigDecimal billIncomeAmount) {
        this.billOutAmount = billOutAmount;
        this.billIncomeAmount = billIncomeAmount;
    }
    public BigDecimal getBillIncomeAmount() {
        return billIncomeAmount;
    }
    public BigDecimal getBillOutAmount() {
        return billOutAmount;
    }


}
