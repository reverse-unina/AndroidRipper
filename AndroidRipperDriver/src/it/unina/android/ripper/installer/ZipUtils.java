/**
 * GNU Affero General Public License, version 3
 * 
 * Copyright (c) 2014-2017 REvERSE, REsEarch gRoup of Software Engineering @ the University of Naples Federico II, http://reverse.dieti.unina.it/
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package it.unina.android.ripper.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	public static boolean containsDirectory(String fileAbsolutePath, String pathToCheck) throws Exception {

		String f = convertToFileURL(fileAbsolutePath);
		// System.out.println(f);
		URI fileURI = URI.create(f);

		Map<String, String> zip_properties = new HashMap<>();
		zip_properties.put("create", "false");

		try (FileSystem zipfs = FileSystems.newFileSystem(fileURI, zip_properties)) {
			Path pathInZipfile = zipfs.getPath(pathToCheck);
			return Files.exists(pathInZipfile);
		}
	}

	public static void deleteFromZip(String fileAbsolutePath) throws Exception {
		String f = convertToFileURL(fileAbsolutePath);
		// System.out.println(f);
		URI uri = URI.create(f);
		deleteFromZip(uri);
	}

	public static void deleteFromZip(URI fileURI) throws Exception {
		Map<String, String> zip_properties = new HashMap<>();
		zip_properties.put("create", "false");
		try (FileSystem zipfs = FileSystems.newFileSystem(fileURI, zip_properties)) {
			Path pathInZipfile = zipfs.getPath("META-INF");
			removeRecursive(pathInZipfile);
			// System.out.println("File successfully deleted");
		}
	}

	public static void removeRecursive(Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				deleteFile(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				// try to delete the file anyway, even if its attributes
				// could not be read, since delete-only access is
				// theoretically possible
				deleteFile(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (exc == null) {
					// Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					// directory iteration failed; propagate exception
					throw exc;
				}
			}

			public void deleteFile(Path file) throws IOException {
				String fileString = file.toString();
				if (fileString != null && (fileString.endsWith(".RSA") || fileString.endsWith(".SF")
						|| fileString.endsWith("serverid") || fileString.endsWith("MANIFEST.MF"))) {
					Files.delete(file);
				}
			}
		});
	}

	public static String convertToFileURL(String filename) {
		String path = new File(filename).getAbsolutePath();
		if (File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		String retVal = "jar:file:" + path;

		return retVal;
	}
	
	public static void unZip(String zipFile, String outputFolder) {

		byte[] buffer = new byte[10240];

		try {

			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				String fileName = ze.getName();				
				
				if (ze.isDirectory()) {
					File newFile = new File(outputFolder + File.separator + fileName + File.separator);
					newFile.mkdirs();
				} else {
					File newFile = new File(outputFolder + File.separator + fileName);
				
					//System.out.println("file unzip : " + newFile.getAbsoluteFile());
	
					// create all non exists folders
					// else you will hit FileNotFoundException for compressed folder
					new File(newFile.getParent()).mkdirs();
	
					FileOutputStream fos = new FileOutputStream(newFile);
	
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
	
					fos.close();
				}
				
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			System.out.println("Unzip " + zipFile + " done!");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void zipDir(String zipFile, String srcDir) throws Exception {
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		addDirToZipArchive(zos, new File(srcDir), null, true);
		zos.flush();
		fos.flush();
		zos.close();
		fos.close();
		System.out.println("Zip " + zipFile + " done!");
	}
	
	public static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName, boolean first) throws Exception {
	    if (fileToZip == null || !fileToZip.exists()) {
	        return;
	    }

	    String zipEntryName = fileToZip.getName();
	    if (parrentDirectoryName!=null && !parrentDirectoryName.isEmpty()) {
	        zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
	    }

	    if (fileToZip.isDirectory()) {
	        //System.out.println("+" + zipEntryName);
	        for (File file : fileToZip.listFiles()) {
	        	if (first) {
	        		addDirToZipArchive(zos, file, "", false);
	        	} else {
	        		addDirToZipArchive(zos, file, zipEntryName, false);
	        	}
	        }
	    } else {
	        //System.out.println("   " + zipEntryName);
	        byte[] buffer = new byte[10240];
	        FileInputStream fis = new FileInputStream(fileToZip);
	        zos.putNextEntry(new ZipEntry(zipEntryName));
	        int length;
	        while ((length = fis.read(buffer)) > 0) {
	            zos.write(buffer, 0, length);
	        }
	        zos.closeEntry();
	        fis.close();
	    }
	}
}
