package vu.mif.ingvaras.galinskas;

import com.google.common.base.Stopwatch;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.pqc.crypto.mceliece.*;
import vu.mif.ingvaras.galinskas.attacks.*;
import vu.mif.ingvaras.galinskas.math.MatrixOperations;
import vu.mif.ingvaras.galinskas.math.domain.BinaryMatrix;

import java.security.SecureRandom;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws InvalidCipherTextException {

        for(int t = 50; t <= 50; t++) {
            McElieceParameters params = new McElieceParameters(10, t);
            McElieceKeyGenerationParameters genParams = new McElieceKeyGenerationParameters(new SecureRandom(), params);

            McElieceKeyPairGenerator keyGeneration = new McElieceKeyPairGenerator();
            keyGeneration.init(genParams);
            AsymmetricCipherKeyPair keyPair = keyGeneration.generateKeyPair();

            McEliecePublicKeyParameters publicKey = (McEliecePublicKeyParameters) keyPair.getPublic();
            McEliecePrivateKeyParameters privateKey = (McEliecePrivateKeyParameters) keyPair.getPrivate();

            System.out.println("n: " + publicKey.getN());
            System.out.println("k: " + publicKey.getK());
            System.out.println("t: " + publicKey.getT());

            McElieceCipher encrypt = new McElieceCipher();
            encrypt.init(true, keyPair.getPublic());
            McElieceCipher decrypt = new McElieceCipher();
            decrypt.init(false, privateKey);

            byte[] message = {23, 14};
            ArrayList<Boolean> cipher = MatrixOperations.toLittleEndian(BinaryMatrix.createBinaryVector(MatrixOperations.byteArrayToBits(encrypt.messageEncrypt(message))));

            System.out.println("message: " + MatrixOperations.trimString(MatrixOperations.computeMessageRepresentative(message, publicKey.getK()).toString()));
            System.out.println("cipher: " + MatrixOperations.convertToString(cipher));

            Stopwatch stopwatch = Stopwatch.createStarted();

            System.out.println("-----BRUTE FORCE ATTACK-----");
            CipherAttack bruteForce = BruteForceAttack.builder()
                    .k(publicKey.getK())
                    .n(publicKey.getN())
                    .t(publicKey.getT())
                    .G(publicKey.getG())
                    .build();

            bruteForce.attack(cipher, stopwatch);

            System.out.println("-----MESSAGE RESEND ATTACK-----");
            stopwatch = Stopwatch.createStarted();
            CipherAttack messageResendAttack = MessageResendAttack.builder()
                    .k(publicKey.getK())
                    .n(publicKey.getN())
                    .t(publicKey.getT())
                    .G(publicKey.getG())
                    .additionalCipher(MatrixOperations.toLittleEndian(BinaryMatrix.createBinaryVector(MatrixOperations.byteArrayToBits(encrypt.messageEncrypt(message)))))
                    .build();

            messageResendAttack.attack(cipher, stopwatch);*/

            System.out.println("-----RECEIVER RESPONSE ATTACK-----");
            stopwatch = Stopwatch.createStarted();
            CipherAttack receiverResponseAttack = ReceiverResponseAttack.builder()
                    .k(publicKey.getK())
                    .n(publicKey.getN())
                    .t(publicKey.getT())
                    .G(publicKey.getG())
                    .decrypt(decrypt)
                    .message(message)
                    .build();

            receiverResponseAttack.attack(cipher, stopwatch);

            int kr = 0;
            for(int i = 0; i < 3; i++) {
                System.out.println("-----PARTIALLY KNOWN TEXT ATTACK-----");
                System.out.println(25+i*25+"%");
                stopwatch = Stopwatch.createStarted();
                kr += publicKey.getK()/4;
                CipherAttack partiallyKnownTextAttack = PartiallyKnownTextAttack.builder()
                        .k(publicKey.getK())
                        .n(publicKey.getN())
                        .t(publicKey.getT())
                        .G(publicKey.getG())
                        .kr(kr)
                        .mr(new ArrayList<>(BinaryMatrix.createBinaryVector(MatrixOperations.trimString(MatrixOperations.computeMessageRepresentative(message, publicKey.getK()).toString())).subList(publicKey.getK() - kr, publicKey.getK())))
                        .build();

                partiallyKnownTextAttack.attack(cipher, stopwatch);
            }
        }

    }

}
