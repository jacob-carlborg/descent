/*******************************************************************************

        copyright:      Copyright (c) 2004 Kris Bell. All rights reserved

        license:        BSD style: $(LICENSE)

        version:        Initial release: Feb 2005 
        version:        Heavily revised for unicode; November 2005
                        Outback release: December 2006
        
        author:         Kris

*******************************************************************************/

module tango.io.Console;

private import  tango.sys.Common;

private import  tango.io.Buffer,
                tango.io.DeviceConduit;


version (Posix)
         private import tango.stdc.posix.unistd;  // needed for isatty()


/*******************************************************************************

        low level console IO support. 
        
        Note that for a while this was templated for each of char, wchar, 
        and dchar. It became clear after some usage that the console is
        more useful if it sticks to Utf8 only. See ConsoleConduit below
        for details.

        Redirecting the standard IO handles (via a shell) operates as one 
        would expect, though the redirected content should likely restrict 
        itself to utf8 

*******************************************************************************/

struct Console 
{
        version (Win32)
                 const char[] Eol = "\r\n";
              else
                 const char[] Eol = "\n";


        /**********************************************************************

                Model console input as a buffer. Note that we read utf8
                only.

        **********************************************************************/

        class Input
        {
                private Buffer  buffer_;
                private bool    redirected_;

                public alias    copyln get;

                /**************************************************************

                        Attach console input to the provided device

                **************************************************************/

                private this (Conduit conduit, bool redirected)
                {
                        redirected_ = redirected;
                        buffer_ = new Buffer (conduit);
                }

                /**************************************************************

                        Return the associated conduit

                **************************************************************/

                InputStream stream ()
                {
                        return buffer_;
                }

                /**************************************************************

                        Return the next line available from the console, 
                        or null when there is nothing available. The value
                        returned is a duplicate of the buffer content (it
                        has .dup applied). 

                        Each line ending is removed unless parameter raw is
                        set to true

                **************************************************************/

                char[] copyln (bool raw = false)
                {
                        char[] line;

                        return readln (line, raw) ? line.dup : null;
                }

                /**************************************************************

                        Retreive a line of text from the console and map
                        it to the given argument. The input is sliced, 
                        not copied, so use .dup appropriately. Each line
                        ending is removed unless parameter raw is set to 
                        true.
                        
                        Returns false when there is no more input.

                **************************************************************/

                bool readln (inout char[] content, bool raw=false)
                {
                        uint line (void[] input)
                        {
                                auto text = cast(char[]) input;
                                foreach (i, c; text)
                                         if (c is '\n')
                                            {
                                            auto j = i;
                                            if (raw)
                                                ++j;
                                            else
                                               if (j && (text[j-1] is '\r'))
                                                   --j;
                                            content = text [0 .. j];
                                            return i+1;
                                            }
                                content = text;
                                return IConduit.Eof;
                        }

                        return buffer_.next (&line) || content.length;
                }

                /**************************************************************

                        Is this device redirected?

                        Returns:
                        True if redirected, false otherwise.

                        Remarks:
                        Reflects the console redirection status from when 
                        this module was instantiated

                **************************************************************/

                bool redirected ()
                {
                        return redirected_;
                }           
        }


        /**********************************************************************

                Model console output as a buffer. Note that we accept 
                utf8 only: the superclass append() methods are hidden
                from view. Buffer.consume is left open, for those who
                require lower-level access ~ along with Buffer.write

        **********************************************************************/

        class Output
        {
                private Buffer  buffer_;
                private bool    redirected_;

                public  alias   append opCall;
                public  alias   flush  opCall;

                /**************************************************************

                        Attach console output to the provided device

                **************************************************************/

                private this (Conduit conduit, bool redirected)
                {
                        // get conduit to notify us of attachments so
                        // that we can adjust our buffer accordingly
                        conduit.notify (&notify);

                        redirected_ = redirected;
                        buffer_ = new Buffer (conduit);
                }

                /**************************************************************

                        Return the associated conduit

                **************************************************************/

                OutputStream stream ()
                {
                        return buffer_;
                }

                /**************************************************************

                        Append to the console. We accept UTF8 only, so
                        all other encodings should be handled via some
                        higher level API

                **************************************************************/

                Output append (char[] x)
                {
                        buffer_.append (x.ptr, x.length);
                        return this;
                } 
                          
                /**************************************************************

                        Append content

                        Params:
                        other = an object with a useful toUtf8() method

                        Returns:
                        Returns a chaining reference if all content was 
                        written. Throws an IOException indicating eof or 
                        eob if not.

                        Remarks:
                        Append the result of other.toUtf8() to the console

                **************************************************************/

                Output append (Object o)        
                {           
                        return append (o.toUtf8);
                }

                /**************************************************************

                        Append a newline and flush the console buffer

                        Returns:
                        Returns a chaining reference if content was written. 
                        Throws an IOException indicating eof or eob if not.

                        Remarks:
                        Emit a newline into the buffer

                **************************************************************/

                Output newline ()
                {
                        buffer_.append (Eol);

                        // experimental: don't flush if we're redirected
                        version (RedirectNoFlush)
                                 if (redirected_)
                                     return this;

                        return flush;
                }           

                /**************************************************************

                        Flush console output

                        Returns:
                        Returns a chaining reference if content was written. 
                        Throws an IOException indicating eof or eob if not.

                        Remarks:
                        Flushes the console buffer to attached conduit

                **************************************************************/

                Output flush ()
                {
                        buffer_.flush;
                        return this;
                }           

                /**************************************************************

                        Is this device redirected?

                        Returns:
                        True if redirected, false otherwise.

                        Remarks:
                        Reflects the console redirection status from when 
                        this module was instantiated

                **************************************************************/

                bool redirected ()
                {
                        return redirected_;
                }           

                /**************************************************************

                        Invoked when an attachment is made to the console
                        Conduit. We use this to point our buffer at the 
                        filter being attached. Without this, said filter
                        would be ignored since Buffer operates in a mode
                        termed 'snapshot' i.e. it doesn't look for changes
                        in the conduit after being connected to it (which
                        is both correct and required behaviour).

                        An alternative would be to simply insert a buffered
                        filter. However, Cin requires readln() support and
                        therefore needs to directly address a buffer rather
                        than an InputStream.

                **************************************************************/

                private void notify (bool)
                {
                        buffer_.setConduit (buffer_.conduit);
                }
        }


        /***********************************************************************

                Conduit for specifically handling the console devices. This 
                takes care of certain implementation details on the Win32 
                platform.

                Note that the console is fixed at Utf8 for both linux and
                Win32. The latter is actually Utf16 native, but it's just
                too much hassle for a developer to handle the distinction
                when it really should be a no-brainer. In particular, the
                Win32 console functions don't work with redirection. This
                causes additional difficulties that can be ameliorated by
                asserting console I/O is always Utf8, in all modes.

        ***********************************************************************/

        class Conduit : DeviceConduit
        {
                private bool redirected = false;

                /***************************************************************

                        Intercept the default file-flushing implementation

                ***************************************************************/

                override void flush ()
                {
                }

                /***************************************************************

                        Windows-specific code

                ***************************************************************/

                version (Win32)
                        {
                        private wchar[] input;
                        private wchar[] output;

                        /*******************************************************

                                Create a FileConduit on the provided 
                                FileDevice. 

                                This is strictly for adapting existing 
                                devices such as Stdout and friends

                        *******************************************************/

                        private this (uint handle)
                        {
                                input = new wchar [1024 * 1];
                                output = new wchar [1024 * 1];
                                reopen (cast(Handle) handle);
                        }    

                        /*******************************************************

                                Gain access to the standard IO handles 

                        *******************************************************/

                        protected override void reopen (Handle handle_)
                        {
                                static const DWORD[] id = [
                                                          cast(DWORD) -10, 
                                                          cast(DWORD) -11, 
                                                          cast(DWORD) -12
                                                          ];
                                static const char[][] f = [
                                                          "CONIN$\0", 
                                                          "CONOUT$\0", 
                                                          "CONOUT$\0"
                                                          ];

                                assert (handle_ < 3);
                                handle = GetStdHandle (id[handle_]);
                                if (handle is null)
                                    handle = CreateFileA (f[handle_].ptr, 
                                             GENERIC_READ | GENERIC_WRITE,  
                                             FILE_SHARE_READ | FILE_SHARE_WRITE, 
                                             null, OPEN_EXISTING, 0, cast(HANDLE) 0);
                                if (handle is null)
                                    error ();

                                // are we redirecting?
                                DWORD mode;
                                if (! GetConsoleMode (handle, &mode))
                                      redirected = true;
                        }

                        /*******************************************************

                                Write a chunk of bytes to the console from the 
                                provided array (typically that belonging to 
                                an IBuffer)

                        *******************************************************/

                        version (Win32SansUnicode) 
                                {} 
                             else
                                {
                                override uint write (void[] src)
                                {
                                if (redirected)
                                    return super.write (src);
                                else
                                   {
                                   DWORD i = src.length;

                                   // protect conversion from empty strings
                                   if (i is 0)
                                       return 0;

                                   // expand buffer appropriately
                                   if (output.length < i)
                                       output.length = i;

                                   // convert into output buffer
                                   i = MultiByteToWideChar (CP_UTF8, 0, cast(char*) src.ptr, i, 
                                                            output.ptr, output.length);
                                            
                                   // flush produced output
                                   for (wchar* p=output.ptr, end=output.ptr+i; p < end; p+=i)
                                       {
                                       const int MAX = 32767;

                                       // avoid console limitation of 64KB 
                                       DWORD len = end - p; 
                                       if (len > MAX)
                                          {
                                          len = MAX;
                                          // check for trailing surrogate ...
                                          if ((p[len-1] & 0xfc00) is 0xdc00)
                                               --len;
                                          }
                                       if (! WriteConsoleW (handle, p, len, &i, null))
                                             error();
                                       }
                                   return src.length;
                                   }
                                }
                                }
                        
                        /*******************************************************

                                Read a chunk of bytes from the console into the 
                                provided array (typically that belonging to 
                                an IBuffer)

                        *******************************************************/

                        version (Win32SansUnicode) 
                                {} 
                             else
                                {
                                protected override uint read (void[] dst)
                                {
                                if (redirected)
                                    return super.read (dst);
                                else
                                   {
                                   DWORD i = dst.length / 4;

                                   assert (i);

                                   if (i > input.length)
                                       i = input.length;
                                       
                                   // read a chunk of wchars from the console
                                   if (! ReadConsoleW (handle, input.ptr, i, &i, null))
                                         error();

                                   // no input ~ go home
                                   if (i is 0)
                                       return Eof;

                                   // translate to utf8, directly into dst
                                   i = WideCharToMultiByte (CP_UTF8, 0, input.ptr, i, 
                                                            cast(char*) dst.ptr, dst.length, null, null);
                                   if (i is 0)
                                       error ();

                                   return i;
                                   }
                                }
                                }

                        }
                     else
                        {
                        /*******************************************************

                                Create a FileConduit on the provided 
                                FileDevice. 

                                This is strictly for adapting existing 
                                devices such as Stdout and friends

                        *******************************************************/

                        private this (Handle handle)
                        {
                                reopen (handle);
                                redirected = (isatty(handle) is 0);
                        }
                        }
        }
}


/******************************************************************************

        Globals representing Console IO

******************************************************************************/

static Console.Input    Cin;                    /// the standard input stream
static Console.Output   Cout,                   /// the standard output stream
                        Cerr;                   /// the standard error stream


/******************************************************************************

        Instantiate Console access

******************************************************************************/

static this ()
{
        auto conduit = new Console.Conduit (0);
        Cin  = new Console.Input (conduit, conduit.redirected);

        conduit = new Console.Conduit (1);
        Cout = new Console.Output (conduit, conduit.redirected);

        conduit = new Console.Conduit (2);
        Cerr = new Console.Output (conduit, conduit.redirected);
}


/******************************************************************************

        Flush outputs before we exit

        (good idea from Frits Van Bommel)

******************************************************************************/

static ~this()
{
        synchronized (Cout)
                      Cout.flush;

        synchronized (Cerr)
                      Cerr.flush;
}
