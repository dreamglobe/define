package com.kamomileware.define.term.web;

import com.kamomileware.define.term.service.DBService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by pepe on 21/08/14.
 */
@Controller
@RequestMapping("/db")
public class ImportExportBDController {

    private static final Logger logger = LoggerFactory.getLogger(ImportExportBDController.class);

    @Autowired
    DBService dbService;

    @RequestMapping(value = "/init", method = RequestMethod.POST)
    public ResponseEntity<String> createBD(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty() ) { //&& "application/json".equals(file.getContentType()
            try {
                byte[] bytes = file.getBytes();
                String serializedDB = new String(bytes);
                dbService.clear();
                dbService.importBD(serializedDB);
                return new ResponseEntity<>(Long.toString(file.getSize()), null, HttpStatus.CREATED);
            } catch (Exception e) {
                logger.error(String.format("Error importing BD from uploaded %s.", file.getName()), e);
                return new ResponseEntity<>(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Bad formatted content", null, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportDB(HttpServletResponse response){
        this.exportDB("term_db", response);
    }

    @RequestMapping(value = "/export/{fileName}", method = RequestMethod.GET)
    public void exportDB(@PathVariable("fileName") String fileName,
                         HttpServletResponse response) {
        String finalFileName = fileName + ".json";
        try {
            final byte[] bytes = dbService.export().getBytes(StandardCharsets.UTF_8);
            writeExportOnResponse(finalFileName, response, bytes);
        } catch (IOException ex) {
            logger.error(String.format("Error writing file to output stream to %s", finalFileName));
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void writeExportOnResponse(String fileName, HttpServletResponse response, byte[] bytes) throws IOException {
        IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        response.setContentType("application/json");
        response.setContentLength(bytes.length);
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
        response.flushBuffer();
    }

}
