package de.schegge.freshmarker.demo;

import jakarta.mail.MessagingException;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.core.output.StandardOutputFormats;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Template text;
    private final Template html;
    private final DemoConfig demoConfig;

    public EmailService(JavaMailSender mailSender, TemplateBuilder builder, DemoConfig demoConfig) throws IOException {
        this.mailSender = mailSender;
        text = builder.getTemplate("text", demoConfig.textResource().getContentAsString(StandardCharsets.UTF_8));
        html = builder.withOutputFormat(StandardOutputFormats.HTML).getTemplate("html", demoConfig.htmlResource().getContentAsString(StandardCharsets.UTF_8));
        this.demoConfig = demoConfig;
    }

    public void sendMail(String to, String subject, Path filePath) throws MessagingException, IOException {
        MimeMessageHelper helper = getMimeMessageHelper(to, subject, filePath);
        mailSender.send(helper.getMimeMessage());
    }

    private MimeMessageHelper getMimeMessageHelper(String to, String subject, Path filePath) throws MessagingException, IOException {
        FileSystemResource file = new FileSystemResource(Objects.requireNonNull(filePath));
        MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
        helper.setTo(to);
        helper.setFrom(demoConfig.from());
        helper.setSubject(subject);

        try (Stream<Path> walk = Files.walk(Path.of("target/classes"), 1)) {
            Map<String, Object> model = Map.of("files", walk.toList(), "date", LocalDate.of(2024, Month.DECEMBER, 25));
            helper.setText(text.process(model), html.process(model));
        }
        helper.addAttachment(filePath.getFileName().toString(), file);
        return helper;
    }
}