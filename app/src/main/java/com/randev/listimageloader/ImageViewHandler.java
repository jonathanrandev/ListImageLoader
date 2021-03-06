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

import android.widget.ImageView;

import java.util.Map;

/**
 * Created by jse on 10/17/2014.
 */
public class ImageViewHandler {

    private Map<ImageView, String> imageViews;

    public ImageViewHandler(Map<ImageView, String> imageViews) {
        this.imageViews = imageViews;
    }

    public boolean imageViewReused(ImageToLoad imageToLoad) {
        String tag = imageViews.get(imageToLoad.getImageView());
        return tag == null || !tag.equals(imageToLoad.getUrl());
    }
}
