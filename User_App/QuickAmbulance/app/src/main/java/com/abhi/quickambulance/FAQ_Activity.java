package com.abhi.quickambulance;

import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
/**
 * Created by Abhi on 17-01-2016.
 */
public class FAQ_Activity extends Activity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();


        listDataHeader.add("Question 1");
        List<String> q1 = new ArrayList<String>();
        q1.add("Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 ");

        
        listDataHeader.add("Question 2");
        List<String> q2 = new ArrayList<String>();
        q2.add("Answer2 [MessageQ] ProcessNewMessage: [XTWWAN-PE] unknown deliver target [OS-Agent]\n" +
                "01-17 04:26:30.138      371-371/? E/QCALOG﹕ [MessageQ] ProcessNewMessage: [XTWiFi-PE] unknown deliver target [OS-Agent]\n" +
                "01-17 04:26:32.308      371-371/? E/QCALOG﹕ [MessageQ] ProcessNewMessage: [XT-CS] unknown deliver target [OS-Agent]\n" +
                "01-17 04:26:32.308      371-371/? E/QCALOG﹕ [MessageQ] ");

        
        listDataHeader.add("Question 3");
        List<String> q3 = new ArrayList<String>();
        q3.add("Answer3 import java.util.ArrayList;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.List;\n" +
                "import android.app.Activity;\n" +
                "import android.os.Bundle;\n" +
                "import android.view.View;\n" +
                "import android.widget.ExpandableListView;\n" +
                "import android.widget.ExpandableListView.OnChildClickListener;\n" +
                "import android.widget.ExpandableListView.OnGroupClickListener;\n" +
                "import android.widget.ExpandableListView.OnGroupCollapseListener;\n" +
                "import android.widget.ExpandableListView.OnGroupExpandListener;\n" +
                "import android.widget.Toast;");

        
        listDataHeader.add("Question 4");
        List<String> q4 = new ArrayList<String>();
        q4.add("Answer4");

        
        listDataHeader.add("Question 5");
        List<String> q5 = new ArrayList<String>();
        q5.add("Answer5");

        listDataHeader.add("Question 6");
        List<String> q6 = new ArrayList<String>();
        q6.add("Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 Answer1 ");


        listDataHeader.add("Question 7");
        List<String> q7 = new ArrayList<String>();
        q7.add("Answer2 [MessageQ] ProcessNewMessage: [XTWWAN-PE] unknown deliver target [OS-Agent]\n" +
                "01-17 04:26:30.138      371-371/? E/QCALOG﹕ [MessageQ] ProcessNewMessage: [XTWiFi-PE] unknown deliver target [OS-Agent]\n" +
                "01-17 04:26:32.308      371-371/? E/QCALOG﹕ [MessageQ] ProcessNewMessage: [XT-CS] unknown deliver target [OS-Agent]\n" +
                "01-17 04:26:32.308      371-371/? E/QCALOG﹕ [MessageQ] ");


        listDataHeader.add("Question 8");
        List<String> q8 = new ArrayList<String>();
        q8.add("Answer3 import java.util.ArrayList;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.List;\n" +
                "import android.app.Activity;\n" +
                "import android.os.Bundle;\n" +
                "import android.view.View;\n" +
                "import android.widget.ExpandableListView;\n" +
                "import android.widget.ExpandableListView.OnChildClickListener;\n" +
                "import android.widget.ExpandableListView.OnGroupClickListener;\n" +
                "import android.widget.ExpandableListView.OnGroupCollapseListener;\n" +
                "import android.widget.ExpandableListView.OnGroupExpandListener;\n" +
                "import android.widget.Toast;");


        listDataHeader.add("Question 9");
        List<String> q9 = new ArrayList<String>();
        q9.add("Answer4");


        listDataHeader.add("Question 10");
        List<String> q10 = new ArrayList<String>();
        q10.add("Answer5");
        



        listDataChild.put(listDataHeader.get(0), q1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), q2); // Header, Child data
        listDataChild.put(listDataHeader.get(2), q3); // Header, Child data
        listDataChild.put(listDataHeader.get(3), q4); // Header, Child data
        listDataChild.put(listDataHeader.get(4), q5); // Header, Child data
        listDataChild.put(listDataHeader.get(5), q6); // Header, Child data
        listDataChild.put(listDataHeader.get(6), q7); // Header, Child data
        listDataChild.put(listDataHeader.get(7), q8); // Header, Child data
        listDataChild.put(listDataHeader.get(8), q9); // Header, Child data
        listDataChild.put(listDataHeader.get(9), q10); // Header, Child data
    }

}
