package fr.labycraft.Modele;

import fr.labycraft.Modele.MazeGenerator;
import java.awt.Point;
import java.util.Arrays;
import java.util.Random;

public class Labyrinthe extends MazeGenerator {
	// The starting coordinates

	private int startX;

	private int startY;

	private Random rand = new Random();

	/**
	 * Creates a new Prim's Algorithm maze generator. A random starting
	 * location will be selected.
	 *
	 * @param width the maze width
	 * @param height the maze height
	 */
	public Labyrinthe(int width, int height) {
		super(width, height);

		this.startX = rand.nextInt(width);
		this.startY = rand.nextInt(height);

		reset();
	}

	/**
	 * Creates a new Prim's Algorithm maze generator. This uses the given
	 * starting location.
	 *
	 * @param width the maze width
	 * @param height the maze height
	 * @param startX the starting X-coordinate
	 * @param startY the starting Y-coordinate
	 */
	public Labyrinthe(int width, int height,
			int startX, int startY) {
		super(width, height);

		checkLocation(startX, startY);

		this.startX = startX;
		this.startY = startY;
	}

	private static final int IN = 0;

	private static final int FRONTIER = 1;

	private static final int OUT = 2;

	/**
	 * Generate the maze.
	 */
	protected void generateMaze() {
		int width = getWidth();
		int height = getHeight();

		int[] cells = new int[width * height];  // States
		Arrays.fill(cells, OUT);
		int[] frontierCells = new int[width * height];

		Point topLeft = new Point();
		Point topRight = new Point();
		Point bottomLeft = new Point();
		Point bottomRight = new Point();

        // Start cell
		// Make all its neighbours FRONTIER
		topLeft.setLocation((width / 2) - (safeWidth / 2), (height / 2) - safeHeigth / 2);

		topRight.setLocation(((width / 2) + (safeWidth / 2)) - 1, (height / 2) - safeHeigth / 2);

		bottomLeft.setLocation((width / 2) - (safeWidth / 2), ((height / 2) + safeHeigth / 2) - 1);

		bottomRight.setLocation(((width / 2) + (safeWidth / 2) - 1), ((height / 2) + safeHeigth / 2) - 1);

		int y2 = (int) topLeft.getY();

		while (y2 <= bottomLeft.getY()) {

			int x = (int) topLeft.getX();
			while (x <= topRight.getX()) {
				cells[y2 * width + x] = FRONTIER;
				x++;
			}
			y2++;
		}

		while (cells[startY * width + startX] == FRONTIER) {
			this.startX = rand.nextInt(width);
			this.startY = rand.nextInt(height);
		}

		cells[startY * width + startX] = IN;

		int frontierCount = 0;
		print(cells);
		for (int i = 0;i < 4;i++) {
			switch (i) {
				case UP:
					if (startY > 0) {
						int index = (startY - 1) * width + startX;
						cells[index] = FRONTIER;
						frontierCells[frontierCount++] = index;
					}
					break;
				case RIGHT:
					if (startX < width - 1) {
						int index = startY * width + (startX + 1);
						cells[index] = FRONTIER;
						frontierCells[frontierCount++] = index;
					}
					break;
				case DOWN:
					if (startY < height - 1) {
						int index = (startY + 1) * width + startX;
						cells[index] = FRONTIER;
						frontierCells[frontierCount++] = index;
					}
					break;
				case LEFT:
					if (startX > 0) {
						int index = startY * width + (startX - 1);
						cells[index] = FRONTIER;
						frontierCells[frontierCount++] = index;
					}
					break;
			}
		}

        // Loop until there are no more frontier cells
		int[] inNeighbours = new int[4];

		while (frontierCount > 0) {
			int frontierCellIndex = rand.nextInt(frontierCount);
			int index = frontierCells[frontierCellIndex];
			int x = index % width;
			int y = index / width;

            // Carve into this frontier cell from one of its IN neighbours
			int inNeighbourCount = 0;
			for (int i = 0;i < 4;i++) {
				switch (i) {
					case UP:
						if (y > 0 && cells[(y - 1) * width + x] == IN) {
							inNeighbours[inNeighbourCount++] = i;
						}
						break;
					case RIGHT:
						if (x < width - 1 && cells[y * width + (x + 1)] == IN) {
							inNeighbours[inNeighbourCount++] = i;
						}
						break;
					case DOWN:
						if (y < height - 1 && cells[(y + 1) * width + x] == IN) {
							inNeighbours[inNeighbourCount++] = i;
						}
						break;
					case LEFT:
						if (x > 0 && cells[y * width + (x - 1)] == IN) {
							inNeighbours[inNeighbourCount++] = i;
						}
						break;
				}
			}

			carve(x, y, inNeighbours[rand.nextInt(inNeighbourCount)]);

            // Mark this cell as IN
			cells[index] = IN;
			if (frontierCellIndex < frontierCount - 1) {
				System.arraycopy(frontierCells, frontierCellIndex + 1,
						frontierCells, frontierCellIndex,
						frontierCount - frontierCellIndex - 1);
			}
			frontierCount--;

            // Mark any neighbouring OUT cells as FRONTIER
			for (int i = 0;i < 4;i++) {
				switch (i) {
					case UP:
						if (y > 0 && cells[index - width] == OUT) {
							cells[index - width] = FRONTIER;
							frontierCells[frontierCount++] = index - width;
						}
						break;
					case RIGHT:
						if (x < width - 1 && cells[index + 1] == OUT) {
							cells[index + 1] = FRONTIER;
							frontierCells[frontierCount++] = index + 1;
						}
						break;
					case DOWN:
						if (y < height - 1 && cells[index + width] == OUT) {
							cells[index + width] = FRONTIER;
							frontierCells[frontierCount++] = index + width;
						}
						break;
					case LEFT:
						if (x > 0 && cells[index - 1] == OUT) {
							cells[index - 1] = FRONTIER;
							frontierCells[frontierCount++] = index - 1;
						}
						break;
				}
			}
		}
	}

	/**
	 * Returns a string representation of this object.
	 *
	 * @return a string representation of this object.
	 */
	public String toString() {
		return "Prim's Algorithm maze generator";
	}

	private void print(int[] cells) {
	}
}
