/*
 * Copyright 2014 Jonathan Senaratne (jonathansenaratne@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.randev.listimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by jse on 10/17/2014.
 */
public class ImageLoader {


    private MemoryCache memoryCache;
    private FileCache fileCache;
    private Map<ImageView, String> imageViews;
    private ExecutorService executorService;
    private String imageCacheLocation;
    private ImageDownloadOptions imageDownloadOptions;
    private int defaultImageResource;

    /**
     * Create the image loader with default cache location and default cache folder.
     *
     * @param context the application context
     * @param imageDownloadOptions Options for downloading image
     * @param defaultImageResource defaultImageResource
     * @throws IllegalArgumentException if the passed in arguments are invalid
     */
    public ImageLoader(Context context, ImageDownloadOptions imageDownloadOptions, int defaultImageResource) throws IllegalArgumentException {
        fileCache = new FileCache(context);
        this.imageDownloadOptions = imageDownloadOptions;
        this.defaultImageResource = defaultImageResource;
        init();
    }

    /**
     * Set your own image cache location. The default folder name will be used for image cache location
     *
     * @param context            the application context
     * @param imageCacheLocation image cache location
     * @param imageDownloadOptions Options for downloading image
     * @param defaultImageResource defaultImageResource
     * @throws IllegalArgumentException if the passed in arguments are invalid
     * @throws IOException              if the imageCacheLocation is invalid
     */
    public ImageLoader(Context context, String imageCacheLocation,
                       ImageDownloadOptions imageDownloadOptions, int defaultImageResource) throws IllegalArgumentException, IOException {
        fileCache = new FileCache(context, imageCacheLocation);
        this.imageDownloadOptions = imageDownloadOptions;
        this.defaultImageResource = defaultImageResource;
        init();
    }

    /**
     * Set your own image cache location and image cache folder name
     *
     * @param context            the application context
     * @param imageCacheLocation image cache location
     * @param cacheFolderName    the name of the folder for having the cache
     * @param imageDownloadOptions Options for downloading image
     * @param defaultImageResource defaultImageResource
     * @throws IllegalArgumentException if the passed in arguments are invalid
     * @throws IOException              if the imageCacheLocation is invalid
     */
    public ImageLoader(Context context, String imageCacheLocation, String cacheFolderName,
                       ImageDownloadOptions imageDownloadOptions, int defaultImageResource) throws IOException {
        fileCache = new FileCache(context, imageCacheLocation, cacheFolderName);
        this.imageDownloadOptions = imageDownloadOptions;
        this.defaultImageResource = defaultImageResource;
        init();
    }

    private void init() {
        memoryCache = new MemoryCache();
        executorService = Executors.newFixedThreadPool(5);
        imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    }


    public void displayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, imageView);
            imageView.setImageResource(R.drawable.stub);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        ImageToLoad imageToLoad = new ImageToLoad(url, imageView);
        executorService.submit(new ImageLoaderRunnable(imageToLoad, memoryCache,
                new ImageViewHandler(imageViews), fileCache,imageDownloadOptions, defaultImageResource));
    }

    /**
     *
     */
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}