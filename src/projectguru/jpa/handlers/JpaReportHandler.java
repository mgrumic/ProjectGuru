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
import org.eclipse.persistence.sessions.Session;
import projectguru.AccessManager;
import projectguru.jpa.JpaAccessManager;
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
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/baza","student", "student");
        }catch(Exception ex){
           System.out.println(ex.getMessage());
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
        TextColumnBuilder<String> usernameColumn = 
                Columns.column("Username", "username", DataTypes.stringType());
        
        TextColumnBuilder<String> passColumn = 
                Columns.column("Password", "password", DataTypes.stringType());
        
        
        TextColumnBuilder<String> nameColumn = 
                Columns.column("Name", "firstname", DataTypes.stringType());
                
        TextColumnBuilder<String> surnameColumn = 
                Columns.column("Surname", "lastname", DataTypes.stringType());
                
        TextColumnBuilder<Integer> rowNumberColumn = 
                DynamicReports.col.reportRowNumberColumn("No.")
                .setFixedColumns(3)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);
        
        /*Bar3DChartBuilder itemChart = DynamicReports.cht.bar3DChart()
                .setTitle("Sales by item")
                .setCategory(itemColumn)
                .addSerie(DynamicReports.cht.serie(unitPriceColumn), DynamicReports.cht.serie(priceColumn));
        */        
        ColumnGroupBuilder passGroup = DynamicReports.grp.group(passColumn);
        passGroup.setPrintSubtotalsWhenExpression(
                DynamicReports.exp.printWhenGroupHasMoreThanOneRow(passGroup)
        );
        StyleBuilder titleStyle =  DynamicReports.stl.style(boldCenteredStyle)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setFontSize(15);
        
        ComponentBuilders cmp = DynamicReports.cmp;
        
        report.columns(
                rowNumberColumn,
                usernameColumn,
                //        .setHorizontalAlignment(HorizontalAlignment.LEFT),
                passColumn
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                nameColumn
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                surnameColumn
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                )
                .title(
                        cmp.horizontalList()
                        .add(cmp.image(getClass().getResourceAsStream("/projectguru/images/pg.png"))
                            .setFixedDimension(148, 82),
                            cmp.text("ProjectGURU").setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.LEFT),
                            cmp.text("Report").setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.RIGHT))
                        .newRow()
                        .add(cmp.filler().setStyle(DynamicReports.stl.style().setTopBorder(DynamicReports.stl.pen2Point())).setFixedHeight(10)))
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStyle))
                .setColumnTitleStyle(columnTitleStyle)
                .groupBy(passGroup)
                .setHighlightDetailEvenRows(Boolean.TRUE)
                .setDataSource(query, connection);
        try{
            report.show();
        }catch(DRException ex){
            System.out.println("Message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
