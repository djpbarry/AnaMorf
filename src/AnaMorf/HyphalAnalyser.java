/* 
 * Copyright (C) 2015 David Barry <david.barry at cancer.org.uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package AnaMorf;

import Graph.Dijkstra;
import Graph.Graph;
import Graph.Node;
import IAClasses.SkeletonProcessor;
import ij.process.ByteProcessor;
import ij.process.ByteStatistics;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Determines the total length (<i>L<sub>t</sub></i>) of a hyphal structure (a
 * binary skeleton), the total number of hyphal tips (<i>N</i>) and the hyphal
 * growth unit (<i>L<sub>t</sub>/N</i>).
 */
public class HyphalAnalyser {

    private ImageProcessor processor;
    private double hyphalGrowthUnit = 0;
    private int hyphalLength = 0, tips = 0, branchpoints = 0, radius;
    private ImageProcessor colorOutput, bwOutput;
    private Rectangle imageBounds, objBounds;
    static int longestPathIndex = 0;

    public HyphalAnalyser(ImageProcessor image, double res, Rectangle imageBox,
            Rectangle objBox) {
        this.processor = image;
        imageBounds = imageBox;
        objBounds = objBox;
//        radius = (int) Math.round(4.0 * 1.12347 / res);
        radius = 2;
        colorOutput = new ColorProcessor(imageBox.width, imageBox.height);
        colorOutput.setLineWidth(radius / 2);
        colorOutput.setColor(Color.black);
        colorOutput.fill();
        bwOutput = new ByteProcessor(imageBox.width, imageBox.height);
        bwOutput.setColor(Color.black);
        bwOutput.fill();
    }

    /**
     * Analyses the skeletal structure contained in <i>image</i> and determines
     * the total length, the number of end-points and the ratio of total length
     * to end-points.
     */
    public void analyse() {
        int x, y, foreground = 0, background = 255, diam = 2 * radius + 1, imageX, imageY;
        colorOutput.setColor(Color.white);
        bwOutput.setColor(Color.white);

        if (processor.isInvertedLut()) {
            foreground = 255;
            background = 0;
        }

        int width = processor.getWidth();
        int height = processor.getHeight();

        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                if (processor.getPixel(x, y) == foreground) {
                    hyphalLength++;
                    imageX = x + objBounds.x;
                    imageY = y + objBounds.y;
                    colorOutput.drawPixel(imageX, imageY);
                    bwOutput.drawPixel(imageX, imageY);
                    if (SkeletonProcessor.isEndPoint(x, y, processor, background)) {
                        if ((imageX > radius)
                                && (imageX < imageBounds.width - radius)
                                && (imageY > radius)
                                && (imageY < imageBounds.height - radius)) {
                            colorOutput.setColor(Color.red);
                            colorOutput.drawOval(imageX - radius, imageY - radius, diam, diam);
                            colorOutput.setColor(Color.white);
                            tips++;
                        }
                    } else if (searchNeighbourhood(x, y, 1, foreground) > 2) {
                        if (SkeletonProcessor.isBranchPoint(x, y, processor, foreground) == 0) {
                            colorOutput.setColor(Color.yellow);
                            colorOutput.drawOval(imageX - radius, imageY - radius, diam, diam);
                            colorOutput.setColor(Color.white);
                            branchpoints++;
                        }
                    }
                }
            }
        }
        if (tips > 0) {
            hyphalGrowthUnit = (double) hyphalLength / tips;
        }
    }

    public int searchNeighbourhood(int x, int y, int radius, int value) {
        int count = 0;

        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if ((processor.getPixel(i, j) == value) && !((x == i) && (y == j))) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * @return the hyphal growth unit (total length / number of tips).
     */
    public double getHGU() {
        return hyphalGrowthUnit;
    }

    /**
     * @return the total hyphal length in pixels.
     */
    public int getLength() {
        return hyphalLength;
    }

    /**
     * @return the total number of hyphal tips.
     */
    public int getTips() {
        return tips;
    }

    public int getBranchpoints() {
        return branchpoints;
    }

    public ImageProcessor getColorOutput() {
        return colorOutput;
    }

    public ImageProcessor getBWOutput() {
        return bwOutput;
    }

    public ArrayList<int[][]> findLongestPath() {
        int foreground = 0, background = 255;
        ImageProcessor ip = null;
        if (processor != null) {
            ip = processor.duplicate();
        }

        int width = ip.getWidth();
        int height = ip.getHeight();

        ArrayList<Node> nodes = new ArrayList();
        radius = 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (ip.getPixel(x, y) == foreground) {
                    int imageX = x;
                    int imageY = y;
                    if (SkeletonProcessor.isEndPoint(x, y, ip, background)) {
                        nodes.add(new Node(Node.END, imageX, imageY));
//                            System.out.println(String.format("%d %d", imageX, imageY));
                    } else if (searchNeighbourhood(x, y, 1, foreground) > 2) {
                        if (SkeletonProcessor.isBranchPoint(x, y, ip, foreground) == 0) {
                            nodes.add(new Node(Node.BRANCH, imageX, imageY));
//                            System.out.println(String.format("%d %d", imageX, imageY));
                        }
                    }
                }
            }
        }
        if (nodes.isEmpty()) {
            return null;
        }

        boolean change = true;
        SkeletonPruner sp = new SkeletonPruner(objBounds);
        int index = 0;
        while (change) {
            change = false;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (ip.getPixel(x, y) == foreground && SkeletonProcessor.isEndPoint(x, y, ip, background)) {
                        change = true;
                        int imageX = x;
                        int imageY = y;
                        int n1 = findNodeIndex(nodes, imageX, imageY);
                        if (n1 < 0) {
                            continue;
                        }
                        Node start = nodes.get(n1);
                        short[][] path = sp.traceBranch(ip, x, y, nodes, new ByteStatistics(processor).histogram[SkeletonPruner.FOREGROUND]);
//                        drawPath(path, index++);
                        int length = path[0].length;
                        int n2 = findNodeIndex(nodes, path[0][length - 1], path[1][length - 1]);
                        if (n2 < 0) {
                            continue;
                        }
                        Node end = nodes.get(n2);
                        start.addDestination(end, path);
                        end.addDestination(start, path);
                        sp.prunePoints(path[0][0], path[0][1], ip);
                        sp.prunePoints(path[0][length - 1], path[1][length - 1], ip);
                    }
                }
            }
        }
//        int nIndex = 0;
        Graph graph = new Graph();
        for (Node n : nodes) {
            if (n.getAdjacentNodes().isEmpty()) {
                continue;
            }
            graph.addNode(n);
//            ByteProcessor bp = new ByteProcessor(objBounds.width, objBounds.height);
//            bp.setColor(255);
//            bp.fill();
//            bp.setColor(0);
//            bp.drawRect(n.getX() - 3, n.getY() - 3, 7, 7);
//            for (Entry<Node, int[][]> nodePath : n.getAdjacentNodes().entrySet()) {
//                drawPath(nodePath.getValue(), bp);
//                bp.drawRect(nodePath.getKey().getX() - 1, nodePath.getKey().getY() - 1, 3, 3);
//            }
//            IJ.saveAs(new ImagePlus("", bp), "PNG", "D:/debugging/anamorf_debug/node_neighbour_paths_" + longestPathIndex + "_" + nIndex++);
        }
        Map<Node, Graph> distanceMaps = new HashMap();
        for (Node n : nodes) {
            if (n.getType() == Node.END) {
                distanceMaps.put(n, new Graph(Dijkstra.calculateShortestPathFromSource(graph, n)));
                graph.resetNodes();
            }
        }
        return drawLongestPath(distanceMaps, width, height);
    }

    ArrayList<int[][]> drawLongestPath(Map<Node, Graph> distanceMaps, int width, int height) {
        int maxDist = -1;
        LinkedList<Node> longestShortestPath = null;
        for (Map.Entry< Node, Graph> nodeGraphPair : distanceMaps.entrySet()) {
            Graph g = nodeGraphPair.getValue();
            Set<Node> nodes = g.getNodes();
            for (Node n : nodes) {
                if (n.getType() == Node.END && n.getDistance() > maxDist && n.getDistance() < Integer.MAX_VALUE) {
//                    System.out.println(String.format("%d %d %d %d"));
                    maxDist = n.getDistance();
                    longestShortestPath = new LinkedList(n.getShortestPath());
                    if (!longestShortestPath.isEmpty()) {
                        longestShortestPath.add(findNode(longestShortestPath.getLast().getAdjacentNodes(), n));
                    }
                }
            }
        }

//        ByteProcessor bp = new ByteProcessor(objBounds.width, objBounds.height);
//        bp.setValue(255);
//        bp.fill();
//        bp.setValue(0);
        if (longestShortestPath != null && !longestShortestPath.isEmpty()) {
//            drawPath(bp, longestShortestPath);
//            IJ.saveAs(new ImagePlus("", bp), "PNG", "D:/debugging/anamorf_debug/longest_path_" + longestPathIndex++);
            return getPathAsBranch(longestShortestPath);
        }
        return null;
    }

    Node findNode(Map<Node, short[][]> adjacentNodes, Node n1) {
        for (Entry<Node, short[][]> adjNode : adjacentNodes.entrySet()) {
            Node n2 = adjNode.getKey();
            if (n1.equals(n2)) {
                return n2;
            }
        }
        return null;
    }

    int findNodeIndex(ArrayList<Node> nodes, int x, int y) {
//        System.out.println(String.format("%d %d", x, y));
        double minD = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < nodes.size(); i++) {
//            double d = nodes.get(i).getSimpleDist(x, y);
            double d = nodes.get(i).getDist(x, y);
            if (d < minD) {
                minD = d;
                minIndex = i;
            }
//            System.out.println(String.format("%d %d %d %d", nodes.get(i).getX(),nodes.get(i).getY(), d, minIndex));
        }
        return minIndex;
    }

    void drawPath(ImageProcessor image, LinkedList<Node> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            short[][] pixels = path.get(i).getAdjacentNodes().get(path.get(i + 1));
            for (int j = 0; j < pixels[0].length; j++) {
                image.drawPixel(pixels[0][j], pixels[1][j]);
            }
            image.drawPixel(path.get(i).getX(), path.get(i).getY());
            image.drawPixel(path.get(i + 1).getX(), path.get(i + 1).getY());
        }
    }

    void drawPath(int[][] path, int index) {
        ByteProcessor bp = new ByteProcessor(objBounds.width, objBounds.height);
        bp.setColor(255);
        bp.fill();
        bp.setColor(0);
        for (int i = 0; i < path[0].length; i++) {
            bp.drawPixel(path[0][i], path[1][i]);
        }
//        IJ.saveAs(new ImagePlus("", bp), "PNG", "D:/debugging/anamorf_debug/draw_path_" + longestPathIndex + "_" + index);
    }

    void drawPath(int[][] path, ImageProcessor bp) {
        for (int i = 0; i < path[0].length; i++) {
            bp.drawPixel(path[0][i], path[1][i]);
        }
    }

    ArrayList<int[][]> getPathAsBranch(LinkedList<Node> path) {
        ArrayList<int[]> output = new ArrayList();
        for (int i = 0; i < path.size() - 1; i++) {
//            output.add(new int[]{path.get(i).getX() + objBounds.x, path.get(i).getY() + objBounds.y});
            short[][] pixels = path.get(i).getAdjacentNodes().get(path.get(i + 1));
            int d1 = path.get(i).getSimpleDist(pixels[0][0], pixels[1][0]);
            int d2 = path.get(i + 1).getSimpleDist(pixels[0][0], pixels[1][0]);
            if (d1 > d2) {
                pixels = reversePath(pixels);
            }
            for (int j = 0; j < pixels[0].length; j++) {
                output.add(new int[]{pixels[0][j] + objBounds.x, pixels[1][j] + objBounds.y});
            }
//            output.add(new int[]{path.get(i + 1).getX() + objBounds.x, path.get(i + 1).getY() + objBounds.y});
        }
        int[][] branch = new int[output.size()][];
        int[][] branch1 = output.toArray(branch);
        ArrayList<int[][]> output2 = new ArrayList();
        output2.add(branch1);
        return output2;
    }

    short[][] reversePath(short[][] path) {
        int l = path[0].length;
        short[][] output = new short[2][l];
        for (int i = l - 1; i >= 0; i--) {
            output[0][l - 1 - i] = path[0][i];
            output[1][l - 1 - i] = path[1][i];
        }
        return output;
    }

}
