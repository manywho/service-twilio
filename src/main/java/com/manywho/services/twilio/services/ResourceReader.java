package com.manywho.services.twilio.services;

import com.google.common.io.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceReader {
    public InputStream getFile(String path) throws URISyntaxException, FileNotFoundException {
        URL resourceConfig = Resources.getResource(path);
        return new FileInputStream(new File(resourceConfig.toURI()));
    }
}
