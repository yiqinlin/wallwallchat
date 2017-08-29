package com.stark.wallwallchat.SortListView;

import com.stark.wallwallchat.bean.SortModel;

import java.util.Comparator;

/**
 * Created by asus on 2017/7/11.
 */
public class PinyinComparator implements Comparator<SortModel>{
    @Override
    public int compare(SortModel lhs, SortModel rhs) {
        if (rhs.getSortLetters().equals("#")) {
            return -1;
        } else if (lhs.getSortLetters().equals("#")) {
            return 1;
        } else {
            return lhs.getSortLetters().compareTo(rhs.getSortLetters());
        }
    }
}
