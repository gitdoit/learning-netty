package org.seefly.mynetty.netty.car;

/**
 * @author liujianxin
 * @date 2019-04-18 09:15
 */
public class Car {
    private int x;
    private int y;
    private byte currDire;

    public static void main(String[] args) {
        Car car = new Car(0,0,'N');
        car.doWork("MMMLMMRRMMRMMMMM");
        System.out.println(car);
    }

    public Car(int x, int y, char currDire) {
        this.x = x;
        this.y = y;
        this.currDire = convert(currDire);
    }





    @Override
    public String toString() {
        return x +":"+ y;
    }

    public void doWork(String cmd){
        for(int i = 0; i < cmd.length() ; i++){
            switch (cmd.charAt(i)){
                case 'R' :
                    turnRight();
                    break;
                case 'L' :
                    turnLeft();
                    break;
                case 'M' :
                    moveOneStep();
                    break;
                default:
                    break;
            }
        }
    }

    private void moveOneStep(){

        if(64 == currDire){
            x++;
        }else if(16 == currDire){
            y--;
        }else if(4 == currDire){
            x--;
        }else if(1 == currDire){
            y++;
        }
    }

    private void turnLeft(){
        currDire = (byte)(currDire << 2 | currDire >> 6);
    }

    private void turnRight(){
        currDire = (byte)(currDire >> 2 | currDire << 6);
    }



    private byte convert(char dire){
        switch (dire){
            case 'E' :
                return 64;
            case 'S' :
                return 16;
            case 'W' :
                return 4;
            case 'N' :
                return 1;
            default:
                throw new IllegalArgumentException("参数有误！");
        }
    }


}
