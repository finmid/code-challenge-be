package com.finmid.bank.model

import javax.persistence.*

@Entity
@Table(name = "account")
data class Account(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "account_id")
        var accountId: Long? = null,
        var balance: Double
){
        constructor() : this(null, 0.00)

}