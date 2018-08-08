package com.kco;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by 666666 on 2018/8/7.
 */
public class Test {
    static class Point{
        final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    /*
        东   east        1 0
        南   south       0 1
        西   west        -1 0
        北   north      0 -1
     */
    public static void main(String[] args) {
        Random random = new Random();
        int wide = 40;
        int height = 18;
        int lightX = random.nextInt(40); // the X position of the light of power
        int lightY = random.nextInt(18); // the Y position of the light of power
        int initialTX = random.nextInt(40); // Thor's starting X position
        int initialTY = random.nextInt(18); // Thor's starting Y position
        Queue<String> queue = new LinkedBlockingQueue<String>();
        int nsDistance = lightY - initialTY;
        int ewDistance = lightX - initialTX;
        while (nsDistance != 0 || ewDistance != 0){
            String isNS;
            String isEW;
            if (nsDistance > 0){
                isNS = "S";
                nsDistance --;
            }else if(nsDistance < 0){
                isNS = "N";
                nsDistance ++;
            }else {
                isNS = "";
            }

            if (ewDistance > 0){
                isEW = "E";
                ewDistance --;
            }else if (ewDistance < 0){
                isEW = "W";
                ewDistance++;
            }else {
                isEW = "";
            }
            queue.add(isEW + isNS);
        }


    }
}
