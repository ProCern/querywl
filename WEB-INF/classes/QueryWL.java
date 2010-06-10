import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.management.*;
import weblogic.management.*;
import weblogic.management.configuration.*;

public class QueryWL extends HttpServlet {

  String mTypes[] = {
    "ExecuteQueueRuntime",
    "JMSRuntime",
    "JTARuntime",
    "JVMRuntime",
    "LogBroadcasterRuntime",
    "ServerRuntime",
    "ServerSecurityRuntime",
    "ServletRuntime",
    "TimeServiceRuntime",
    "WebAppComponentRuntime"
  };


  public synchronized void init( ServletConfig config )
    throws ServletException {

    super.init();
  }

  public void service( HttpServletRequest req, HttpServletResponse res )
    throws ServletException, IOException {
    return;

    res.setContentType("text/plain");
    PrintWriter out = res.getWriter();
    out.println("test");

    try {
      Context ctx = new InitialContext();
      MBeanHome home = (MBeanHome)ctx.lookup("weblogic.management.adminhome");

      ArrayList apps = new ArrayList();

      for(int i = 0; i < mTypes.length; i++ ) {
        Set beans = home.getMBeansByType( mTypes[i] );

        out.println("\nTYPE: "+mTypes[i]+"\n");
        for(Iterator j = beans.iterator(); j.hasNext(); ) {
          WebLogicMBean mbean = (WebLogicMBean)j.next();
          WebLogicObjectName objectName = mbean.getObjectName();
          String mName = objectName.getName();
          if (apps.isEmpty() ||  apps.contains(mName)) {
            out.println("NAME: "+mName);	       

            MBeanInfo mInfo = mbean.getMBeanInfo();
            MBeanAttributeInfo infos[] = mInfo.getAttributes();
            String aNames[] = new String[infos.length];

            for(int k = 0; k < infos.length; k++ ) {
              aNames[k] = infos[k].getName();
            }

            AttributeList attList = mbean.getAttributes(aNames);

            for(int l = 0; l < attList.size(); l++) {
              Attribute att = (Attribute)attList.get(l);
              String attName = att.getName();
              Object attValue = att.getValue();

              String attString = new String();

              if( attValue != null ) {
                attString = attValue.toString();
              } else {
                attString = "";
              }

             try {
                dd = Double.parseDouble(attString);
                out.println("Attrib: "+attName+"\tValue: "+attString);
             }
            catch (NumberFormatException exc) { } 
            }
          }
        }
      }
    } catch( NamingException e ) {
      e.printStackTrace();
    } 
  }
}
