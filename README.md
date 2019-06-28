# arbio

# Adding and placing 3D models
1. Set up your development environment following  https://developers.google.com/ar/develop/java/quickstart.
2. Print a paper copy of https://github.com/broadinstitute/arbio/blob/master/b_lymphocyte.png
3. Open this project in your IDE, as set up in Step 1.
4. Add your 3D model file (e.g. .gltf, .obj/.mtl) to `sceneform-android-sdk/samples/augmentedimage/app/sampledata/models`.
5. In IDE, right-click on model file and click "Import Sceneform Asset".
6. In IDE, edit `setBroadLobbyImages` in com/google/ar/sceneform/samples/augmentedimage/AugmentedImageNode.java to add your models of interest using coordinates of existing models as reference.
7. Connect to your Android device (e.g. Pixel phone) via USB, click "Run" in IDE, "Run...", "Edit Configuration...", configure Run settings to use your device, then click "Run".
