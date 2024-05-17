package com.test.services.symetric;

import java.io.Serializable;
import java.util.Optional;

import com.test.controllers.config.ApiDownloadInput;
import com.test.models.local.symetric.SymetricDataInput;
import com.test.models.local.symetric.SymetricDataOutput;
import com.test.models.local.symetric.SymetricFileDTO;
import com.test.models.local.symetric.SymetricKey;
import com.test.models.local.symetric.SymetricModelHolder;
import com.test.models.local.symetric.SymetricSealdObject;
import com.test.models.local.symetric.SymetricType;

public interface SymetricEncryption {

  public SymetricKey getKey(SymetricType symetricType);

  public SymetricDataOutput encryptData(SymetricDataInput symetricDataInput);

  public SymetricDataOutput decryptData(SymetricDataInput symetricDataInput);

  public <T extends Serializable> SymetricDataOutput encryptObject(SymetricModelHolder<T> symetricModelHolder);

  public <T extends Serializable> Optional<T> decryptObject(SymetricSealdObject symetricSealdObject, Class<T> clazz);

  public ApiDownloadInput encryptFile(SymetricFileDTO symetricFileDTO);

  public ApiDownloadInput decryptFile(SymetricFileDTO symetricFileDTO);
}