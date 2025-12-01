package fr.unice.polytech.paymentProcessing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.YearMonth;
import java.util.Objects;

public class BankInfo {
    private String cardNumber;
    private int CVV;
    private YearMonth expirationDate;

    @JsonCreator
    public BankInfo(@JsonProperty("cardNumber") String cardNumber,
            @JsonProperty("cvv") int CVV,
            @JsonProperty("month") int month,
            @JsonProperty("year") int year) {
        this.cardNumber = cardNumber;
        this.CVV = CVV;
        this.expirationDate = YearMonth.of(year, month);
    }

    public BankInfo() {
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCVV(int CVV) {
        this.CVV = CVV;
    }

    public void setExpirationDate(YearMonth expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getCVV() {
        return CVV;
    }

    public YearMonth getExpirationDate() {
        return expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        BankInfo bankInfo = (BankInfo) o;
        return CVV == bankInfo.CVV && Objects.equals(cardNumber, bankInfo.cardNumber)
                && Objects.equals(expirationDate, bankInfo.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, CVV, expirationDate);
    }
}
