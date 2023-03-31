package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.service.AwsStorageService;
import com.example.assignmenttwo_starter.service.StorageService;


import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/file")
@RestController
public class StorageController {


    @Autowired
    private AwsStorageService awsStorageService;

    @PostMapping("/upload")
    @ApiOperation(value = "Upload file to AWS S3", notes = "Uploads a file to the specified AWS S3 bucket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File uploaded successfully")
    })
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(awsStorageService.uploadFile(file, "test"), HttpStatus.OK);
    }

    @GetMapping("/download/{fileName}")
    @ApiOperation(value = "Download file from AWS S3", notes = "Downloads a file from the specified AWS S3 bucket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = awsStorageService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    @ApiOperation(value = "Delete file from AWS S3", notes = "Deletes a file from the specified AWS S3 bucket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(awsStorageService.deleteFile(fileName), HttpStatus.OK);
    }
}
