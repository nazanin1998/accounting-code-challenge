package com.nazanin.accounting.entity;

import com.nazanin.accounting.dto.AddMoneyRequestDto;
import com.nazanin.accounting.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction", indexes = {
        @Index(name = "referenceId_index", columnList = "referenceId", unique = true),
        @Index(name = "userId_index", columnList = "userId")
})
@EntityListeners(AuditingEntityListener.class)
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REFERENCE_ID")
    private String referenceId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "AMOUNT")
    private Long amount;

    @Column(name = "TRANSACTION_TYPE")
    private TransactionType transactionType;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    private Long balanceAfterTransaction;

    public static TransactionRecord from(AddMoneyRequestDto request) {
        return new TransactionRecord()
                .setAmount(request.getAmount())
                .setUserId(request.getUserId())
                .setTransactionType(TransactionType.INCREASE);
    }
}
