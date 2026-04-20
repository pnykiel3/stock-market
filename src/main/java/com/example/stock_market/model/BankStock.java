package com.example.stock_market.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "bank_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankStock {

    @Id
    private String name;

    private Integer quantity;


}
