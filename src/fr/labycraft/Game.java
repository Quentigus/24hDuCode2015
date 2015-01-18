/**
 * Copyright (c) 2008-2010 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it under the
 * terms of its license which may be found in the accompanying LICENSE file or
 * at <http://www.ardor3d.com/LICENSE>.
 */
package fr.labycraft;

import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.GrabbedState;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyHeldCondition;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.NormalsMode;
import com.ardor3d.scenegraph.shape.Pyramid;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.SimpleResourceLocator;
import com.ardorcraft.base.ArdorCraftGame;
import com.ardorcraft.base.CanvasRelayer;
import com.ardorcraft.collision.IntersectionResult;
import com.ardorcraft.data.Pos;
import com.ardorcraft.generators.DataGenerator;
import com.ardorcraft.objects.QuadBox;
import com.ardorcraft.objects.SkyDome;
import com.ardorcraft.player.PlayerWithPhysics;
import com.ardorcraft.util.BlockUtil;
import com.ardorcraft.util.geometryproducers.MeshProducer;
import com.ardorcraft.world.BlockType;
import com.ardorcraft.world.BlockWorld;
import com.ardorcraft.world.IServerConnection;
import com.ardorcraft.world.WorldSettings;
import fr.labycraft.Modele.Monstre;
import fr.labycraft.generators.LabyrintheGenerator;
import fr.labycraft.network.GameLocalServerConnection;
import fr.labycraft.network.GameServerDataHandler;
import java.util.Date;

/**
 * A bigger example that will grow over time...
 */
public class Game implements ArdorCraftGame {

    private final long cycle = 2000;//10000;
    private float globalLight = 1f;
    private final float minLight = .2f;
    private final float maxLight = 1f;
    private final float nightLimit = .4f;
    private final boolean showHD = false;

    private final ColorRGBA fogColor = new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f);
    private final ColorRGBA topColor = new ColorRGBA(0.5f, 0.6f, 1.0f, 1.0f);

    private final LabyrintheGenerator generator = new LabyrintheGenerator();

    private final int tileSize = 16;
    private final int gridSize = 20;
    private final int height = 150;

    private long previousState = 0;
    private boolean fadingSky = true;
    private boolean currentlyNight = false;

    private BlockWorld blockWorld;
    private double farPlane = 10000.0;

    private CanvasRelayer canvas;
    private Node root;
    private Camera camera;
    private PlayerWithPhysics player;

    private Node worldNode;
    private SkyDome skyDome;
    private QuadBox selectionBox;

    private final IntersectionResult intersectionResult = new IntersectionResult();

    @Override
    public void update(final ReadOnlyTimer timer) {
        player.update(blockWorld, timer);

        blockWorld.tracePicking(player.getPosition(), player.getDirection(), 50, intersectionResult);
        if (intersectionResult.hit) {
            final Pos hitPos = intersectionResult.pos;
            selectionBox.setTranslation(hitPos.x + 0.5, hitPos.y + 0.5, hitPos.z + 0.5);
        }

        camera.setLocation(player.getPosition());
        camera.setDirection(player.getDirection());
        camera.setUp(player.getUp());
        camera.setLeft(player.getLeft());

        skyDome.setTranslation(player.getPosition());

        // The infinite world update
        blockWorld.updatePlayer(player.getPosition(), player.getDirection());
        blockWorld.update(timer);

        long currentState = new Date().getTime();

        if (this.previousState + this.cycle / 80 < currentState) {

            if (this.fadingSky) {
                globalLight = (float) Math.max(globalLight - 0.01 * 0.4, minLight);
                blockWorld.setGlobalLight(globalLight);
            } else {
                globalLight = (float) Math.min(globalLight + 0.01 * 0.4, maxLight);
                blockWorld.setGlobalLight(globalLight);
            }

            if (globalLight <= minLight || globalLight >= maxLight) {
                this.fadingSky = !this.fadingSky;
            }

            if (!this.currentlyNight && globalLight <= this.nightLimit) {
                this.currentlyNight = true;
                this.generator.generatePorte(blockWorld);
                Monstre m = new Monstre(1, 1, 1, blockWorld);
                
            } else if (this.currentlyNight && globalLight > this.nightLimit) {
                this.currentlyNight = false;
               
                this.generator.generateLabyrinthe();
                this.generator.generateLabyrinthe(blockWorld);
            }

            this.previousState = currentState;
            updateLighting();
        }
    }

    @Override
    public void render(final Renderer renderer) {
        // root.draw(renderer);

        // Taking over the drawing to draw in specific order without performance
        // hogging renderqueue sorting...
        skyDome.draw(renderer);
        worldNode.draw(renderer);
        if (intersectionResult.hit) {
            selectionBox.draw(renderer);
        }
    }

    @Override
    public void init(final Node root, final CanvasRelayer canvas, final LogicalLayer logicalLayer, final PhysicalLayer physicalLayer, final MouseManager mouseManager) {
        this.root = root;
        this.canvas = canvas;

        try {
            final SimpleResourceLocator srl = new SimpleResourceLocator(ResourceLocatorTool.getClassPathResource(
                    Game.class, "com/ardorcraft/resources"));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, srl);
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_MODEL, srl);
        } catch (final URISyntaxException ex) {
            ex.printStackTrace();
        }

        canvas.setTitle("Les Titouz 3.0");

        farPlane = (gridSize - 1) / 2 * tileSize;

        camera = canvas.getCanvasRenderer().getCamera();
        camera.setFrustumPerspective(75.0, (float) camera.getWidth() / (float) camera.getHeight(), 0.1, farPlane);

        // Create player object
        player = new PlayerWithPhysics(logicalLayer);
        player.getPosition().set(25, 70, 26);
        player.setWalking(true);

        registerTriggers(logicalLayer, mouseManager);

        // Create main blockworld handler
        createBlockWorld();

        // Set block 45 (brickblock) to be a pyramid drawn with the meshproducer
        final BlockUtil blockUtil = blockWorld.getBlockUtil();
        final int blockId = 45;
        blockUtil.setBlockMapping(blockId, 7, 0); // brick block tile coords
        blockUtil.setBlockType(blockId, BlockType.Transparent); // Not covering the entire box block = not solid
        final Mesh mesh = new Pyramid("pyramid", 1.0, 1.0);
        final MeshProducer meshProducer = new MeshProducer(mesh);
        meshProducer.createOrientations(); // create all permutation rotations of the mesh
        meshProducer.setTransformTextureCoords(true); // transform 0-1 texcoords to the specific tile
        blockUtil.setGeometryProducer(blockId, meshProducer);

        worldNode = blockWorld.getWorldNode();
        root.attachChild(worldNode);

        skyDome = new SkyDome("Dome", 8, 8, 10);
        root.attachChild(skyDome);

        // Create box to show selected box
        selectionBox = new QuadBox("SelectionBox", new Vector3(), 0.501, 0.501, 0.501);
        selectionBox.getSceneHints().setNormalsMode(NormalsMode.Off);
        selectionBox.setDefaultColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 0.4f));
        selectionBox.getSceneHints().setRenderBucketType(RenderBucketType.Skip);
        final BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        selectionBox.setRenderState(bs);
        final WireframeState ws = new WireframeState();
        ws.setLineWidth(2);
        selectionBox.setRenderState(ws);
        selectionBox.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        root.attachChild(selectionBox);

        updateLighting();

        blockWorld.startThreads();
    }

    private void updateLighting() {
        final float light = globalLight * 0.9f + 0.1f;
        final ReadOnlyColorRGBA newColor = new ColorRGBA(fogColor).multiplyLocal(light);
        //fogState.setColor(newColor);
        skyDome.getMidColor().set(newColor);
        skyDome.getTopColor().set(topColor).multiplyLocal(light);
        skyDome.updateColors();

        GameTaskQueueManager.getManager(ContextManager.getCurrentContext()).getQueue(GameTaskQueue.RENDER)
                .enqueue(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        canvas.getCanvasRenderer().getRenderer().setBackgroundColor(newColor);
                        return true;
                    }
                });
    }

    private void registerTriggers(final LogicalLayer logicalLayer, final MouseManager mouseManager) {
        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.F), new TriggerAction() {
            @Override
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                player.setWalking(!player.isWalking());
            }
        }));

        logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.SPACE), new TriggerAction() {
            @Override
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                player.jump();
            }
        }));

        if (mouseManager.isSetGrabbedSupported()) {
            mouseManager.setGrabbed(GrabbedState.GRABBED);
        }
    }

    @Override
    public void destroy() {
        blockWorld.stopThreads();
    }

    @Override
    public void resize(final int newWidth, final int newHeight) {
    }

    private void createBlockWorld() {
        final WorldSettings settings = new WorldSettings();
        settings.setWaterTexture(ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "water.png"));
        settings.setTileSize(tileSize);
        settings.setTileHeight(height);
        settings.setGridSize(gridSize);

        if (this.showHD) {
            settings.setTerrainTextureTileSize(64);
            settings.setTerrainTexture(ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "terrainHD64.png"));
        } else {
            settings.setTerrainTextureTileSize(16);
            settings.setTerrainTexture(ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "terrainQ.png"));
        }

        final IServerConnection serverConnection = new GameLocalServerConnection(new GameServerDataHandler(tileSize, height, gridSize, (DataGenerator)generator, null));
        settings.setServerConnection(serverConnection);

        blockWorld = new BlockWorld(settings);
    }
}
