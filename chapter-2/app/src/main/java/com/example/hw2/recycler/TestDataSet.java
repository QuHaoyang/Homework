package com.example.hw2.recycler;

import java.util.ArrayList;
import java.util.List;

public class TestDataSet {

    public static List<TestData> getData() {
        List<TestData> result = new ArrayList();
        result.add(new TestData("浙江大学小学期", "100"));
        result.add(new TestData("现在的点击效果跟之前不一样了", "98"));
        result.add(new TestData("长按也是", "95"));
        result.add(new TestData("我随便改掉了", "92"));
        result.add(new TestData("我编不出来热搜了", "88"));
        result.add(new TestData("真编不下去了", "86"));
        result.add(new TestData("没东西了", "85"));
        result.add(new TestData("不会有这么多东西搜的", "83"));
        result.add(new TestData("要不算了吧", "80"));
        result.add(new TestData("不要热搜了", "79"));
        result.add(new TestData("随便是什么都行", "77"));
        return result;
    }

}
