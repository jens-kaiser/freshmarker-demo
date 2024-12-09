package de.schegge.freshmarker.demo;

import de.schegge.test.mail.GreenMailBox;
import de.schegge.test.mail.GreenMailExtension;
import de.schegge.test.mail.Mail;
import de.schegge.test.mail.SmtpTest;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static de.schegge.test.mail.MailMatchers.hasFrom;
import static de.schegge.test.mail.MailMatchers.hasSubject;
import static de.schegge.test.mail.MailMatchers.hasTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(GreenMailExtension.class)
@SpringBootTest
class EmailServiceTest {
    @Autowired
    EmailService emailService;

    @SmtpTest
    void testSmtpFindAllWithCount(GreenMailBox mailBox) throws ExecutionException, InterruptedException, MessagingException, IOException {
        Path attachment = Path.of("src/main/resources/attachment.png");
        emailService.sendMail("info@ahnen.de", "Subject1", attachment);
        emailService.sendMail("admin@vorfahren.org", "Subject2", attachment);
        List<Mail> receivedMessages = mailBox.findAll(2).get();
        assertThat(receivedMessages,
                hasItems(
                        allOf(
                                hasTo(hasItem("info@ahnen.de")),
                                hasSubject("Subject1")),
                        allOf(hasTo(hasItem("admin@vorfahren.org")),
                                hasSubject("Subject2"),
                                hasFrom(endsWith("schegge.de")))));
        assertNotNull(receivedMessages.getFirst().getMessage());
    }
}