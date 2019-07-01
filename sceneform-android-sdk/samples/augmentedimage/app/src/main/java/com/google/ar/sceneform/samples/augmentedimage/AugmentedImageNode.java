/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.augmentedimage;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

  private static final String TAG = "AugmentedImageNode";

  // The augmented image represented by this node.
  private AugmentedImage image;

  private final Context nodeContext;

  // eweitz: Renderable resources for Broad Institute AR lobby app
  private static CompletableFuture<ModelRenderable> whiteBloodCell;
  private static CompletableFuture<ModelRenderable> protein;
  private static CompletableFuture<ModelRenderable> brain;
  private static CompletableFuture<ModelRenderable> cesiumMan;
  private static CompletableFuture<ModelRenderable> maccawAnimation;
  private static CompletableFuture<ViewRenderable> ratGenome;

  private static final String GLTF_ASSET =
          "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf";

  private static Map<String, Object> assets;

  private static String readUrl(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(urlString);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer buffer = new StringBuffer();
      int read;
      char[] chars = new char[1024];
      while ((read = reader.read(chars)) != -1)
        buffer.append(chars, 0, read);

      return buffer.toString();
    } finally {
      if (reader != null)
        reader.close();
    }
  }

  private static void setAssets() {
    String content = "";
    try {
      content = readUrl("https://storage.googleapis.com/arbio/ar-assets-config.json");
      Log.i(TAG, "Fetched AR assets configuration");
    } catch (Exception e) {
      Log.e(TAG, "Cannot fetch AR assets configuration", e);
    }

    Type type = new TypeToken<Map<String, Object>>(){}.getType();
    Map<String, Object> jsonContent = new Gson().fromJson(content, type);
    Log.i(TAG, "Parsed AR assets JSON:");
    Log.i(TAG, jsonContent.toString());

    Log.i(TAG, "Assets in JSON");
    Log.i(TAG, jsonContent.get("assets").toString());

    assets = (Map<String, Object>) jsonContent.get("assets");

  }

  public AugmentedImageNode(Context context) {
    this.nodeContext = context;


    // TODO: Refactor to not fetch in main thread.
    if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }

    setAssets();

    for(Map.Entry<String, Object> entry : assets.entrySet()){

      Log.i(TAG, "entry:");
      Log.i(TAG, entry.toString());

      Map<String, Object> asset = (Map<String, Object>) entry.getValue();

      Log.i(TAG, "asset:");
      Log.i(TAG, asset.toString());

      Uri uri = Uri.parse(asset.get("url").toString());
      ArrayList position = (ArrayList) asset.get("position");

      CompletableFuture<ModelRenderable> model = ModelRenderable.builder()
              .setSource(context, RenderableSource.builder().setSource(
                      context,
                      uri,
                      RenderableSource.SourceType.GLTF2).build())
              .setRegistryId(uri)
              .build();

      asset.put("url", uri);
      asset.put("position", position);
      asset.put("model", model);

      assets.put(entry.getKey(), asset);
    }

    if (whiteBloodCell == null) {
      whiteBloodCell =
              ModelRenderable.builder()
//                      .setSource(context, Uri.parse("1408 White Blood Cell.sfb"))
                      .setSource(context, RenderableSource.builder().setSource(
                              context,
//                  Uri.parse("https://storage.googleapis.com/arbio/1408%20White%20Blood%20Cell.fullpath.gltf"),
                              Uri.parse("https://storage.googleapis.com/arbio/1408%20White%20Blood%20Cell.gltf"),
                              RenderableSource.SourceType.GLTF2).build())
//                .setRegistryId(Uri.parse("https://storage.googleapis.com/arbio/1408%20White%20Blood%20Cell.fullpath.gltf"))
                      .setRegistryId(Uri.parse("https://storage.googleapis.com/arbio/1408%20White%20Blood%20Cell.gltf"))
                      .build();
    }

    if (protein == null) {
      protein =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("1408 White Blood Cell.sfb"))
                      .build();
    }

    if (brain == null) {
      brain =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("brain_areas/scene.sfb"))
                      .build();
    }

    if (cesiumMan == null) {
      cesiumMan =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("CesiumMan.sfb"))
                      .build();
    }

    if (maccawAnimation == null) {
      maccawAnimation =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse("5ebaec95694b4b9faecacecf06d7b5f4.fbx.sfb"))
                      .build();
    }

    if (ratGenome == null) {
      ratGenome =
              ViewRenderable.builder()
                      .setView(nodeContext, R.layout.rat_genome)
                      .build();
    }
  }

  /**
   * Called upon detecting a flat image atop the short stand at west in 415 Main lobby
   */
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setBroadLobbyImages(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!whiteBloodCell.isDone()) {
      CompletableFuture.allOf(whiteBloodCell)
              .thenAccept((Void aVoid) -> setBroadLobbyImages(image))
              .exceptionally(
                      throwable -> {
                        Log.e(TAG, "Exception loading", throwable);
                        return null;
                      });
    }

    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));

    Vector3 localPosition = new Vector3();
    Node node;
    
    Map<String, Object> asset = (Map<String, Object>) assets.get("b_lymphocyte");
    ArrayList position = (ArrayList) asset.get("position");
    Float x = ((Double) position.get(0)).floatValue();
    Float y = ((Double) position.get(1)).floatValue();
    Float z = ((Double) position.get(2)).floatValue();

    CompletableFuture<ModelRenderable> model = (CompletableFuture<ModelRenderable>) asset.get("model");

    // Top of mezzanine stairs
    localPosition.set(x, y, z); // Tuned for west stand
    node = new Node();
    node.setParent(this);
    node.setLocalPosition(localPosition);
    node.setRenderable(model.getNow(null));

    // "Stories retold" inset
    localPosition.set(-11f, 0f, 6.5f); // Tuned for west stand
    node = new Node();
    node.setParent(this);
    node.setLocalPosition(localPosition);
    node.setRenderable(brain.getNow(null));

    // Hybridization oven
    localPosition.set(-11.5f, 0f, -5f); // Tuned for west stand
    node = new Node();
    node.setParent(this);
    node.setLocalPosition(localPosition);
    node.setRenderable(maccawAnimation.getNow(null));

  }

  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setProteinImage(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!protein.isDone()) {
      CompletableFuture.allOf(whiteBloodCell)
              .thenAccept((Void aVoid) -> setProteinImage(image))
              .exceptionally(
                      throwable -> {
                        Log.e(TAG, "Exception loading", throwable);
                        return null;
                      });
    }
    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));

    // Make the 4 corner nodes.
    Vector3 localPosition = new Vector3();
    Node node;

    // Upper left corner.
    localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    node = new Node();
    node.setParent(this);
    node.setLocalPosition(localPosition);
    node.setRenderable(protein.getNow(null));
  }


  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setBrainImage(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!brain.isDone()) {
      CompletableFuture.allOf(brain)
              .thenAccept((Void aVoid) -> setBrainImage(image))
              .exceptionally(
                      throwable -> {
                        Log.e(TAG, "Exception loading", throwable);
                        return null;
                      });
    }

    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));
    Vector3 localPosition = new Vector3();
    Node node;

    localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    node = new Node();
    node.setParent(this);
    node.setLocalPosition(localPosition);
    node.setRenderable(brain.getNow(null));
  }


  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setCesiumManImage(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!cesiumMan.isDone()) {
      CompletableFuture.allOf(cesiumMan)
              .thenAccept((Void aVoid) -> setCesiumManImage(image))
              .exceptionally(
                      throwable -> {
                        Log.e(TAG, "Exception loading", throwable);
                        return null;
                      });
    }

    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));

    // Make the 4 corner nodes.
    Vector3 localPosition = new Vector3();
    Node node;

    // Upper left corner.
    localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    node = new Node();
    node.setParent(this);
    node.setLocalPosition(localPosition);
    node.setRenderable(cesiumMan.getNow(null));
  }

  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setMaccawAnimationImage(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!maccawAnimation.isDone()) {
      CompletableFuture.allOf(maccawAnimation)
              .thenAccept((Void aVoid) -> setMaccawAnimationImage(image))
              .exceptionally(
                      throwable -> {
                        Log.e(TAG, "Exception loading", throwable);
                        return null;
                      });
    }

    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));

    // Make the 4 corner nodes.
    Vector3 localPosition = new Vector3();
    Node node;

    // Upper left corner.
    localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    node = new Node();
    node.setParent(this);
    node.setLocalPosition(localPosition);
    node.setRenderable(maccawAnimation.getNow(null));
  }

  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setRatGenomeImage(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!ratGenome.isDone()) {
      CompletableFuture.allOf(ratGenome)
              .thenAccept((Void aVoid) -> setRatGenomeImage(image))
              .exceptionally(
                      throwable -> {
                        Log.e(TAG, "Exception loading", throwable);
                        return null;
                      });
    }

    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));

    // Make the 4 corner nodes.
    Vector3 localPosition = new Vector3();
    Node node;

    // Upper left corner.
    localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    node = new Node();
    node.setParent(this);
    node.setLocalPosition(localPosition);
    node.setRenderable(ratGenome.getNow(null));
  }


  public AugmentedImage getImage() {
    return image;
  }
}
