package uk.gov.dhsc.htbhf.circleci

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HtbhfCircleciStatusApplication

fun main(args: Array<String>) {
	runApplication<HtbhfCircleciStatusApplication>(*args)
}
