package com.finmid.bank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@ComponentScan("com.finmid")
@SpringBootApplication
class ApiApplication


fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}
