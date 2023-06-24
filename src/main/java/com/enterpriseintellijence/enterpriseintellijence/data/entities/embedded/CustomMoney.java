package com.enterpriseintellijence.enterpriseintellijence.data.entities.embedded;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.Currency;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;


@Embeddable
@NoArgsConstructor
public class CustomMoney {

    private Double price;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Transient
    private Money money;

    public CustomMoney(Double price, Currency currency){
        this.price=price;
        this.currency=currency;
        money=Money.of(CurrencyUnit.of(currency.toString()),price);
    }
    public CustomMoney(Money money){
        this.price = Double.valueOf(String.valueOf(money.getAmount())) ;
        this.currency = Currency.valueOf(String.valueOf(money.getCurrencyUnit()));
    }

    public Money getMoney() {
        return money;
    }

    public Double getPrice() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setPrice(Double price) {
        this.price = price;
        this.money=Money.of(CurrencyUnit.of(currency.toString()),price);
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        this.money=Money.of(CurrencyUnit.of(currency.toString()),price);
    }
}
