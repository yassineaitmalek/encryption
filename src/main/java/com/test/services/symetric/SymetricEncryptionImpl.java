package com.test.services.symetric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.test.controllers.config.ApiDownloadInput;
import com.test.exception.config.ServerSideException;
import com.test.models.local.symetric.SymetricDataInput;
import com.test.models.local.symetric.SymetricDataOutput;
import com.test.models.local.symetric.SymetricFileDTO;
import com.test.models.local.symetric.SymetricKey;
import com.test.models.local.symetric.SymetricModelHolder;
import com.test.models.local.symetric.SymetricSealdObject;
import com.test.models.local.symetric.SymetricType;
import com.test.utility.EncryptionUtility;
import com.test.utility.FileUtility;

import io.vavr.control.Try;

@Service
public class SymetricEncryptionImpl implements SymetricEncryption {

  public KeyGenerator getKeyGenerator(SymetricType symetricType) throws NoSuchAlgorithmException {
    return KeyGenerator.getInstance(symetricType.getAlgo());
  }

  public SymetricKey getKey(SymetricType symetricType) {

    return Try.of(() -> symetricType)
        .mapTry(this::getKeyGenerator)
        .map(KeyGenerator::generateKey)
        .map(SecretKey::getEncoded)
        .map(EncryptionUtility::bytesToString)
        .map(e -> new SymetricKey(e, symetricType))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public Key parseKey(String key, SymetricType symetricType) {

    return Try.of(() -> key)
        .map(EncryptionUtility::stringToBytes)
        .map(e -> new SecretKeySpec(e, 0, e.length, symetricType.getAlgo()))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public AlgorithmParameterSpec getInitializationVector() {
    byte[] initializationVector = { 22, 33, 11, 44, 55, 99, 66, 77 };
    return Optional.ofNullable(initializationVector).map(IvParameterSpec::new).orElse(null);
  }

  public Cipher encryptMode(String secretKey, SymetricType symetricType) {
    return Try.of(() -> secretKey)
        .mapTry(e -> encryptModeImpl(e, symetricType))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public Cipher encryptModeImpl(String secretKey, SymetricType symetricType)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
    Cipher cipher = Cipher.getInstance(symetricType.getTransformation());
    if (symetricType.isRequireInitializationVector()) {
      cipher.init(Cipher.ENCRYPT_MODE, parseKey(secretKey, symetricType), getInitializationVector());
    } else {
      cipher.init(Cipher.ENCRYPT_MODE, parseKey(secretKey, symetricType));
    }

    return cipher;
  }

  public String encryptData(byte[] bytes, String secretKey, SymetricType symetricType) {
    return Try.of(() -> secretKey)
        .mapTry(e -> encryptMode(e, symetricType))
        .mapTry(e -> Base64.getEncoder().encodeToString(e.doFinal(bytes)))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public Cipher decryptMode(String secretKey, SymetricType symetricType) {
    return Try.of(() -> secretKey)
        .mapTry(e -> decryptModeImpl(e, symetricType))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public Cipher decryptModeImpl(String secretKey, SymetricType symetricType)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
    Cipher cipher = Cipher.getInstance(symetricType.getTransformation());
    if (symetricType.isRequireInitializationVector()) {
      cipher.init(Cipher.DECRYPT_MODE, parseKey(secretKey, symetricType), getInitializationVector());
    } else {
      cipher.init(Cipher.DECRYPT_MODE, parseKey(secretKey, symetricType));
    }
    return cipher;
  }

  public String decryptData(String data, String secretKey, SymetricType symetricType) {
    return Try.of(() -> secretKey)
        .mapTry(e -> decryptMode(e, symetricType))
        .mapTry(e -> new String(e.doFinal(Base64.getDecoder().decode(data))))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public SymetricDataOutput encryptData(SymetricDataInput symetricDataInput) {

    return Try.of(() -> symetricDataInput)
        .map(e -> encryptData(EncryptionUtility.stringToBytesISO(e.getData()), e.getSecretKey(), e.getSymetricType()))
        .map(e -> new SymetricDataOutput(e, symetricDataInput.getSymetricType()))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public SymetricDataOutput decryptData(SymetricDataInput symetricDataInput) {

    return Try.of(() -> symetricDataInput)
        .map(e -> decryptData(e.getData(), e.getSecretKey(), e.getSymetricType()))
        .map(e -> new SymetricDataOutput(e, symetricDataInput.getSymetricType()))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public <T extends Serializable> SymetricDataOutput encryptObject(SymetricModelHolder<T> symetricModelHolder) {
    return Try.of(() -> symetricModelHolder)
        .mapTry(e -> encryptObjectImpl(e.getModel(), e.getSecretKey(), e.getSymetricType()))
        .onFailure(ServerSideException::reThrow)
        .get();

  }

  public <T extends Serializable> SymetricDataOutput encryptObjectImpl(T data, String secretKey,
      SymetricType symetricType)
      throws IOException, IllegalBlockSizeException {

    Cipher cipher = encryptMode(secretKey, symetricType);
    SealedObject sealedObject = new SealedObject(data, cipher);
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    ObjectOutput out = new ObjectOutputStream(boas);
    out.writeObject(sealedObject);
    return new SymetricDataOutput(EncryptionUtility.bytesToString(boas.toByteArray()), symetricType);

  }

  public <T extends Serializable> Optional<T> decryptObject(SymetricSealdObject symetricSealdObject, Class<T> clazz) {

    Cipher cipher = decryptMode(symetricSealdObject.getSecretKey(), symetricSealdObject.getSymetricType());
    return Try.of(symetricSealdObject::getSealedObject)
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

  public ApiDownloadInput encryptFile(SymetricFileDTO symetricFileDTO) {

    Cipher cipher = encryptMode(symetricFileDTO.getSecretKey(), symetricFileDTO.getSymetricType());
    try (InputStream inputStream = symetricFileDTO.getFile().getInputStream();
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
          .fileName(FileUtility.getFileNameWithoutExtension(symetricFileDTO.getFile().getOriginalFilename()))
          .ext((FileUtility.getFileExtension(symetricFileDTO.getFile().getOriginalFilename()).concat(".")
              .concat(symetricFileDTO.getSymetricType().getEncExt())))
          .build();

    } catch (Exception e) {
      throw new ServerSideException(e);
    }

  }

  public ApiDownloadInput decryptFile(SymetricFileDTO symetricFileDTO) {

    Cipher cipher = decryptMode(symetricFileDTO.getSecretKey(), symetricFileDTO.getSymetricType());
    try (InputStream inputStream = symetricFileDTO.getFile().getInputStream();
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
          .fileName(FileUtility.getFileNameWithoutExtension(symetricFileDTO.getFile().getOriginalFilename()))
          .ext(FileUtility.getFileExtension(symetricFileDTO.getFile())
              .replace(symetricFileDTO.getSymetricType().getEncExt(), ""))
          .build();

    } catch (Exception e) {
      throw new ServerSideException(e);
    }

  }

}