/**
*Author: Steven Childs
*Email: schilds@email.sc.edu
*
*/
package osp.Memory;

import osp.Hardware.*;
import osp.Tasks.*;
import osp.Threads.*;
import osp.Devices.*;
import osp.Utilities.*;
import osp.IFLModules.*;
/**
   The PageTableEntry object contains information about a specific virtual
   page in memory, including the page frame in which it resides.
   
   @OSPProject Memory

*/

public class PageTableEntry extends IflPageTableEntry
{
    /**
       The constructor. Must call

       	   super(ownerPageTable,pageNumber);
	   
       as its first statement.

       @OSPProject Memory
    */
    public PageTableEntry(PageTable ownerPageTable, int pageNumber)
    {
        // your code goes here
	super(ownerPageTable, pageNumber);
    }

    /**
       This method increases the lock count on the page by one. 

	The method must FIRST increment lockCount, THEN  
	check if the page is valid, and if it is not and no 
	page validation event is present for the page, start page fault 
	by calling PageFaultHandler.handlePageFault().

	@return SUCCESS or FAILURE
	FAILURE happens when the pagefault due to locking fails or the 
	that created the IORB thread gets killed.

	@OSPProject Memory
     */
    public int do_lock(IORB iorb)
    {
        // your code goes here
	ThreadCB iorbsThread = iorb.getThread();	//Get the thread of the IORB so we can see it later

	//series if cheks to see page is valid. If not, no page validation event is present for page, do page fault
	if(!isValid()){
		if(getValidatingThread() == null){
			PageFaultHandler.handlePageFault(iorbsThread, MemoryLock, this);//
		}else{//The invalid thread is not null
			if(getValidatingThread() != iorbsThread){//This would indicate that there's another thread dealing wtih the page
				iorbsThread.suspend(this);
				if(iorbsThread.getStatus() == GlobalVariables.ThreadKill){//cannot be locked because the thread has kill status
					return FAILURE; 
				}
			}
		}
	}
	getFrame().incrementLockCount();
	return SUCCESS; 
    }

    /** This method decreases the lock count on the page by one. 

	This method must decrement lockCount, but not below zero.

	@OSPProject Memory
    */
    public void do_unlock()
    {
        // your code goes here
	getFrame().decrementLockCount();
    }


    /*
       Feel free to add methods/fields to improve the readability of your code
    */

}

/*
      Feel free to add local classes to improve the readability of your code
*/
