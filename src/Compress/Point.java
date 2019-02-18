package Compress;

import java.text.MessageFormat;

public class Point {  
        private double x;  //latitude
        private double y;  //longtitude
        private String GPS_ID;
        private String taxi_id;
        private String speed;
        private String angle;
        private String state;
        private String time;
        public Point(String x,String y,String gps_id,String Angle,String Speed,String Time,String State,String Taxi_id){  
            this.x = Double.parseDouble(x);  
            this.y = Double.parseDouble(y);  
            this.GPS_ID=gps_id;
            this.speed=Speed;
            this.angle=Angle;
            this.time=Time;
            this.state= State;
            this.taxi_id= Taxi_id;
        }  
        public Point(String x,String y){  
            this.x = Double.parseDouble(x);  //latitude
            this.y = Double.parseDouble(y);  //longtitude
        }  
        public String getGPS_ID(){
            return GPS_ID;
        }
        public String getTaxi_id(){
        	return taxi_id;
        }
        public String getSpeed(){
        	return speed;
        }
        public String getAngle(){
        	return angle;
        }
        public String getState(){
        	return state;
        }
        public String getTime(){
        	return time;
        }
        	
        public double getX() {  
            return x;  
        }  
        public void setX(double x) {  
            this.x = x;  
        }  
        public double getY() {  
            return y;  
        }  
        public void setY(double y) {  
            this.y = y;  
        }  
        public void printPoint(){  
            System.out.print(MessageFormat.format("({0},{1})", this.x,this.y));  
        }  
      
    }  