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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility methods for processing the image input stream that was downloaded
 *
 * Created by jse on 10/17/2014.
 */
public class ImageUtils {

    /**
     * Copies the given input stream that the image was downloaded to a given output stream.
     *
     * @param is the Input Stream of the downloaded image.
     * @param os the Output Stream where the image should be saved.
     *
     * @throws IllegalArgumentException if the Input Stream or the Output Stream passed in is null
     * @throws IOException if an error occurs while writing to this stream.
     */
    public static void CopyStream(InputStream is, OutputStream os) throws IllegalArgumentException, IOException {
        if (is == null) throw new IllegalArgumentException("The input stream cannot be null");
        if (os == null) throw new IllegalArgumentException("The output stream cannot be null");

        final int buffer_size = 1024;

        byte[] bytes = new byte[buffer_size];
        for (; ; ) {
            int count = is.read(bytes, 0, buffer_size);
            if (count == -1) break;

            os.write(bytes, 0, count);
        }
    }
}