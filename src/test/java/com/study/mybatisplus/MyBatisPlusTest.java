//package com.study.mybatisplus;
//
//import com.study.mybatisplus.domain.ParentSign;
//import com.study.mybatisplus.mapper.ParentSignMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.*;
//
//@SpringBootTest
//public class MyBatisPlusTest {
//    @Autowired
//    private ParentSignMapper parentsignMapper;
//    @Test
//    public void testSelectList(){
//        List<ParentSign> list=parentsignMapper.selectList(null);
//        list.forEach(System.out::println);
//    }
//    @Test
//    public void testInsert(){
//        ParentSign parentSign=new ParentSign(14,"测试名称");
//        int result=parentsignMapper.insert(parentSign);
//        System.out.println("result:" + result);
//        System.out.println("id:" + parentSign.getId());
//
//    }
//    @Test
//    public void testDelete(){
////        int result=parentsignMapper.deleteById(14);
////        System.out.println("result:" + result);
////        Map<String,Object> map=new HashMap<>();
////        map.put("name","测试名称");
////        int result=parentsignMapper.deleteByMap(map);
////        System.out.println("result:" + result);
//        Collection<Integer> idList=Arrays.asList(13,14);
//        int result=parentsignMapper.deleteByIds(idList);
//        System.out.println("result:" + result);
//    }
//    @Test
//    public void testUpdate(){
//        ParentSign parentSign=new ParentSign(14,"测试名称2");
//        int result=parentsignMapper.updateById(parentSign);
//        System.out.println("result:" + result);
//    }
//    @Test
//    public void testSelect(){
////        parentSign parentSign=parentsignMapper.selectById(14);
////        System.out.println(parentSign);
////        List<Integer> idList=Arrays.asList(13,14);
////        List<parentSign> parentSignList=parentsignMapper.selectBatchIds(idList);
////        parentSignList.forEach(System.out::println);
//          Map<String,Object> map=new HashMap<>();
//          map.put("name","测试名称2");
//        List<ParentSign> parentSignList=parentsignMapper.selectByMap(map);
//         parentSignList.forEach(System.out::println);
//    }
//}
