package DataMining;

public class Point {
	public double x;
	public double y;
	String taxi_id;
	String GPS_ID;
	String time;
	String Speed;
	String Angle;
	String state;
	
	public int order = -1;
	//鐐圭殑搴忓彿锛堟墍灞炶建杩圭殑搴忓彿锛�
	int num = -1;
	public Point(){
		
	}
	public int getOrder() {
		return order;
	}
    public void setGPS_ID(String GPS_ID){
    	this.GPS_ID = GPS_ID;
    }
    public String getGPS_ID(){
    	return GPS_ID;
    }
    public void setTime(String time){
    	this.time = time;
    }
    public String getTime(){
    	return time;
    }
    public void setSpeed(String speed){
    	this.Speed = speed;
    }
    public String getSpeed(){
    	return Speed;
    }
    public void setAngle(String Angle){
    	this.Angle = Angle;
    }
    public String getAngle(){
    	return Angle;
    }
    public void setState(String state){
    	this.state = state;
    }
    public String getState(){
    	return state;
    }
	public void setOrder(int order) {
		this.order = order;
	}
    public void setTaxi_id(String taxi_id){
    	this.taxi_id = taxi_id;
    }
    public String getTaxi_id(){
    	return taxi_id;
    }
	public Point(double x , double y){
		this.x = x;
		this.y = y;
	}
	
	public int getNum() {
		return num;
	}
	/**
	 * @description 璁剧疆杞ㄨ抗搴忓彿
	 * @param num
	 */
	public void setNum(int num) {
		this.num = num;
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
	@Override
	public String toString() {
		return x + "\t" + y;
	}
	
}
