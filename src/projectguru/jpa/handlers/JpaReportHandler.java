/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;
import projectguru.handlers.ReportHandler;
import java.sql.Connection;


import java.awt.Color;
import java.math.BigDecimal;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.column.PercentageColumnBuilder;
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
            report.show(false);
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
       
        report = DynamicReports.report().floatColumnFooter();
        fillColumns(type);
        StyleBuilder boldStyle = DynamicReports.stl.style().bold();
        StyleBuilder boldCenteredStyle = DynamicReports.stl.style(boldStyle)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);
        StyleBuilder columnTitleStyle  = DynamicReports.stl.style(boldCenteredStyle)
                                    .setBorder(DynamicReports.stl.pen1Point())
                                    .setBackgroundColor(Color.LIGHT_GRAY);    
            StyleBuilder titleStyle =  DynamicReports.stl.style(boldCenteredStyle)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setFontSize(15);

            ComponentBuilders cmp = DynamicReports.cmp;

            
            
            report.title(
                cmp.horizontalList()
                .add(cmp.image(getClass().getResourceAsStream("/projectguru/images/pg.png"))
                    .setFixedDimension(148, 82),
                    cmp.text(type.getText()).setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.CENTER),
                    cmp.text(this.project.getName()).setStyle(titleStyle).setHorizontalAlignment(HorizontalAlignment.RIGHT)
                    )
                .newRow()
                .add(cmp.filler().setStyle(DynamicReports.stl.style().setTopBorder(DynamicReports.stl.pen2Point())).setFixedHeight(10))
            )
            .pageFooter(Components.pageNumber().setStyle(boldCenteredStyle), Components.currentDate().setStyle(boldStyle).setHorizontalAlignment(HorizontalAlignment.RIGHT))
            .setColumnTitleStyle(columnTitleStyle)
            .setHighlightDetailEvenRows(Boolean.TRUE);
        String query = "";
        if(type == ReportType.FINANSIJSKI_PREGLED_PRIHODA_REPORT || type == ReportType.FINANSIJSKI_PREGLED_RASHODA_REPORT)
            query = type.getQuerry()+ " where ID = " + project.getId();
        report.setDataSource(query, connection);
    }
    
    private void fillColumns(ReportType type){
        StyleBuilder boldStyle = DynamicReports.stl.style().bold();
        if(type == ReportType.FINANSIJSKI_PREGLED_PRIHODA_REPORT || type == ReportType.FINANSIJSKI_PREGLED_RASHODA_REPORT){
            
                TextColumnBuilder<BigDecimal> ammountColumn = 
                        Columns.column("Количина новца", "MoneyAmmount", DataTypes.bigDecimalType());


                TextColumnBuilder<String> currencyColumn = 
                        Columns.column("Валута", "Currency", DataTypes.stringType());

                TextColumnBuilder<String> descriptionColumn = 
                        Columns.column("Опис", "Description", DataTypes.stringType());
                
                TextColumnBuilder<Integer> rowNumberColumn = 
                        DynamicReports.col.reportRowNumberColumn("Бр.")
                        .setFixedColumns(3)
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT);
                
                PercentageColumnBuilder pricePercColumn = Columns.percentageColumn("Количина [%]", ammountColumn)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);
                
            report.columns(
                        rowNumberColumn,
                        descriptionColumn,
                        //        .setHorizontalAlignment(HorizontalAlignment.LEFT),
                        ammountColumn
                                .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                        currencyColumn
                                .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                     pricePercColumn
                                .setHorizontalAlignment(HorizontalAlignment.RIGHT)        
            );
            report.subtotalsAtSummary(DynamicReports.sbt.sum(ammountColumn).setStyle(boldStyle.setForegroundColor(
                    type == ReportType.FINANSIJSKI_PREGLED_PRIHODA_REPORT? Color.GREEN : Color.RED)));
        }
    }
}
