package com.umsa.storage.web.rest;

import com.umsa.storage.service.FileService;
import com.umsa.storage.service.dto.FileDTO;
import com.umsa.storage.web.rest.errors.BadRequestAlertException;

import org.springframework.http.MediaType;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import java.io.FileInputStream;


/**
 * REST controller for managing Video.
 */
@RestController
@RequestMapping("/api")
public class FileController {
    
    private static final String ENTITY_NAME = "file";
    
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    @PostMapping("/files/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("pdfFile") MultipartFile pdfFile,
            @RequestParam("type") String type, @RequestParam("name") String name, HttpServletRequest request) throws IOException, URISyntaxException {
        String home = System.getProperty("user.home");
        String dirLocation = "/.umsa/forms/";
        if(type.equals("STUDENT")){
            dirLocation += "students/";
        }
        if(type.equals("TEACHER")) {
            dirLocation += "teacher/";
        }
        if(type.equals("PROFESSIONAL")) {
            dirLocation += "professional/";
        }
        String dir1 = dirLocation;
        String dir =  home + dirLocation;
        String typeImage = pdfFile.getOriginalFilename().substring(
                pdfFile.getOriginalFilename().lastIndexOf(".") + 1, pdfFile.getOriginalFilename().length());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String imgName = "";
        if (typeImage.equalsIgnoreCase("pdf")) {
            imgName = sdf.format(new Date()) + timestamp.getTime() + ".pdf";
        } else {
            throw new BadRequestAlertException("El archivo tiene un formato incorrecto", "fileContorller", "invalidFormat");
        }
        File dirLoc = new File(dir);
        if (!dirLoc.exists()) {
            dirLoc.mkdirs();
        }
        File file = new File(dirLoc.getAbsolutePath() + "/" + imgName);
        pdfFile.transferTo(file);
        String server = "http://localhost:8080" + "/pdf/";

        String imageUrl = server + "file" + "/get-pdf/" + imgName + "?homeEntity=" + "user.home" + "&url="
                + dir1;
        return ResponseEntity.created(new URI("/api/file/" + "pdf-form")).body(imageUrl);
    }
}