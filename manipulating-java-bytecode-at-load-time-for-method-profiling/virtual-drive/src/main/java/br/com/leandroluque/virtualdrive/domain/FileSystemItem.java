package br.com.leandroluque.virtualdrive.domain;

import java.util.Collection;

public interface FileSystemItem {
  String getName();

  Folder getParent();

  long getSizeMB();

  Collection<FileSystemItem> getPathFromRoot();
}
