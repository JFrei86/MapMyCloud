
import java.util.List;
import java.util.ArrayList;
public class DropBoxFolder extends DropBoxDataType {
	
	List<DropBoxDataType> links_to;
	List<DropBoxFolder> sub_folders;
	
	public DropBoxFolder()
	{
		super();
		links_to = new ArrayList<DropBoxDataType>();
		sub_folders = new ArrayList<DropBoxFolder>();
	}
	
	public DropBoxFolder(char new_name, List<DropBoxDataType> contains)
	{
		super();
		name = new_name;
		links_to = contains;
		
	}
	
	public DropBoxFolder(char new_name, int new_size, List<DropBoxDataType> contains)
	{ 
		super(new_name, new_size);
		links_to = contains;
	}
	
	public DropBoxFolder(char new_name, int new_size)
	{ 
		super(new_name, new_size);
	}
	
	public List<DropBoxDataType> Get_List_Of_Files()
	{
		return links_to;
	}
	
	public void Add_To_List_Of_Files(DropBoxDataType new_file)
	{
		links_to.add(new_file);
	}
	
	public void change_name_of_folder(char new_name)
	{
		name = new_name;
	}
	
	protected int determine_size()
	{
		size = 0;
		for(DropBoxDataType d : links_to)
		{
			if(!(d instanceof DropBoxFolder)) size+=d.size;
			else if(d instanceof DropBoxFolder)
			{
				//DropBoxFolder p = d;
				size+=((DropBoxFolder)(d)).determine_size();
			}
		}	
		return size;
	}
}
