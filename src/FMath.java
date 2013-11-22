import java.awt.Color;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class FMath {
	public static final String SOUND_PATH = "/sounds/";
	
	public static double angle(Point p1, Point p2, Point p3) {
		double AB = length(p2, p1);
		double BC = length(p2, p3);
		double AC = length(p3, p1);
	    return Math.acos((sqr(BC) + sqr(AB) - sqr(AC)) / (2 * BC * AB));
	}
	
	public static boolean doIntersect(Point p1, Point p2, Point p3, Point p4) {		
		if(length(p3, p1) < length(p3, p2)) {
			Point temp = p1;
			p1 = p2;
			p2 = temp;
		}
		double a = angle(p4, p1, p2);
		double b = angle(p1, p4, p3);
		double c = Math.toRadians(180 - Math.toDegrees(b) - Math.toDegrees(a));
				
		if(Math.toDegrees(c) <= 0 || Math.toDegrees(c) >= 180) return false;
		if(Math.abs((length(p4, p1) * Math.sin(a)) / Math.sin(c)) > length(p4, p3)) return false;
		if(Math.abs((length(p4, p1) * Math.sin(b)) / Math.sin(c)) > length(p1, p2)) return false;
		
		/*StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.filledCircle(p1.x, p1.y, 0.9);
		StdDraw.line(p4.x, p4.y, p1.x, p1.y);
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.filledCircle(p2.x, p2.y, 1.2);
		StdDraw.line(p1.x, p1.y, p2.x, p2.y);
		StdDraw.setPenColor(StdDraw.CYAN);
		StdDraw.filledCircle(p4.x, p4.y, 0.9);
		StdDraw.filledCircle(p3.x, p3.y, 0.9);
		StdDraw.line(p4.x, p4.y, p3.x, p3.y);
		
		System.out.println("p1: " + p1);
		System.out.println("p2: " + p2);
		System.out.println("p4: " + p4);
		System.out.println("p3: " + p3);
		System.out.println("A: " + a + " B: " + b + " C: " + c);
		System.out.println("A: " + Math.toDegrees(a) + " B: " + Math.toDegrees(b) + " C: " + Math.toDegrees(c));
		System.out.println("length of a:     " + Math.abs((length(p4, p1) * Math.sin(a)) / Math.sin(c)));
		System.out.println("length of p4-p3: " + length(p4, p3) + "(Cyan)");
		System.out.println("length of b:     " + Math.abs((length(p4, p1) * Math.sin(b)) / Math.sin(c)));
		System.out.println("length of p1-p2: " + length(p1, p2) + "(Orange)");
		System.out.println("length of c:     " + length(p4, p1) + "(Blue)");
		System.out.println("---------------------------------");
		Flatscape.stop = true;*/
		return true;
	}
	
	public static String getColorString(Color color) {
		if(color == Color.GREEN) {
			return "Green";
		} else if(color == Color.PINK) {
			return "Pink";
		} else if(color == Color.RED) {
			return "Red";
		} else if(color == Color.WHITE) {
			return "White";
		}
		return "Color";
	}
	
	public static boolean hitboxCheck(Point boxPoint1, Point boxPoint2, Point point) {
		return (point.x > Math.min(boxPoint1.x, boxPoint2.x) && point.x < Math.max(boxPoint1.x, boxPoint2.x)
				&& point.y > Math.min(boxPoint1.y, boxPoint2.y) && point.y < Math.max(boxPoint1.y, boxPoint2.y));
	}
	
	public static boolean insidePolygon(Point p, Point[] polygon) {
		if(polygon.length < 3) return false;
				
		Point extreme = new Point(Flatscape.INFINITY, p.y);
		int count = 0, i = 0;
		
		do {
			int next = (i + 1) % polygon.length;			
			if(doIntersect(polygon[i], polygon[next], p, extreme)) {
				count++;
			}
			i = next;
		} while(i != 0);
		
		return (count % 2 == 1);
	}
	
	public static double length(Point point1, Point point2) {
		return Math.sqrt(sqr(point1.x - point2.x) + sqr(point1.y - point2.y));
	}
	
	public static int orientation(Point p, Point q, Point r) {
	    double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
	 
	    return val == 0.0 ? 0 : val > 0.0 ? 1 : 2;
	}
	
	public static synchronized void playSound(final String url) {
		try {
			Clip clip = AudioSystem.getClip();
			InputStream stream = Flatscape.class.getResourceAsStream(SOUND_PATH + url + ".wav");
			if(stream == null) System.out.println("stream null");
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(stream);
			clip.open(inputStream);
			clip.start(); 
		} catch (Exception e) {
			e.printStackTrace();
			//System.err.println(e.getMessage());
		}
	}
	
	public static double sqr(double doub) {
		return Math.pow(doub, 2);
	}
	
	public static double randomNegative(double doub) {
		if(Math.random() > .5) {
			doub *= -1;
		}
		return doub;
	}
	
	public static Point smallerHypot(double adj, double opp, double targetHypot) {
		double angle = Math.atan(opp / adj);
		Point returnee = new Point(Math.cos(angle) * targetHypot * (adj / Math.abs(adj)), Math.sin(angle) * targetHypot * (adj / Math.abs(adj)));
		return returnee;
	}
}
