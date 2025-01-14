

import androidx.annotation.NonNull;

import com.pedropathing.localization.Pose;

public class Sample implements Comparable<Sample> {
    String name;
    double x;
    double y;
    Pose targetPose;

    public Sample(String name, double x, double y, Pose targetPose) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.targetPose = targetPose;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " at (" + x + ", " + y + ")";
    }

    @Override
    public int compareTo(Sample sample) {
        return Double.compare(this.getDistance(), sample.getDistance());
    }

    public double getDistance(){
        return calculateDistance(x,y,targetPose.getX(),targetPose.getY());
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The Euclidean distance between the two points.
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}