package com.example.authserver.service.auth;

import com.example.authserver.model.AppleAuth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * Created by SingleMalt on 4/1/16.
 */
public class AppleAuthService extends AuthService<AppleAuth> {
    private static final Logger logger = LogManager.getLogger(AppleAuthService.class);

    @Override
    public boolean isFirstPartyAuthed(AppleAuth auth) {
        try {
            // validate request with apple
            // first, check the url
            URI uri = new URI(auth.publicKeyUrl);
            String[] uriParts = uri.getHost().split("\\.");
            if(!uriParts[uriParts.length-2].equals("apple") || !uriParts[uriParts.length-1].equals("com")) {
                throw new IllegalArgumentException("Invalid publicKeyUrl entered");
            }

            // next, build the request to apple
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
            byteBuffer.putLong(Long.parseLong(auth.timestamp));

            byte[] bytePlayerID = auth.playerId.getBytes("UTF-8");
            byte[] bundleID = auth.bundleId.getBytes("UTF-8");
            byte[] timestamp = byteBuffer.array();
            byte[] salt = DatatypeConverter.parseBase64Binary(auth.salt);
            byte[] signature = DatatypeConverter.parseBase64Binary(auth.signature);

            ByteBuffer verifyBuffer = ByteBuffer.allocate(bytePlayerID.length+bundleID.length+8+salt.length)
                    .put(bytePlayerID)
                    .put(bundleID)
                    .put(timestamp)
                    .put(salt);

            boolean verified;

            Certificate cert = CertificateFactory.getInstance("X.509")
                    .generateCertificate(new URL(auth.publicKeyUrl).openConnection().getInputStream());

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(cert);

            sig.update(verifyBuffer.array());

            verified = sig.verify(signature);

            logger.debug(String.format("Apple verification response: %s", verified));

            return verified;
        } catch (Exception e) {
            logger.error("Cert/Validation issue with Apple: {}", e);
            return false;
        }
    }
}
