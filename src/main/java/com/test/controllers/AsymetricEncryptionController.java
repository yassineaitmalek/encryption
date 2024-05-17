package com.test.controllers;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.controllers.config.AbstractController;
import com.test.controllers.config.ApiDataResponse;
import com.test.models.local.TestModel;
import com.test.models.local.asymetric.AsymetricDataDecryptionInput;
import com.test.models.local.asymetric.AsymetricDataEncryptionInput;
import com.test.models.local.asymetric.AsymetricDataOutput;
import com.test.models.local.asymetric.AsymetricDecryptionFileDTO;
import com.test.models.local.asymetric.AsymetricEncryptionFileDTO;
import com.test.models.local.asymetric.AsymetricKey;
import com.test.models.local.asymetric.AsymetricModelHolder;
import com.test.models.local.asymetric.AsymetricSealdObject;
import com.test.models.local.asymetric.AsymetricType;
import com.test.services.asymetric.AsymetricEncryption;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/encryption/asymetric")
@RequiredArgsConstructor
public class AsymetricEncryptionController implements AbstractController {

  private final AsymetricEncryption asymetricEncryption;

  @GetMapping("/key")
  public ResponseEntity<ApiDataResponse<AsymetricKey>> getKey(AsymetricType asymetricType) {

    return ok(() -> asymetricEncryption.getKey(asymetricType));
  }

  @PostMapping("/encrypt/data")
  public ResponseEntity<ApiDataResponse<AsymetricDataOutput>> encryptData(
      @RequestBody AsymetricDataEncryptionInput asymetricDataEncryptionInput) {

    return ok(() -> asymetricEncryption.encryptData(asymetricDataEncryptionInput));

  }

  @PostMapping("/decrypt/data")
  public ResponseEntity<ApiDataResponse<AsymetricDataOutput>> decryptData(
      @RequestBody AsymetricDataDecryptionInput asymetricDataDecryptionInput) {

    return ok(() -> asymetricEncryption.decryptData(asymetricDataDecryptionInput));
  }

  @PostMapping("/encrypt/object")
  public ResponseEntity<ApiDataResponse<AsymetricDataOutput>> encryptObject(
      @RequestBody AsymetricModelHolder<TestModel> asymetricModelHolder) {

    return ok(() -> asymetricEncryption.encryptObject(asymetricModelHolder));

  }

  @PostMapping("/decrypt/object")
  public ResponseEntity<ApiDataResponse<Optional<TestModel>>> decryptObjectsymetricEncryption(
      @RequestBody AsymetricSealdObject asymetricSealdObject) {

    return ok(() -> asymetricEncryption.decryptObject(asymetricSealdObject, TestModel.class));
  }

  @PutMapping(value = "/encrypt/file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<byte[]> encryptFile(AsymetricEncryptionFileDTO asymetricEncryptionFileDTO) {

    return download(asymetricEncryption.encryptFile(asymetricEncryptionFileDTO));

  }

  @PutMapping(value = "/decrypt/file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<byte[]> decryptFile(@ModelAttribute AsymetricDecryptionFileDTO asymetricDecryptionFileDTO) {

    return download(asymetricEncryption.decryptFile(asymetricDecryptionFileDTO));

  }

}
