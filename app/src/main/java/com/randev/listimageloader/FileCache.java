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

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Handles all transactions with the file cache. The File cache stores the temporarily downloaded
 * items in the list
 * <p/>
 * Created by jse on 10/17/2014.
 */
public class FileCache {

    private File cacheDirectory;

    private static final String DEFAULT_FOLDER = "LazyLoad";
    private static final String TAG = FileCache.class.getSimpleName();

    /**
     * Create the file cache in the default location and default folder name
     *
     * @param context the application context
     * @throws IllegalArgumentException if the passed in arguments are null
     */
    public FileCache(Context context) throws IllegalArgumentException {

        if (context == null)
            throw new IllegalArgumentException("The context cannot be null");

        initializeCacheDirectory(context, null, null);
    }

    /**
     * Set custom location for the file cache. Will use the default folder name for this.
     *
     * @param context      the context of the application
     * @param fileLocation the location for the file cache
     * @throws java.lang.IllegalArgumentException if the arguments passed is null
     * @throws IOException                        if the file cache directory does not exist.
     */
    public FileCache(Context context, String fileLocation) throws IllegalArgumentException, IOException {

        if (context == null)
            throw new IllegalArgumentException("The context cannot be null");
        if (fileLocation == null)
            throw new IllegalArgumentException("The file location cannot be null");

        if (!new File(fileLocation).exists())
            throw new IOException("The location for the cache does not exist");

        initializeCacheDirectory(context, fileLocation, null);
    }

    /**
     * Set custom location for the file cache
     *
     * @param context      the context of the application
     * @param fileLocation the location for the file cache
     * @param folderName   the name of the folder inside the directory
     * @throws java.lang.IllegalArgumentException if the either of the parameters passed in is null
     * @throws IOException                        if the file cache directory does not exist.
     */
    public FileCache(Context context, String fileLocation, String folderName) throws IllegalArgumentException, IOException {

        if (context == null)
            throw new IllegalArgumentException("The context cannot be null");
        if (fileLocation == null)
            throw new IllegalArgumentException("The file location cannot be null");
        if (folderName == null)
            throw new IllegalArgumentException("The folder name cannot be null");

        if (!new File(fileLocation).exists())
            throw new IOException("The location for the cache does not exist");

        initializeCacheDirectory(context, fileLocation, folderName);
    }

    private void initializeCacheDirectory(Context context, String folderLocation, String folderName) {

        if (folderLocation == null) {
            Log.i(TAG, "Using the default folder location");
            folderLocation = context.getExternalFilesDir(null).getAbsolutePath();
        }
        if (folderName == null) {
            Log.i(TAG, "Using the default folder name");
            folderName = DEFAULT_FOLDER;
        }

        if (isExternalStorageAvailable()) {
            cacheDirectory = new File(folderLocation, folderName);
        } else {
            cacheDirectory = context.getCacheDir();
        }

        if (!cacheDirectory.exists()) {
            boolean cacheDirectoryCreationSuccessful = cacheDirectory.mkdirs();
            if (!cacheDirectoryCreationSuccessful) {
                Log.e(TAG, "Unable to create cache directory");
            }
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * The images are identified using the hash code of the URL.
     *
     * @param url the url that the image will be downloaded from
     * @return {@link java.io.File} located in the cache directory.
     */
    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        return new File(cacheDirectory, filename);
    }

    /**
     * Clear the file cache
     */
    public void clear() {
        File[] files = cacheDirectory.listFiles();
        if (files == null) return;

        for (File file : files) {
            boolean deleteSuccessful = file.delete();
            if (!deleteSuccessful) {
                Log.e(TAG, "Could not delete cache directory file " + file.getName());
            }
        }
    }
}
