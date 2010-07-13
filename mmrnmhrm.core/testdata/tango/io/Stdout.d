/*******************************************************************************

        copyright:      Copyright (c) 2005 Kris Bell. All rights reserved

        license:        BSD style: $(LICENSE)

        version:        Nov 2005: Initial release

        author:         Kris

        Standard, global formatters for console output. If you don't need
        formatted output or unicode translation, consider using the module
        tango.io.Console directly

        Note that both the buffer and conduit in use are exposed by these
        global instances ~ this can be leveraged, for instance, to copy a
        file to the standard output:
        ---
        Stdout.stream.copy (new FileConduit ("myfile"));
        ---

        Stdout & Stderr expose this style of usage:
        ---
        Stdout ("hello");               => hello
        Stdout (1);                     => 1
        Stdout (3.14);                  => 3.14
        Stdout ('b');                   => b
        Stdout (1, 2, 3);               => 1, 2, 3         
        Stdout ("abc", 1, 2, 3);        => abc, 1, 2, 3        
        Stdout ("abc", 1, 2) ("foo");   => abc, 1, 2foo        
        Stdout ("abc") ("def") (3.14);  => abcdef3.14

        Stdout.format ("abc {}", 1);    => abc 1
        Stdout.format ("abc ", 1);      => abc
        ---

        Note that the last example does not throw an exception. There
        are several use-cases where dropping an argument is legitimate,
        so we're currently not enforcing any particular trap mechanism.

        Flushing the output is achieved through the flush() method, or
        via an empty pair of parens: 
        ---
        Stdout ("hello world") ();
        Stdout ("hello world").flush;

        Stdout ("hello ") ("world") ();
        Stdout ("hello ") ("world").flush;

        Stdout.format ("hello {}", "world") ();
        Stdout.format ("hello {}", "world").flush;
        ---
        
        Newline is handled by either placing '\n' in the output, or via
        the newline() method. The latter also flushes the output:
        ---
        Stdout ("hello ") ("world").newline;
        Stdout.format ("hello {}", "world").newline;
        Stdout.formatln ("hello {}", "world");
        ---

        The format() method of both Stderr and Stdout support the range
        of formatting options provided by tango.text.convert.Layout and
        extensions thereof; including the full I18N extensions where it
        has been configured in that manner. To enable a French Stdout, 
        do the following:
        ---
        import tango.text.locale.Locale;

        Stdout.layout = new Locale (Culture.getCulture ("fr-FR"));
        ---
        
        Note that Stdout is a shared entity, so every usage of it will
        be affected by the above example. For applications supporting 
        multiple regions create multiple Locale instances instead, and 
        cache them in an appropriate manner

*******************************************************************************/

module tango.io.Stdout;

private import  tango.io.Print,
                tango.io.Console;

private import  tango.text.convert.Layout;

/*******************************************************************************

        Construct Stdout & Stderr when this module is loaded

*******************************************************************************/

static this()
{
        auto layout = new Layout!(char);

        Stdout = new Print!(char) (layout, Cout.stream);
        Stderr = new Print!(char) (layout, Cerr.stream);
}

public static Print!(char) Stdout,      /// global standard output
                           Stderr;      /// global error output


/******************************************************************************

******************************************************************************/

debug (Stdout)
{
        void main() 
        {
        Stdout ("hello").newline;               
        Stdout (1).newline;                     
        Stdout (3.14).newline;                  
        Stdout ('b').newline;                   
        Stdout ("abc") ("def") (3.14).newline;  
        Stdout ("abc", 1, 2, 3).newline;        
        Stdout (1, 2, 3).newline;        

        Stdout ("abc {}{}{}", 1, 2, 3).newline; 
        Stdout.format ("abc {}{}{}", 1, 2, 3).newline; 
        }
}
