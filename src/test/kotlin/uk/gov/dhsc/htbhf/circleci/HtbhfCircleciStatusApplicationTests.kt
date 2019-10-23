package uk.gov.dhsc.htbhf.circleci

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = ["circleci.token = foo"])
class HtbhfCircleciStatusApplicationTests {

	@Test
	fun contextLoads() {
	}

}
