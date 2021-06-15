package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.logic.MapObject.actors.Player;
import com.codecool.dungeoncrawl.logic.MapObject.actors.Skeleton;
import com.codecool.dungeoncrawl.logic.MapObject.items.Item;
import com.codecool.dungeoncrawl.logic.MapObject.items.Key;

import java.io.InputStream;
import java.util.Scanner;

public class MapLoader {
    public static GameMap loadMap(String mapFile) {
        InputStream is = MapLoader.class.getResourceAsStream(mapFile);
        Scanner scanner = new Scanner(is);
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        scanner.nextLine(); // empty line

        GameMap map = new GameMap(width, height, CellType.EMPTY);
        for (int y = 0; y < height; y++) {
            String line = scanner.nextLine();
            for (int x = 0; x < width; x++) {
                if (x < line.length()) {
                    Cell cell = map.getCell(x, y);
                    switch (line.charAt(x)) {
                        case ' ': // not player area
                            cell.setType(CellType.EMPTY);
                            break;
                        case '#': // wall
                            cell.setType(CellType.WALL);
                            break;
                        case '.': // empty cell
                            cell.setType(CellType.FLOOR);
                            break;
                        case 's': // skeleton
                            cell.setType(CellType.FLOOR);
                            new Skeleton(cell);
                            break;
                        case '@': // player
                            cell.setType(CellType.FLOOR);
                            map.setPlayer(new Player(cell));
                            break;
                        case 'i': // item
                            cell.setType(CellType.FLOOR);
                            new Key(cell);
                            break;
                        default:
                            throw new RuntimeException("Unrecognized character: '" + line.charAt(x) + "'");
                    }
                }
            }
        }
        return map;
    }

}
