package br.com.leandroluque.virtualdrive.domain;

import java.util.ArrayList;
import java.util.Collection;

public class Folder extends BaseFileSystemItem {
	private final Collection<FileSystemItem> children;

	public Folder(String name) {
		this(name, null);
	}
	
	public Folder(String nome, Folder parent) {
		super(nome, parent);
		this.children = new ArrayList<>();
	}

	public boolean isRoot() {
		return getParent() == null;
	}

	public void addFile(File file) {
		this.children.add(file);
	}

	public File newFile(String name, long sizeMB) {
		File newFile = new File(this, name, sizeMB);
		this.addFile(newFile);
		return newFile;
	}

	public Folder newFolder(String name) {
		Folder newFolder = new Folder(name, this);
		this.children.add(newFolder);
		return newFolder;
	}

	public long getSizeMB() {
		return children.stream().mapToLong(FileSystemItem::getSizeMB).sum();
	}

	public Collection<FileSystemItem> getChildren() {
		return new ArrayList<>(this.children);
	}
}