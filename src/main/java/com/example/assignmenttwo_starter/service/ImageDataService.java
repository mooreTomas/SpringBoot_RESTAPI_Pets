package com.example.assignmenttwo_starter.service;

import com.example.assignmenttwo_starter.model.ImageData;
import com.example.assignmenttwo_starter.repository.ImageDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageDataService {

    @Autowired
    private ImageDataRepository imageDataRepository;

    public ImageData save(ImageData imageData) {
        return imageDataRepository.save(imageData);
    }

}
