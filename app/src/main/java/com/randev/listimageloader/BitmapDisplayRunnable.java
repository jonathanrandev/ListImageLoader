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

/**
 * Handles displaying the image
 * Created by jse on 10/17/2014.
 */
class BitmapDisplayRunnable implements Runnable {
    private Bitmap bitmap;
    private ImageToLoad imageToLoad;
    private ImageViewHandler imageViewHandler;
    private int defaultImageResource;

    public BitmapDisplayRunnable(Bitmap bitmap, ImageToLoad imageToLoad, ImageViewHandler imageViewHandler, int defaultImageResource) {
        this.bitmap = bitmap;
        this.imageToLoad = imageToLoad;
        this.imageViewHandler = imageViewHandler;
        this.defaultImageResource = defaultImageResource;
    }

    @Override
    public void run() {
        if (imageViewHandler.imageViewReused(imageToLoad)) return;

        if (bitmap != null) {
            imageToLoad.getImageView().setImageBitmap(bitmap);
        } else {
            imageToLoad.getImageView().setImageResource(defaultImageResource);
        }
    }
}
