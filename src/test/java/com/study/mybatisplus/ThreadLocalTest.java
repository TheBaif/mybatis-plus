//package com.study.mybatisplus;
//
//import org.junit.jupiter.api.Test;
//
//public class ThreadLocalTest {
//    @Test
//    public void testThreadLocalSetAndGet(){
//        ThreadLocal tl=new ThreadLocal();
//
//        new Thread(()->{
//            tl.set("thebai");
//            System.out.println(Thread.currentThread().getName() + ": " + tl.get());
//            System.out.println(Thread.currentThread().getName() + ": " + tl.get());
//            System.out.println(Thread.currentThread().getName() + ": " + tl.get());
//        },"蓝色").start();
//
//        new Thread(()->{
//            tl.set("zkr");
//            System.out.println(Thread.currentThread().getName() + ": " + tl.get());
//            System.out.println(Thread.currentThread().getName() + ": " + tl.get());
//            System.out.println(Thread.currentThread().getName() + ": " + tl.get());
//        },"绿色").start();
//    }
//}
