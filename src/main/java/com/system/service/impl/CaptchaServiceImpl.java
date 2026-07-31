package com.system.service.impl;

import com.system.common.BusinessException;
import com.system.service.CaptchaService;
import com.system.vo.CaptchaVO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

/**
 * 验证码服务实现。
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final String CODE_SOURCE = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 4;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int EXPIRE_MINUTES = 2;
    private static final String IMAGE_PREFIX = "data:image/png;base64,";
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    private final SecureRandom random = new SecureRandom();

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CaptchaVO generateCaptcha() {
        String code = generateCode();
        String captchaKey = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(buildCaptchaKey(captchaKey), code, Duration.ofMinutes(EXPIRE_MINUTES));

        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaKey(captchaKey);
        captchaVO.setCaptchaImage(IMAGE_PREFIX + drawCaptchaImage(code));
        return captchaVO;
    }

    @Override
    public void validateCaptcha(String captchaKey, String captchaCode) {
        String cacheCode = stringRedisTemplate.opsForValue().getAndDelete(buildCaptchaKey(captchaKey));
        if (cacheCode == null) {
            throw new BusinessException("验证码不存在或已过期");
        }
        if (captchaCode == null || !cacheCode.equalsIgnoreCase(captchaCode.trim())) {
            throw new BusinessException("验证码错误");
        }
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_SOURCE.charAt(random.nextInt(CODE_SOURCE.length())));
        }
        return code.toString();
    }

    private String drawCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(new Color(245, 247, 250));
            graphics.fillRect(0, 0, WIDTH, HEIGHT);
            drawNoise(graphics);
            graphics.setFont(new Font("Arial", Font.BOLD, 26));
            for (int i = 0; i < code.length(); i++) {
                graphics.setColor(randomColor(40, 120));
                graphics.drawString(String.valueOf(code.charAt(i)), 18 + i * 24, 29 + random.nextInt(5));
            }
        } finally {
            graphics.dispose();
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new BusinessException("验证码生成失败");
        }
    }

    private void drawNoise(Graphics2D graphics) {
        for (int i = 0; i < 8; i++) {
            graphics.setColor(randomColor(120, 220));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            graphics.drawLine(x1, y1, x2, y2);
        }
    }

    private Color randomColor(int min, int max) {
        int red = min + random.nextInt(max - min);
        int green = min + random.nextInt(max - min);
        int blue = min + random.nextInt(max - min);
        return new Color(red, green, blue);
    }

    private String buildCaptchaKey(String captchaKey) {
        return CAPTCHA_KEY_PREFIX + captchaKey;
    }
}
