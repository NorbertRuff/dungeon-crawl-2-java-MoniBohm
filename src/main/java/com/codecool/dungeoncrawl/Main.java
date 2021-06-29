package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.UI.*;
import com.codecool.dungeoncrawl.dao.GameDatabaseManager;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.MapObject.items.general.Key;
import com.codecool.dungeoncrawl.util.Direction;
import com.codecool.dungeoncrawl.util.SaveState;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {
    GameDatabaseManager gameDatabaseManager = new GameDatabaseManager();
    public static final int PIXEL_OFFSET = 32;
    public static boolean isNextMap;
    public static boolean isEnteringDungeon;
    public static boolean isExitingDungeon;
    public static boolean isPreviousMap;
    public static String playerName;
    private String lastMap = "/map3.txt";
    private boolean isLasRound = false;
    private int currentMap = 0;
    BorderPane borderPane;
    List<String> nameOfFiles = setMapNames();
    List<String> nameOfDungeonFiles = setMapDungeonNames();
    Stage window;
    String mapFilename = nameOfFiles.get(currentMap);
    GameMap map = MapLoader.loadMap(mapFilename, playerName);
    SaveState saveState = new SaveState();
    Canvas canvas = new Canvas(
            map.getWidth() * Tiles.TILE_WIDTH,
            map.getHeight() * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Label healthLabel = new Label();
    Label armorLabel = new Label();
    Label damageLabel = new Label();
    Label coinLabel = new Label();

    final KeyCombination keyCombinationCTRLS = new KeyCodeCombination(
            KeyCode.S, KeyCombination.CONTROL_DOWN);

    public static void main(String[] args) {
        launch(args);
    }

    public void testDatabaseConnection() {
        try {
            gameDatabaseManager.setup();
        } catch (SQLException throwables) {
            System.err.println("Could not connect to the database.");
            return;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        testDatabaseConnection();
        playerName = GetPlayerNameAlertBox.display();
        map.getPlayer().setName(playerName);
        map.collectEnemies();
        GridPane ui = initUI(primaryStage);
        BorderPane innerBorderPane = getBorderPane();
        addTopBar(ui, innerBorderPane);
        addOverLayToMap(innerBorderPane);
        Scene scene = adCSS(innerBorderPane);
        addEventListenerToPlayerMovement(primaryStage, scene);
        setTitle(primaryStage);
    }

    private BorderPane getBorderPane() {
        borderPane = new BorderPane();
        BorderPane innerBorderPane = new BorderPane();
        innerBorderPane.setCenter(borderPane);
        return innerBorderPane;
    }

    private void addTopBar(GridPane ui, BorderPane innerBorderPane) {
        innerBorderPane.setTop(ui);
        borderPane.setCenter(canvas);
        ui.setId("topBar");
    }

    private void addOverLayToMap(BorderPane innerBorderPane) {
        Image overlayImg = new Image("file:./src/main/resources/overlay.png");
        ImageView overlay = new ImageView();
        overlay.setImage(overlayImg);
        HBox box = new HBox();
        box.getChildren().add(overlay);
        innerBorderPane.getChildren().add(box);
    }

    private Scene adCSS(BorderPane innerBorderPane) {
        Scene scene = new Scene(innerBorderPane);
        scene.getStylesheets().add("style.css");
        return scene;
    }

    private void addEventListenerToPlayerMovement(Stage primaryStage, Scene scene) {
        primaryStage.setScene(scene);
        refreshGameMap();
        scene.setOnKeyPressed(this::onKeyPressed);
        scene.setOnKeyReleased(this::onKeyReleased);
    }
    private void onKeyReleased(KeyEvent keyEvent) {
        KeyCombination exitCombinationMac = new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
        KeyCombination exitCombinationWin = new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN);
        if (exitCombinationMac.match(keyEvent)
                || exitCombinationWin.match(keyEvent)
                || keyEvent.getCode() == KeyCode.ESCAPE) {
            exit();
        }
    }


    private void setTitle(Stage primaryStage) {
        primaryStage.setTitle("Free-range Chicken");
        primaryStage.show();
    }

    private void initCloseButton() {
        window.setOnCloseRequest(e -> {
            e.consume();
            closeApp();
        });
    }

    private GridPane initUI(Stage primaryStage) {
        setWindow(primaryStage);
        GridPane ui = initInventory();
        setInventory(ui);
        initCloseButton();
        return ui;
    }

    private void setInventory(GridPane ui) {
        Label player = new Label(playerName + " | ");
        player.setId("playerName");
        ui.add(player, 1, 0);
        ui.add(new Label("Health: "), 2, 0);
        ui.add(healthLabel, 3, 0);
        ui.add(new Label(" Armor: "), 4, 0);
        ui.add(armorLabel, 5, 0);
        ui.add(new Label(" Damage: "), 6, 0);
        ui.add(damageLabel, 7, 0);
        ui.add(new Label(" Coin: "), 8, 0);
        ui.add(coinLabel, 9, 0);
        ui.add(new Label(" Inventory: press I "), 40, 0);
    }

    private GridPane initInventory() {
        GridPane ui = new GridPane();
        ui.setPrefWidth(1200);
        ui.setPrefHeight(80);
        ui.setPadding(new Insets(20));
        return ui;
    }

    private void setWindow(Stage primaryStage) {
        window = primaryStage;
        window.setMaxWidth(1200);
        window.setMaxHeight(800);
        window.setMinWidth(1200);
        window.setMinHeight(800);
    }

    private void closeApp() {
        Boolean answer = ConfirmBox.display("Exit", "Do you want to exit?");
        if (answer) {
            window.close();
        }
    }

    private void onKeyPressed(KeyEvent keyEvent) {
//        if (keyCombinationCTRLS.match(keyEvent)) {
//            gameDatabaseManager.savePlayer(map.getPlayer());
//            System.out.println("CTRL + S Pressed");
//
//        }
        switch (keyEvent.getCode()) {
            case UP:
                manageMovement(Direction.UP);
                break;
            case DOWN:
                manageMovement(Direction.DOWN);
                break;
            case LEFT:
                manageMovement(Direction.LEFT);
                break;
            case RIGHT:
                manageMovement(Direction.RIGHT);
                break;
            case I:
                getInventory();
                break;
            case C:
                getCheat();
                break;
            case S:
                gameDatabaseManager.saveGame(map, mapFilename, currentMap);
                break;
        }

    }

    private void getCheat() {
        CheatMenu.display(map.getPlayer());
        refreshGameMap();
    }

    private void getInventory() {
        InventoryBox ibox = new InventoryBox();
        ibox.display(map.getPlayer().getInventory(), map.getPlayer().getCell());
        refreshGameMap();
    }


    private void manageMovement(Direction direction){
        boolean successfulMove;
        successfulMove = map.getPlayer().initMove(direction.dx, direction.dy);
        if (successfulMove) {
            setFollowCamera(direction);
        }
        map.moveEnemies();
        ChangeMapIfTrue();
        refreshGameMap();
    }

    private void setFollowCamera(Direction direction) {
        switch (direction){
            case UP ->  borderPane.setTranslateY(borderPane.getTranslateY() + PIXEL_OFFSET);
            case DOWN ->  borderPane.setTranslateY(borderPane.getTranslateY() - PIXEL_OFFSET);
            case LEFT ->  borderPane.setTranslateX(borderPane.getTranslateX() + PIXEL_OFFSET);
            case RIGHT ->  borderPane.setTranslateX(borderPane.getTranslateX() - PIXEL_OFFSET);
        }

    }

    private void ChangeMapIfTrue() {
        if (isNewMap()) {
            initNewMap(false);
        }
        if (isDungeonMovement()) {
            initNewMap(true);
        }
    }

    private boolean isNewMap() {
        return isNextMap || isPreviousMap;
    }

    private boolean isDungeonMovement() {
        return isEnteringDungeon || isExitingDungeon;
    }

    public void initNewMap(Boolean isDungeon) {
        Key.count = 0;
        borderPane.setTranslateX(0);
        borderPane.setTranslateY(0);
        saveState.setPlayer(map.getPlayer());
        if (isDungeon) generateDungeonFileName();
        else generateMapFileName();
        generateMap();
        Cell newCell = map.getPlayer().getCell();
        map.setPlayer(saveState.getPlayer());
        map.getPlayer().setCell(newCell);
    }

    public void generateMapFileName() {
        if (isNextMap) {
            if (isLasRound) {
                winGame();
            }
            if (currentMap < nameOfFiles.size() - 1) {
                moveToNextMap();
            }
        }
        if (isPreviousMap) {
            if (currentMap > 0) {
                moveToPreviousMap();
            }
        }
    }

    private void moveToNextMap() {
        currentMap++;
        mapFilename = nameOfFiles.get(currentMap);
        if (isLastMap()) {
            isLasRound = true;
        }
        isNextMap = false;
    }

    private boolean isLastMap() {
        return mapFilename.equals(lastMap);
    }

    private void moveToPreviousMap() {
        currentMap--;
        mapFilename = nameOfFiles.get(currentMap);
        isPreviousMap = false;
        isLasRound = false;
    }

    public void generateDungeonFileName() {
        if (isEnteringDungeon) {
            enterDungeon();
        }
        if (isExitingDungeon) {
            exitDungeon();
        }
    }

    private void enterDungeon() {
        mapFilename = nameOfDungeonFiles.get(currentMap);
        isEnteringDungeon = false;
    }

    private void exitDungeon() {
        mapFilename = nameOfFiles.get(currentMap);
        isExitingDungeon = false;
    }

    private List<String> setMapNames() {
        List<String> nameOfFiles = new ArrayList();
        nameOfFiles.add("/map1.txt");
        nameOfFiles.add("/map2.txt");
        nameOfFiles.add("/map3.txt");
        return nameOfFiles;
    }

    private List<String> setMapDungeonNames() {
        List<String> nameOfFiles = new ArrayList();
        nameOfFiles.add("/map1dungeon.txt");
        nameOfFiles.add("/map2dungeon.txt");
        nameOfFiles.add("/map3dungeon.txt");
//        nameOfFiles.add("/map4dungeon.txt");
        return nameOfFiles;
    }

    public void generateMap() {
        map = MapLoader.loadMap(mapFilename, playerName);
        map.collectEnemies();
        canvas = new Canvas(
                map.getWidth() * Tiles.TILE_WIDTH,
                map.getHeight() * Tiles.TILE_WIDTH);
        refreshGameMap();
    }

    private void refreshGameMap() {
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int x = 0; x < map.getWidth(); x++) {
                fillRow(x);
        }
        setInventoryLabels();
    }

    private void fillRow(int x) {
        for (int y = 0; y < map.getHeight(); y++) {
            Cell cell = map.getCell(x, y);
            getCellIcon(x, y, cell);
        }
    }

    private void setInventoryLabels() {
        healthLabel.setText("" + map.getPlayer().getHealth() + " Hp ");
        damageLabel.setText("" + map.getPlayer().getDamage() + " Dp ");
        armorLabel.setText("" + map.getPlayer().getArmor() + " ");
        coinLabel.setText("" + map.getPlayer().getCoin() + " ");
    }

    private void getCellIcon(int x, int y, Cell cell) {
        if (isActor(cell)) {
            Tiles.drawTile(context, cell.getActor(), x, y);
        } else if (isItem(cell)) {
            Tiles.drawTile(context, cell.getItem(), x, y);
        } else {
            Tiles.drawTile(context, cell, x, y);
        }
    }

    private boolean isItem(Cell cell) {
        return cell.getItem() != null;
    }

    private boolean isActor(Cell cell) {
        return cell.getActor() != null;
    }


    private void winGame() {
        WinGameBox.display();
        System.exit(0);
    }

    private void exit() {
        try {
            stop();
        } catch (Exception e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
