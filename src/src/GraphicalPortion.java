package src;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;



public class GraphicalPortion extends JFrame {
	
	private String token;
	private DropBoxHandler handler;
	
	public GraphicalPortion(String applicationTitle, String chartTitle, String auth_token) throws DbxException{
		super(applicationTitle);
                token = auth_token;
		
		handler = new DropBoxHandler(token, applicationTitle);
		
                //handler.getFilesInDir("/", 1);
                System.out.println("created dbhandler");
                ArrayList<DbxEntry> data = new ArrayList<DbxEntry>();
                data = handler.getFilesInDir("/", 1);
                System.out.print("data size: ");
                System.out.println(data.size());
		PieDataset dataset = createDataset(data);
                System.out.println("done creating dataset");
		JFreeChart chart = createChart(dataset, chartTitle);
		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new java.awt.Dimension(800,600));
		this.setContentPane(panel);
		
	}
	private JFreeChart createChart(PieDataset dataset, String chartTitle) {
		JFreeChart chart = ChartFactory.createPieChart(chartTitle, dataset);
		return chart;
	}
	private PieDataset createDataset(ArrayList<DbxEntry> data) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		for(int i = 0; i < data.size(); i++){
                    if(data.get(i).isFolder())
                    {
                        try {
                            System.out.println("getting total folder size of folder: " + data.get(i).name);
							dataset.setValue(data.get(i).name, handler.getFolderSize(data.get(i).path));
			System.out.println("done getting total folder");			
                        } catch (DbxException e) {
                            System.out.println("caught");
							e.printStackTrace();
						}
                    }
                    else
                    {
                        System.out.println("getting file size");
                    	dataset.setValue(data.get(i).name, data.get(i).asFile().numBytes);
                    }
		}
		return dataset;
	}
	
}
