/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;
import projectguru.handlers.ReportHandler;
import java.sql.Connection;


import java.awt.Color;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
import net.sf.dynamicreports.report.builder.component.ComponentBuilders;
import projectguru.AccessManager;
import projectguru.entities.Project;
import projectguru.handlers.LoggedUser;
import projectguru.jpa.JpaAccessManager;
import projectguru.utils.ReportType;
/**
 *
 * @author medlan
 */
public class JpaReportHandler implements ReportHandler{
    private JasperReportBuilder report;
    private LoggedUser user;
    private Project project;
    
    public JpaReportHandler(LoggedUser user){
        this.user = user;
        this.project = null;
        this.report = null;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
    
    
    @Override
    public void showReport(){
        
        try{
            report.show();
        }catch(Exception ex){
            System.out.println("Message: " + ex.getMessage());
        }
    }
    
    @Override
    public void generateReport(ReportType type){
        Connection connection = null;
        try{
            EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            connection = em.unwrap(Connection.class);
            em.getTransaction().commit();
        }catch(Exception ex){
           System.out.println(ex.getMessage());
        }
       
        report = DynamicReports.report();
        
        StyleBuilder boldStyle = DynamicReports.stl.style().bold();
        StyleBuilder boldCenteredStyle = DynamicReports.stl.style(boldStyle)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        StyleBuilder columnTitleStyle  = DynamicReports.stl.style(boldCenteredStyle)
                                    .setBorder(DynamicReports.stl.pen1Point())
                                    .setBackgroundColor(Color.LIGHT_GRAY);
        TextColumnBuilder<String> usernameColumn = 
                Columns.column("Корисничко име", "username", DataTypes.stringType());
        
        TextColumnBuilder<String> passColumn = 
                Columns.column("Шифра", "password", DataTypes.stringType());
        
        
        TextColumnBuilder<String> nameColumn = 
                Columns.column("Име", "firstname", DataTypes.stringType());
                
        TextColumnBuilder<String> surnameColumn = 
                Columns.column("Презиме", "lastname", DataTypes.stringType());
                
        TextColumnBuilder<Integer> rowNumberColumn = 
                DynamicReports.col.reportRowNumberColumn("Бр.")
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
                            cmp.text(this.project.getName()).setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.LEFT),
                            cmp.text(type.getText()).setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.RIGHT))
                        .newRow()
                        .add(cmp.filler().setStyle(DynamicReports.stl.style().setTopBorder(DynamicReports.stl.pen2Point())).setFixedHeight(10)))
                .pageFooter(Components.pageXofY().setStyle(boldCenteredStyle))
                .setColumnTitleStyle(columnTitleStyle)
                .groupBy(passGroup)
                .setHighlightDetailEvenRows(Boolean.TRUE)
        .setDataSource("select * from user", connection);
    }
}
