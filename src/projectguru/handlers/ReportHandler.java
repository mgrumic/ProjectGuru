/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import projectguru.utils.ReportType;

/**
 *
 * @author medlan
 */
public interface ReportHandler {
    public void generateReport(ReportType rt);
    public void showReport();
}
