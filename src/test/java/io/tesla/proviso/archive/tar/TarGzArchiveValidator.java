package io.tesla.proviso.archive.tar;

import io.tesla.proviso.archive.AbstractArchiveValidator;
import io.tesla.proviso.archive.tar.TarGzArchiveSource;
import java.io.File;

public class TarGzArchiveValidator extends AbstractArchiveValidator {

  public TarGzArchiveValidator(File archive) throws Exception {
    super(new TarGzArchiveSource(archive));
  }

}
