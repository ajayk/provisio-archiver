package ca.vanzyl.provisio.archive.source;

import ca.vanzyl.provisio.archive.ExtendedArchiveEntry;
import ca.vanzyl.provisio.archive.Source;
import com.google.common.collect.Iterators;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.codehaus.plexus.util.DirectoryScanner;

public class DirectorySource implements Source {
  private final File[] sourceDirectories;

  public DirectorySource(File... sourceDirectories) {
    this.sourceDirectories = sourceDirectories;
  }

  @Override
  public Iterable<ExtendedArchiveEntry> entries() {
    return () -> {
      DirectoryEntryIterator[] iterators = new DirectoryEntryIterator[sourceDirectories.length];
      for (int i = 0; i < iterators.length; i++) {
        iterators[i] = new DirectoryEntryIterator(sourceDirectories[i]);
      }
      return Iterators.concat(iterators);
    };
  }

  static class DirectoryEntryIterator implements Iterator<ExtendedArchiveEntry> {
    final String[] files;
    final File sourceDirectory;
    int currentFileIndex;

    DirectoryEntryIterator(File sourceDirectory) {
      DirectoryScanner scanner = new DirectoryScanner();
      scanner.setBasedir(sourceDirectory);
      scanner.setCaseSensitive(true);
      scanner.scan();
      List<String> entries = new ArrayList<>();
      // We need to include the directories to preserved the archiving of empty directories
      Stream.of(scanner.getIncludedFiles(), scanner.getIncludedDirectories()).flatMap(Stream::of).sorted().forEach(f -> {
        if (!f.isEmpty()) {
          entries.add(f.replace('\\', '/'));
        }
      });
      this.files = entries.toArray(new String[0]);
      this.sourceDirectory = sourceDirectory;
    }

    @Override
    public boolean hasNext() {
      return currentFileIndex != files.length;
    }

    @Override
    public ExtendedArchiveEntry next() {
      String pathRelativeToSourceDirectory = files[currentFileIndex++];
      File file = new File(sourceDirectory, pathRelativeToSourceDirectory);
      String archiveEntryName = String.format("%s/%s", sourceDirectory.getName().replace('\\', '/'), pathRelativeToSourceDirectory);
      return new FileEntry(archiveEntryName, file);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove method not implemented");
    }
  }

  @Override
  public void close() throws IOException {}

  @Override
  public boolean isDirectory() {
    return true;
  }

}
