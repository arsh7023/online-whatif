package au.org.aurin.wif.controller.upload;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
public class ZipFileUploadController {

  /** The file to postgis exporter. */
  @Resource
  private FileToPostgisExporter fileToPostgisExporter;
  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ZipFileUploadController.class);

  /**
   * Display form.
   * 
   * @return the string
   */
  @RequestMapping(value = "/upload", method = RequestMethod.GET)
  public String displayForm() {
    LOGGER.info("Displaying upload direct");
    return "file_upload";
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
   *           the shape file2 post gis creation exception
   * @throws ZipFileExtractionException
   *           the zip file extraction exception
   */
  @RequestMapping(value = "/saveFile", method = RequestMethod.POST, produces = "text/plain")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String save(@ModelAttribute("uploadForm") UploadForm uploadForm, Model map)
      throws ShapeFile2PostGISCreationException, ZipFileExtractionException {
    LOGGER.info("Saving the following file:");
    MultipartFile multipartFile = uploadForm.getFile();
    String filename = multipartFile.getOriginalFilename();
    LOGGER.info("Filename = {}", filename);
    File resultFile = fileToPostgisExporter.getZipFile(multipartFile);
    LOGGER.info("AbsolutePath = {}", resultFile.getAbsolutePath());

    return resultFile.getAbsolutePath();
  }

  /**
   * Save zip shape file, and returns a Jason with the temp file path
   * 
   * @param roleId
   *          the role id
   * @param file
   *          the file
   * @param response
   *          the response
   * @return the string
   * @throws ShapeFile2PostGISCreationException
   *           the shape file2 post gis creation exception
   * @throws ZipFileExtractionException
   *           the zip file extraction exception
   */
  @RequestMapping(value = "/saveZip", method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String saveFile(
  // TODO not yet necessary
  // @RequestHeader(HEADER_USER_ID_KEY) String roleId,
      @RequestParam("file") MultipartFile file, HttpServletResponse response)
      throws ShapeFile2PostGISCreationException, ZipFileExtractionException {
    LOGGER.info("Saving the following zip file:");
    String filename = file.getOriginalFilename();
    LOGGER.info("Filename = {}", filename);
    File resultFile = fileToPostgisExporter.getZipFile(file);
    LOGGER.info("AbsolutePath = {}", resultFile.getAbsolutePath());
    String result = "{\"filename\": \""
        + StringEscapeUtils.escapeJavaScript(resultFile.getAbsolutePath())
        + "\", \"success\": true}";
    LOGGER.info("returning Jason = {}", result);
    return result;
  }
}
