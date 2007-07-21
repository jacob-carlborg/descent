package descent.tests.debugger;

import org.eclipse.debug.core.DebugEvent;
import org.jmock.Expectations;

import descent.debug.core.model.IDebugger;
import descent.internal.debug.core.model.ddbg.DdbgDebugger;

public class DdbgDebuggerTests extends AbstractDebuggerTests {
	
	@Override
	protected IDebugger createDebugger() {
		return new DdbgDebugger();
	}
	
	@Override
	protected String getEndCommunicationString() {
		return "->";
	}
	
	public void testGetDebuggerCommandLineArguments() {
		assertTrue(debugger.getDebuggerCommandLineArguments().isEmpty());
	}
	
	public void testDebugeeCommandLineArguments() {
		assertInIterable(debugger.getDebugeeCommandLineArguments(
				new String[] { "one", "two" }),
				"one", "two");
	}
	
	public void testEndCommunicationString() {
		assertEquals("->", debugger.getEndCommunicationString());
	}
	
	public void testStart() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("er\n");
			one(proxy).write("nc\n");
			one(proxy).write("r\n");
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.start();
			}
		});
		
		endCommunicationTimes(3);
		
		mockery.assertIsSatisfied();
	}
	
	public void testResume() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("r\n");
		}});
		
		debugger.resume();
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testTerminate() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("q\n");
		}});
		
		debugger.terminate();
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepOverEndsProgram() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_OVER);
			one(proxy).write("ov\n");
			one(listener).terminated();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepOver();
			}
		});		
		
		interpret("Process terminated");
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepOverContinues() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_OVER);
			one(proxy).write("ov\n");
			one(listener).stepEnded();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepOver();
			}
		});		
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepIntoEndsProgram() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_INTO);
			one(proxy).write("in\n");
			one(listener).terminated();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepInto();
			}
		});		
		
		interpret("Process terminated");
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepIntoContinues() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_INTO);
			one(proxy).write("in\n");
			one(listener).stepEnded();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepInto();
			}
		});		
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepReturnEndsProgram() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_RETURN);
			one(proxy).write("out\n");
			one(listener).terminated();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepReturn();
			}
		});		
		
		interpret("Process terminated");
		
		mockery.assertIsSatisfied();
	}
	
	public void testStepReturnContinues() throws Exception {
		checking(new Expectations() {{
			one(listener).resumed(DebugEvent.STEP_RETURN);
			one(proxy).write("out\n");
			one(listener).stepEnded();
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.stepReturn();
			}
		});		
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testAddBreakpoing() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("bp main.d:26\n");
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.addBreakpoint("main.d", 26);
			}
		});
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testRemoveBreakpoing() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("dbp main.d:26\n");
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.removeBreakpoint("main.d", 26);
			}
		});
		
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetStackFramesForm1() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("us\n");
			one(factory).newStackFrame("_Dmain()", 0, null, -1);
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getStackFrames();
			}
		});
		
		interpret("#0 _Dmain () from main.obj");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetStackFramesForm2() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("us\n");
			one(factory).newStackFrame("_main()", 1, null, -1);
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getStackFrames();
			}
		});
		
		interpret("#1 0x004020ac in _main () from dmain2");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetStackFramesForm3() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("us\n");
			one(factory).newStackFrame("??()", 2, null, -1);
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getStackFrames();
			}
		});
		
		interpret("#2 0x7c816fd7 in ?? () from KERNEL32.dll");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetStackFramesForm4() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("us\n");
			one(factory).newStackFrame("main.X._ctor()", 4, "main.d", 5);
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getStackFrames();
			}
		});
		
		interpret("#4 main.X._ctor () at main.d:5");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetStackFramesForm5() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("us\n");
			one(factory).newStackFrame("_Dmain()", 1, "main.d", 2);
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getStackFrames();
			}
		});
		
		interpret("#1 0x0040204a in _Dmain () at main.d:2");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetRegisters() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("dr cpu fpu mmx sse\n");
			one(factory).newRegister(registerGroup, "EAX", "00000001");
			one(factory).newRegister(registerGroup, "EBX", "003903c4");
			one(factory).newRegister(registerGroup, "ECX", "0012ff4c");
			one(factory).newRegister(registerGroup, "EDX", "0012ff4c");
			one(factory).newRegister(registerGroup, "EIP", "004020e4");
			one(factory).newRegister(registerGroup, "EFL", "00000346");
			one(factory).newRegister(registerGroup, "FCW", "137f");
			one(factory).newRegister(registerGroup, "FSW", "0100");
			one(factory).newRegister(registerGroup, "FTW", "ffff");
			one(factory).newRegister(registerGroup, "FOP", "06d9");
			one(factory).newRegister(registerGroup, "ST0", "0.0000000000000000e+00");
			one(factory).newRegister(registerGroup, "ST7", "inf");
			one(factory).newRegister(registerGroup, "MM0", "2022201d201c2019\n[1.32243e-19, 1.37326e-19]");
			one(factory).newRegister(registerGroup, "MM1", "009d0152203a0160\n[1.57553e-19, 1.44186e-38]");
			one(factory).newRegister(registerGroup, "MXCSR", "00001f80");
			one(factory).newRegister(registerGroup, "XMM0", "00d900d800d700d600d500d400d300d2\n[1.93776e-38, 1.95613e-38, 1.97449e-38, 1.99286e-38]\n[1.19638397306e-304, 1.4242350129e-304]");
			one(factory).newRegister(registerGroup, "XMM1", "00c100c000df00de00dd00dc00db00da\n[2.01123e-38, 2.0296e-38, 2.04796e-38, 1.77245e-38]\n[1.65208605274e-304, 4.8425951336e-305]");
		}});		
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getRegisters(registerGroup);
			}
		});
		
		
		interpret("EAX = 00000001\tEBX = 003903c4\tECX = 0012ff4c\tEDX = 0012ff4c");
		interpret("EIP = 004020e4\tEFL = 00000346");
		interpret("");
		interpret("FCW = 137f\tFSW = 0100\tFTW = ffff\tFOP = 06d9");
		interpret("ST0 =  0.0000000000000000e+00");
		interpret("ST7 =  inf");
		interpret("");
		interpret("MM0 = 2022201d201c2019");
		interpret("    = [1.32243e-19, 1.37326e-19]");
		interpret("MM1 = 009d0152203a0160");
		interpret("    = [1.57553e-19, 1.44186e-38]");
		interpret("");
		interpret("MXCSR = 00001f80");
		interpret("XMM0 = 00d900d800d700d600d500d400d300d2");
		interpret("     = [1.93776e-38, 1.95613e-38, 1.97449e-38, 1.99286e-38]");
		interpret("     = [1.19638397306e-304, 1.4242350129e-304]");
		interpret("XMM1 = 00c100c000df00de00dd00dc00db00da");
		interpret("     = [2.01123e-38, 2.0296e-38, 2.04796e-38, 1.77245e-38]");
		interpret("     = [1.65208605274e-304, 4.8425951336e-305]");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetVariablesNoResponse() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("f 1\n");
			one(proxy).write("lsv\n");
		}});		
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getVariables(1);
			}
		});
		
		endCommunication();
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetVariablesSimpleAnswer() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("f 1\n");
			one(proxy).write("lsv\n");
			one(factory).newParentVariable(1, "x", "2");
		}});		
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getVariables(1);
			}
		});
		
		endCommunication();
		
		interpret("x = 2\n");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetVariablesSimpleAnswers() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("f 1\n");
			one(proxy).write("lsv\n");
			one(factory).newParentVariable(1, "x", "2");
			one(factory).newParentVariable(1, "y", "3");
		}});		
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getVariables(1);
			}
		});
		
		endCommunication();
		
		interpret("x = 2\n");
		interpret("y = 3\n");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetNestedVariables() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("f 1\n");
			one(proxy).write("lsv\n");
			one(proxy).write("t x\n");
			one(factory).newParentVariable(1, "x", "class main.X*");
			one(factory).newParentVariable(1, "y", "2");
			one(factory).newParentVariable(1, "z", "3");
		}});		
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getVariables(1);
			}
		});
		
		endCommunication();
		
		interpret("x = {\n");
		interpret("  y = 2\n");
		interpret("  z = 3\n");
		interpret("}\n");
		endCommunication();
		
		interpret("PC4main1Class\n");
		interpret("class main.X*\n");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}
	
	public void testGetVariablesLazy() throws Exception {
		checking(new Expectations() {{
			one(proxy).write("f 1\n");
			one(proxy).write("lsv\n");
			one(proxy).write("t x\n");
			one(factory).newLazyVariable(1, "x", "class main.X*", "x");
		}});
		
		runInThread(new InThreadRunnable() {
			void run() throws Exception {
				debugger.getVariables(1);
			}
		});
		
		endCommunication();
		
		interpret("x = ...\n");
		endCommunication();
		
		interpret("PC4main1Class\n");
		interpret("class main.X*\n");
		endCommunication();
		
		mockery.assertIsSatisfied();
	}

}
