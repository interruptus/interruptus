package org.cad.interruptus.rest;


import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.model.ApiInfo;
import javax.servlet.http.HttpServlet;

public class Bootstrap extends HttpServlet 
{
  static {
    ApiInfo info = new ApiInfo(
      "Interruptus",                                                    /* title */
      "A framework for scalable monitoring",                            /* description */
      "http://interruptus.control-alt-del.org/",                        /* TOS URL */
      "interruptus@control-alt-del.org",                                /* Contact */
      "GNU",                                                            /* license */
      "https://github.com/marksteele/interruptus/blob/master/LICENSE"   /* license URL */
    );

    ConfigFactory.config().setApiInfo(info);
  }
}