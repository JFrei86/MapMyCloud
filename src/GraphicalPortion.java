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


public class GraphicalPortion extends JFrame {
	public GraphicalPortion(String applicationTitle, String chartTitle, ArrayList<DropBoxDataType> data){
		super(applicationTitle);
		PieDataset dataset = createDataset(data);
		JFreeChart chart = createChart(dataset, chartTitle);
		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new java.awt.Dimension(800,600));
		this.setContentPane(panel);
	}
	private JFreeChart createChart(PieDataset dataset, String chartTitle) {
		JFreeChart chart = ChartFactory.createPieChart(chartTitle, dataset);
		return chart;
	}
	private PieDataset createDataset(ArrayList<DropBoxDataType> data) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(int i = 0; i < data.size(); i++){
			dataset.setValue((Comparable)data.get(i).getName(), data.get(i).size);
		}
		return dataset;
	}
	public static void main(String[] args){
		GraphicalPortion gp = new GraphicalPortion("Application Title", "Your Dropbox:", null);
		gp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gp.setSize(800,600);
		gp.pack();
		gp.setVisible(true);
	}
}
