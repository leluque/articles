package br.com.leandroluque.virtualdrive.domain;

public class File extends BaseFileSystemItem {
	private long sizeMB;

	public File(Folder parent, String name, long sizeMB) {
		super(name, parent);
		setSizeMB(sizeMB);
	}

	public long getSizeMB() {
		return this.sizeMB;
	}

	public void setSizeMB(long sizeMB) {
		if (sizeMB < 0) {
			throw new IllegalArgumentException("Size cannot be negative");
		}
		this.sizeMB = sizeMB;
	}
}
