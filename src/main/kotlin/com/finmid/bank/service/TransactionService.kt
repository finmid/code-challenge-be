package com.finmid.bank.service

import com.finmid.bank.model.Account
import com.finmid.bank.model.Transaction
import com.finmid.bank.repository.AccountRepository
import com.finmid.bank.repository.TransactionRepository
import com.finmid.interview.bank.InvalidAccountException
import com.finmid.interview.bank.InvalidBalanceException
import org.springframework.stereotype.Service


@Service
class TransactionService(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
    fun executeTransaction(accountOriginId: Long, accountDestinataryId: Long, amount: Double) {
        val accountOrigin = accountRepository.findById(accountOriginId)
        val accountDestinatary = accountRepository.findById(accountDestinataryId)

        if(accountOrigin.isPresent && accountDestinatary.isPresent){
            deductAccountBalance(accountOrigin.get(), amount)
            updateAccountBalance(accountDestinatary.get(), amount)
            transactionRepository.save(Transaction(to = accountOriginId, from = accountDestinataryId, amount = amount))
        }else{
            throw InvalidAccountException()
        }
    }

    fun deductAccountBalance(account: Account, amountToTransfer: Double) {
        val newBalance =  account.balance - amountToTransfer
        if(newBalance < 0){
            throw InvalidBalanceException()
        }
        val accountToUpdate = Account(accountId = account.accountId, balance = newBalance)
        accountRepository.save(accountToUpdate)
    }

    fun updateAccountBalance(account: Account, amount: Double) {
        accountRepository.save(account.copy(balance = account.balance + amount))
    }

}