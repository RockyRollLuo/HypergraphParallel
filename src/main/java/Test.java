import util.ToolUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class Test {
    public static void fun1() {
        ArrayList<ArrayList<Integer>> edgeList = new ArrayList<>();

        ArrayList<Integer> list1 = new ArrayList<>();
        list1.add(1);
        list1.add(2);
        list1.add(3);

        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(4);
        list2.add(5);

        edgeList.add(list1);
        edgeList.add(list2);

        System.out.println("===1===");
        System.out.println(edgeList.toString());

        //这种方式无法删除成功
        ArrayList<Integer> list3 = new ArrayList<>();
        list3.add(5);
        list3.add(4);
        edgeList.remove(list3);

        //这种方式能删除成功
//        ArrayList<Integer> list4 = new ArrayList<>();
//        list4.add(4);
//        list4.add(5);
//        edgeList.remove(list4);

        //要想删除list中的list,就必须将每个edge中的node节点排序

        System.out.println("===2===");
        System.out.println(edgeList.toString());
    }

    public static void fun2() {
        HashMap<Integer, Integer> map = new HashMap<>();

        map.put(2, 1);
        map.put(1, 2);
        map.put(5, 3);
        map.put(7, 4);
        map.put(3, 5);

        System.out.println("===unsorted===");
        System.out.println(map.toString());

        map = (HashMap<Integer, Integer>) ToolUtils.sortMapByValue(map, 1);
        System.out.println("===sorted===");
        System.out.println(map.toString());
    }

    public static void fun3() {
        ArrayList<Integer> list1 = new ArrayList<>();
        list1.add(new Integer(1));
        list1.add(new Integer(2));
        list1.add(new Integer(3));

//        ArrayList<Integer> nodeList2 = nodeList1;
//        nodeList2.remove(new Integer(2));
//        System.out.println(nodeList1.toString());


        ArrayList<Integer> list3 = new ArrayList<>(list1);
        list3.remove(new Integer(2));
        System.out.println(list1.toString());
        System.out.println(list3.toString());

        ArrayList<ArrayList<Integer>> lists4 = new ArrayList<>();
        lists4.add(list1);
        lists4.add(list3);
        System.out.println(lists4.toString());

        ArrayList<ArrayList<Integer>> lists5 = new ArrayList<>(lists4);
        ArrayList<Integer> list6 = new ArrayList<>();
        list6.add(1);
        list6.add(3);

        lists5.remove(list6);

        System.out.println("lists4:"+lists4.toString());
        System.out.println("lists5:"+lists5.toString());

    }

    public static void main(String[] args) {
//        fun1();

//        fun2();
        fun3();

    }

}
