package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.logic.MapObject.actors.*;
import com.codecool.dungeoncrawl.logic.MapObject.items.booster.HealthPotion;
import com.codecool.dungeoncrawl.logic.MapObject.items.booster.ManaPotion;
import com.codecool.dungeoncrawl.logic.MapObject.items.general.*;
import com.codecool.dungeoncrawl.logic.MapObject.items.armor.BodyArmor;
import com.codecool.dungeoncrawl.logic.MapObject.items.armor.HeadGear;
import com.codecool.dungeoncrawl.logic.MapObject.items.armor.LegArmor;
import com.codecool.dungeoncrawl.logic.MapObject.items.weapon.MagicStaff;
import com.codecool.dungeoncrawl.logic.MapObject.items.weapon.Uzi;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Scanner;

public class MapLoader implements Serializable {

    public static GameMap loadMap(String mapFile, String playerName) {
        InputStream is = MapLoader.class.getResourceAsStream(mapFile);
        Scanner scanner = new Scanner(is);
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        scanner.nextLine();

        GameMap map = new GameMap(width, height, CellType.EMPTY);
        for (int y = 0; y < height; y++) {
            String line = scanner.nextLine();
            for (int x = 0; x < width; x++) {
                if (x < line.length()) {
                    Cell cell = map.getCell(x, y);
                    getTypeAndEmojiForCell(playerName, map, line, x, cell);
                }
            }
        }
        return map;
    }

    public static void getTypeAndEmojiForCell(String playerName, GameMap map, String line, int x, Cell cell) {
        switch (line.charAt(x)) {
            case ' ': // not player area
                cell.setType(CellType.EMPTY);
                break;
            case 'f': // fire
                cell.setType(CellType.FIRE);
                break;
            case 'D': // fire
                cell.setType(CellType.DUCK);
                break;
            case 'g': // grave
                cell.setType(CellType.GRAVE);
                break;
            case 'x': // corpse
                cell.setType(CellType.CORPSE);
                break;
            case '-': // bodyguard
                cell.setType(CellType.BODYGUARD);
                break;
            case '*': // bodyguard2
                cell.setType(CellType.BODYGUARD2);
                break;
            case '║': // water
                cell.setType(CellType.WATER_VERTICAL);
                break;
            case '+': // water
                cell.setType(CellType.WATER_BASE);
                break;
            case '═': // water
                cell.setType(CellType.WATER_HORIZONTAL);
                break;
            case '╗': // water
                cell.setType(CellType.WATER_CORNER_1);
                break;
            case '╝': // water
                cell.setType(CellType.WATER_CORNER_2);
                break;
            case '╚': // water
                cell.setType(CellType.WATER_CORNER_3);
                break;
            case '╔': // water
                cell.setType(CellType.WATER_CORNER_4);
                break;
            case 't': // tree
                cell.setType(CellType.TREE_1);
                break;
            case 'T': // tree
                cell.setType(CellType.TREE_2);
                break;
            case '┬': // tree
                cell.setType(CellType.TREE_3);
                break;
            case '1': // 1
                cell.setType(CellType.DIGIT_1);
                break;
            case '2': // 2
                cell.setType(CellType.DIGIT_2);
                break;
            case '3': // 3
                cell.setType(CellType.DIGIT_3);
                break;
            case '#': // wall
                cell.setType(CellType.WALL);
                break;
            case '.': // empty cell
                cell.setType(CellType.FLOOR);
                break;
            case 'b': // enemy: bucket
                cell.setType(CellType.FLOOR);
                new Bucket(cell);
                break;
            case 'd': // enemy: drumstick
                cell.setType(CellType.FLOOR);
                new Drumstick(cell);
                break;
            case 'G':
                cell.setType(CellType.FLOOR);
                new GhostChicken(cell);
                break;
            case 'c': // enemy: colonel
                cell.setType(CellType.FLOOR);
                new Colonel(cell);
                break;
            case 'A': // enemy: Archenemy
                cell.setType(CellType.FLOOR);
                new ArchEnemy(cell);
                break;
            case 'k': // key
                cell.setType(CellType.FLOOR);
                new Key(cell);
                break;
            case 'C': // coin
                cell.setType(CellType.FLOOR);
                new Coin(cell);
                break;
            case 'h': // head gear
                cell.setType(CellType.FLOOR);
                new HeadGear(cell);
                break;
            case 'a': // body armor
                cell.setType(CellType.FLOOR);
                new BodyArmor(cell);
                break;
            case 'l': // leg armor
                cell.setType(CellType.FLOOR);
                new LegArmor(cell);
                break;
            case 'E': // next stage door
                cell.setType(CellType.FLOOR);
                new NextStageDoor(cell);
                break;
           case 'p': // prev stage door
                cell.setType(CellType.FLOOR);
                new PrevStageDoor(cell);
                break;
            case 'Æ': // dungeon entrance
                cell.setType(CellType.FLOOR);
                new DungeonEntrance(cell);
                break;
            case 'æ': // dungeon exit
                cell.setType(CellType.FLOOR);
                new DungeonExit(cell);
                break;
            case 'u':// Uzi
                cell.setType(CellType.FLOOR);
                new Uzi(cell);
                break;
            case 'z':// MagicStaff
                cell.setType(CellType.FLOOR);
                new MagicStaff(cell);
                break;
            case 'm':// ManaPotion
                cell.setType(CellType.FLOOR);
                new ManaPotion(cell);
                break;
            case 'j':// HealthPotion
                cell.setType(CellType.FLOOR);
                new HealthPotion(cell);
                break;
            case '@': // player
                cell.setType(CellType.FLOOR);
                map.setPlayer(new Player(cell, playerName));
                break;
            case 'K': // CAGE
                cell.setType(CellType.CAGE);
                break;
            default:
                throw new RuntimeException("Unrecognized character: '" + line.charAt(x) + "'");
        }
    }

}
