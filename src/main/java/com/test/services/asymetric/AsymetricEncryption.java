package com.test.services.asymetric;

import java.io.Serializable;
import java.util.Optional;

import com.test.controllers.config.ApiDownloadInput;
import com.test.models.local.asymetric.AsymetricDataDecryptionInput;
import com.test.models.local.asymetric.AsymetricDataEncryptionInput;
import com.test.models.local.asymetric.AsymetricDataOutput;
import com.test.models.local.asymetric.AsymetricDecryptionFileDTO;
import com.test.models.local.asymetric.AsymetricEncryptionFileDTO;
import com.test.models.local.asymetric.AsymetricKey;
import com.test.models.local.asymetric.AsymetricModelHolder;
import com.test.models.local.asymetric.AsymetricSealdObject;
import com.test.models.local.asymetric.AsymetricType;

public interface AsymetricEncryption {

  public AsymetricKey getKey(AsymetricType asymetricType);

  public AsymetricDataOutput encryptData(AsymetricDataEncryptionInput asymetricDataEncryptionInput);

  public AsymetricDataOutput decryptData(AsymetricDataDecryptionInput asymetricDataDecryptionInput);

  public ApiDownloadInput encryptFile(AsymetricEncryptionFileDTO asymetricEncryptionFileDTO);

  public ApiDownloadInput decryptFile(AsymetricDecryptionFileDTO asymetricDecryptionFileDTO);

  public <T extends Serializable> AsymetricDataOutput encryptObject(AsymetricModelHolder<T> asymetricModelHolder);

  public <T extends Serializable> Optional<T> decryptObject(AsymetricSealdObject asymetricSealdObject, Class<T> clazz);

}
