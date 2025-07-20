package com.rallyup.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RallyupSpringApplication

fun main(args: Array<String>) {
    runApplication<RallyupSpringApplication>(*args)
}
