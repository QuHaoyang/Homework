package com.example.chapter3.homework;

import java.util.ArrayList;
import java.util.List;

public class Dataset {
    public static List<Friend> getData(int page){
        List<Friend> result = new ArrayList();
        if(page < 5){
            switch (page){
                case 0:
                    result.add(new Friend("张三","888888"));
                    result.add(new Friend("zj","12306"));
                    break;
                case 1:
                    result.add(new Friend("李四","666666"));
                    result.add(new Friend("zj","10086"));
                    break;
                case 2:
                    result.add(new Friend("想不出人名了","123456"));
                    break;
                case 3:
                    result.add(new Friend("zjuabc","134567"));
                    break;
                case 4:
                    result.add(new Friend("????","10000"));
                    break;
                default:
                    break;
            }
        }
        return result;
    }
}
