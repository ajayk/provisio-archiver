package io.tesla.proviso.archive;


public abstract class ArchiveHandlerSupport implements ArchiveHandler {

  @Override
  public ExtendedArchiveEntry createEntryFor(String entryName, Entry archiveEntry, boolean isExecutable) {
    ExtendedArchiveEntry entry = newEntry(entryName);
    entry.setSize(archiveEntry.getSize());
    if (archiveEntry.getFileMode() != -1) {
      entry.setFileMode(archiveEntry.getFileMode());
      if (isExecutable) {
        entry.setFileMode(FileMode.makeExecutable(entry.getFileMode()));
      }
    } else {
      if (isExecutable) {
        entry.setFileMode(FileMode.EXECUTABLE_FILE.getBits());
      }
    }
    return entry;
  }
}
