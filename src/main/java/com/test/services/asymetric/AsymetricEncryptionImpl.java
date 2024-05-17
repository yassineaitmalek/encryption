package com.test.services.asymetric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

import org.springframework.stereotype.Service;

import com.test.controllers.config.ApiDownloadInput;
import com.test.exception.config.ServerSideException;
import com.test.models.local.asymetric.AsymetricDataDecryptionInput;
import com.test.models.local.asymetric.AsymetricDataEncryptionInput;
import com.test.models.local.asymetric.AsymetricDataOutput;
import com.test.models.local.asymetric.AsymetricDecryptionFileDTO;
import com.test.models.local.asymetric.AsymetricEncryptionFileDTO;
import com.test.models.local.asymetric.AsymetricKey;
import com.test.models.local.asymetric.AsymetricModelHolder;
import com.test.models.local.asymetric.AsymetricSealdObject;
import com.test.models.local.asymetric.AsymetricType;
import com.test.utility.EncryptionUtility;
import com.test.utility.FileUtility;

import io.vavr.control.Try;

@Service
public class AsymetricEncryptionImpl implements AsymetricEncryption {

  public KeyFactory getKeyFactory(AsymetricType asymetricType) throws NoSuchAlgorithmException {
    return KeyFactory.getInstance(asymetricType.getAlgo());
  }

  public KeyPairGenerator getKeyGenerator(AsymetricType asymetricType) throws NoSuchAlgorithmException {
    return KeyPairGenerator.getInstance(asymetricType.getAlgo());

  }

  public AsymetricKey getKey(AsymetricType asymetricType) {

    return Try.of(() -> asymetricType)
        .mapTry(this::getKeyGenerator)
        .map(e -> generateKeyPairs(e, asymetricType))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public AsymetricKey generateKeyPairs(KeyPairGenerator keyPairGenerator, AsymetricType asymetricType) {
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    String publicKey = EncryptionUtility.bytesToString(keyPair.getPublic().getEncoded());
    String privateKey = EncryptionUtility.bytesToString(keyPair.getPrivate().getEncoded());
    return new AsymetricKey(publicKey, privateKey, asymetricType);

  }

  public Key parsePublicKey(String key, AsymetricType asymetricType) {

    return Try.of(() -> key)
        .map(EncryptionUtility::stringToBytes)
        .map(X509EncodedKeySpec::new)
        .mapTry(e -> getKeyFactory(asymetricType).generatePublic(e))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public Key parsePrivateKey(String key, AsymetricType asymetricType) {

    return Try.of(() -> key)
        .map(EncryptionUtility::stringToBytes)
        .map(PKCS8EncodedKeySpec::new)
        .mapTry(e -> getKeyFactory(asymetricType).generatePrivate(e))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public Cipher encryptMode(String publicKey, AsymetricType asymetricType) {
    return Try.of(() -> publicKey)
        .mapTry(e -> encryptModeImpl(e, asymetricType))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public Cipher encryptModeImpl(String publicKey, AsymetricType asymetricType)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
    Cipher cipher = Cipher.getInstance(asymetricType.getTransformation());
    cipher.init(Cipher.ENCRYPT_MODE, parsePublicKey(publicKey, asymetricType));
    return cipher;
  }

  public Cipher decryptMode(String privateKey, AsymetricType asymetricType) {
    return Try.of(() -> privateKey)
        .mapTry(e -> decryptModeImpl(e, asymetricType))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public Cipher decryptModeImpl(String privateKey, AsymetricType asymetricType)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
    Cipher cipher = Cipher.getInstance(asymetricType.getTransformation());
    cipher.init(Cipher.DECRYPT_MODE, parsePrivateKey(privateKey, asymetricType));
    return cipher;
  }

  public String encryptData(byte[] bytes, String publicKey, AsymetricType asymetricType) {
    return Try.of(() -> publicKey)
        .mapTry(e -> encryptMode(e, asymetricType))
        .mapTry(e -> Base64.getEncoder().encodeToString(e.doFinal(bytes)))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public String decryptData(String data, String privateKey, AsymetricType asymetricType) {
    return Try.of(() -> privateKey)
        .mapTry(e -> decryptMode(e, asymetricType))
        .mapTry(e -> new String(e.doFinal(Base64.getDecoder().decode(data))))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public AsymetricDataOutput encryptData(AsymetricDataEncryptionInput asymetricDataEncryptionInput) {

    return Try.of(() -> asymetricDataEncryptionInput)
        .map(e -> encryptData(EncryptionUtility.stringToBytesISO(e.getData()), e.getPublicKey(), e.getAsymetricType()))
        .map(e -> new AsymetricDataOutput(e, asymetricDataEncryptionInput.getAsymetricType()))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public AsymetricDataOutput decryptData(AsymetricDataDecryptionInput asymetricDataDecryptionInput) {

    return Try.of(() -> asymetricDataDecryptionInput)
        .map(e -> decryptData(e.getData(), e.getPrivateKey(), e.getAsymetricType()))
        .map(e -> new AsymetricDataOutput(e, asymetricDataDecryptionInput.getAsymetricType()))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public ApiDownloadInput encryptFile(AsymetricEncryptionFileDTO asymetricEncryptionFileDTO) {

    Cipher cipher = encryptMode(asymetricEncryptionFileDTO.getPublicKey(),
        asymetricEncryptionFileDTO.getAsymetricType());
    try (InputStream inputStream = asymetricEncryptionFileDTO.getFile().getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[64];
      int bytesRead;

      while ((bytesRead = inputStream.read(buffer)) != -1) {
        byte[] output = cipher.update(buffer, 0, bytesRead);
        if (output != null) {
          baos.write(output);

        }
      }
      byte[] outputBytes = cipher.doFinal();
      if (outputBytes != null) {
        baos.write(outputBytes);
      }

      return ApiDownloadInput.builder()
          .bytes(baos.toByteArray())
          .fileName(FileUtility.getFileNameWithoutExtension(asymetricEncryptionFileDTO.getFile().getOriginalFilename()))
          .ext((FileUtility.getFileExtension(asymetricEncryptionFileDTO.getFile().getOriginalFilename()).concat(".")
              .concat(asymetricEncryptionFileDTO.getAsymetricType().getEncExt())))
          .build();

    } catch (Exception e) {
      throw new ServerSideException(e);
    }

  }

  public ApiDownloadInput decryptFile(AsymetricDecryptionFileDTO asymetricDecryptionFileDTO) {

    Cipher cipher = decryptMode(asymetricDecryptionFileDTO.getPrivateKey(),
        asymetricDecryptionFileDTO.getAsymetricType());
    try (InputStream inputStream = asymetricDecryptionFileDTO.getFile().getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

      byte[] buffer = new byte[64];
      int bytesRead;

      while ((bytesRead = inputStream.read(buffer)) != -1) {
        byte[] output = cipher.update(buffer, 0, bytesRead);
        if (output != null) {
          baos.write(output);

        }
      }
      byte[] outputBytes = cipher.doFinal();
      if (outputBytes != null) {
        baos.write(outputBytes);
      }

      return ApiDownloadInput.builder()
          .bytes(baos.toByteArray())
          .fileName(FileUtility.getFileNameWithoutExtension(asymetricDecryptionFileDTO.getFile().getOriginalFilename()))
          .ext(FileUtility.getFileExtension(asymetricDecryptionFileDTO.getFile())
              .replace(asymetricDecryptionFileDTO.getAsymetricType().getEncExt(), ""))
          .build();

    } catch (Exception e) {
      throw new ServerSideException(e);
    }

  }

  public <T extends Serializable> AsymetricDataOutput encryptObject(AsymetricModelHolder<T> asymetricModelHolder) {
    return Try.of(() -> asymetricModelHolder)
        .mapTry(e -> encryptObjectImpl(e.getModel(), e.getPublicKey(), e.getAsymetricType()))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public <T extends Serializable> AsymetricDataOutput encryptObjectImpl(T data, String publicKey,
      AsymetricType asymetricType)
      throws IOException, IllegalBlockSizeException {

    Cipher cipher = encryptMode(publicKey, asymetricType);
    SealedObject sealedObject = new SealedObject(data, cipher);
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    ObjectOutput out = new ObjectOutputStream(boas);
    out.writeObject(sealedObject);
    return new AsymetricDataOutput(EncryptionUtility.bytesToString(boas.toByteArray()), asymetricType);

  }

  public <T extends Serializable> Optional<T> decryptObject(AsymetricSealdObject asymetricSealdObject, Class<T> clazz) {

    Cipher cipher = decryptMode(asymetricSealdObject.getPrivateKey(), asymetricSealdObject.getAsymetricType());
    return Try.of(asymetricSealdObject::getSealedObject)
        .map(EncryptionUtility::stringToBytes)
        .map(ByteArrayInputStream::new)
        .mapTry(ObjectInputStream::new)
        .mapTry(ObjectInput::readObject)
        .filter(SealedObject.class::isInstance)
        .map(SealedObject.class::cast)
        .mapTry(e -> e.getObject(cipher))
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .map(Optional::ofNullable)
        .getOrElse(Optional::empty);

  }

}
