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
import com.test.models.local.symetric.SymetricDataInput;
import com.test.models.local.symetric.SymetricDataOutput;
import com.test.models.local.symetric.SymetricFileDTO;
import com.test.models.local.symetric.SymetricKey;
import com.test.models.local.symetric.SymetricModelHolder;
import com.test.models.local.symetric.SymetricSealdObject;
import com.test.models.local.symetric.SymetricType;
import com.test.services.symetric.SymetricEncryption;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/encryption/symetric")
@RequiredArgsConstructor
public class SymetricEncryptionController implements AbstractController {

  private final SymetricEncryption symetricEncryption;

  @GetMapping("/key")
  public ResponseEntity<ApiDataResponse<SymetricKey>> getKey(SymetricType symetricType) {

    return ok(() -> symetricEncryption.getKey(symetricType));
  }

  @PostMapping("/encrypt/data")
  public ResponseEntity<ApiDataResponse<SymetricDataOutput>> encryptData(
      @RequestBody SymetricDataInput symetricDataInput) {

    return ok(() -> symetricEncryption.encryptData(symetricDataInput));

  }

  @PostMapping("/decrypt/data")
  public ResponseEntity<ApiDataResponse<SymetricDataOutput>> decryptData(
      @RequestBody SymetricDataInput symetricDataInput) {

    return ok(() -> symetricEncryption.decryptData(symetricDataInput));
  }

  @PostMapping("/encrypt/object")
  public ResponseEntity<ApiDataResponse<SymetricDataOutput>> encryptObject(
      @RequestBody SymetricModelHolder<TestModel> symetricModelHolder) {

    return ok(() -> symetricEncryption.encryptObject(symetricModelHolder));

  }

  @PostMapping("/decrypt/object")
  public ResponseEntity<ApiDataResponse<Optional<TestModel>>> decryptObjectsymetricEncryption(
      @RequestBody SymetricSealdObject symetricSealdObject) {

    return ok(() -> symetricEncryption.decryptObject(symetricSealdObject, TestModel.class));
  }

  @PutMapping(value = "/encrypt/file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<byte[]> encryptFile(@ModelAttribute SymetricFileDTO symetricFileDTO) {

    return download(symetricEncryption.encryptFile(symetricFileDTO));

  }

  @PutMapping(value = "/decrypt/file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<byte[]> decryptFile(@ModelAttribute SymetricFileDTO symetricFileDTO) {

    return download(symetricEncryption.decryptFile(symetricFileDTO));

  }

}
