/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;
import projectguru.handlers.ReportHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import java.awt.Color;
import net.sf.dynamicreports.report.builder.column.PercentageColumnBuilder;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.group.ColumnGroupBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilders;
/**
 *
 * @author medlan
 */
public class JpaReportHandler implements ReportHandler{
    
    @Override
    public void generateReport(String query){
        Connection connection = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dynamicreports_test","root", "");
        }catch(SQLException ex){
            ex.printStackTrace();
            return;
        }catch(ClassNotFoundException ex){
            ex.printStackTrace();
            return;
        }
       
        JasperReportBuilder report = DynamicReports.report();
        
        StyleBuilder leftAligned = DynamicReports.stl.style().setAlignment(
                HorizontalAlignment.LEFT, VerticalAlignment.TOP);
        StyleBuilder boldStyle = DynamicReports.stl.style().bold();
        StyleBuilder boldCenteredStyle = DynamicReports.stl.style(boldStyle)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        StyleBuilder columnTitleStyle  = DynamicReports.stl.style(boldCenteredStyle)
                                    .setBorder(DynamicReports.stl.pen1Point())
                                    .setBackgroundColor(Color.LIGHT_GRAY);
        TextColumnBuilder<java.math.BigDecimal> unitPriceColumn = 
                Columns.column("Unit price", "price", DataTypes.bigDecimalType());
        
        TextColumnBuilder<java.lang.Integer> quantityColumn = 
                Columns.column("Quantity", "quantity", DataTypes.integerType());
        
        TextColumnBuilder<java.math.BigDecimal> priceColumn = 
                unitPriceColumn.multiply(quantityColumn).setTitle("Price");
        
        TextColumnBuilder<String> itemColumn = 
                Columns.column("Item", "item", DataTypes.stringType());
                
        TextColumnBuilder<Integer> rowNumberColumn = 
                DynamicReports.col.reportRowNumberColumn("No.")
                .setFixedColumns(3)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);
        
        PercentageColumnBuilder pricePercColumn = Columns.percentageColumn("Price %", priceColumn)
                .setHorizontalAlignment(HorizontalAlignment.LEFT);
        
        Bar3DChartBuilder itemChart = DynamicReports.cht.bar3DChart()
                .setTitle("Sales by item")
                .setCategory(itemColumn)
                .addSerie(DynamicReports.cht.serie(unitPriceColumn), DynamicReports.cht.serie(priceColumn));
        
        ColumnGroupBuilder itemGroup = DynamicReports.grp.group(itemColumn);
        itemGroup.setPrintSubtotalsWhenExpression(
                DynamicReports.exp.printWhenGroupHasMoreThanOneRow(itemGroup)
        );
        StyleBuilder titleStyle =  DynamicReports.stl.style(boldCenteredStyle)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(15);
        
        ComponentBuilders cmp = DynamicReports.cmp;
        
        report.columns(
                rowNumberColumn,
                itemColumn,
                //        .setHorizontalAlignment(HorizontalAlignment.LEFT),
                quantityColumn
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                unitPriceColumn
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                priceColumn
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                pricePercColumn
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                )
                .title(
                        cmp.horizontalList()
                        .add(cmp.image(new Object().getClass().getResourceAsStream("../../guru-logo.png"))
                            .setFixedDimension(64, 64), 
                            cmp.text("ProjectGURU").setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.LEFT),
                            cmp.text("Report").setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.RIGHT))
                        .newRow()
                        .add(cmp.filler().setStyle(DynamicReports.stl.style().setTopBorder(DynamicReports.stl.pen2Point())).setFixedHeight(10)))
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStyle))
                .subtotalsAtSummary(DynamicReports.sbt.sum(unitPriceColumn).setStyle(boldStyle), DynamicReports.sbt.sum(priceColumn).setStyle(boldStyle))
                .subtotalsAtFirstGroupFooter(DynamicReports.sbt.sum(unitPriceColumn).setStyle(boldStyle), DynamicReports.sbt.sum(priceColumn).setStyle(boldStyle))
                .setColumnTitleStyle(columnTitleStyle)
                .groupBy(itemGroup)
                .setHighlightDetailEvenRows(Boolean.TRUE)
                .summary(itemChart)
                .setDataSource("select item, quantity, price from test_table", connection);
        try{
            report.show();
        }catch(DRException ex){
            ex.printStackTrace();
        }
    }
}
