package com.umsa.storage.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.StreamSupport;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.sql.Timestamp;

import org.apache.commons.io.IOUtils;

import org.springframework.http.*;

/**
 * REST controller for managing Curso.
 */
@RestController
@RequestMapping("/pdf")
public class PdfController {

    private final Logger log = LoggerFactory.getLogger(PdfController.class);

    private static final String ENTITY_NAME = "image";

    public PdfController () {
    }

    @GetMapping("/{entity}/get-pdf/{filename:.+}")
    public void getImageFile(@PathVariable String entity, @PathVariable String filename, HttpServletResponse response, HttpServletRequest request,
        String homeEntity, String url) throws IOException {
        String home = System.getProperty(homeEntity);
        String dirLocation = home + url;
        System.out.println(dirLocation);
        ServletContext cntx = request.getServletContext();
        String mime = cntx.getMimeType(filename);
        if (mime == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.setContentType(mime);
        String dir = dirLocation;
        File file = new File(dir + "/" + filename);
        response.setContentLength((int)file.length());
        FileInputStream in = new FileInputStream(file);
        System.out.println(mime);
        IOUtils.copy(in, response.getOutputStream());
    }
}
