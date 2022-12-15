package com.finmid.bank.model

import javax.persistence.*

@Entity
@Table(name = "transaction")
data class Transaction(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "txId")
        val txId: Long? = null,
        @Column(name = "toAccountId")
        val to: Long,
        @Column(name = "fromAccountId")
        val from: Long,
        @Column(name = "amount")
        val amount: Double
){
        constructor() : this(null, 0, 0, 0.00)
}