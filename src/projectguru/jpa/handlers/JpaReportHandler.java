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
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;
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
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import projectguru.AccessManager;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.handlers.LoggedUser;
import projectguru.jpa.JpaAccessManager;
import projectguru.tasktree.TaskNode;
import projectguru.tasktree.TaskTree;
import projectguru.utils.ProjectReportBean;
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
       
        report = DynamicReports.report();
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
        String query = type.getQuerry();
        if(type == ReportType.FINANSIJSKI_PREGLED_PRIHODA_REPORT || type == ReportType.FINANSIJSKI_PREGLED_RASHODA_REPORT){
            query = query + " where ProjectID = " + project.getId();
            report.setDataSource(query, connection);
        }else if(type == ReportType.PREGLED_AKTIVNOSTI_REPORT){
            query = query + " where IDProject = " + project.getId();
            report.setDataSource(query, connection);
        }else if(type == ReportType.STANJE_PROJEKTA_REPORT){
            ArrayList<ProjectReportBean> list = new ArrayList<ProjectReportBean>();
            fillList(list);
            JRBeanCollectionDataSource bean = new JRBeanCollectionDataSource(list);
            report.setDataSource(bean);
        }
    }
    
    private void fillColumns(ReportType type){
        StyleBuilder boldStyle = DynamicReports.stl.style().bold();
        StyleBuilder boldStyleRed = DynamicReports.stl.style().bold().setForegroundColor(Color.RED);
        StyleBuilder boldStyleGreen = DynamicReports.stl.style().bold().setForegroundColor(Color.GREEN);
        StyleBuilder boldStyleOrange = DynamicReports.stl.style().bold().setForegroundColor(Color.ORANGE);
        
        if(type == ReportType.FINANSIJSKI_PREGLED_PRIHODA_REPORT || type == ReportType.FINANSIJSKI_PREGLED_RASHODA_REPORT){
            
                TextColumnBuilder<BigDecimal> ammountColumn = 
                        Columns.column("Количина новца", "MoneyAmmount", DataTypes.bigDecimalType());


                TextColumnBuilder<String> currencyColumn = 
                        Columns.column("Валута", "Currency", DataTypes.stringType());

                TextColumnBuilder<String> descriptionColumn = 
                        Columns.column("Опис", "Description", DataTypes.stringType());
                
                TextColumnBuilder<String> idColumn = 
                        Columns.column("ИД", "ID", DataTypes.stringType());
                
                TextColumnBuilder<Integer> rowNumberColumn = 
                        DynamicReports.col.reportRowNumberColumn("Бр.")
                        .setFixedColumns(3)
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT);
                
                PercentageColumnBuilder pricePercColumn = Columns.percentageColumn("Количина [%]", ammountColumn)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);
                
                Bar3DChartBuilder ammountChart = DynamicReports.cht.bar3DChart()
                .setTitle(type==ReportType.FINANSIJSKI_PREGLED_PRIHODA_REPORT ? "Приходи по ИД" : "Расходи по ИД")
                .setCategory(idColumn)
                .addSerie(DynamicReports.cht.serie(ammountColumn));
                
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
            ).summary(ammountChart);
            report.subtotalsAtSummary(DynamicReports.sbt.sum(ammountColumn).setStyle(boldStyle.setForegroundColor(
                    type == ReportType.FINANSIJSKI_PREGLED_PRIHODA_REPORT? Color.GREEN : Color.RED)));
        } else if(type == ReportType.PREGLED_AKTIVNOSTI_REPORT){
            TextColumnBuilder<String> idColumn = 
                        Columns.column("ИД", "ID", DataTypes.stringType());
            
            TextColumnBuilder<String> nameColumn = 
                        Columns.column("Назив", "Name", DataTypes.stringType());
            
            TextColumnBuilder<String> dateColumn = 
                        Columns.column("Датум креирања", "CreationDate", DataTypes.stringType());
            
            TextColumnBuilder<String> remarkColumn = 
                        Columns.column("Коментар", "Remark", DataTypes.stringType());
            
            TextColumnBuilder<String> descriptionColumn = 
                        Columns.column("Опис", "Description", DataTypes.stringType());
            
            TextColumnBuilder<String> usernameColumn = 
                        Columns.column("Корисник", "Username", DataTypes.stringType());
            
            TextColumnBuilder<String> taskNameColumn = 
                        Columns.column("Назив задатка", "ZadatakName", DataTypes.stringType());
        
            report.columns(
                        idColumn,
                        nameColumn,
                        //        .setHorizontalAlignment(HorizontalAlignment.LEFT),
                        dateColumn
                                .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                        remarkColumn
                                .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                     descriptionColumn
                                .setHorizontalAlignment(HorizontalAlignment.RIGHT),
                     usernameColumn,
                     taskNameColumn
            );
        }else if(type == ReportType.STANJE_PROJEKTA_REPORT){
            TextColumnBuilder<Integer> percentageColumn = 
                        Columns.column("Проценат [%]", "percentage", DataTypes.integerType());
            
            TextColumnBuilder<String> taskNameColumn = 
                        Columns.column("Назив задатка", "taskName", DataTypes.stringType());
            
            TextColumnBuilder<Integer> manHoursColumn = 
                        Columns.column("Предвиђено човјек часова", "assumedManHours", DataTypes.integerType());
            
            TextColumnBuilder<Double> manHoursDoneColumn = 
                        Columns.column("Потрошено човјек часова", "doneManHours", DataTypes.doubleType());
            
            TextColumnBuilder<String> startDateColumn = 
                        Columns.column("Датум почетка", "startDate", DataTypes.stringType());
            
            TextColumnBuilder<String> endDateColumn = 
                        Columns.column("Датум завршетка", "endDate", DataTypes.stringType());
            
            TextColumnBuilder<String> deadlineDateColumn = 
                        Columns.column("Крајњи рок", "deаdlineDate", DataTypes.stringType());
            
            TextColumnBuilder<Integer> rowNumberColumn = 
                        DynamicReports.col.reportRowNumberColumn("Бр.")
                        .setFixedColumns(3)
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT);
            
            Bar3DChartBuilder awesomeChart = DynamicReports.cht.bar3DChart()
                .setTitle("Однос урађених/предвиђених човјек сати")
                .setCategory(taskNameColumn)
                .addSerie(DynamicReports.cht.serie(manHoursColumn), DynamicReports.cht.serie(manHoursDoneColumn));
            
            report.columns(rowNumberColumn,
                           percentageColumn, 
                           taskNameColumn.setHorizontalAlignment(HorizontalAlignment.RIGHT), 
                           manHoursColumn, 
                           manHoursDoneColumn,
                           startDateColumn.setHorizontalAlignment(HorizontalAlignment.RIGHT)
                                          .setStyle(boldStyleGreen), 
                           endDateColumn.setHorizontalAlignment(HorizontalAlignment.RIGHT)
                                        .setStyle(boldStyleOrange), 
                           deadlineDateColumn.setHorizontalAlignment(HorizontalAlignment.RIGHT)
                                             .setStyle(boldStyleRed)
            ).summary(awesomeChart);
        }
    }
    private void fillList(ArrayList<ProjectReportBean> list) {
        Task rootTask = project.getIDRootTask();
                if (rootTask != null) {
                    
                    TaskTree tree = user.getTaskHandler().getTaskTree(rootTask);
                    list.add(generateBeanFromTaskNode(tree.getRoot()));
                    recursiveTaskTreeLoad(tree.getRoot(), list);
                }
    }

    private void recursiveTaskTreeLoad(TaskNode task, ArrayList<ProjectReportBean> list) {
        List<TaskNode> children = task.getChildren();
        children.stream().forEach((child) -> {
            list.add(generateBeanFromTaskNode(child));
            recursiveTaskTreeLoad(child, list);
        });
    }
    
    private ProjectReportBean generateBeanFromTaskNode(TaskNode node){
        Task task = node.getTask();
        return new ProjectReportBean(task.getName(), (int)(100*node.getPartDone()), task.getAssumedManHours(),
                                     task.getStartDate(), task.getEndDate(), task.getDeadline(), node.getWorkedManHours());
    }
}
