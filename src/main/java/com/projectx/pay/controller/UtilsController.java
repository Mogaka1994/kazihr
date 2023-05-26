package com.projectx.pay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectx.pay.service.FileService;
import com.projectx.pay.utils.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
@RestController
@RequestMapping("/api/auth")
public class UtilsController {
    @Autowired
    FileService fileService;

    HashMap<String,String> req = new HashMap<>();
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws JsonProcessingException {
        String message = "";

        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                fileService.save(file,file.getOriginalFilename());
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                req.put("responseCode","00");
                req.put("responseMessage",message);
                return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(req));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                req.put("responseCode","01");
                req.put("responseMessage",e.getLocalizedMessage());
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ObjectMapper().writeValueAsString(req));
            }
        }

        message = "Please upload an excel file!";
        req.put("responseCode","01");
        req.put("responseMessage",message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((new ObjectMapper().writeValueAsString(req)));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> getFile() {
        String filename = "Employee.xlsx";
        InputStreamResource file = new InputStreamResource(fileService.load());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

}
