package com.rd.util;

import java.util.*;

public class DiceUtil {
    /**
     * 根据权值随机id
     *
     * @param list 元素集合
     * @return
     */
    public static Ele dice(List<Ele> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int sum = 0;
        for (Ele element : list) {
            sum += element.getChance();
        }
        Collections.sort(list, new Comparator<Ele>() {

            @Override
            public int compare(Ele o1, Ele o2) {
                if (o1.chance > o2.chance) return 1;
                if (o1.chance < o2.chance) return -1;
                return 0;
            }
        });
        Random random = new Random();
        int n = random.nextInt(sum);
        int m = 0;
        for (Ele element : list) {
            if (m <= n && n < m + element.getChance()) {
                return element;
            }
            m += element.getChance();
        }
        return null;
    }

    /**
     * 不重复随机
     *
     * @param list     所有元素
     * @param received 已领取元素
     * @return
     */
    public static Ele noRepeatDice(List<Ele> list, List<Integer> received) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (received == null) {
            received = new ArrayList<>();
        }
        int sum = 0;
        Iterator<Ele> iter = list.iterator();
        while (iter.hasNext()) {
            Ele j = iter.next();
            for (Integer i : received) {
                if (i.equals(j.getId())) {
                    iter.remove();
                }
            }
        }
        for (Ele ele : list) {
            sum += ele.getChance();
        }
        Collections.sort(list, new Comparator<Ele>() {

            @Override
            public int compare(Ele o1, Ele o2) {
                if (o1.chance > o2.chance) return 1;
                if (o1.chance < o2.chance) return -1;
                return 0;
            }
        });
        Random random = new Random();
        int n = random.nextInt(sum);
        int m = 0;
        for (Ele element : list) {
            if (m <= n && n < m + element.getChance()) {
                return element;
            }
            m += element.getChance();
        }
        return null;
    }

    public static void main(String[] args) {
        List<Ele> list = new ArrayList<>();
        Ele ele = new Ele(5, 10000);
        Ele ele1 = new Ele(8, 10000);
        Ele ele2 = new Ele(10, 10000);
        Ele ele3 = new Ele(12, 10000);
        Ele ele4 = new Ele(16, 1);
        Ele ele5 = new Ele(16, 100);
        list.add(ele);
        list.add(ele1);
        list.add(ele2);
        list.add(ele3);
        list.add(ele4);
        list.add(ele5);
        List<Integer> list1 = new ArrayList<>();
        list1.add(5);
        list1.add(9);
        list1.add(12);
        list1.add(8);
        list1.add(10);
        Ele ran = noRepeatDice(list, list1);
        System.out.println(ran.id);
    }

    /**
     * @param list
     * @return
     */
    public static int dice2Int(Collection<Integer> list) {
        if (list == null || list.isEmpty()) {
            return -1;
        }
        int sum = list.size();
        Random random = new Random();
        int i = random.nextInt(sum);
        return new ArrayList<>(list).get(i);
    }

    public static int getIntByOrder(int num, Collection<Integer> list) {
        if (list == null || list.isEmpty()) {
            return -1;
        }
        int sum = list.size();
        if (num <= 0) return getListMin(list);
        return num % sum + 1;
    }

    public static int getListMin(Collection<Integer> list) {
        int min = Integer.MAX_VALUE;
        for (Integer i : list) {
            if (i < min) min = i;
        }
        return min;
    }

    public static int[] getSumNum(List<Integer> list, int sum) {
        Collections.shuffle(list);
        if (list == null || list.isEmpty()) return new int[]{0, 0};
        int[] two = new int[2];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (list.get(i) + list.get(j) == sum) {
                    two[0] = list.get(i);
                    two[1] = list.get(j);
                }
            }
        }
        return two;
    }

    public static class Ele {
        int id;
        int chance;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getChance() {
            return chance;
        }

        public void setChance(int chance) {
            this.chance = chance;
        }

        public Ele(int id, int chance) {
            super();
            this.id = id;
            this.chance = chance;
        }
    }
}
