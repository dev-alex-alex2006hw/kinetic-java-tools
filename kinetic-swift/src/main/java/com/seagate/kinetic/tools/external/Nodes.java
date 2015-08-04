/**
 * 
 */
package com.seagate.kinetic.tools.external;

/**
 * @author mshafiq
 *
 */
public class Nodes implements ExternalCommandService {
		public Nodes() {}
	    @Override
	    public ExternalResponse execute(ExternalRequest request) {
	        System.out.println("** received request: " + request.toJson());
	        CommandFilter filt = CommandFilter.getInstance();
	        String Cmd  = Globals.SWIFT_GET_NODES;
	        String Partition = request.getPartition();
	        String file = request.getFile();
	        Cmd += ("," + "--partition=" + Partition + "," + file);
	        return filt.ExecCmd(Cmd,  Globals.GetSwiftDir(request));
	    }
	    

}