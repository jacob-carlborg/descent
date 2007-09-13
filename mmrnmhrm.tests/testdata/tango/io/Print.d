/*******************************************************************************

        copyright:      Copyright (c) 2007 Kris Bell. All rights reserved

        license:        BSD style: $(LICENSE)

        version:        Feb 2007: Seperated from Stdout 
                
        author:         Kris

*******************************************************************************/

module tango.io.Print;

private import  tango.io.model.IBuffer,
                tango.io.model.IConduit;

private import  tango.text.convert.Layout;

/*******************************************************************************

        A bridge between a Layout instance and a Buffer. This is used for
        the Stdout & Stderr globals, but can be used for general purpose
        buffer-formatting as desired. The Template type 'T' dictates the
        text arrangement within the target buffer ~ one of char, wchar or
        dchar (utf8, utf16, or utf32). 
        
        Print exposes this style of usage:
        ---
        auto print = new Print!(char) (...);

        print ("hello");                        => hello
        print (1);                              => 1
        print (3.14);                           => 3.14
        print ('b');                            => b
        print (1, 2, 3);                        => 1, 2, 3         
        print ("abc", 1, 2, 3);                 => abc, 1, 2, 3        
        print ("abc", 1, 2) ("foo");            => abc, 1, 2foo        
        print ("abc") ("def") (3.14);           => abcdef3.14

        print.format ("abc {}", 1);             => abc 1
        print.format ("abc {}:{}", 1, 2);       => abc 1:2
        print.format ("abc {1}:{0}", 1, 2);     => abc 2:1
        print.format ("abc ", 1);               => abc
        ---

        Note that the last example does not throw an exception. There
        are several use-cases where dropping an argument is legitimate,
        so we're currently not enforcing any particular trap mechanism.

        Flushing the output is achieved through the flush() method, or
        via an empty pair of parens: 
        ---
        print ("hello world") ();
        print ("hello world").flush;

        print ("hello ") ("world") ();
        print ("hello ") ("world").flush;

        print.format ("hello {}", "world") ();
        print.format ("hello {}", "world").flush;
        ---
        
        Newline is handled by either placing '\n' in the output, or via
        the newline() method. The latter also flushes the output:
        ---
        print ("hello ") ("world").newline;

        print.format ("hello {}", "world").newline;
        ---

        The format() method supports the range of formatting options 
        exposed by tango.text.convert.Layout and extensions thereof; 
        including the full I18N extensions where configured in that 
        manner. To create a French instance of Print:
        ---
        import tango.text.locale.Locale;

        auto locale = new Locale (Culture.getCulture ("fr-FR"));
        auto print = new Print!(char) (locale, ...);
        ---
        
*******************************************************************************/

class Print(T)
{
        private T[]             eol;
        private OutputStream    output;
        private Layout!(T)      convert;

        public alias print      opCall;

        version (Win32)
                 private const T[] Eol = "\r\n";
             else
                private const T[] Eol = "\n";

        /**********************************************************************

                Construct a Print instance, tying the provided
                buffer to a formatter

        **********************************************************************/

        this (Layout!(T) convert, OutputStream output, T[] eol = Eol)
        {
                this.convert = convert;
                this.output = output;
                this.eol = eol;
        }

        /**********************************************************************

                Layout using the provided formatting specification

        **********************************************************************/

        final Print format (T[] fmt, ...)
        {
                convert (&sink, _arguments, _argptr, fmt);
                return this;
        }

        /**********************************************************************

                Layout using the provided formatting specification

        **********************************************************************/

        final Print formatln (T[] fmt, ...)
        {
                convert (&sink, _arguments, _argptr, fmt);
                return newline;
        }

        /**********************************************************************

                Unformatted layout, with commas inserted between args

        **********************************************************************/

        final Print print (...)
        {
                static  T[][] fmt =
                        [
                        "{}",
                        "{}, {}",
                        "{}, {}, {}",
                        "{}, {}, {}, {}",
                        "{}, {}, {}, {}, {}",
                        "{}, {}, {}, {}, {}, {}",
                        "{}, {}, {}, {}, {}, {}, {}",
                        "{}, {}, {}, {}, {}, {}, {}, {}",
                        "{}, {}, {}, {}, {}, {}, {}, {}, {}",
                        "{}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
                        "{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
                        "{}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
                        ];
               
                assert (_arguments.length <= fmt.length);

                if (_arguments.length is 0)
                    output.flush;
                else
                   convert (&sink, _arguments, _argptr, fmt[_arguments.length - 1]);
                         
                return this;
        }

        /***********************************************************************

                Output a newline and flush

        ***********************************************************************/

        final Print newline ()
        {
                output.write(eol);
                return flush;
        }

        /**********************************************************************

               Flush the output buffer

        **********************************************************************/

        final Print flush ()
        {
                output.flush;
                return this;
        }

        /**********************************************************************

                Return the associated conduit

        **********************************************************************/

        final OutputStream stream ()
        {
                return output;
        }

        /**********************************************************************

                Return the associated Layout

        **********************************************************************/

        final Layout!(T) layout ()
        {
                return convert;
        }

        /**********************************************************************

                Set the associated Layout

        **********************************************************************/

        final Print layout (Layout!(T) layout)
        {
                convert = layout;
                return this;
        }

        /**********************************************************************

                Sink for passing to the formatter

        **********************************************************************/

        private final uint sink (T[] s)
        {
                return output.write (s);
        }
}
