package com.collapsar.springboot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenyong6 on 2017/9/13.
 */
public class HashcodeTest {

    static class A{
        int a = 0;
        String str = null;

        public A(int a, String str){
            this.a = a;
            this.str = str;
        }

        @Override
        public int hashCode() {
            return a;
        }
    }

    public static void main(String[] args){
        A a1 = new A(1, "aa");
        A a2 = new A(1, "aa");

        Set<A> set = new HashSet<A>();
        set.add(a1);
        set.add(a1);
        System.out.println(set.size());

//        Map<String, A> map = new HashMap<String, A>();
//        map.put(a1)
//        System.out.println(set.);
    }
}
