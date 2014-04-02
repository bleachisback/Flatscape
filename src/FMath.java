import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class FMath {
	public static String SOUND_PATH = "/sounds/default/";
	
	public static double angle(Point p1, Point p2, Point p3) {
		double AB = length(p2, p1);
		double BC = length(p2, p3);
		double AC = length(p3, p1);
	    return Math.acos((sqr(BC) + sqr(AB) - sqr(AC)) / (2 * BC * AB));
	}
	
	public static Point circlePoint(double length, double angle) {
		return new Point(length * Math.sin(Math.toRadians(angle)), length * Math.cos(Math.toRadians(angle)));
	}
	
	public static boolean doIntersect(Point p1, Point p2, Point p3, Point p4) {		
		if(Math.abs(p3.y - p1.y) < Math.abs(p3.y - p2.y)) {
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
			InputStream stream = FMath.class.getResourceAsStream(SOUND_PATH + url + ".wav");
			InputStream bufferedStream = new BufferedInputStream(stream);
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedStream);
			clip.open(inputStream);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
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
		double negative = 1;
		if(adj == 0) {
			if(opp == 0) {
				return new Point(0, 0);
			} else {
				negative = opp / Math.abs(opp);
			}
		} else {
			negative = adj / Math.abs(adj);
		}
		Point returnee = new Point(Math.cos(angle) * targetHypot * negative, Math.sin(angle) * targetHypot * negative);
		return returnee;
	}
}
