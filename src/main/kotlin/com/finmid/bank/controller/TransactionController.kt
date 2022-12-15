package com.finmid.interview.bank

import com.finmid.bank.ErrorResponse
import com.finmid.bank.TransactionDTO
import com.finmid.bank.service.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

class InvalidBalanceException : Exception()
class InvalidAccountException : Exception()
class InvalidInputException : Exception()

@Controller
@RequestMapping("/transaction")
class TransactionController(
    private val transactionService: TransactionService
) {
    @PostMapping
    fun executeTransaction(
        @RequestBody transactionCreateBody: TransactionDTO
    ): ResponseEntity<HttpStatus> {
        if(transactionCreateBody.from == transactionCreateBody.to || transactionCreateBody.amount <= 0) {
            throw InvalidInputException()
        }
        transactionService.executeTransaction(
            transactionCreateBody.from,
            transactionCreateBody.to,
            transactionCreateBody.amount
        )
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @ExceptionHandler(InvalidBalanceException::class)
    fun handleInvalidBalance(): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            "Transfer is not possible, balance is not enough",
            HttpStatus.BAD_REQUEST
        )
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidInputException::class)
    fun handleInvalidInput(): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            "One or more inputs are invalid",
            HttpStatus.BAD_REQUEST
        )
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidAccountException::class)
    fun handleInvalidAccount(): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            "Invalid account",
            HttpStatus.NOT_FOUND
        )
        return ResponseEntity(errorMessage, HttpStatus.NOT_FOUND)
    }


}
