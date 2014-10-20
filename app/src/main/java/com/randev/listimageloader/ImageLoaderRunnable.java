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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Loads an image with the provided URL
 * <p/>
 * Created by jse on 10/17/2014.
 */
class ImageLoaderRunnable implements Runnable {
    private ImageToLoad imageToLoad;
    private ImageViewHandler imageViewHandler;
    private MemoryCache memoryCache;
    private Handler handler;
    private FileCache fileCache;
    private ImageDownloadOptions imageDownloadOptions;
    private int defaultImageResource;

    private static final String TAG = ImageLoaderRunnable.class.getSimpleName();

    ImageLoaderRunnable(ImageToLoad imageToLoad, MemoryCache memoryCache,
                        ImageViewHandler imageViewHandler, FileCache fileCache,
                        ImageDownloadOptions imageDownloadOptions,
                        int defaultImageResource) {
        this.imageToLoad = imageToLoad;
        this.memoryCache = memoryCache;
        this.imageViewHandler = imageViewHandler;
        this.fileCache = fileCache;
        this.imageDownloadOptions = imageDownloadOptions;
        this.defaultImageResource = defaultImageResource;
        handler = new Handler();


    }

    @Override
    public void run() {
        if (imageViewHandler.imageViewReused(imageToLoad)) return;

        Bitmap bitmap = getBitmap(imageToLoad.getUrl());
        memoryCache.put(imageToLoad.getUrl(), bitmap);

        if (imageViewHandler.imageViewReused(imageToLoad)) return;

        BitmapDisplayRunnable bitmapDisplayRunnable = new BitmapDisplayRunnable(bitmap, imageToLoad,
                imageViewHandler, defaultImageResource);

        handler.post(bitmapDisplayRunnable);
    }

    private Bitmap getBitmap(String url) {
        File fileFromFileCache = fileCache.getFile(url);
        Bitmap bitmap = decodeFile(fileFromFileCache);

        if (bitmap != null) return bitmap;

        try {
            URL imageUrl = new URL(url);
            HttpURLConnection imageDownloadConnection = (HttpURLConnection) imageUrl.openConnection();
            imageDownloadConnection.setConnectTimeout(imageDownloadOptions.imageDownloadConnectionTimeOut);
            imageDownloadConnection.setReadTimeout(imageDownloadOptions.imageDownloadReadTimeOut);
            imageDownloadConnection.setInstanceFollowRedirects(imageDownloadOptions.instanceFollowRedirects);
            InputStream inputStream = imageDownloadConnection.getInputStream();

            OutputStream fileOutputStream = new FileOutputStream(fileFromFileCache);
            ImageUtils.CopyStream(inputStream, fileOutputStream);
            fileOutputStream.close();
            imageDownloadConnection.disconnect();

            bitmap = decodeFile(fileFromFileCache);

            return bitmap;

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Unable to download image", e);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to download image", e);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Unable to download image", e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to download image", e);
        }
        return null;
    }

    private Bitmap decodeFile(File file) {

        try {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            FileInputStream fileInputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(fileInputStream, null, bitmapOptions);
            fileInputStream.close();

            final int REQUIRED_SIZE = 85;

            int width_tmp = bitmapOptions.outWidth, height_tmp = bitmapOptions.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;

        } catch (FileNotFoundException e) {
            Log.e(TAG, "There was an error while decoding the file", e);
        } catch (IOException e) {
            Log.e(TAG, "There was an error while decoding the file", e);
        }
        return null;
    }
}
