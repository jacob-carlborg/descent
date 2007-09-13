// D import file generated from 'core\Exception.d'
module tango.core.Exception;
private
{
    alias void(* assertHandlerType)(char[] file, size_t line, char[] msg = null);
    alias TracedExceptionInfo(* traceHandlerType)(void* ptr = null);
    assertHandlerType assertHandler = null;
    traceHandlerType traceHandler = null;
}
interface TracedExceptionInfo
{
    int opApply(int delegate(inout char[]));
}
class OutOfMemoryException : Exception
{
    this(char[] file, size_t line)
{
super("Memory allocation failed",file,line);
}
    char[] toUtf8()
{
return msg ? super.toUtf8() : "Memory allocation failed";
}
}
class TracedException : Exception
{
    this(char[] msg)
{
super(msg);
m_info = traceContext();
}
    this(char[] msg, Exception e)
{
super(msg,e);
m_info = traceContext();
}
    this(char[] msg, char[] file, size_t line)
{
super(msg,file,line);
m_info = traceContext();
}
    char[] toUtf8();
    int opApply(int delegate(inout char[] buf) dg);
    private:
    TracedExceptionInfo m_info;
}
class PlatformException : TracedException
{
    this(char[] msg)
{
super(msg);
}
}
class AssertException : TracedException
{
    this(char[] file, size_t line)
{
super("Assertion failure",file,line);
}
    this(char[] msg, char[] file, size_t line)
{
super(msg,file,line);
}
}
class ArrayBoundsException : TracedException
{
    this(char[] file, size_t line)
{
super("Array index out of bounds",file,line);
}
}
class FinalizeException : TracedException
{
    ClassInfo info;
    this(ClassInfo c, Exception e = null)
{
super("Finalization error",e);
info = c;
}
    char[] toUtf8()
{
return "An exception was thrown while finalizing an instance of class " ~ info.name;
}
}
class SwitchException : TracedException
{
    this(char[] file, size_t line)
{
super("No appropriate switch clause found",file,line);
}
}
class TextException : TracedException
{
    this(char[] msg)
{
super(msg);
}
}
class UnicodeException : TextException
{
    size_t idx;
    this(char[] msg, size_t idx)
{
super(msg);
this.idx = idx;
}
}
class ThreadException : PlatformException
{
    this(char[] msg)
{
super(msg);
}
}
class FiberException : ThreadException
{
    this(char[] msg)
{
super(msg);
}
}
class SyncException : PlatformException
{
    this(char[] msg)
{
super(msg);
}
}
class IOException : PlatformException
{
    this(char[] msg)
{
super(msg);
}
}
class SocketException : IOException
{
    this(char[] msg)
{
super(msg);
}
}
class HostException : IOException
{
    this(char[] msg)
{
super(msg);
}
}
class AddressException : IOException
{
    this(char[] msg)
{
super(msg);
}
}
class SocketAcceptException : SocketException
{
    this(char[] msg)
{
super(msg);
}
}
class ProcessException : PlatformException
{
    this(char[] msg)
{
super(msg);
}
}
class RegexException : TextException
{
    this(char[] msg)
{
super(msg);
}
}
class RegistryException : TracedException
{
    this(char[] msg)
{
super(msg);
}
}
class CipherException : TracedException
{
    this(char[] msg)
{
super(msg);
}
}
class IllegalArgumentException : TracedException
{
    this(char[] msg)
{
super(msg);
}
}
class IllegalElementException : IllegalArgumentException
{
    this(char[] msg)
{
super(msg);
}
}
class NoSuchElementException : TracedException
{
    this(char[] msg)
{
super(msg);
}
}
class CorruptedIteratorException : NoSuchElementException
{
    this(char[] msg)
{
super(msg);
}
}
void setAssertHandler(assertHandlerType h)
{
assertHandler = h;
}
void setTraceHandler(traceHandlerType h)
{
traceHandler = h;
}
extern (C) 
{
    void onAssertError(char[] file, size_t line);
}
extern (C) 
{
    void onAssertErrorMsg(char[] file, size_t line, char[] msg);
}
TracedExceptionInfo traceContext(void* ptr = null);
extern (C) 
{
    void onArrayBoundsError(char[] file, size_t line);
}
extern (C) 
{
    void onFinalizeError(ClassInfo info, Exception ex);
}
extern (C) 
{
    void onOutOfMemoryError();
}
extern (C) 
{
    void onSwitchError(char[] file, size_t line);
}
extern (C) 
{
    void onUnicodeError(char[] msg, size_t idx);
}
