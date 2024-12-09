package de.schegge.freshmarker.demo;

import jakarta.mail.MessagingException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.output.StandardOutputFormats;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Template text;
    private final Template html;

    public EmailService(JavaMailSender mailSender, Configuration.TemplateBuilder builder, @Value("mail-text.fmt") ClassPathResource textResource, @Value("mail-html.fmt") ClassPathResource htmlResource)
            throws IOException {
        this.mailSender = mailSender;
        text = builder.getTemplate("text", textResource.getContentAsString(StandardCharsets.UTF_8));
        html = builder.withOutputFormat(StandardOutputFormats.HTML).getTemplate("html", htmlResource.getContentAsString(StandardCharsets.UTF_8));
    }

    public void sendMail(String to, String subject, Path filePath) throws MessagingException, IOException {
        MimeMessageHelper helper = getMimeMessageHelper(to, subject, filePath);
        mailSender.send(helper.getMimeMessage());
    }

    private MimeMessageHelper getMimeMessageHelper(String to, String subject, Path filePath) throws MessagingException, IOException {
        FileSystemResource file = new FileSystemResource(Objects.requireNonNull(filePath));
        MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), true);
        helper.setTo(to);
        helper.setFrom("admin@schegge.de");
        helper.setSubject(subject);

        try (Stream<Path> walk = Files.walk(Path.of("target/classes"), 1)) {
            Map<String, Object> model = Map.of("files", walk.toList(), "date", LocalDate.of(2024, Month.DECEMBER, 25));
            helper.setText(text.process(model), html.process(model));
        }
        helper.addAttachment(filePath.getFileName().toString(), file);
        return helper;
    }
}