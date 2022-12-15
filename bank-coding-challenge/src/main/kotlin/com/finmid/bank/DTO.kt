package com.finmid.bank

import org.springframework.http.HttpStatus
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TransactionDTO(
    val to: Long,
    val from: Long,
    val amount: Double
)

data class AccountDTO(
    val balance: Double
)

data class ErrorResponse(
    val message: String,
    val status: HttpStatus
)