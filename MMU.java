/**
*Author: STeven Childs
*email: schilds@email.sc.edu
*
*
*/

package osp.Memory;

import java.util.*;
import osp.IFLModules.*;
import osp.Threads.*;
import osp.Tasks.*;
import osp.Utilities.*;
import osp.Hardware.*;
import osp.Interrupts.*;

/**
    The MMU class contains the student code that performs the work of
    handling a memory reference.  It is responsible for calling the
    interrupt handler if a page fault is required.

    @OSPProject Memory
*/
public class MMU extends IflMMU
{
    /** 
        This method is called once before the simulation starts. 
	Can be used to initialize the frame table and other static variables.

        @OSPProject Memory
    */
static ArrayList<FrameTableEntry> frameTable;
    public static void init()
    {
	//static ArrayList<FrameTableEntry> frameTable;
        // your code goes here
	//Use a for loop to initialize each frame in the frame table. 
		for(int i = 0; i<MMU.getFrameTableSize();i++){
			setFrame(i, new FrameTableEntry(i));
		}
		frameTable = new ArrayList<FrameTableEntry>(); //initialize the frameTable that we will use in this class
    }

    /**
       This method handlies memory references. The method must 
       calculate, which memory page contains the memoryAddress,
       determine, whether the page is valid, start page fault 
       by making an interrupt if the page is invalid, finally, 
       if the page is still valid, i.e., not swapped out by another 
       thread while this thread was suspended, set its frame
       as referenced and then set it as dirty if necessary.
       (After pagefault, the thread will be placed on the ready queue, 
       and it is possible that some other thread will take away the frame.)
       
       @param memoryAddress A virtual memory address
       @param referenceType The type of memory reference to perform 
       @param thread that does the memory access
       (e.g., MemoryRead or MemoryWrite).
       @return The referenced page.

       @OSPProject Memory
    */
    static public PageTableEntry do_refer(int memoryAddress,
					  int referenceType, ThreadCB thread)
    {
        // your code goes here
	//calculate which memory page contains memoryAddress
	int pageAddress = (int)(memoryAddress/Math.pow(2, getVirtualAddressBits()- getPageAddressBits()));
	//determine if page is valid
	//first get the page
	PageTableEntry ourPage = getPTBR().pages[pageAddress];
	//Then get the pageframe
	FrameTableEntry pageFrame = ourPage.getFrame();
	//now run validity checky
	if(! ourPage.isValid()){
			if(ourPage.getValidatingThread() == null){
				InterruptVector.setInterruptType(referenceType);
				InterruptVector.setPage(ourPage);
				InterruptVector.setThread(thread);
				CPU.interrupt(PageFault);
			}
			else{
				thread.suspend(ourPage);
			}
			
			if(thread.getStatus() == ThreadKill){
				return ourPage; 
			}
		
	}
	ourPage.getFrame().setReferenced(true);
	if(referenceType == MemoryWrite){
		ourPage.getFrame().setDirty(true);
	}
	
	return ourPage; 
}

    /** Called by OSP after printing an error message. The student can
	insert code here to print various tables and data structures
	in their state just after the error happened.  The body can be
	left empty, if this feature is not used.
     
	@OSPProject Memory
     */
    public static void atError()
    {
        // your code goes here
	
    }

    /** Called by OSP after printing a warning message. The student
	can insert code here to print various tables and data
	structures in their state just after the warning happened.
	The body can be left empty, if this feature is not used.
     
      @OSPProject Memory
     */
    public static void atWarning()
    {
        // your code goes here

    }


    /*
       Feel free to add methods/fields to improve the readability of your code
    */

}

/*
      Feel free to add local classes to improve the readability of your code
*/
