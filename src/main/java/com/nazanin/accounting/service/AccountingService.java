package com.nazanin.accounting.service;

import com.nazanin.accounting.dto.AddMoneyRequestDto;
import com.nazanin.accounting.dto.AddMoneyResponseDto;
import com.nazanin.accounting.dto.GetBalanceResponseDto;
import com.nazanin.accounting.entity.TransactionRecord;
import com.nazanin.accounting.enums.TransactionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class AccountingService {

    @PersistenceContext
    private EntityManager em;

    public AddMoneyResponseDto addMoney(AddMoneyRequestDto request) {
        long currentBalanceOfUser = getBalanceOfUser(request.getUserId());
        TransactionRecord record = TransactionRecord.from(request)
                .setBalanceAfterTransaction(request.getAmount() + currentBalanceOfUser);
        persistTransactionRecord(record);
        return new AddMoneyResponseDto().setReferenceId(record.getReferenceId());
    }

    public GetBalanceResponseDto getBalance(Long userId) {
        return new GetBalanceResponseDto()
                .setBalance(getBalanceOfUser(userId));
    }

    public void endOfDaySumCalculation() {

        Optional<TransactionRecord> endOfDayRecord = getLastEndOfDayRecord();

        long sum = getSumOfTodayRecords(endOfDayRecord);
        if (endOfDayRecord.isPresent()) {
            sum = sum + endOfDayRecord.get().getAmount();
        }
        persistTransactionRecord(new TransactionRecord()
                .setTransactionType(TransactionType.SUM_END_OF_DAY)
                .setAmount(sum));

        log.info("total amount of transaction until today is : {}", sum);
    }

    private void persistTransactionRecord(TransactionRecord record) {
        em.persist(record);
        record.setReferenceId(StringUtils.leftPad(record.getId().toString(), 10, "0"));
    }


    private long getBalanceOfUser(Long userId) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TransactionRecord> query = builder.createQuery(TransactionRecord.class);
        Root<TransactionRecord> root = query.from(TransactionRecord.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("userId"), userId));
        predicates.add(builder.equal(root.get("transactionType"), TransactionType.INCREASE));
        predicates.stream().reduce(builder::and).ifPresent(query::where);

        query.orderBy(builder.desc(root.get("createdDate")));

        List<TransactionRecord> records = em.createQuery(query).setMaxResults(1).getResultList();
        if (CollectionUtils.isEmpty(records)) return 0L;
        return Objects.requireNonNull(CollectionUtils.firstElement(records)).getBalanceAfterTransaction();
    }

    private Optional<TransactionRecord> getLastEndOfDayRecord() {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TransactionRecord> query = builder.createQuery(TransactionRecord.class);
        Root<TransactionRecord> root = query.from(TransactionRecord.class);

        Predicate predicate = builder.equal(root.get("transactionType"), TransactionType.SUM_END_OF_DAY);
        builder.and(predicate);
        query.where(predicate);

        query.orderBy(builder.desc(root.get("createdDate")));

        List<TransactionRecord> records = em.createQuery(query).setMaxResults(1).getResultList();
        return Optional.ofNullable(CollectionUtils.firstElement(records));
    }

    private long getSumOfTodayRecords(Optional<TransactionRecord> lastEodRecord) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<TransactionRecord> root = query.from(TransactionRecord.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("transactionType"), TransactionType.INCREASE));

        lastEodRecord.ifPresent(transactionRecord -> predicates.add(
                builder.greaterThanOrEqualTo(root.get("createdDate"), transactionRecord.getCreatedDate())));

        predicates.stream().reduce(builder::and).ifPresent(query::where);
        query.select(builder.sum(root.get("amount")));

        Long sumOfRecordsAfterEOD = em.createQuery(query).getSingleResult();
        return sumOfRecordsAfterEOD == null ? 0L : sumOfRecordsAfterEOD;
    }


}
