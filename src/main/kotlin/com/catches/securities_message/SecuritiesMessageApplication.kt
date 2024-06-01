package com.catches.securities_message

import com.catches.securities_message.properties.HttpProperty
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(
	HttpProperty::class,
)
@SpringBootApplication
class SecuritiesMessageApplication

fun main(args: Array<String>) {
	runApplication<SecuritiesMessageApplication>(*args)
}
