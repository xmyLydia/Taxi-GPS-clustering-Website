package CharacteristicPoints;

import java.util.ArrayList;

public class Trajectory {  
    private Point start;  
    private Point end;  
    private ArrayList<Point> linePoints = new ArrayList<Point>();  
    private double A;  
    private double B;  
    private double C;  
    private int index;//最大距离所对应曲线上的点的索引号  
    private double distance;//最大距离，与阈值比较  
    public ArrayList<Point> getLinePoints() {  
        return linePoints;  
    }  
    public void setLinePoints(ArrayList<Point> linePoints) {  
        this.linePoints = linePoints;  
          
    }  
    //已知了线的两个端点，求两个端点所连线段到线的最大距离  
    public Trajectory(Point start,Point end ){  
        this.start = start;  
        this.end = end;  
        linePoints.add(start);  
        linePoints.add(end);  
    }  
      
    public Trajectory(ArrayList<Point> linePoints){  
        this.linePoints = linePoints;  
        this.start = linePoints.get(0);  
        this.end = linePoints.get(linePoints.size()-1);  
      //  initABC();  
      //  computeLineToLine();  
        this.linePoints = linePoints;
          
    }  
    public Trajectory(Point start,Point end,ArrayList<Point> linePoints){  
        this.start = start;  
        this.end = end;  
        this.linePoints = linePoints;  
    }  
    public Point getStart() {  
        return start;  
    }  
    public void setStart(Point start) {  
        this.start = start;  
    }  
    public Point getEnd() {  
        return end;  
    }  
    public void setEnd(Point end) {  
        this.end = end;  
    }  
    public double getDistance() {  
        return distance;  
    }  
    public void setDistance(double distance) {  
        this.distance = distance;  
    }  
    public void computeLineToLine(){  
        double maxDist=Double.MIN_VALUE;  
        double dist = 0;  
        index=0;  
        for(int i=0;i<linePoints.size();i++){  
            dist = computePointToLineDist(linePoints.get(i));  
            if(dist>maxDist)  
                {  
                maxDist = dist;  
                index = i;  
                }  
                   
        }  
        this.distance = maxDist;  
    }  
    public double getA() {  
        return A;  
    }  
    public void setA(double a) {  
        A = a;  
    }  
    public double getB() {  
        return B;  
    }  
    public void setB(double b) {  
        B = b;  
    }  
    public double getC() {  
        return C;  
    }  
    public void setC(double c) {  
        C = c;  
    }  
    public int getIndex() {  
        return index;  
    }  
    public void setIndex(int index) {  
        this.index = index;  
    }  
    public double computePointToLineDist(Point point){  
          
        return (A*point.getX()+B*point.getY()+C)/Math.sqrt(A*A+B*B);  
          
    }  
    public void initABC(){  
        this.A = start.getY()-end.getY();  
        this.B = end.getX()-start.getX();  
        this.C = start.getY()*(-B)-start.getX()*A;  
    }  
    public void printLine(){  
        System.out.print("-------");  
        for(Point point:linePoints){  
              
            // point.printPoint();  
              
        }  
        System.out.print("-------");  
    }  
    public void addPoint(Point p){  
        this.linePoints.add(p);  
    } 
  
}  