package au.org.aurin.wif.controller.upload;

import java.io.File;

import javax.annotation.Resource;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import au.org.aurin.wif.exception.io.ShapeFile2PostGISCreationException;
import au.org.aurin.wif.exception.io.ZipFileExtractionException;
import au.org.aurin.wif.io.FileToPostgisExporter;
import au.org.aurin.wif.io.forms.UploadForm;

/**
 * The Class FileUploadController.
 */
@Controller
@RequestMapping("/")
public class FileUploadController {

  @Resource
  private FileToPostgisExporter fileToPostgisExporter;
  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(FileUploadController.class);

  /**
   * Display form.
   * 
   * @return the string
   */
  @RequestMapping(value = "/uploadForm", method = RequestMethod.GET)
  public String displayForm() {
    LOGGER.info("Displaying upload form");
    return "file_upload_form";
  }

  /**
   * Save. TODO Make this a real automated test.
   * 
   * @param uploadForm
   *          the upload form
   * @param map
   *          the map
   * @return the string
   * @throws ShapeFile2PostGISCreationException
   * @throws ZipFileExtractionException
   */
  @RequestMapping(value = "/saveAndShow", method = RequestMethod.POST)
  public String save(@ModelAttribute("uploadForm") UploadForm uploadForm,
      Model map) throws ShapeFile2PostGISCreationException,
      ZipFileExtractionException {
    LOGGER.info("Saving the following file:");
    MultipartFile multipartFile = uploadForm.getFile();
    String filename = multipartFile.getOriginalFilename();
    LOGGER.info("Filename = {}", filename);
    File resultFile = fileToPostgisExporter.getZipFile(multipartFile);
    LOGGER.info("AbsolutePath = {}", resultFile.getAbsolutePath());
    File zipFile = new File(resultFile.getAbsolutePath());
    SimpleFeatureCollection featureCollection = fileToPostgisExporter
        .zipFileToPostGIS(zipFile);
    map.addAttribute("files", filename);
    map.addAttribute("features", featureCollection.size());
    return "file_upload_success";
  }

}
