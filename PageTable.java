package osp.Memory;
/**
    The PageTable class represents the page table for a given task.
    A PageTable consists of an array of PageTableEntry objects.  This
    page table is of the non-inverted type.

    @OSPProject Memory
*/
import java.lang.Math;
import osp.Tasks.*;
import osp.Utilities.*;
import osp.IFLModules.*;
import osp.Hardware.*;

public class PageTable extends IflPageTable
{
	int pagesArraySize;
    /** 
	The page table constructor. Must call
	
	    super(ownerTask)

	as its first statement.

	@OSPProject Memory
    */
    public PageTable(TaskCB ownerTask)
    {
        // your code goes here
	super(ownerTask);

	//Need the size of the pagesArray which is given by the MMU's address bit number
	pagesArraySize = (int)Math.pow(2, MMU.getPageAddressBits());
	//set  a page to a new page table of that size
	pages = new PageTableEntry[pagesArraySize];
	//itinialize that page
	for(int i = 0; i <pagesArraySize; i++){
		pages[i] = new PageTableEntry(this, i);
	}
    }

    /**
       Frees up main memory occupied by the task.
       Then unreserves the freed pages, if necessary.

       @OSPProject Memory
    */
    public void do_deallocateMemory()
    {
        // your code goes here
	//for loop that sets the fraemes for the pageTable
	for(int i = 0; i <pagesArraySize; i++){
		FrameTableEntry tempFrame = pages[i].getFrame();
		if(tempFrame != null && tempFrame.getPage().getTask() == getTask()){
			tempFrame.setPage(null);
			tempFrame.setDirty(false);
			tempFrame.setReferenced(false);
		}
	}
	//for loop that sets the sets what task is used to reserve in the frame
	for(int i = 0; i< MMU.getFrameTableSize(); i++){
		TaskCB task = getTask();
		if(MMU.getFrame(i).getReserved() == task){
			MMU.getFrame(i).setUnreserved(task);
		}
	}
    }


    /*
       Feel free to add methods/fields to improve the readability of your code
    */

}

/*
      Feel free to add local classes to improve the readability of your code
*/
