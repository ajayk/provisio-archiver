package io.tesla.proviso.archive.zip;

import com.google.common.io.ByteStreams;
import io.tesla.proviso.archive.Entry;
import io.tesla.proviso.archive.ExtendedSource;
import io.tesla.proviso.archive.perms.FileMode;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class ZipArchiveSource implements ExtendedSource {

  private ZipFile zipFile;
  private Enumeration<ZipArchiveEntry> entries;

  public ZipArchiveSource(File archive) {
    try {
      zipFile = new ZipFile(archive, "UTF8", true) {
        @Override
        protected void finalize() throws Throwable {
          super.close();
        }
      };
      entries = zipFile.getEntries();
    } catch (IOException e) {
      throw new RuntimeException(String.format("Cannot determine the type of archive %s.", archive), e);
    }
  }

  public Entry entry(String name) {
    return new EntrySourceArchiveEntry(zipFile.getEntry(name));
  }

  @Override
  public Iterable<Entry> entries() {
    return () -> new ArchiveEntryIterator();
  }

  @Override
  public void close() throws IOException {
    zipFile.close();
  }

  @Override
  public boolean isDirectory() {
    return true;
  }

  class EntrySourceArchiveEntry implements Entry {

    final ZipArchiveEntry archiveEntry;

    public EntrySourceArchiveEntry(ZipArchiveEntry archiveEntry) {
      this.archiveEntry = archiveEntry;
    }

    @Override
    public String getName() {
      return archiveEntry.getName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return zipFile.getInputStream(archiveEntry);
    }

    @Override
    public long getSize() {
      return archiveEntry.getSize();
    }

    @Override
    public void writeEntry(OutputStream outputStream) throws IOException {
      // We specifically do not close the entry because if you do then you can't read anymore archive removalsAndDifferences from the stream
      ByteStreams.copy(getInputStream(), outputStream);
    }

    @Override
    public int getFileMode() {
      return archiveEntry.getUnixMode();
    }

    @Override
    public boolean isDirectory() {
      return archiveEntry.isDirectory();
    }

    @Override
    public boolean isExecutable() {
      return FileMode.EXECUTABLE_FILE.equals(getFileMode());
    }

    @Override
    public long getTime() {
      return archiveEntry.getTime();
    }
  }

  class ArchiveEntryIterator implements Iterator<Entry> {

    @Override
    public Entry next() {
      return new EntrySourceArchiveEntry(entries.nextElement());
    }

    @Override
    public boolean hasNext() {
      return entries.hasMoreElements();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove method not implemented");
    }
  }

}
