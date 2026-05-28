package saberPro.infrastructure.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import saberPro.infrastructure.gateways.CorreoGateway;

import java.util.Properties;

public class CorreoGatewayImpl implements CorreoGateway {

    private static final String CORREO_REMITENTE  = "saberproanalisis@gmail.com";
    private static final String PASSWORD_REMITENTE = "lckcepfstlutlpga";

    @Override
    public void enviarCodigo(String correoDestino, String codigo) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host",             "smtp.gmail.com");
        props.setProperty("mail.smtp.starttls.enable",  "true");
        props.setProperty("mail.smtp.port",             "587");
        props.setProperty("mail.smtp.auth",             "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CORREO_REMITENTE, PASSWORD_REMITENTE);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(CORREO_REMITENTE));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoDestino));
        message.setSubject("Código de verificación - Saber Pro");
        message.setText("Tu código de verificación es: " + codigo);

        Transport.send(message);
    }
}