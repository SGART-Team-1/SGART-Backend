package com.team1.sgart.backend.http;


import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team1.sgart.backend.services.TwoFactorAuthService;

@RestController
@RequestMapping("/api")
public class TwoFactorAuthController {

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @GetMapping("/generate-qr")
    public ResponseEntity<String> generateQRCode(@RequestParam String username) {
        try {
            String secretKey = twoFactorAuthService.generateSecretKey(); // Genera la clave secreta
            byte[] qrCodeImage = twoFactorAuthService.generateQRCodeImage(username, secretKey);
            String base64QRCode = Base64.getEncoder().encodeToString(qrCodeImage);
            
            // Devuelve la clave secreta también si es necesario almacenar
            return ResponseEntity.ok("{\"qrCode\": \"" + base64QRCode + "\", \"secretKey\": \"" + secretKey + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"Error al generar el código QR\"}");
        }
    }

    @PostMapping("/validate-totp")
    public ResponseEntity<String> validateTOTP(@RequestBody TOTPRequest request) {
        try {
            boolean isValid = twoFactorAuthService.validateTOTP(request.getMail(), request.getCode());
            if (isValid) {
                return ResponseEntity.ok("{\"status\": \"valid\"}");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("{\"status\": \"invalid\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"Error al validar el código TOTP\"}");
        }
    }
}


