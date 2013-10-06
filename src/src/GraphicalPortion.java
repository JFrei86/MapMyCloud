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
	
	public GraphicalPortion(String applicationTitle, String chartTitle, ArrayList<DbxEntry> data, String auth_token){
		super(applicationTitle);
		PieDataset dataset = createDataset(data);
		JFreeChart chart = createChart(dataset, chartTitle);
		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new java.awt.Dimension(800,600));
		this.setContentPane(panel);
		token = auth_token;
		try {
			handler = new DropBoxHandler(token, "MapMyCloud");
		} catch (DbxException e) {
			e.printStackTrace();
		}
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
							dataset.setValue(data.get(i).name, handler.getFolderSize(data.get(i).path));
						} catch (DbxException e) {
							e.printStackTrace();
						}
                    }
                    else
                    {
                    	dataset.setValue(data.get(i).name, data.get(i).asFile().numBytes);
                    }
		}
		return dataset;
	}
	public static void main(String[] args){
		GraphicalPortion gp = new GraphicalPortion("Application Title", "Your Dropbox:", null, null);
		gp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gp.setSize(800,600);
		gp.pack();
		gp.setVisible(true);
	}
}
