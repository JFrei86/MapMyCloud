package src;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ToolTipManager;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;



public class GraphicalPortion extends JFrame {
	private Object lastMouseOverKey = null;
	private String token;
	private DropBoxHandler handler;
        private String curDir;
        private int curDepth;
        private Stack<PieDataset> datahistory;
        private Stack<String> dirHistory;
        
        
	public GraphicalPortion(String applicationTitle, String chartTitle, String auth_token) throws DbxException, IOException, ClassNotFoundException{
		super(applicationTitle);
                token = auth_token;
		datahistory = new Stack<PieDataset>();
                ToolTipManager.sharedInstance().setInitialDelay(20);
		handler = new DropBoxHandler(token, applicationTitle);
		dirHistory = new Stack<String>();
                //handler.getFilesInDir("/", 1);
//                this.fr
                System.out.println("created dbhandler");
                ArrayList<DbxEntry> data = new ArrayList<DbxEntry>();
                data = handler.getFilesInDir("/", 1);
                curDir = "/";
                curDepth = 1;
                System.out.print("data size: ");
                System.out.println(data.size());
                PieDataset dataset = createDataset(data);
               // ((DefaultPieDataset) dataset).setValue("Empty Space", handler.getFreeSpace());
                System.out.println("done creating dataset");
                //JPieChart c = new JPieChart();
              //  c.setDataset(dataset);
                //c.setPreferredSize(new java.awt.Dimension(800,600));
                //this.setContentPane(c);
                //ChartPanel panel = c;
                
		JFreeChart chart = createChart(dataset, chartTitle);
                ChartPanel panel = new ChartPanel(chart);
		
                
               // panel.addch
                panel.setPopupMenu(null);
             
		panel.addChartMouseListener(new ChartMouseListener(){

                        @Override
			public void chartMouseClicked(ChartMouseEvent chartmouseevent)
                        {
                                
                               int button = chartmouseevent.getTrigger().getButton();
                               System.out.println("Button: " + button);
                               PiePlot pplot = (PiePlot) chartmouseevent.getChart().getPlot();
                                
                                ChartEntity c = chartmouseevent.getEntity();
                                //chartentity.
                                if (c != null && c instanceof PieSectionEntity)
                                {
                                    PieSectionEntity chartentity = (PieSectionEntity)c;
                                    //.get(chartentity.getSectionIndex()).
                                        //System.out.println("Mouse clicked: " + chartentity.toString());
                                       //System.out.println(chartentity.getSectionIndex());
                                    if(button == 3)
                                    {
                                        
                                     if(curDir == "/")   
                                     {
                                         return;
                                     }
                                     else
                                     {
                                        
                                       if(!dirHistory.isEmpty())
                                        { //gobackwards
                                          //String cdir = new String(curDir);
                                           curDir = dirHistory.pop();//datatemp.get(chartentity.getSectionIndex()).path;
                                           //dirHistory.push(new String(cdir));
                                           curDepth-=1;
                                           DefaultPieDataset piesettemp = (DefaultPieDataset) datahistory.pop();
                                          // datahistory.push(new DefaultPieDataset(pplot.getDataset()));
                                           pplot.setDataset(piesettemp);
                                         
                                         }
                                    }
                                       
                                    }
                                       ArrayList<DbxEntry> datatemp = null;     
                                    try {
                                        datatemp = handler.getFilesInDir(curDir, curDepth);
                                    } catch (DbxException ex) {
                                        Logger.getLogger(GraphicalPortion.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                       if(datatemp.get(chartentity.getSectionIndex()).isFolder())
                                       {
                                           //curDir = dirHistory.pop();//datatemp.get(chartentity.getSectionIndex()).path;
                                           dirHistory.push(new String(curDir));
                                           curDir = datatemp.get(chartentity.getSectionIndex()).path;
                                           curDepth+=1;
                                           datahistory.push(new DefaultPieDataset(pplot.getDataset()));
                                            try {
                                                datatemp = handler.getFilesInDir(curDir, curDepth);
                                            } catch (DbxException ex) {
                                                Logger.getLogger(GraphicalPortion.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            try {
                                                pplot.setDataset(createDataset(datatemp));
                                            } catch (DbxException ex) {
                                                Logger.getLogger(GraphicalPortion.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                           // System.out.println("done with refresh need to actually refresh");
                                      
                                       }
                                }
                        }   
                                
                        
                         @Override
                         public void chartMouseMoved(ChartMouseEvent event)
                        {
                            ChartEntity entity = event.getEntity();
                            if (entity != null && entity instanceof PieSectionEntity)
                            {
                                PieSectionEntity e = (PieSectionEntity) entity;
                                    String key = (String) e.getSectionKey();//.getCategory();
                                  //  System.out.println("key: " + key);
                                    if (lastMouseOverKey != key)
                                    {	// Used to only fire mouseOver on mouseEnter events, not mouseMoved
                                            lastMouseOverKey = key;
                                            StandardPieToolTipGenerator tgen = new StandardPieToolTipGenerator();
      
                                            tgen.generateToolTip(e.getDataset(), key);
                                            //e.setToolTipText(key);
                                    }
                            }
                            else
                                    lastMouseOverKey = null;
                        }
                       		
		});
              
                
                
		panel.setPreferredSize(new java.awt.Dimension(800,600));
		this.setContentPane(panel);
	}
       
            
         
	private JFreeChart createChart(PieDataset dataset, String chartTitle)
        {
             JFreeChart chart = ChartFactory.createPieChart3D(chartTitle, dataset, false, true, false);
            
		/*JFreeChart chart = ChartFactory.createPieChart(chartTitle,          // chart title
	            dataset,                // data
	            true,                   // include legend
	            true,
	            false);*/

	        PiePlot3D plot = (PiePlot3D) chart.getPlot();
                plot.setDepthFactor(.3);
                plot.setLabelGenerator(null);
               
	        plot.setBackgroundPaint(Color.white);
	        //plot.setForegroundAlpha(0.5f);
	        //plot.setDirection(Rotation.CLOCKWISE);
	        return chart;
	}
	private PieDataset createDataset(ArrayList<DbxEntry> data) throws DbxException {
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		for(int i = 0; i < data.size(); i++){
                    System.out.println("name: " + data.get(i).name + " " + i);
                    
                    if(data.get(i).isFolder())
                    {
                        System.out.println("FOLDER SIZE: " + handler.folderHash.get(data.get(i).path));
                       dataset.setValue(data.get(i).name,handler.folderHash.get(data.get(i).path));///handler.getQuota()));//should be max efficeny
                    }
                    else
                    {
                        System.out.println("getting file size");
                    	dataset.setValue(data.get(i).name, (data.get(i).asFile().numBytes));///handler.getQuota());
                    }
		}
               // dataset.ad
                //dataset.addChangeListener((DatasetChangeListener) this);
                System.out.println("done with dataset");
		return dataset;
	}
	
}
