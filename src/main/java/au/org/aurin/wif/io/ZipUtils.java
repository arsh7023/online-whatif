package au.org.aurin.wif.io;

import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtils {
  public static final Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);

  /**
   * Takes in the outputDir(extracted files are placed here), zipArchive and
   * file encoding;
   * 
   * @param outputDir
   * @param zipArchive
   * @param encoding
   * @return the output directory
   * @throws IOException
   */
  public static File extract(final String outputDir, final File zipArchive,
      final String encoding) throws IOException {
    org.apache.commons.compress.archivers.zip.ZipFile file = null;
    InputStream in = null;
    OutputStream out = null;
    File outDir = new File(outputDir);

    try {
      file = new org.apache.commons.compress.archivers.zip.ZipFile(zipArchive,
          encoding);
      final Enumeration<ZipArchiveEntry> entries = file.getEntries();
      while (entries.hasMoreElements()) {
        final ZipArchiveEntry entry = entries.nextElement();
        final String entryName = entry.getName();
        if (entry.isDirectory()) {
          outDir = new File(outputDir + File.separator + entryName);
          forceMkdir(outDir);
          LOGGER.info("created output directory: " + outDir.getAbsolutePath());
          continue;
        }

        final File outFile = new File(outDir + File.separator + entryName);
        out = openOutputStream(outFile);
        in = file.getInputStream(entry);
        copy(in, out);
        LOGGER.info("extracted: " + outFile.getAbsolutePath());
        closeQuietly(in);
        closeQuietly(out);
      }
    } finally {
      closeQuietly(in);
      closeQuietly(out);
      org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly(file);
    }

    // ali
    final String str = zipArchive.getAbsolutePath();
    zipArchive.delete();
    LOGGER.info("zip file deleted: " + str);
    // end ali

    LOGGER.info("returning output directory: " + outDir.getAbsolutePath());
    return outDir;
  }

  /*
   * public static void extractTar(String outputDir, File tarFile, String
   * encoding) throws IOException {
   * org.apache.commons.compress.archivers.zip.ZipFile file = null; InputStream
   * in = null; OutputStream out = null; try { file = new
   * org.apache.commons.compress.archivers.zip.ZipFile(tarFile, encoding);
   * TarArchiveInputStream tin = new TarArchiveInputStream(new
   * FileInputStream(tarFile)); tin.get Enumeration<ZipArchiveEntry> entries =
   * file.getEntries(); while (entries.hasMoreElements()) { ZipArchiveEntry
   * entry = entries.nextElement(); String entryName = entry.getName(); if
   * (entry.isDirectory()) { forceMkdir(new File(outputDir + File.separator +
   * entryName)); continue; } File outFile = new File(outputDir +
   * File.separator+ entryName); out = openOutputStream(outFile); in =
   * file.getInputStream(entry); copy(in, out);
   * LOGGER.info("extracted: "+outFile.getAbsolutePath()); closeQuietly(in);
   * closeQuietly(out); } } finally { closeQuietly(in); closeQuietly(out);
   * org.apache.commons.compress.archivers.zip.ZipFile.closeQuietly(file); } }
   */
  /**
   * Takes in the contents directory and the zipfile and writes the contents of
   * the directory to the zipfile.
   * 
   * @param contentsDirPath
   * @param zipFile
   *          - The final output
   * @throws IOException
   */

  public static void compress(final String contentsDirPath, final File zipFile)
      throws IOException {
    final List<File> files = new ArrayList<File>();
    final File contentsDir = new File(contentsDirPath);
    listFileAndDirectories(contentsDir, files);
    InputStream in = null;
    ZipArchiveOutputStream out = null;

    try {
      out = new ZipArchiveOutputStream(zipFile);
      final String contentsDirAbsolutePath = contentsDir.getAbsolutePath();
      for (final File file : files) {
        final String entryName = file.getAbsolutePath().substring(
            contentsDirAbsolutePath.length() + 1)
            + (file.isDirectory() ? File.separator : "");
        final ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
        out.putArchiveEntry(entry);
        if (file.isDirectory()) {
          continue;
        }
        in = new FileInputStream(file);
        copy(in, out);
        out.closeArchiveEntry();
        closeQuietly(in);
      }
    } finally {
      closeQuietly(in);
      closeQuietly(out);
    }
  }

  static void listFileAndDirectories(final File directory, final List<File> list) {
    final File[] files = directory.listFiles();
    for (final File file : files) {
      list.add(file);
      if (file.isDirectory()) {
        listFileAndDirectories(file, list);
      }
    }

  }
}
