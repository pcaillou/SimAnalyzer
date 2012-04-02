/** (c) 2007-2009 Uri Wilensky. See README.txt for terms of use. **/

package org.nlogo.extensions.profiler;

import org.nlogo.api.Syntax;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.Argument; 
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;

public class ProfilerExtension extends org.nlogo.api.DefaultClassManager
{
    public void load( org.nlogo.api.PrimitiveManager primManager ) {
	
		primManager.addPrimitive( "start", new ProfilerStart());
		primManager.addPrimitive( "stop", new ProfilerStop());
		primManager.addPrimitive( "reset", new ProfilerReset());
		primManager.addPrimitive( "report", new ProfilerReport());
		primManager.addPrimitive( "calls", new ProfilerProcedureCalls());
		primManager.addPrimitive( "exclusive-time", new ProfilerProcedureExclusiveTime());
		primManager.addPrimitive( "inclusive-time", new ProfilerProcedureInclusiveTime());
    }
    
    public void runOnce( org.nlogo.api.ExtensionManager em )
        throws org.nlogo.api.ExtensionException
    {
		org.nlogo.nvm.Tracer.profilingTracer = new QuickTracer() ;
		// we disable it once it is installed, so that
		// we don't start collecting profiling data until
		// ProfilerStart is called.  -- CLB
		org.nlogo.nvm.Tracer.profilingTracer.disable() ;
    }

    public static class ProfilerStart extends DefaultCommand
    {
		public Syntax getSyntax() {
			return Syntax.commandSyntax() ;
		}
		public void perform(Argument args[], Context context) throws ExtensionException {

			if ( Boolean.getBoolean( "org.nlogo.noGenerator" ) )
			{
				throw new ExtensionException
					("The profiler extension requires the NetLogo Bytecode generator, "
					 + "which is currently turned off, see the org.nlogo.noGenerator property.");
			}
			org.nlogo.nvm.Tracer.profilingTracer.enable() ;
		}
    }
    
    public static class ProfilerStop extends DefaultCommand
    {
		public Syntax getSyntax() {
			return Syntax.commandSyntax() ;
		}
		public void perform(Argument args[], Context context) throws ExtensionException {
			if ( org.nlogo.nvm.Tracer.profilingTracer != null )
			{
				org.nlogo.nvm.Tracer.profilingTracer.disable() ;
			}
		}
    }
    
    public static class ProfilerReset extends DefaultCommand
    {
		public Syntax getSyntax() {
			return Syntax.commandSyntax() ;
		}
		public void perform(Argument args[], Context context) throws ExtensionException {
			if ( org.nlogo.nvm.Tracer.profilingTracer != null )
			{
				org.nlogo.nvm.Tracer.profilingTracer.reset() ;
			}
		}
	
    }

    public static class ProfilerReport extends DefaultReporter
    {
		public Syntax getSyntax() {
			return Syntax.reporterSyntax(Syntax.TYPE_LIST) ;
		}
		public Object report(Argument args[], Context context) throws ExtensionException {
			if ( org.nlogo.nvm.Tracer.profilingTracer != null )
			{
				java.io.ByteArrayOutputStream outArray = new java.io.ByteArrayOutputStream() ;
				java.io.PrintStream out = new java.io.PrintStream( outArray ) ;
				org.nlogo.nvm.Tracer.profilingTracer.dump( out ) ;
				return outArray.toString() ;
			}
			return "" ;
		}
    }
    public static class ProfilerProcedureCalls extends DefaultReporter
    {
		public Syntax getSyntax() {
			return Syntax.reporterSyntax
				( new int[] { Syntax.TYPE_STRING } ,
				  Syntax.TYPE_NUMBER ) ;
		}
		public Object report(Argument args[], Context context) throws ExtensionException, LogoException {
			if ( org.nlogo.nvm.Tracer.profilingTracer != null )
			{
				String arg0 = args[ 0 ].getString().toUpperCase() ;
				return Double.valueOf( org.nlogo.nvm.Tracer.profilingTracer.calls( arg0 )) ;
			}
			return Double.valueOf(0) ;
		}
	}

    public static class ProfilerProcedureExclusiveTime extends DefaultReporter
    {
		public Syntax getSyntax() {
			return Syntax.reporterSyntax
				( new int[] { Syntax.TYPE_STRING } ,
				  Syntax.TYPE_NUMBER ) ;
		}
		public Object report(Argument args[], Context context) throws ExtensionException, LogoException {
			if ( org.nlogo.nvm.Tracer.profilingTracer != null )
			{
				String arg0 = args[ 0 ].getString().toUpperCase() ;
				return Double.valueOf( org.nlogo.nvm.Tracer.profilingTracer.exclusiveTime( arg0 ) / 1000000.0 ) ;
			}
			return Double.valueOf(0) ;
		}
	}

	public static class ProfilerProcedureInclusiveTime extends DefaultReporter
    {
		public Syntax getSyntax() {
			return Syntax.reporterSyntax
				( new int[] { Syntax.TYPE_STRING } ,
				  Syntax.TYPE_NUMBER ) ;
		}
		public Object report(Argument args[], Context context) throws ExtensionException, LogoException {
			if ( org.nlogo.nvm.Tracer.profilingTracer != null )
			{
				String arg0 = args[ 0 ].getString().toUpperCase() ;
				return Double.valueOf( org.nlogo.nvm.Tracer.profilingTracer.inclusiveTime( arg0 ) / 1000000.0 ) ;
			}
			return Double.valueOf(0) ;
		}
	}
}
