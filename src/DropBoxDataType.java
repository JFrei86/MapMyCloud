
/*
 * Two classes available:
 * 		 DropBoxDataType, which covers all types except for folders.
 * 		 DropBoxFolder, which is a subclass of DropBoxDataType with one difference:
 * 			Holds a list of DropBoxDataType objects
 */

public class DropBoxDataType {
	
	protected char name;
	protected int size;
	
	public DropBoxDataType()
	{
		name = '\n';
		size = 0;
	}
	
	public DropBoxDataType(char new_name, int new_size)
	{
		name = new_name;
		size = new_size;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public char getName()
	{
		return name;
	}
	
	public void setName(char name_of_file)
	{
		name = name_of_file;
	}
	
	public void changeSize(int manual_size)
	{
		size = manual_size;
	}
}

