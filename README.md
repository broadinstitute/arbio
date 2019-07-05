# arbio
Augmented reality for biology, using Google ARCore

# Get started
You can try this app yourself!

* Go to https://storage.googleapis.com/arbio/arbio.apk from a web browser on a supported Android device (e.g. Pixel 2 phone).  This will install the "Broad AR" app.
* Print a paper copy of this cell image: https://github.com/broadinstitute/arbio/blob/master/b_lymphocyte.png.  
* Start the Broad AR app.
* When you see the "Fit the image you're scanning" message, point your phone's camera at your printed cell image.
* When the message disappears, raise your phone and see the 3D models placed around your physical environment.

# Configure new 3D models
Artists can contribute 3D models for this app.

* Contact Eric Weitz (eweitz@broadinstitute.org) for write access to https://console.cloud.google.com/storage/browser/arbio.
* Download https://storage.googleapis.com/arbio/ar-assets-config.json
* Edit `ar-assets-config.json` with the name of your GLTF 3D model, e.g. `protein.gltf`.
* Upload your edited copy of `ar-assets-config.json` to https://console.cloud.google.com/storage/browser/arbio via the "Upload files" button.  
* Upload all files for your 3D model (e.g. `protein.gltf`, `protein.bin`, `protein.png`) to https://console.cloud.google.com/storage/browser/arbio via the "Upload files" or "Upload folder" buttons.
* Restart the Broad AR app, fit the printed cell image.
* See your new 3D models.
