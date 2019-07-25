package com.tigerit.exam;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tigerit.exam.IO.*;

/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */
public class Solution implements Runnable {
    @Override
    public void run() {
        int testCase = readLineAsInteger();
        if(testCase >= 1 && testCase <= 10) {
            int v = 0;
            while (v < testCase) {
                v++;
                int tableNo = readLineAsInteger();

                String[] tableNameList = new String[tableNo];
                String[][] allTableColumnList = new String[tableNo][100];
                String[][][] tableData = new String[tableNo][100][100];


                int COLUMN_NO = 0;
                int ROW_NO = 0;

                if(tableNo < 2 || tableNo > 10) return;

                for (int i = 0; i < tableNo; i++) {

                    tableNameList[i] = readLine();

                    String rowColumn[] = readLine().split(" ");

                    COLUMN_NO = Integer.parseInt(rowColumn[0]);
                    ROW_NO = Integer.parseInt(rowColumn[1]);

                    if(COLUMN_NO < 2 || COLUMN_NO > 100) return;

                    String tableColumns = readLine();

                    String[] allColumns = tableColumns.split(" ");

                    for (int k = 0; k < COLUMN_NO; k++) {
                        allTableColumnList[i][k] = allColumns[k];
                    }

                    String row;
                    for (int l = 0; l < ROW_NO; l++) {
                        row = readLine();
                        tableData[i][l] = row.split(" ");
                    }
                }

                Map<String, List<String>> tableWithColumnList = new HashMap<>();
                Map<String, List<List<String>>> tableDataList = new HashMap<>();

                for (int i = 0; i < tableNameList.length; i++) {
                    String tableName = tableNameList[i];
                    String[] tempAry = new String[COLUMN_NO];

                    for (int j = 0; j < COLUMN_NO; j++) {
                        tempAry[j] = allTableColumnList[i][j];
                    }
                    tableWithColumnList.put(tableName, Arrays.asList(tempAry));

                    List<List<String>> tableDataRows = new ArrayList<>();
                    for (int k = 0; k < ROW_NO; k++) {
                        String tempRowData[] = tableData[i][k];
                        String[] tempAry1 = new String[COLUMN_NO];
                        for (int l = 0; l < COLUMN_NO; l++) {
                            tempAry1[l] = tempRowData[l];
                        }
                        tableDataRows.add(Arrays.asList(tempAry1));
                    }
                    tableDataList.put(tableName, tableDataRows);
                }

                int sqlTestCase = readLineAsInteger();

                String sqlQueries[] = new String[sqlTestCase];
                for (int b = 0; b < sqlTestCase; b++) {
                    StringBuffer sb = new StringBuffer();
                    for(int c = 0; c<4; c++){
                        sb.append(readLine());
                    }
                    readLine();
                    sqlQueries[b] = sb.toString();
                }

                printLine("Test: " + v);
                for (int a = 0; a < sqlTestCase; a++) {
                    String sql = sqlQueries[a];
                    String[] inputTables = new String[2];
                    String[] inputTablesAlias = new String[2];

                    String[] inputTablesAliasFromOnKeyword = new String[2];
                    String[] inputTablesColumn = new String[2];

                    Pattern p1 = Pattern.compile("(from|join)\\s+(\\w+)\\s+(\\w+)\\s+", Pattern.CASE_INSENSITIVE);
                    Pattern p2 = Pattern.compile("(on)\\s+(\\w+).(\\w+)\\s*=\\s*(\\w+).(\\w+)", Pattern.CASE_INSENSITIVE);

                    //table alias is present or not
                    Pattern p3 = Pattern.compile("((from)\\s+(\\w+)\\s+(join))", Pattern.CASE_INSENSITIVE);
                    //if present this will work
                    Pattern p4 = Pattern.compile("((from|join)\\s+(\\w+))", Pattern.CASE_INSENSITIVE);

                    //finding selected column from sql statement
                    Pattern p5 = Pattern.compile("(select)\\s+((\\w+).(\\w+)(,)?\\s?)+(from)", Pattern.CASE_INSENSITIVE);


                    Matcher m = p1.matcher(sql);
                    Matcher n = p2.matcher(sql);
                    Matcher o = p3.matcher(sql);
                    Matcher q = p4.matcher(sql);
                    Matcher r = p5.matcher(sql);


                    boolean tableAliasStatus = o.find();

                    if (!tableAliasStatus) {
                        int t = 0;
                        while (m.find()) {
                            String[] findings = m.group(0).split(" ");
                            inputTables[t] = findings[1];
                            inputTablesAlias[t] = findings[2];
                            t++;
                        }
                    }

                    if (tableAliasStatus) {
                        int t = 0;
                        while (q.find()) {
                            String[] findings = q.group(0).split(" ");
                            inputTables[t] = findings[1];
                            inputTablesAlias[t] = findings[1];
                            t++;
                        }
                    }

                    boolean areColumnSpecifiedInSelectClause = r.find();
                    String[] selectedColumn = {};
                    if (areColumnSpecifiedInSelectClause) {
                        String[] selectedClause = r.group(0).split("(\\s|,)");
                        selectedColumn = Arrays.copyOfRange(selectedClause, 1, selectedClause.length - 1);
                    }


                    if (n.find()) {
                        //ON ta.a2 = tb.b2
                        String findings[] = n.group(0).split(" ", 2);
                        String findingsAfterSplitWithEqualSign[] = findings[1].split(" ");

                        String firstPart = findingsAfterSplitWithEqualSign[0].trim();
                        String secondPart = findingsAfterSplitWithEqualSign[2].trim();

                        String firstPartTokens[] = firstPart.split("\\.");
                        String secondPartTokens[] = secondPart.split("\\.");


                        if (firstPartTokens[0].equals(inputTablesAlias[0])) {
                            inputTablesAliasFromOnKeyword[0] = firstPartTokens[0];
                            inputTablesColumn[0] = firstPartTokens[1];
                        } else {
                            inputTablesAliasFromOnKeyword[1] = firstPartTokens[0];
                            inputTablesColumn[1] = firstPartTokens[1];
                        }

                        if (secondPartTokens[0].equals(inputTablesAlias[1])) {
                            inputTablesAliasFromOnKeyword[1] = secondPartTokens[0];
                            inputTablesColumn[1] = secondPartTokens[1];
                        } else {
                            inputTablesAliasFromOnKeyword[0] = secondPartTokens[0];
                            inputTablesColumn[0] = secondPartTokens[1];
                        }
                    }

                    //get table from select statement
                    String tableOne = inputTables[0];
                    String tableTwo = inputTables[1];

                    //get table ON clause column here
                    String columnForTableOne = inputTablesColumn[0]; //a1
                    String columnForTableTwo = inputTablesColumn[1]; //b1

                    //get table column name
                    List<String> tableOneColumn = tableWithColumnList.get(tableOne);
                    List<String> tableTwoColumn = tableWithColumnList.get(tableTwo);

                    //get column index
                    int tableOneColumnIndex = tableOneColumn.indexOf(columnForTableOne);
                    int tableTowColumnIndex = tableTwoColumn.indexOf(columnForTableTwo);

                    //get table data
                    List<List<String>> tableOneData = tableDataList.get(tableOne);
                    List<List<String>> tableTwoData = tableDataList.get(tableTwo);


                    Map<String, List<Integer>> selectedColumnAfterFilter = new HashMap<>();
                    List<String> columnSequence = new ArrayList<>();
                    if (areColumnSpecifiedInSelectClause) {
                        //alias for selected tables
                        String aliasNameForTableOne = inputTablesAliasFromOnKeyword[0];
                        String aliasNameForTableTwo = inputTablesAliasFromOnKeyword[1];

                        List<Integer> columnForOne = new ArrayList<>();
                        List<Integer> columnForTwo = new ArrayList<>();

                        List<String> tableOneColumnData = tableWithColumnList.get(tableOne);
                        List<String> tableTwoColumnData = tableWithColumnList.get(tableTwo);
                        for (String column : selectedColumn) {
                            if (!column.equals("")) {
                                String columnName = column.split("\\.")[1].replaceAll(",$", "");
                                if (column.startsWith(aliasNameForTableOne)) {
                                    Integer columnIndex = tableOneColumnData.indexOf(columnName);
                                    columnForOne.add(columnIndex);
                                } else if (column.startsWith(aliasNameForTableTwo)) {
                                    Integer columnIndex = tableTwoColumnData.indexOf(columnName);
                                    columnForTwo.add(columnIndex);
                                }
                                columnSequence.add(columnName);
                            }
                        }
                        selectedColumnAfterFilter.put(tableOne, columnForOne);
                        selectedColumnAfterFilter.put(tableTwo, columnForTwo);
                    }

                    StringBuilder sb = new StringBuilder();
                    if (selectedColumnAfterFilter.size() != 0) {
                        for (String s : columnSequence) {
                            sb.append(s);
                            sb.append(" ");
                        }
                    } else {
                        for (String s : tableOneColumn) {
                            sb.append(s);
                            sb.append(" ");
                        }
                        for (String s : tableTwoColumn) {
                            sb.append(s);
                            sb.append(" ");
                        }
                    }

                    printLine(sb.toString());
                    for (List<String> tableOneDataRow : tableOneData) {
                        String firstTableColumnData = tableOneDataRow.get(tableOneColumnIndex);
                        for (List<String> tableTwoDataRow : tableTwoData) {
                            String secondTableColumnData = tableTwoDataRow.get(tableTowColumnIndex);
                            if (firstTableColumnData.equals(secondTableColumnData)) {
                                if (selectedColumnAfterFilter.size() == 0) {
                                    for (String cellData : tableOneDataRow) {
                                        System.out.print(cellData + " ");
                                    }

                                    for (String cellData : tableTwoDataRow) {
                                        System.out.print(cellData + " ");
                                    }
                                }
                                if (areColumnSpecifiedInSelectClause) {
                                    List<Integer> columnIndexesForTableOne = selectedColumnAfterFilter.get(tableOne);
                                    for (String cellData : tableOneDataRow) {
                                        Integer index = tableOneDataRow.indexOf(cellData);
                                        if (columnIndexesForTableOne.contains(index)) {
                                            System.out.print(cellData + " ");
                                        }
                                    }

                                    List<Integer> columnIndexesForTableTwo = selectedColumnAfterFilter.get(tableTwo);
                                    for (String cellData : tableTwoDataRow) {
                                        Integer index = tableTwoDataRow.indexOf(cellData);
                                        if (columnIndexesForTableTwo.contains(index)) {
                                            System.out.print(cellData + " ");
                                        }
                                    }
                                }
                                System.out.println();
                            }
                        }
                    }
                    System.out.println();
                }
            }
        }
    }
}
