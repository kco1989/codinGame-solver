package com.kco;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt(); // the number of temperatures to analyse
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            list.add(in.nextInt());
        }

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");
        if (list.isEmpty()){
            System.out.println("0");
            return;
        }
        Optional<Integer> first = list.stream().sorted((o1, o2) -> {
            int sub = Math.abs(o1) - Math.abs(o2);
            if (sub == 0){
                return o1 - o2;
            }else {
                return sub;
            }
        } ).findFirst();
        System.out.println(first);
    }
}