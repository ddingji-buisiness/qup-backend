package com.qup.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QupSpringApplication

fun main(args: Array<String>) {
    runApplication<QupSpringApplication>(*args)
}
