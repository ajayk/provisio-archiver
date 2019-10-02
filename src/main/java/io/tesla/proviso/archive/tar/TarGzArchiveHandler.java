package io.tesla.proviso.archive.tar;

import io.tesla.proviso.archive.ArchiveHandlerSupport;
import io.tesla.proviso.archive.Entry;
import io.tesla.proviso.archive.ExtendedArchiveEntry;
import io.tesla.proviso.archive.ExtendedSource;
import io.tesla.proviso.archive.Source;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

public class TarGzArchiveHandler extends ArchiveHandlerSupport {

  private final File archive;
  private final boolean posixLongFileMode;

  public TarGzArchiveHandler(File archive, boolean posixLongFileMode) {
    this.archive = archive;
    this.posixLongFileMode = posixLongFileMode;
  }

  @Override
  public ExtendedArchiveEntry newEntry(String entryName, Entry entry) {
    return new ExtendedTarArchiveEntry(entryName, entry);
  }

  @Override
  public ArchiveInputStream getInputStream() throws IOException {
    return new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(archive)));
  }

  @Override
  public ArchiveOutputStream getOutputStream() throws IOException {
    TarArchiveOutputStream stream = new TarArchiveOutputStream(new GzipCompressorOutputStream(new FileOutputStream(archive)));
    if (posixLongFileMode) {
      stream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
    }
    return stream;
  }

  @Override
  public Source getArchiveSource() {
    return new TarGzArchiveSource(archive);
  }

  @Override
  public ExtendedSource getArchiveExtendedSource() {
    return new TarGzArchiveSource(archive);
  }

}
