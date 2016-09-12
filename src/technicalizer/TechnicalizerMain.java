package technicalizer;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

public class TechnicalizerMain {
	public static void main(String[] args) {
		
		System.out.println("Running Technicalizer with arguments:");
		for (String arg : args) {
			System.out.println(arg);
		}

		if (args.length < 2) {
			System.out.println("Please provide input and output image filenames as two command line parameters for this aplication.");
		} else {

			BufferedImage img = null;
			try {
				img = ImageIO.read(new File(args[0]));
			} catch (IOException e) {
			}
			
			Random rnd = new Random(0);
			
			Vector<Point> points = new Vector<Point>();
			
			Color col;
			for (int i = 0; i < img.getWidth(); i++) {
				for(int j = 0; j < img.getHeight(); j++) {
					col = new Color(img.getRGB(i, j));
					float brightness = (float)(
							2 * col.getRed()   +
							4 * col.getGreen() +
							1 * col.getBlue()  ) / 6f;
					brightness /= 256f;
					float r = rnd.nextFloat();
					if (brightness < r) {
						points.add(new Point(i, j));
					}
				}
			}
			
			Collections.shuffle(points, rnd);
			Vector<Vector<Point>> connections = new Vector<>();
			
			while(points.size() > 0) {
				// set start point
				Point start = points.get(0);
				points.remove(0);
				
				Vector<Point> connection = new Vector<Point>();
				connection.add(start);
				connections.add(connection);
				
				Point current = start;

				// travel
				boolean hasTraveled = true;
				while(hasTraveled) {
					hasTraveled = false;
					System.out.print(current.getX() + "," + current.getY() + " --> ");
					for (int i = 0; i < points.size(); i++) {
						Point toCheck = points.get(i);
						if (	Math.abs(current.getX() - toCheck.getX()) < 2 &&
								Math.abs(current.getY() - toCheck.getY()) < 2 &&
								!isCrossing(current, toCheck, connections)) {
							connection.add(toCheck);
							current = toCheck;
							points.remove(i);
							hasTraveled = true;
							break;
						}
					}
				}
				System.out.println();
				
				
			}
			
			String svg = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\" ?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\"  \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\"><svg width=\"" + img.getWidth() + "\" height=\"" + img.getHeight() + "\" xmlns=\"http://www.w3.org/2000/svg\"  xmlns:xlink=\"http://www.w3.org/1999/xlink\">";
			for (Vector<Point> vector : connections) {
				svg += "<circle cx=\"" + vector.get(0).getX() + "\" cy=\"" + vector.get(0).getY() + "\" r=\"0.4\" fill=\"black\" />";
				svg += "<circle cx=\"" + vector.get(vector.size() - 1).getX() + "\" cy=\"" + vector.get(vector.size() - 1).getY() + "\" r=\"0.4\" fill=\"black\" />";
				
				
				svg += "<path d=\"";
				for (int i = 0; i < vector.size(); i++) {
					if (i == 0) {
						svg += "M ";
					} else {
						svg += "L ";
					}
					svg += vector.get(i).getX() + " " + vector.get(i).getY() + " ";
				}
				svg += "\" stroke=\"black\" stroke-linejoin=\"round\" stroke-width=\"0.4\" stroke-linecap=\"round\" fill=\"none\"/>";
			}
			svg += "</svg>";
			try {
				PrintWriter writer = new PrintWriter(args[1]);
				writer.print(svg);
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Found " + connections.size() + " fancy connections");
		}
	}
	
	private static boolean isCrossing(Point sourceA, Point targetA, Vector<Vector<Point>> existing) {
		for (Vector<Point> vector : existing) {
			for (int i = 1; i < vector.size(); i++) {
				if (vector.get(i) != sourceA && vector.get(i) != targetA) {
					if (Line2D.linesIntersect(sourceA.getX(), sourceA.getY(), targetA.getX(), targetA.getY(), vector.get(i-1).getX(), vector.get(i-1).getY(), vector.get(i).getX(), vector.get(i).getY())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
