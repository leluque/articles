package br.com.leandroluque.virtualdrive.domain;

import java.util.*;

public abstract class BaseFileSystemItem implements FileSystemItem {
	private String name;
	private Folder parent;
	
	public BaseFileSystemItem(String name) {
		this(name, null);
	}
	
	public BaseFileSystemItem(String name, Folder parent) {
		setName(name);
		setParent(parent);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		Objects.requireNonNull(name);
		name = name.trim();
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty");
		}
		this.name = name;
	}

	public Folder getParent() {
		return this.parent;
	}
	
	public void setParent(Folder parent) {
		this.parent = parent;
	}
	
	public Collection<FileSystemItem> getPathFromRoot() {
		List<FileSystemItem> path = new LinkedList<>();
		FileSystemItem currentFolder = this;
		do {
			path.add(0, currentFolder);
			currentFolder = currentFolder.getParent();
		} while(currentFolder != null);
		return path;
	}
}
