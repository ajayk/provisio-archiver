package io.tesla.proviso.archive.zip;

import io.tesla.proviso.archive.ArchiveHandlerSupport;
import io.tesla.proviso.archive.Entry;
import io.tesla.proviso.archive.ExtendedArchiveEntry;
import io.tesla.proviso.archive.Source;
import io.tesla.proviso.archive.delta.Hash;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class ZipArchiveHandler extends ArchiveHandlerSupport {

  private final File archive;

  public ZipArchiveHandler(File archive) {
    this.archive = archive;
  }

  @Override
  public ExtendedArchiveEntry newEntry(String entryName, Entry entry) {
    return new ExtendedZipArchiveEntry(entryName, entry);
  }

  @Override
  public ArchiveInputStream getInputStream() throws IOException {
    return new ZipArchiveInputStream(new FileInputStream(archive), "UTF8", true, true);
  }

  @Override
  public ArchiveOutputStream getOutputStream() throws IOException {
    return new ZipArchiveOutputStream(new FileOutputStream(archive));
  }

  @Override
  public Source getArchiveSource() {
    return new ZipArchiveSource(archive);
  }
}
