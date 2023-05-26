package com.projectx.pay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Table(name = "AdvancePayments")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class AdvancePayments implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "PROCESSING_STATUS")
    private String processing_status;
    @Column(name = "TRANSACTION_TYPE")
    private String trx_type;
    @Column(name = "AMOUNT")
    private String amount;
    @Column(name = "MSISDN")
    private String msisdn;
    @Column(name = "MERCHANT_REQUEST_ID")
    private String request_id;
    @Column(name = "CHECKOUT_REQUEST_ID")
    private String checkout_id;
    @Column(name = "COMPLETED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completed_date;
    @Column(name = "RECEIVER_PARTY")
    private String receiver_party;
    @Column(name = "SENDER_PARTY")
    private String sender_party;
    @Column(name = "BILL_REFERENCE_NUMBER")
    private String billrefnumber;
    @Column(name = "MPESA_RECEIPT")
    private String mpesa_receipt;
}
          