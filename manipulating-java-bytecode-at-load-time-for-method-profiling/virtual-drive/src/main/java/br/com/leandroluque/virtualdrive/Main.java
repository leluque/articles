package br.com.leandroluque.virtualdrive;

import br.com.leandroluque.virtualdrive.domain.File;
import br.com.leandroluque.virtualdrive.domain.FileSystemItem;
import br.com.leandroluque.virtualdrive.domain.Folder;

public class Main {
	public static void main(String[] args) {
		Folder root = new Folder("root");
		Folder opt = root.newFolder("opt");
		Folder homebrew = opt.newFolder("homebrew");
		Folder lib = homebrew.newFolder("lib");
		File libboost_regex = lib.newFile("libboost_regex.dylib", 49/(1024*1024));

		System.out.println(libboost_regex.getPathFromRoot().stream().map(FileSystemItem::getName).reduce("", (a, b) -> a + "/" + b));
	}
}

