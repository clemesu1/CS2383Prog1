package ca.unbsj.cs2383;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.UF;

import java.awt.*;
import java.io.*;
import java.util.*;

public class Prog1 {

    public static void main(String[] args) {

        while (true) {
            System.out.println("Please select an option.\n[1] Single Run (Visualized)\n[2] Multiple Runs (Not Visualized)\n[3] Quit");

            Scanner scan = new Scanner(System.in);
            String input = scan.nextLine();
            int selection = 0;
            try {
                selection = Integer.parseInt(input);
            } catch (InputMismatchException e) {
                System.err.println("Error: " + e.getLocalizedMessage());
            }

            switch (selection) {
                case 1:
                    runSimulation(1, true);
                    break;
                case 2:
                    System.out.print("Please enter the number of simulations: ");
                    int numOfSimulations = 0;
                    try {
                        numOfSimulations = Integer.parseInt(scan.nextLine());
                    } catch (InputMismatchException e) {
                        System.err.println("Invalid input.");
                    }

                    if (numOfSimulations <= 0) {
                        System.out.println("Please enter a value greater than 0.");
                    } else {
                        runSimulation(numOfSimulations, false);
                    }
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid input. Please try again.");
                    break;
            }
        }
    }

    public static void runSimulation(int numOfSimulations, boolean visualization) {
        try {
            String outputFileName = "C:\\Users\\coliw\\IdeaProjects\\Prog1\\Prog1Output.txt";
            File outputFile = new File(outputFileName);
            PrintWriter writer = new PrintWriter(new FileWriter(outputFile));

            for (int count = 0; count < numOfSimulations; count++) {
                Picture pix = new Picture("src/main/resources/motherboard-152501.png");

                // how many pixels have a fair bit of green?
                int yMax = pix.height();
                int xMax = pix.width();

                boolean[][] greenComponents = new boolean[xMax][yMax];

                for (int i = 0; i < yMax; ++i)
                    for (int j = 0; j < xMax; ++j) {
                        Color c = pix.get(j, i);
                        if (isGreen(c))
                            greenComponents[j][i] = true;
                    }

                // Create object that contains unions for image;
                // for each pixel, check if connected, check if green, then union

                // Create union object that has n=num of pixels in image

                int N = yMax * xMax;
                UF uf = new UF(N);
                UF original = new UF(N);

                for (int y = 0; y < yMax; y++) {
                    for (int x = 0; x < xMax; x++) {
                        Color c = pix.get(x, y);
                        // check below and to right
                        if (isGreen(c)) {
                            // create number that represents x and y positions
                            int p = getId(x, y, xMax);

                            if (isWithinBounds(x + 1, y, xMax, yMax)) {
                                Color neighbour = pix.get(x + 1, y);
                                if (isGreen(neighbour)) {
                                    int q = getId(x + 1, y, xMax);
                                    if (!uf.connected(p, q)) {
                                        uf.union(p, q);
                                        original.union(p, q);
                                    }
                                }
                            }

                            if (isWithinBounds(x, y + 1, xMax, yMax)) {
                                Color neighbour = pix.get(x, y + 1);
                                if (isGreen(neighbour)) {
                                    int q = getId(x, y + 1, xMax);
                                    if (!uf.connected(p, q)) {
                                        uf.union(p, q);
                                        original.union(p, q);
                                    }
                                }
                            }
                        }
                    }
                }

                boolean flag = false;
                int errorComponent = 0;

                Random random = new Random();
                int timer = 0;

                for (int y = 0; y < yMax; y++) {
                    for (int x = 0; x < xMax; x++) {

                        int dustX = random.nextInt(xMax);
                        int dustY = random.nextInt(yMax);

                        pix.set(dustX, dustY, Color.BLUE);

                        int p = getId(dustX, dustY, xMax);

                        Color c = pix.get(x, y);
                        if (isGreen(c) || isBlue(c)) {
                            if (isWithinBounds(dustX + 1, dustY, xMax, yMax)) {
                                Color neighbour = pix.get(dustX + 1, dustY);
                                if (isGreen(neighbour) || isBlue(neighbour)) {
                                    int q = getId(dustX + 1, dustY, xMax);
                                    if (!uf.connected(p, q)) {
                                        uf.union(p, q);
                                    }
                                }
                            }

                            if (isWithinBounds(dustX, dustY + 1, xMax, yMax)) {
                                Color neighbour = pix.get(dustX, dustY + 1);
                                if (isGreen(neighbour) || isBlue(neighbour)) {
                                    int q = getId(dustX, dustY + 1, xMax);
                                    if (!uf.connected(p, q)) {
                                        uf.union(p, q);
                                    }
                                }
                            }

                            if (isWithinBounds(dustX - 1, dustY, xMax, yMax)) {
                                Color neighbour = pix.get(dustX - 1, dustY);
                                if (isGreen(neighbour) || isBlue(neighbour)) {
                                    int q = getId(dustX - 1, dustY, xMax);
                                    if (!uf.connected(p, q)) {
                                        uf.union(p, q);
                                    }
                                }
                            }

                            if (isWithinBounds(dustX, dustY - 1, xMax, yMax)) {
                                Color neighbour = pix.get(dustX, dustY - 1);
                                if (isGreen(neighbour) || isBlue(neighbour)) {
                                    int q = getId(dustX, dustY - 1, xMax);
                                    if (!uf.connected(p, q)) {
                                        uf.union(p, q);
                                    }
                                }
                            }
                        }

                        if (greenComponents[x][y]) {
                            // get the id for the component
                            int id = getId(x, y, xMax);
                            // check if the component is not the same id as the original component
                            if (original.find(id) != uf.find(id)) {
                                // save the error components id
                                errorComponent = uf.find(id);
                                flag = true;
                                break;
                            }
                        }
                        timer++;
                    }
                    if (flag) break;
                }

                if (flag) {
                    for (int y = 0; y < yMax; y++) {
                        for (int x = 0; x < xMax; x++) {
                            int id = getId(x, y, xMax);
                            if (uf.find(id) == errorComponent) {
                                pix.set(x, y, Color.red);
                            }
                        }
                    }
                    if (visualization) {
                        pix.show();
                    } else {
                        System.out.println(timer);
                        writer.println(timer);
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("An error has occurred.");
            e.printStackTrace();
        }
    }

    private static int getId(int x, int y, int xMax) {
        return y * xMax + x;
    }

    public static boolean isGreen(Color c) {
        return c.getRed() == 52 && c.getGreen() == 171 && c.getBlue() == 0;
    }

    public static boolean isBlue(Color c) {
        return c.getRed() == 0 && c.getGreen() == 0 && c.getBlue() == 255;
    }


    private static boolean isWithinBounds(int x, int y, int xMax, int yMax) {
        return x >= 0 && x < xMax && y >= 0 && y < yMax;
    }

}

