package rewards;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import rewards.CaptureSystemOutput.OutputCapture;
import rewards.internal.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SystemTestConfig.class })
@EnableAutoConfiguration
public class LoggingAspectTests {

	@Autowired
	AccountRepository repository;

	@Test
	@CaptureSystemOutput
	public void testLogger(OutputCapture capture) { //captura la salida por consola

		/*
		 * This would be a join point, this
		 * method will be enhanced with a proxy, not only executing his own code but
		 * also executing the advice code beforehand
		 * in another way, this join point is intercepted by a proxy to execute both the
		 * method's own logic and the advice logic before it.
		 */
		repository.findByCreditCard("1234123412341234");
		if (TestConstants.CHECK_CONSOLE_OUTPUT) {
			// AOP VERIFICATION
			// LoggingAspect should have output an INFO message to console
			String consoleOutput = capture.toString();
			assertTrue(consoleOutput.startsWith("INFO"));
			assertTrue(consoleOutput.contains("rewards.internal.aspects.LoggingAspect"));
		}
	}
}
