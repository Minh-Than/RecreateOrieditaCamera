package model;

import components.CPLine;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

/**
 * A Singleton class to hold and handle crease pattern lines
 */
public class FoldLineSet {
    List<CPLine> list = new ArrayList<>();

    List<CPLine> eList = new ArrayList<>();
    List<CPLine> mList = new ArrayList<>();
    List<CPLine> vList = new ArrayList<>();
    List<CPLine> aList = new ArrayList<>();

    private static final FoldLineSet foldLineSet = new FoldLineSet();

    private FoldLineSet(){}

    public static FoldLineSet getInstance(){ return foldLineSet; }

    public List<CPLine> getList() { return list; }

    public List<CPLine> getEList() { return eList; }

    public List<CPLine> getMList() { return mList; }

    public List<CPLine> getVList() { return vList; }

    public List<CPLine> getAList() { return aList;}

    public void appendLine(CPLine cpLine){
        if(cpLine == null) return;

        list.add(cpLine);

        Color lineType = cpLine.getLineType();
        if (lineType.equals(Color.BLACK)) eList.add(cpLine);
        else if (lineType.equals(Color.RED)) mList.add(cpLine);
        else if (lineType.equals(Color.BLUE)) vList.add(cpLine);
        else if (lineType.equals(Color.CYAN)) aList.add(cpLine);
    }

    public void clearLists(){
        list.clear();
        eList.clear();
        mList.clear();
        vList.clear();
        aList.clear();
    }
}
