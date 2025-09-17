package com.nexus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.name}")
    private String fromName;

    /**
     * 이메일 인증 코드 발송
     */
    public void sendVerificationCode(String toEmail, String verificationCode) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("[Nexus] 이메일 인증 코드");

            String htmlContent = buildVerificationEmailContent(verificationCode);
            helper.setText(htmlContent, true);

            emailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {}", toEmail, e);
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 비밀번호 재설정 이메일 발송
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("[Nexus] 비밀번호 재설정");

            String htmlContent = buildPasswordResetEmailContent(resetToken);
            helper.setText(htmlContent, true);

            emailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("비밀번호 재설정 이메일 발송에 실패했습니다.", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending password reset email to: {}", toEmail, e);
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 간단한 텍스트 이메일 발송
     */
    public void sendSimpleMessage(String toEmail, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
            log.info("Simple email sent to: {} with subject: {}", toEmail, subject);

        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 이메일 인증 코드 HTML 템플릿
     */
    private String buildVerificationEmailContent(String verificationCode) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Nexus 이메일 인증</title>
                    <style>
                        body {
                            font-family: 'Segoe UI', Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            padding: 30px;
                            text-align: center;
                            border-radius: 10px 10px 0 0;
                        }
                        .content {
                            background: #f8f9fa;
                            padding: 30px;
                            border-radius: 0 0 10px 10px;
                        }
                        .verification-code {
                            background: #e3f2fd;
                            border: 2px solid #2196f3;
                            border-radius: 8px;
                            padding: 20px;
                            text-align: center;
                            margin: 20px 0;
                            font-size: 32px;
                            font-weight: bold;
                            letter-spacing: 5px;
                            color: #1976d2;
                        }
                        .footer {
                            margin-top: 30px;
                            padding-top: 20px;
                            border-top: 1px solid #ddd;
                            font-size: 14px;
                            color: #666;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>🎮 Nexus</h1>
                        <p>리그오브레전드 내전 플랫폼</p>
                    </div>
                    <div class="content">
                        <h2>이메일 인증 코드</h2>
                        <p>안녕하세요! Nexus 플랫폼에 가입해 주셔서 감사합니다.</p>
                        <p>아래 인증 코드를 입력하여 회원가입을 완료해 주세요:</p>

                        <div class="verification-code">
                            %s
                        </div>

                        <p><strong>중요:</strong></p>
                        <ul>
                            <li>이 코드는 10분 후에 만료됩니다.</li>
                            <li>본인이 요청하지 않은 경우 이 이메일을 무시하세요.</li>
                            <li>코드를 다른 사람과 공유하지 마세요.</li>
                        </ul>

                        <p>문의사항이 있으시면 언제든지 저희에게 연락하세요.</p>

                        <div class="footer">
                            <p>© 2024 Nexus Platform. All rights reserved.</p>
                            <p>이 이메일은 자동으로 발송되었습니다. 답장하지 마세요.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(verificationCode);
    }

    /**
     * 비밀번호 재설정 HTML 템플릿
     */
    private String buildPasswordResetEmailContent(String resetToken) {
        String resetUrl = "http://localhost:3000/reset-password?token=" + resetToken;

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Nexus 비밀번호 재설정</title>
                    <style>
                        body {
                            font-family: 'Segoe UI', Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            padding: 30px;
                            text-align: center;
                            border-radius: 10px 10px 0 0;
                        }
                        .content {
                            background: #f8f9fa;
                            padding: 30px;
                            border-radius: 0 0 10px 10px;
                        }
                        .reset-button {
                            display: inline-block;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            padding: 15px 30px;
                            text-decoration: none;
                            border-radius: 8px;
                            margin: 20px 0;
                            font-weight: bold;
                        }
                        .footer {
                            margin-top: 30px;
                            padding-top: 20px;
                            border-top: 1px solid #ddd;
                            font-size: 14px;
                            color: #666;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>🎮 Nexus</h1>
                        <p>리그오브레전드 내전 플랫폼</p>
                    </div>
                    <div class="content">
                        <h2>비밀번호 재설정 요청</h2>
                        <p>비밀번호 재설정 요청을 받았습니다.</p>
                        <p>아래 버튼을 클릭하여 새로운 비밀번호를 설정하세요:</p>

                        <p style="text-align: center;">
                            <a href="%s" class="reset-button">비밀번호 재설정</a>
                        </p>

                        <p><strong>중요:</strong></p>
                        <ul>
                            <li>이 링크는 1시간 후에 만료됩니다.</li>
                            <li>본인이 요청하지 않은 경우 이 이메일을 무시하세요.</li>
                            <li>링크를 다른 사람과 공유하지 마세요.</li>
                        </ul>

                        <p>링크가 작동하지 않는 경우 아래 URL을 복사하여 브라우저에 붙여넣으세요:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>

                        <div class="footer">
                            <p>© 2024 Nexus Platform. All rights reserved.</p>
                            <p>이 이메일은 자동으로 발송되었습니다. 답장하지 마세요.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(resetUrl, resetUrl);
    }
}