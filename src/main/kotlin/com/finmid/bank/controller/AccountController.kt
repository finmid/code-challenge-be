package com.finmid.interview.bank

import com.finmid.bank.AccountDTO
import com.finmid.bank.ErrorResponse
import com.finmid.bank.model.Account
import com.finmid.bank.repository.AccountRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

class InvalidBalanceInputException : Exception()

@Controller
@RequestMapping("/account")
class AccountController(
    private val accountRepository: AccountRepository,
) {

    @GetMapping("{accountId}")
    fun getAccount(
        @PathVariable("accountId", required = true)
        accountId: Long
    ): ResponseEntity<AccountDTO> {
        val account = accountRepository.findById(accountId)
        if(account.isPresent){
            return ResponseEntity(AccountDTO(account.get().balance),HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }


    @PostMapping
    fun createAccount(
        @RequestBody @Valid account: AccountDTO
    ): ResponseEntity<Account> {
        if(account.balance < 0){
            throw InvalidBalanceInputException()
        }
        val accountCreated = accountRepository.save(Account(balance = account.balance))
        return ResponseEntity(accountCreated, HttpStatus.CREATED)
    }

    @PutMapping("{accountId}")
    fun updateAccount(
        @PathVariable("accountId", required = true)
        accountId: Long,
        @RequestBody account: AccountDTO
    ): ResponseEntity<HttpStatus> {
        if(account.balance < 0){
            throw InvalidBalanceInputException()
        }
        val accountToUpdate = accountRepository.findById(accountId)
        if(accountToUpdate.isPresent){
            val accountUpdated = accountToUpdate.get().copy(balance = account.balance)
            accountRepository.save(accountUpdated)
            return ResponseEntity(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InvalidBalanceInputException::class)
    fun handleInvalidBalance(): ResponseEntity<ErrorResponse> {
        val errorMessage = ErrorResponse(
            "Invalid balance input",
            HttpStatus.BAD_REQUEST
        )
        return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
    }


}
